package com.cleanroommc.groovyscript.server.features.textureDecoration;

import com.cleanroommc.groovyscript.mapper.ObjectMapper;
import com.cleanroommc.groovyscript.mapper.TextureBinder;
import com.cleanroommc.groovyscript.sandbox.FileUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.prominic.groovyls.compiler.ast.ASTContext;
import net.prominic.groovyls.compiler.util.GroovyASTUtils;
import net.prominic.groovyls.providers.DocProvider;
import net.prominic.groovyls.util.GroovyLSUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.expr.*;
import org.eclipse.lsp4j.Range;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class TextureDecorationProvider extends DocProvider {

    private static final int ICON_W = 16, ICON_H = 16;
    private static final int ICON_X = 0, ICON_Y = 0;
    private static final Map<String, TextureDecoration> textures = new Object2ObjectOpenHashMap<>();

    private final String cacheRoot = FileUtil.makePath(FileUtil.getMinecraftHome(), "cache", "groovy", "textureDecorations");


    public TextureDecorationProvider(URI doc, ASTContext context) {
        super(doc, context);
    }

    public CompletableFuture<List<TextureDecorationInformation>> provideTextureDecorations() {
        List<TextureDecorationInformation> decorationInformations = new ArrayList<>();
        List<TextureDecoration> queueDeco = new ArrayList<>();
        List<Range> queueRange = new ArrayList<>();
        Set<MethodCallExpression> mappers = new ObjectOpenHashSet<>();
        for (ASTNode node : getNodes()) {
            ASTNode start;
            MethodCallExpression call;
            if (node instanceof PropertyExpression prop) {
                if (prop.getObjectExpression() instanceof MethodCallExpression mc) {
                    start = prop;
                    call = mc;
                } else continue;
            } else if (node instanceof MethodCallExpression method) {
                if (method.getObjectExpression() instanceof MethodCallExpression mc) {
                    start = method;
                    call = mc;
                } else {
                    start = method;
                    call = method;
                }
            } else continue;
            if (mappers.contains(call) ||
                !(call.getArguments() instanceof ArgumentListExpression args) ||
                args.getExpressions().isEmpty()) continue;
            ObjectMapper<?> mapper;
            try {
                mapper = GroovyASTUtils.getMapperOfNode(call, astContext);
            } catch (GroovyBugError e) {
                continue;
            }
            if (mapper == null) continue;
            mappers.add(call);

            if (!args.getExpressions().stream().allMatch(e -> e instanceof ConstantExpression)) {
                continue;
            }

            var binder = mapper.getTextureBinder();
            if (binder == null) continue;

            var textureName = computeTextureName(mapper.getName(), args.getExpressions());
            var uri = getURIForDecoration(textureName);
            var decoration = textures.get(uri);

            if (decoration == null) {
                var bindable = getObjectWithConstArgs(mapper, args);
                if (bindable == null) continue;
                decoration = new TextureDecoration(textureName, binder, bindable, uri);
                textures.put(uri, decoration);
            }

            var range = GroovyLSUtils.astNodeToRange(start, call);
            if (decoration.isFileExists()) {
                decorationInformations.add(new TextureDecorationInformation(range, uri));
                continue;
            }
            if (!decoration.queued) {
                decoration.queued = true;
                queueDeco.add(decoration);
                queueRange.add(range);
            }
        }

        if (queueDeco.isEmpty()) {
            return CompletableFuture.completedFuture(decorationInformations);
        }

        var future = new CompletableFuture<List<TextureDecorationInformation>>();

        Minecraft.getMinecraft()
                .addScheduledTask(() -> render(queueDeco, queueRange, decorationInformations))
                .addListener(() -> future.complete(decorationInformations), Runnable::run);

        return future;
    }

    private static <T> T getObjectWithConstArgs(ObjectMapper<T> mapper, ArgumentListExpression args) {
        var additionalArgs = new Object[args.getExpressions().size() - 1];
        for (int i = 0; i < additionalArgs.length; i++) {
            if (args.getExpressions().get(i + 1) instanceof ConstantExpression argExpression)
                additionalArgs[i] = argExpression.getValue();
        }
        return mapper.invoke(true, args.getExpressions().get(0).getText(), additionalArgs);
    }

    private String computeTextureName(String name, List<Expression> expressions) {
        var sb = new StringBuilder(name);
        for (Expression expression : expressions) {
            sb.append(expression.getText());
        }
        return DigestUtils.sha1Hex(sb.toString());
    }

    private void render(List<TextureDecoration> queueDeco, List<Range> queueRange,
                        List<TextureDecorationInformation> decorationInformations) {
        var framebuffer = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);

        var texture = GL11.glGenTextures();
        GlStateManager.bindTexture(texture);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, ICON_W, ICON_H, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, texture, 0);

        var renderBuffer = GL30.glGenRenderbuffers();

        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, renderBuffer);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH_COMPONENT32F, ICON_W, ICON_H);
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);

        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, renderBuffer);

        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            throw new IllegalStateException("Framebuffer is not complete!");
        }

        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);

        GlStateManager.viewport(0, 0, ICON_W, ICON_H);

        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, ICON_W, ICON_H, 0.0D, 1000.0D, 21000.0D);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
        GlStateManager.translate(ICON_X, ICON_Y, -2000.0F);
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);

        var buffer = BufferUtils.createByteBuffer(ICON_W * ICON_H * 4);

        for (int i = 0; i < queueDeco.size(); i++) {
            TextureDecoration decoration = queueDeco.get(i);
            Range range = queueRange.get(i);
            GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            decoration.render();

            GL11.glReadPixels(0, 0, ICON_W, ICON_H, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

            saveImage(decoration.getFile(), buffer);
            buffer.rewind();

            decoration.fileExists = true;
            decoration.queued = false;
            decorationInformations.add(new TextureDecorationInformation(range, decoration.getUri()));
        }

        GlStateManager.enableAlpha();
        GlStateManager.disableDepth();

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GlStateManager.deleteTexture(texture);
        GL30.glDeleteRenderbuffers(renderBuffer);
        GL30.glDeleteFramebuffers(framebuffer);
    }

    private static void saveImage(File file, ByteBuffer buffer) {
        var image = new BufferedImage(ICON_W, ICON_H, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < ICON_H; y++) {
            for (int x = 0; x < ICON_W; x++) {
                int r = buffer.get() & 0xFF;
                int g = buffer.get() & 0xFF;
                int b = buffer.get() & 0xFF;
                int a = buffer.get() & 0xFF;
                int color = (a << 24) | (r << 16) | (g << 8) | b;
                image.setRGB(x, ICON_H - 1 - y, color); // Flip the y-coordinate to correct the image orientation
            }
        }

        try {
            ImageIO.write(image, "PNG", file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getURIForDecoration(String name) {
        return FileUtil.makeFile(cacheRoot, name + ".png").toURI().toString();
    }

    private class TextureDecoration {

        private final String name;
        private final String uri;
        private final TextureBinder<?> binder;
        private final Object bindable;
        private boolean fileExists;
        private boolean queued;

        private TextureDecoration(String name, TextureBinder<?> binder, Object bindable, String uri) {
            this.name = name;
            this.binder = binder;
            this.bindable = bindable;
            this.uri = uri;
            File file = getFile();
            this.fileExists = !file.getParentFile().mkdirs() && file.exists();
        }

        public String getUri() {
            return uri;
        }

        private @NotNull File getFile() {
            return FileUtil.makeFile(cacheRoot, name + ".png");
        }

        public void render() {
            ((TextureBinder) binder).accept(bindable);
        }

        public boolean isFileExists() {
            return fileExists;
        }
    }
}
