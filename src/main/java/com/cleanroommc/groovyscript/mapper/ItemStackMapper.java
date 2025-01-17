package com.cleanroommc.groovyscript.mapper;

import com.cleanroommc.groovyscript.api.Result;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.server.CompletionParams;
import com.cleanroommc.groovyscript.server.Completions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ItemStackMapper extends AbstractObjectMapper<ItemStack> {

    public static final ItemStackMapper INSTANCE = new ItemStackMapper("item", null);

    protected ItemStackMapper(String name, GroovyContainer<?> mod) {
        super(name, mod, ItemStack.class);
        addSignature(String.class, int.class);
        this.documentation = docOfType("item stack");
    }

    @Override
    public Result<ItemStack> getDefaultValue() {
        return Result.some(ItemStack.EMPTY);
    }

    @Override
    public @NotNull Result<ItemStack> parse(String mainArg, Object[] args) {
        return ObjectMappers.parseItemStack(mainArg, args);
    }

    @Override
    public void provideCompletion(int index, CompletionParams params, Completions items) {
        if (index == 0) items.addAllOfRegistry(ForgeRegistries.ITEMS);
    }

    @Override
    public void bindTexture(ItemStack item) {
        GlStateManager.enableDepth();
        RenderHelper.enableGUIStandardItemLighting();
        var mc = Minecraft.getMinecraft();
        var fontRenderer = item.getItem().getFontRenderer(item);
        if (fontRenderer == null) fontRenderer = mc.fontRenderer;
        mc.getRenderItem().renderItemAndEffectIntoGUI(null, item, 0, 0);
        mc.getRenderItem().renderItemOverlayIntoGUI(fontRenderer, item, 0, 0, null);
        GlStateManager.disableBlend();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.disableDepth();
    }

    @Override
    public @NotNull List<String> getTooltip(ItemStack itemStack) {
        return Collections.singletonList(itemStack.getDisplayName());
    }

    @Override
    public boolean hasTextureBinder() {
        return true;
    }
}
