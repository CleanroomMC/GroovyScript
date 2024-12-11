package com.cleanroommc.groovyscript.server.features.textureDecoration;

import com.cleanroommc.groovyscript.mapper.AbstractObjectMapper;
import com.cleanroommc.groovyscript.mapper.TooltipEmbedding;
import com.cleanroommc.groovyscript.sandbox.SandboxData;
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
import org.jetbrains.annotations.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.io.File;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class TextureDecorationProvider extends DocProvider {

    public static final int ICON_W = 16;
    public static final int ICON_H = 16;
    public static final int ICON_X = 0;
    public static final int ICON_Y = 0;
    private static final Map<String, TextureDecoration<?>> textures = new Object2ObjectOpenHashMap<>();

    public static final File cacheRoot = new File(SandboxData.getCachePath(), "texdecs");

    static {
        cacheRoot.mkdirs();
    }

    public TextureDecorationProvider(URI doc, ASTContext context) {
        super(doc, context);
    }

    public CompletableFuture<List<TextureDecorationInformation>> provideTextureDecorations() {
        List<TextureDecorationInformation> decorationInformations = new ArrayList<>();
        List<TextureDecoration<?>> queueDeco = new ArrayList<>();
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
            if (mappers.contains(call) || !(call.getArguments() instanceof ArgumentListExpression args) || args.getExpressions().isEmpty()) continue;
            AbstractObjectMapper<?> mapper;
            try {
                mapper = GroovyASTUtils.getMapperOfNode(call, astContext);
            } catch (GroovyBugError e) {
                continue;
            }
            if (mapper == null) continue;
            mappers.add(call);

            if (!mapper.hasTextureBinder() || !args.getExpressions().stream().allMatch(e -> e instanceof ConstantExpression)) {
                continue;
            }

            var textureName = computeTextureName(mapper.getName(), args.getExpressions());
            var uri = getURIForDecoration(textureName);

            TextureDecoration<?> decoration;
            synchronized (textures) {
                decoration = textures.get(uri);
                if (decoration == null) {
                    decoration = TextureDecoration.create(textureName, uri, mapper, args);
                    if (decoration == null) continue;
                    textures.put(uri, decoration);
                }
            }

            var range = GroovyLSUtils.astNodeToRange(start, call);
            if (decoration.isFileExists()) {
                List<String> tooltips = decoration.getTooltip();
                if (formatTooltips(tooltips, null)) {
                    decorationInformations.add(new TextureDecorationInformation(range, uri, tooltips));
                    continue;
                }
            }
            synchronized (decoration) {
                if (!decoration.queued) {
                    decoration.queued = true;
                    queueDeco.add(decoration);
                    queueRange.add(range);
                }
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

    private String computeTextureName(String name, List<Expression> expressions) {
        var sb = new StringBuilder(name);
        for (Expression expression : expressions) {
            sb.append(expression.getText());
        }
        return DigestUtils.sha1Hex(sb.toString());
    }

    private void render(List<TextureDecoration<?>> queueDeco,
                        List<Range> queueRange,
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
            TextureDecoration<?> decoration = queueDeco.get(i);
            Range range = queueRange.get(i);
            decoration.render(buffer);
            var tooltipStrings = decoration.getTooltip();
            formatTooltips(tooltipStrings, buffer);
            decorationInformations.add(new TextureDecorationInformation(range, decoration.getUri(), tooltipStrings));
        }

        GlStateManager.enableAlpha();
        GlStateManager.disableDepth();

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GlStateManager.deleteTexture(texture);
        GL30.glDeleteRenderbuffers(renderBuffer);
        GL30.glDeleteFramebuffers(framebuffer);
    }

    private boolean formatTooltips(List<String> tooltipStrings, @Nullable ByteBuffer buffer) {
        if (tooltipStrings.isEmpty()) return true;
        for (int j = 0; j < tooltipStrings.size(); j++) {
            var str = tooltipStrings.get(j);
            var tooltip = TooltipEmbedding.parseEmbeddings(str);

            for (var embedding : tooltip) {
                var uri = getURIForDecoration(embedding.getTextureName());
                TextureDecoration<?> decoration;
                synchronized (textures) {
                    decoration = textures.get(uri);
                    if (decoration == null) {
                        decoration = TextureDecoration.of(embedding);
                        textures.put(uri, decoration);
                    }
                }
                if (!decoration.isFileExists()) {
                    if (buffer == null) {
                        return false;
                    }
                    decoration.render(buffer);
                }

                var before = embedding.getStart() == 0 ? "" : str.substring(0, embedding.getStart());
                var after = embedding.getEnd() == str.length() - 1 ? "" : str.substring(embedding.getEnd());

                str = before + decoration.getUri() + after;
            }

            tooltipStrings.set(j, str);
        }

        return true;
    }

    static String getURIForDecoration(String name) {
        return new File(cacheRoot, name + ".png").toURI().toString();
    }
}
