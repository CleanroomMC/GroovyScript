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
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This interface draws objects, so they can be rendered as icons in an ide.
 *
 * This is marked as experimental. This class is likely to change and may even be removed in a future update.
 */
@ApiStatus.Experimental
public interface TextureBinder<T> extends Function<T, List<String>> {

    static <A, T> TextureBinder<A> of(Function<A, T> mapper, TextureBinder<T> binder) {
        return o -> binder.apply(mapper.apply(o));
    }

    static <A, T> TextureBinder<A> of(Function<A, List<T>> mapper, TextureBinder<T> binder, Function<T, String> tooltipMapper) {
        return o -> {
            var list = mapper.apply(o);
            if (list == null || list.isEmpty()) return Collections.emptyList();
            binder.apply(list.get(0));
            return list.stream().map(tooltipMapper).collect(Collectors.toList());
        };
    }

    static TextureBinder<ItemStack> ofItem() {
        return item -> {
            if (Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
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
            }

            return Collections.singletonList(item.getDisplayName());
        };
    }

    static TextureBinder<FluidStack> ofFluid() {
        return fluid -> {
            if (Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
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
            }

            return Collections.singletonList(fluid.getLocalizedName());
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
