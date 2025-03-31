package com.cleanroommc.groovyscript.server.features.textureDecoration;

import com.cleanroommc.groovyscript.mapper.AbstractObjectMapper;
import com.cleanroommc.groovyscript.mapper.TooltipEmbedding;
import net.minecraft.client.renderer.GlStateManager;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class TextureDecoration<T> {

    private static <T> T getObjectWithConstArgs(AbstractObjectMapper<T> mapper, ArgumentListExpression args) {
        var additionalArgs = new Object[args.getExpressions().size() - 1];
        for (int i = 0; i < additionalArgs.length; i++) {
            if (args.getExpressions().get(i + 1) instanceof ConstantExpression argExpression) {
                additionalArgs[i] = argExpression.getValue();
            }
        }
        return mapper.invoke(true, args.getExpressions().get(0).getText(), additionalArgs);
    }

    private final String name;
    private final String uri;
    private final AbstractObjectMapper<T> mapper;
    private final T bindable;
    boolean fileExists;
    boolean queued;

    public static <T> TextureDecoration<T> create(String name, String uri, AbstractObjectMapper<T> mapper, ArgumentListExpression args) {
        T bindable = getObjectWithConstArgs(mapper, args);
        if (bindable == null) return null;
        return new TextureDecoration<>(name, mapper, bindable, uri);
    }

    public static <T> TextureDecoration<T> of(TooltipEmbedding<T> embedding) {
        return new TextureDecoration<>(embedding.getTextureName(), embedding.getMapper(), embedding.getContext(), TextureDecorationProvider.getURIForDecoration(embedding.getTextureName()));
    }

    private TextureDecoration(String name, AbstractObjectMapper<T> mapper, T bindable, String uri) {
        this.name = name;
        this.mapper = mapper;
        this.bindable = bindable;
        this.uri = uri;
        File file = getFile();
        this.fileExists = file.exists();
    }

    public void bindTexture() {
        this.mapper.bindTexture(this.bindable);
    }

    public List<String> getTooltip() {
        List<String> tooltip = this.mapper.getTooltip(this.bindable);
        return tooltip.isEmpty() ? tooltip : new ArrayList<>(tooltip);
    }

    public String getUri() {
        return uri;
    }

    private @NotNull File getFile() {
        return new File(TextureDecorationProvider.cacheRoot, name + ".png");
    }

    public boolean isFileExists() {
        return fileExists;
    }

    void render(ByteBuffer buffer) {
        GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        bindTexture();
        GL11.glReadPixels(0, 0, TextureDecorationProvider.ICON_W, TextureDecorationProvider.ICON_H, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        saveImage(getFile(), buffer);
        buffer.rewind();
        synchronized (this) {
            this.fileExists = true;
            this.queued = false;
        }
    }

    private static void saveImage(File file, ByteBuffer buffer) {
        final int w = TextureDecorationProvider.ICON_W;
        final int h = TextureDecorationProvider.ICON_H;
        var image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int r = buffer.get() & 0xFF;
                int g = buffer.get() & 0xFF;
                int b = buffer.get() & 0xFF;
                int a = buffer.get() & 0xFF;
                int color = (a << 24) | (r << 16) | (g << 8) | b;
                image.setRGB(x, h - 1 - y, color); // Flip the y-coordinate to correct the image orientation
            }
        }

        try {
            ImageIO.write(image, "PNG", file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
