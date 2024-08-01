package com.cleanroommc.groovyscript.keybind;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.command.TextCopyable;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.jei.JeiPlugin;
import com.cleanroommc.groovyscript.compat.mods.jei.removal.JeiRemovalHelper;
import mezz.jei.api.IRecipesGui;
import mezz.jei.api.gui.IRecipeLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.*;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class RemoveRecipeKey extends GroovyScriptKeybinds.Key {

    private static final ITextComponent GOLD_SEPARATOR = new TextComponentString("====================================").setStyle(new Style().setColor(TextFormatting.GOLD));

    private static final int SHORTHAND_TRIM_LENGTH = 50;
    private static final int SHORTHAND_TRIM_MARGIN = 5;

    private static final String ERROR_KEY = "key.groovyscript.remove_recipe_from_jei.unknown_category";
    private static final Style ERROR_STYLE = new Style().setColor(TextFormatting.RED);
    private static final Style ERROR_COPY_STYLE = new Style().setColor(TextFormatting.GOLD);

    private String lastUid = "";
    private IRecipeLayout lastLayout;
    private int combo;
    private Pair<String, List<String>> removal;

    public RemoveRecipeKey() {
        super("remove_recipe_from_jei", KeyConflictContext.GUI, KeyModifier.CONTROL, Keyboard.KEY_X);
    }

    public static void createKeybind() {
        if (ModSupport.JEI.isLoaded()) GroovyScriptKeybinds.addKey(new RemoveRecipeKey());
    }

    @Override
    public boolean isValid() {
        return Minecraft.getMinecraft().currentScreen instanceof IRecipesGui && !JeiPlugin.jeiRuntime.getIngredientListOverlay().hasKeyboardFocus();
    }

    private static void missingUid(String uid) {
        Minecraft.getMinecraft().player.sendMessage(
                new TextComponentTranslation(ERROR_KEY, TextCopyable.string(uid, uid).build().setStyle(ERROR_COPY_STYLE)).setStyle(ERROR_STYLE));
    }

    private static void printMessage(String prefix, String shorthand) {
        var msg = shorthand.length() > SHORTHAND_TRIM_LENGTH + SHORTHAND_TRIM_MARGIN
                  ? shorthand.substring(0, SHORTHAND_TRIM_LENGTH) + TextFormatting.RED + "..."
                  : shorthand;
        printMessage(TextCopyable.string(String.format("%s.%s", prefix, shorthand), msg).build());
    }

    private static void printMessage(ITextComponent message) {
        GroovyLog.get().debug(message.getUnformattedText());
        Minecraft.getMinecraft().player.sendMessage(message);
    }

    @Override
    public void handleKeybind() {
        // only actually check if this is the downpress
        if (Keyboard.getEventKeyState()) {

            var newLayout = JeiRemovalHelper.getRecipeLayoutUnderMouse();
            if (newLayout == null) return;
            var newUid = JeiRemovalHelper.getFocusedRecipeUid();

            var matchesLast = lastUid.equals(newUid) && lastLayout == newLayout;

            if (matchesLast && removal != null && !removal.getValue().isEmpty()) {
                adjustCombo();
            } else {
                lastUid = newUid;
                lastLayout = newLayout;
                combo = 0;
                removal = JeiRemovalHelper.getRemovalMethod(JeiRemovalHelper.getFocusedRecipeUid(), newLayout);
            }

            if (removal == null) {
                missingUid(newUid);
            } else {
                Minecraft.getMinecraft().player.sendMessage(GOLD_SEPARATOR);

                var text = TextFormatting.getTextWithoutFormattingCodes(String.format("%s.%s", removal.getKey(), removal.getValue().get(combo)));
                if (text != null) GuiScreen.setClipboardString(text);

                if (matchesLast) {
                    printMessage(removal.getKey(), removal.getValue().get(combo));
                } else {
                    for (String s : removal.getValue()) {
                        printMessage(removal.getKey(), s);
                    }
                }
            }
        }
    }

    private void adjustCombo() {
        if (removal == null || removal.getValue().isEmpty()) {
            combo = 0;
            return;
        }
        if (GuiScreen.isShiftKeyDown()) combo--;
        else combo++;
        if (combo > removal.getValue().size() - 1) combo = 0;
        if (combo < 0) combo = removal.getValue().size() - 1;
    }

}
