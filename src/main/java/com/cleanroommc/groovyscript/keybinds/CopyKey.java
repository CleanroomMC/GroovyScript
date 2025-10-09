package com.cleanroommc.groovyscript.keybinds;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.jei.JeiPlugin;
import com.cleanroommc.groovyscript.helper.RayTracingHelper;
import com.cleanroommc.groovyscript.helper.StyleConstant;
import com.google.common.collect.ImmutableList;
import mezz.jei.api.IRecipesGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class CopyKey extends GroovyScriptKeybinds.Key {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public CopyKey() {
        super("copy", KeyModifier.CONTROL, Keyboard.KEY_C);
    }

    public static CopyKey createKeybind() {
        return new CopyKey();
    }

    private static void gatherInfo(InfoParserPackage info, EntityPlayer player) {
        if (mc.inGameHasFocus) {
            info.setStack(player.getHeldItem(EnumHand.MAIN_HAND));
            if (info.getStack().isEmpty()) info.setStack(player.getHeldItem(EnumHand.OFF_HAND));
            if (info.getStack().isEmpty()) {
                info.setEntity(RayTracingHelper.getEntityLookingAt(player));
                if (info.getEntity() == null) {
                    info.copyFromPos(RayTracingHelper.getBlockLookingAt(player));
                    if (info.getPos() == null) {
                        info.setEntity(player);
                    }
                }
            }
        } else {
            if (mc.currentScreen instanceof GuiContainer container && container.hoveredSlot != null) {
                info.setStack(container.hoveredSlot.getStack());
            }
            if (info.getStack().isEmpty() && ModSupport.JEI.isLoaded()) info.setStack(getJeiStack());
        }
    }

    private static ItemStack getJeiStack() {
        var entry = getJeiObject();
        if (entry == null) return ItemStack.EMPTY;
        var type = JeiPlugin.itemRegistry.getIngredientType(entry);
        return JeiPlugin.itemRegistry.getIngredientHelper(type).getCheatItemStack(entry);
    }

    private static Object getJeiObject() {
        if (mc.currentScreen instanceof IRecipesGui gui) {
            var entry = gui.getIngredientUnderMouse();
            if (entry != null) return entry;
        }
        var entry = JeiPlugin.jeiRuntime.getBookmarkOverlay().getIngredientUnderMouse();
        if (entry != null) return entry;
        return JeiPlugin.jeiRuntime.getIngredientListOverlay().getIngredientUnderMouse();
    }

    private static void print(EntityPlayer player, List<ITextComponent> messages) {
        if (messages.isEmpty()) {
            player.sendMessage(new TextComponentString("Couldn't find anything being focused!").setStyle(StyleConstant.getErrorStyle()));
        } else {
            // have a horizontal bar to improve readability when running multiple consecutive info hand commands
            player.sendMessage(new TextComponentString("================================").setStyle(StyleConstant.getEmphasisStyle()));
            messages.forEach(player::sendMessage);
        }
    }

    @Override
    public boolean isValid() {
        return mc.isIntegratedServerRunning();
    }

    // only runs if isIntegratedServerRunning() is true, so getIntegratedServer() cannot be null
    @SuppressWarnings("DataFlowIssue")
    @Override
    public void runOperation() {
        var player = mc.player;
        List<ITextComponent> messages = new ArrayList<>();
        InfoParserPackage info = new InfoParserPackage(mc.getIntegratedServer(), player, ImmutableList.of("all"), messages, false);
        gatherInfo(info, player);
        info.parse(true);
        print(player, messages);
    }
}
