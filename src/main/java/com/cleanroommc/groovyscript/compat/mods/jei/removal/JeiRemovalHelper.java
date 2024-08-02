package com.cleanroommc.groovyscript.compat.mods.jei.removal;

import com.cleanroommc.groovyscript.api.INamed;
import com.cleanroommc.groovyscript.api.jeiremoval.IJEIRemoval;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.jei.JeiPlugin;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.core.mixin.jei.RecipeGuiLogicAccessor;
import com.cleanroommc.groovyscript.core.mixin.jei.RecipesGuiAccessor;
import com.google.common.collect.Lists;
import mezz.jei.api.gui.IRecipeLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Mouse;

import java.util.List;

public class JeiRemovalHelper {

    /**
     * Generates one or more methods to remove the targeted recipe in JEI.
     *
     * @param uid    the id of the targeted JEI category
     * @param layout the recipe layout being targeted
     * @return a pair where the left value is a String representing the category and
     * the right value is a List of Strings representing any number of methods
     */
    public static Pair<String, List<String>> getRemovalMethod(String uid, IRecipeLayout layout) {

        // TODO JEI why does vanilla have to be special cased, can that be changed?
        for (INamed registry : Lists.newArrayList(VanillaModule.crafting, VanillaModule.furnace)) {
            if (registry.isEnabled() && registry instanceof IJEIRemoval removal && removal.getCategories().contains(uid)) {
                var operation = removal.getRemoval(layout);
                if (operation.isEmpty()) continue;
                return Pair.of(registry.getName(), operation);
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
                var operation = removal.getRemoval(layout);
                if (operation.isEmpty()) continue;
                return Pair.of(registry.getName(), operation);
            }
        }

        for (var groovyContainer : ModSupport.getAllContainers()) {
            if (!groovyContainer.isLoaded()) continue;
            for (var registry : groovyContainer.get().getRegistries()) {
                if (registry.isEnabled() && registry instanceof IJEIRemoval removal && removal.getCategories().contains(uid)) {
                    var operation = removal.getRemoval(layout);
                    if (operation.isEmpty()) continue;
                    return Pair.of(String.format("mods.%s.%s", groovyContainer.getModId(), registry.getName()), operation);
                }
            }
        }
        return null;
    }


    public static String getFocusedRecipeUid() {
        var state = ((RecipeGuiLogicAccessor) ((RecipesGuiAccessor) JeiPlugin.jeiRuntime.getRecipesGui()).getLogic()).getState();
        if (state == null) return "";
        return state.getRecipeCategories().get(state.getRecipeCategoryIndex()).getUid();
    }

    /**
     * Get the recipe layout being hovered and return the ingredient groups for further parsing
     *
     * @return ingredient groups attached to targeted recipe layout
     */
    public static IRecipeLayout getRecipeLayoutUnderMouse() {
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

}
