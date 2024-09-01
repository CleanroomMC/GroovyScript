package com.cleanroommc.groovyscript.server.features.textureDecoration;

import com.cleanroommc.groovyscript.mapper.ObjectMapper;
import com.cleanroommc.groovyscript.mapper.TextureBinder;
import com.cleanroommc.groovyscript.sandbox.FileUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.prominic.groovyls.compiler.ast.ASTContext;
import net.prominic.groovyls.compiler.util.GroovyASTUtils;
import net.prominic.groovyls.util.GroovyLSUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TextureDecorationProvider {

    private final String cacheRoot = FileUtil.makePath(FileUtil.getMinecraftHome(), "cache", "groovy", "textureDecorations");

    private final ASTContext context;

    private final List<TextureDecoration> decorations = new ArrayList<>();
    private final List<TextureDecorationInformation> decorationInformations = new ArrayList<>();

    public TextureDecorationProvider(ASTContext context) {
        this.context = context;
    }

    public CompletableFuture<List<TextureDecorationInformation>> provideTextureDecorations(TextDocumentIdentifier textDocument) {
        for (ASTNode node : context.getVisitor().getNodes()) {
            if (node instanceof MethodCallExpression expression && expression.getArguments() instanceof ArgumentListExpression args && !args.getExpressions().isEmpty()) {
                ObjectMapper<?> goh;
                try {
                    goh = GroovyASTUtils.getGohOfNode(expression, context);
                } catch (GroovyBugError e) {
                    continue;
                }
                if (goh == null) continue;

                if (!args.getExpressions().stream().allMatch(e -> e instanceof ConstantExpression))
                    continue;

                var binder = goh.getTextureBinder();
                if (binder == null) continue;

                var additionalArgs = new Object[args.getExpressions().size() - 1];
                for (int i = 0; i < additionalArgs.length; i++) {
                    if (args.getExpressions().get(i + 1) instanceof ConstantExpression argExpression)
                        additionalArgs[i] = argExpression.getValue();
                }

                var bindable = goh.doCall(args.getExpressions().get(0).getText(), additionalArgs);

                if (bindable == null) continue;

                var textureName = computeTextureName(goh.getName(), args.getExpressions());

                var decoration = new TextureDecoration(textureName, binder, bindable, GroovyLSUtils.astNodeToRange(expression));

                var textureFile = decoration.getFile();

                if (!textureFile.getParentFile().mkdirs() && textureFile.exists()) {
                    decorationInformations.add(new TextureDecorationInformation(decoration.getRange(), decoration.getUri()));
                    continue;
                }

                decorations.add(decoration);
            }
        }

        if (decorations.isEmpty())
            return CompletableFuture.completedFuture(decorationInformations);

        var future = new CompletableFuture<List<TextureDecorationInformation>>();

        Minecraft.getMinecraft()
                .addScheduledTask(this::render)
                .addListener(() -> future.complete(decorationInformations), Runnable::run);

        return future;
    }

    private String computeTextureName(String name, List<Expression> expressions) {
        var sb = new StringBuilder(name);
        for (Expression expression : expressions) {
            sb.append(expression.getText());
        }
        return DigestUtils.sha1Hex(sb.toString());
    }


    private void render() {
        var framebuffer = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);

        var texture = GL11.glGenTextures();
        GlStateManager.bindTexture(texture);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, 16, 16, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, texture, 0);

        var renderBuffer = GL30.glGenRenderbuffers();

        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, renderBuffer);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH_COMPONENT32F, 16, 16);
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);

        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, renderBuffer);

        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            throw new IllegalStateException("Framebuffer is not complete!");
        }

        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);

        GlStateManager.viewport(0, 0, 16, 16);

        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, 16.0D, 16.0D, 0.0D, 1000.0D, 21000.0D);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);

        var buffer = BufferUtils.createByteBuffer(16 * 16 * 4);

        for (TextureDecoration decoration : decorations) {
            GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            decoration.render();

            GL11.glReadPixels(0, 0, 16, 16, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

            saveImage(decoration.getFile(), buffer);
            buffer.rewind();

            decorationInformations.add(new TextureDecorationInformation(decoration.getRange(), decoration.getUri()));
        }

        GlStateManager.enableAlpha();
        GlStateManager.disableDepth();

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GlStateManager.deleteTexture(texture);
        GL30.glDeleteRenderbuffers(renderBuffer);
        GL30.glDeleteFramebuffers(framebuffer);
    }

    private static void saveImage(File file, ByteBuffer buffer) {
        var image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                int r = buffer.get() & 0xFF;
                int g = buffer.get() & 0xFF;
                int b = buffer.get() & 0xFF;
                int a = buffer.get() & 0xFF;
                int color = (a << 24) | (r << 16) | (g << 8) | b;
                image.setRGB(x, 15 - y, color); // Flip the y-coordinate to correct the image orientation
            }
        }

        try {
            ImageIO.write(image, "PNG", file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private class TextureDecoration {

        private final String name;
        private final TextureBinder<?> binder;
        private final Object bindable;
        private final Range range;

        private TextureDecoration(String name, TextureBinder<?> binder, Object bindable, Range range) {
            this.name = name;
            this.binder = binder;
            this.bindable = bindable;
            this.range = range;
        }

        public String getUri() {
            return "file://" + getFile().getAbsolutePath();
        }

        private @NotNull File getFile() {
            return FileUtil.makeFile(cacheRoot, name + ".png");
        }

        public Range getRange() {
            return range;
        }

        public void render() {
            ((TextureBinder) binder).accept(bindable);
        }
    }
}
