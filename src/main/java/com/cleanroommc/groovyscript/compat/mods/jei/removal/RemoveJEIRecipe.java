package com.cleanroommc.groovyscript.compat.mods.jei.removal;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.command.TextCopyable;
import mezz.jei.api.gui.IRecipeLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class RemoveJEIRecipe {

    public static final RemoveJEIRecipe INSTANCE = new RemoveJEIRecipe();

    private static final ITextComponent GOLD_SEPARATOR = new TextComponentString("====================================").setStyle(new Style().setColor(TextFormatting.GOLD));

    private static final int SHORTHAND_TRIM_LENGTH = 60;
    private static final int SHORTHAND_TRIM_MARGIN = 5;
    private static final String SHORTHAND_INDICATOR = TextFormatting.RED + "...";

    private static final String ERROR_KEY = "groovyscript.jei.remove_recipe.unknown_category";
    private static final Style ERROR_STYLE = new Style().setColor(TextFormatting.RED);
    private static final Style ERROR_COPY_STYLE = new Style().setColor(TextFormatting.GOLD);

    private static final String ISSUES_LINK = "https://github.com/CleanroomMC/GroovyScript/issues";
    private static final String GITHUB_NAME = "GitHub";
    private static final Style COPY_STYLE = new Style()
            .setColor(TextFormatting.LIGHT_PURPLE)
            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentTranslation("groovyscript.jei.remove_recipe.view_on_github")))
            .setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, ISSUES_LINK));

    private boolean hasSeenFirstExecutionNotice;

    private String lastUid = "";
    private IRecipeLayout lastLayout;
    private int combo;
    private Pair<String, List<String>> removal;

    private static void missingUid(String uid) {
        Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation(ERROR_KEY, TextCopyable.string(uid, uid).build().setStyle(ERROR_COPY_STYLE)).setStyle(ERROR_STYLE));
    }

    private static void printMessage(String prefix, String shorthand) {
        var msg = shorthand.length() > SHORTHAND_TRIM_LENGTH + SHORTHAND_TRIM_MARGIN
                  ? shorthand.substring(0, SHORTHAND_TRIM_LENGTH) + SHORTHAND_INDICATOR
                  : shorthand;
        printMessage(TextCopyable.string(String.format("%s.%s", prefix, shorthand), msg).build());
    }

    private static void printMessage(ITextComponent message) {
        GroovyLog.get().debug(message.getUnformattedText());
        Minecraft.getMinecraft().player.sendMessage(message);
    }

    public boolean checkRemoval(IRecipeLayout layout, String uid, boolean inverseComboOrder) {
        if (!hasSeenFirstExecutionNotice) handleFirstExecution();

        var matchesLast = lastUid.equals(uid) && lastLayout == layout;

        if (matchesLast && removal != null && !removal.getValue().isEmpty()) {
            adjustCombo(inverseComboOrder);
        } else {
            lastUid = uid;
            lastLayout = layout;
            combo = 0;
            removal = JeiRemovalHelper.getRemovalMethod(JeiRemovalHelper.getFocusedRecipeUid(), layout);
        }

        if (removal == null) {
            missingUid(uid);
            return false;
        }

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
        return true;
    }

    private void adjustCombo(boolean inverseComboOrder) {
        if (removal == null || removal.getValue().isEmpty()) {
            combo = 0;
            return;
        }
        if (inverseComboOrder) combo--;
        else combo++;
        if (combo > removal.getValue().size() - 1) combo = 0;
        if (combo < 0) combo = removal.getValue().size() - 1;
    }

    private void handleFirstExecution() {
        Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("groovyscript.jei.remove_recipe.first_time", new TextComponentString(GITHUB_NAME).setStyle(COPY_STYLE)));
        hasSeenFirstExecutionNotice = true;
    }

}
