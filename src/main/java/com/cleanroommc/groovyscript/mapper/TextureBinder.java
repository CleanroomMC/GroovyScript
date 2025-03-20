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
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.Function;

/**
 * This interface draws objects, so they can be rendered as icons in an ide.
 * <p>
 * This is marked as experimental. This class is likely to change and may even be removed in a future update.
 */
@ApiStatus.Experimental
public interface TextureBinder<T> {

    void bindTexture(T t);

    static <A, T> TextureBinder<A> of(Function<A, T> mapper, TextureBinder<T> binder) {
        return o -> binder.bindTexture(mapper.apply(o));
    }

    static <A, T> TextureBinder<A> ofList(Function<A, List<T>> mapper, TextureBinder<T> binder) {
        return o -> {
            var list = mapper.apply(o);
            if (list == null || list.isEmpty()) return;
            binder.bindTexture(list.get(0));
        };
    }

    static <A, T> TextureBinder<A> ofArray(Function<A, T[]> mapper, TextureBinder<T> binder) {
        return o -> {
            var array = mapper.apply(o);
            if (array == null || array.length == 0) return;
            binder.bindTexture(array[0]);
        };
    }

    static TextureBinder<ItemStack> ofItem() {
        if (FMLCommonHandler.instance().getSide().isServer()) return x -> {};
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
        if (FMLCommonHandler.instance().getSide().isServer()) return x -> {};
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

    @SideOnly(Side.CLIENT)
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
