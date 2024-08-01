package com.cleanroommc.groovyscript.compat.mods.jei.removal;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.INamed;
import com.cleanroommc.groovyscript.command.TextCopyable;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.jei.JeiPlugin;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.core.mixin.jei.RecipeGuiLogicAccessor;
import com.cleanroommc.groovyscript.core.mixin.jei.RecipesGuiAccessor;
import com.google.common.collect.Lists;
import mezz.jei.api.gui.IRecipeLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JeiRemovalHelper {

    /**
     * uses the focused category uid to filter
     */
    public static List<String> getRemovalMethod() {
        return getRemovalMethod(getUid());
    }

    /**
     * generates a method to remove the targeted recipe in JEI and prints the via {@link #say(String)}.
     *
     * @param uid the id of the targeted JEI category
     */
    public static List<String> getRemovalMethod(String uid) {
        var output = getUnderMouse();
        if (output == null) return Collections.emptyList();
        return getRemovalMethod(uid, output);
    }

    /**
     * generates a method to remove the targeted recipe in JEI and prints the via {@link #say(String)}.
     *
     * @param uid the id of the targeted JEI category
     */
    public static List<String> getRemovalMethod(String uid, IRecipeLayout output) {
        var list = new ArrayList<String>();


        // TODO JEI why does vanilla have to be special cased, can that be changed?
        for (INamed registry : Lists.newArrayList(VanillaModule.crafting, VanillaModule.furnace)) {
            if (registry.isEnabled() && registry instanceof IJEIRemoval removal && removal.getCategories().contains(uid)) {
                var operation = removal.getRemoval(output);
                if (operation.isEmpty()) continue;
                for (var s : operation) {
                    var message = String.format("%s.%s", registry.getName(), s);
                    list.add(message);
                    say(message);
                }
                return list;
            }
        }

        // TODO implode
        for (INamed registry : Lists.newArrayList(
                VanillaModule.inWorldCrafting.fluidToFluid,
                VanillaModule.inWorldCrafting.fluidToItem,
                VanillaModule.inWorldCrafting.fluidToBlock,
                VanillaModule.inWorldCrafting.explosion,
                VanillaModule.inWorldCrafting.burning,
                VanillaModule.inWorldCrafting.pistonPush)) {
            if (registry.isEnabled() && registry instanceof IJEIRemoval removal && removal.getCategories().contains(uid)) {
                var operation = removal.getRemoval(output);
                if (operation.isEmpty()) continue;
                for (var s : operation) {
                    var message = String.format("%s.%s", registry.getName(), s);
                    list.add(message);
                    say(message);
                }
                return list;
            }
        }

        for (var groovyContainer : ModSupport.getAllContainers()) {
            if (!groovyContainer.isLoaded()) continue;
            for (var registry : groovyContainer.get().getRegistries()) {
                if (registry.isEnabled() && registry instanceof IJEIRemoval removal && removal.getCategories().contains(uid)) {
                    var operation = removal.getRemoval(output);
                    if (operation.isEmpty()) continue;
                    for (var s : operation) {
                        var message = String.format("mods.%s.%s.%s", groovyContainer.getModId(), registry.getName(), s);
                        list.add(message);
                        say(message);
                    }
                    return list;
                }
            }
        }
        say(String.format("Couldn't find a way to remove the targeted recipe in category %s", uid));
        return list;
    }


    public static String getUid() {
        var state = ((RecipeGuiLogicAccessor) ((RecipesGuiAccessor) JeiPlugin.jeiRuntime.getRecipesGui()).getLogic()).getState();
        if (state == null) return "";
        return state.getRecipeCategories().get(state.getRecipeCategoryIndex()).getUid();
    }

    /**
     * Get the recipe layout being hovered and return the ingredient groups for further parsing
     *
     * @return ingredient groups attached to targeted recipe layout
     */
    public static IRecipeLayout getUnderMouse() {
        var layouts = ((RecipesGuiAccessor) JeiPlugin.jeiRuntime.getRecipesGui()).getRecipeLayouts();
        if (layouts == null) return null;
        var scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
        var mouseX = Mouse.getX() * scaledresolution.getScaledWidth() / Minecraft.getMinecraft().displayWidth;
        var mouseY = scaledresolution.getScaledHeight() - Mouse.getY() * scaledresolution.getScaledHeight() / Minecraft.getMinecraft().displayHeight - 1;

        for (var layout : layouts) {
            if (layout.isMouseOver(mouseX, mouseY)) {
                return layout;
            }
        }
        return null;
    }

    /**
     * Currently prints the message to the log and to chat, but perhaps this should be reworked to support appending to a specific file?
     * TODO JEI this
     *
     * @param message message to get printed to the log
     */
    public static void say(String message) {
        GroovyLog.get().warn(message);
        Minecraft.getMinecraft().player.sendMessage(TextCopyable.string(message, message).build());
    }

    /**
     * @param method name of the method to call
     * @param params one or more parameters of the method
     * @return a string representing a GrS method
     * @see #format(String, List)
     */
    public static @NotNull String format(String method, String... params) {
        return String.format("%s(%s)", method, String.join(", ", params));
    }

    /**
     * @param method name of the method to call
     * @param params one or more parameters of the method
     * @return a string representing a GrS method
     */
    public static @NotNull String format(String method, List<String> params) {
        return String.format("%s(%s)", method, String.join(", ", params));
    }

}
