package com.cleanroommc.groovyscript.mapper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;
import java.util.function.Function;

public interface TextureBinder<T> extends Consumer<T> {

    static <A, T> TextureBinder<A> of(Function<A, T> mapper, TextureBinder<T> binder) {
        return o -> binder.accept(mapper.apply(o));
    }

    static TextureBinder<ItemStack> ofItem() {
        return item -> {
            GlStateManager.enableDepth();
            RenderHelper.enableGUIStandardItemLighting();
            var mc = Minecraft.getMinecraft();
            var fontRenderer = item.getItem().getFontRenderer(item);
            if (fontRenderer == null)
                fontRenderer = mc.fontRenderer;
            mc.getRenderItem().renderItemAndEffectIntoGUI(null, item, 0, 0);
            mc.getRenderItem().renderItemOverlayIntoGUI(fontRenderer, item, 0, 0, null);
            GlStateManager.disableBlend();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.enableAlpha();
            GlStateManager.disableDepth();
        };
    }

    static TextureBinder<FluidStack> ofFluid() {
        return fluid -> {
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();

            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            var texture = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(fluid.getFluid().getStill(fluid).toString());

            if (texture == null) {
                texture = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
            }

            var color = fluid.getFluid().getColor(fluid);
            GlStateManager.color((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, (color >> 24 & 0xFF) / 255.0F);

            drawSprite(texture);

            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
        };
    }

    static void drawSprite(TextureAtlasSprite textureSprite) {
        double uMin = textureSprite.getMinU();
        double uMax = textureSprite.getMaxU();
        double vMin = textureSprite.getMinV();
        double vMax = textureSprite.getMaxV();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferBuilder.pos(0, 16, 0).tex(uMin, vMax).endVertex();
        bufferBuilder.pos(16, 16, 0).tex(uMax, vMax).endVertex();
        bufferBuilder.pos(16, 0, 0).tex(uMax, vMin).endVertex();
        bufferBuilder.pos(0, 0, 0).tex(uMin, vMin).endVertex();
        tessellator.draw();
    }
}
