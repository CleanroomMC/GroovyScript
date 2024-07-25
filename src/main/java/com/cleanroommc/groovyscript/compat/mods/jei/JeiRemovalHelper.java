package com.cleanroommc.groovyscript.compat.mods.jei;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.INamed;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.jei.RecipeGuiLogicAccessor;
import com.cleanroommc.groovyscript.core.mixin.jei.RecipeLayoutAccessor;
import com.cleanroommc.groovyscript.core.mixin.jei.RecipesGuiAccessor;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.gui.ingredients.IngredientLookupState;
import mezz.jei.gui.recipes.RecipeLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Mouse;

import java.util.List;
import java.util.Map;

public class JeiRemovalHelper {

    /**
     * uses the focused category uid to filter
     */
    public static void getRemovalMethod() {
        IngredientLookupState state = ((RecipeGuiLogicAccessor) ((RecipesGuiAccessor) JeiPlugin.jeiRuntime.getRecipesGui()).getLogic()).getState();
        if (state == null) return;
        String recipeCategoryUid = state.getRecipeCategories().get(state.getRecipeCategoryIndex()).getUid();
        getRemovalMethod(recipeCategoryUid);
    }

    /**
     * generates a method to remove the targeted recipe in JEI and prints the via {@link #say(String)}.
     * TODO doesnt work with vanilla, or anything not a mod container.
     *
     * @param uid the id of the targeted JEI category
     */
    private static void getRemovalMethod(String uid) {
        Map<IIngredientType, IGuiIngredientGroup> output = getUnderMouse();
        if (output == null) return;

        for (GroovyContainer<? extends GroovyPropertyContainer> groovyContainer : ModSupport.getAllContainers()) {
            if (!groovyContainer.isLoaded()) continue;
            for (INamed registry : groovyContainer.get().getRegistries()) {
                if (registry.isEnabled() && registry instanceof IJEIRemoval removal && removal.getCategories().contains(uid)) {
                    String operation = removal.getRemoval(output);
                    if (operation.isEmpty()) continue;
                    String message = String.format("mods.%s.%s.%s", groovyContainer.getModId(), registry.getName(), operation);
                    say(message);
                    return;
                }
            }
        }
        say("Couldn't find a way to remove the targeted recipe");
    }


    /**
     * Get the recipe layout being hovered and return the ingredient groups for further parsing
     *
     * @return ingredient groups attached to targeted recipe layout
     */
    private static Map<IIngredientType, IGuiIngredientGroup> getUnderMouse() {
        List<RecipeLayout> recipeLayouts = ((RecipesGuiAccessor) JeiPlugin.jeiRuntime.getRecipesGui()).getRecipeLayouts();
        if (recipeLayouts == null) return null;
        ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
        int mouseX = Mouse.getX() * scaledresolution.getScaledWidth() / Minecraft.getMinecraft().displayWidth;
        int mouseY = scaledresolution.getScaledHeight() - Mouse.getY() * scaledresolution.getScaledHeight() / Minecraft.getMinecraft().displayHeight - 1;

        for (RecipeLayout recipeLayout : recipeLayouts) {
            if (recipeLayout.isMouseOver(mouseX, mouseY)) {
                return ((RecipeLayoutAccessor) recipeLayout).getGuiIngredientGroups();
            }
        }
        return null;
    }

    /**
     * Currently prints the message to the log and to chat, but perhaps this should be reworked to support appending to a specific file?
     * TODO this
     *
     * @param message message to get printed to the log
     */
    private static void say(String message) {
        GroovyLog.get().warn(message);
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(message));
    }

    /**
     * Converts the RecipeLayout ingredient groups into an output method for a GroovyScript script.
     * Removes by input {@link ItemStack}, and the input should be unique.
     *
     * @param map ingredients in the RecipeLayout to convert
     * @return a string representing a GrS method to remove the recipe by input
     */
    public static @NotNull String getFromSingleUniqueItemInput(Map<IIngredientType, IGuiIngredientGroup> map) {
        Map<Integer, IGuiIngredient<?>> ingredientMap = map.get(VanillaTypes.ITEM).getGuiIngredients();
        for (IGuiIngredient<?> slot : ingredientMap.values()) {
            if (slot.isInput() && !slot.getAllIngredients().isEmpty()) {
                ItemStack stack = (ItemStack) slot.getAllIngredients().get(0);
                return String.format("removeByInput(%s)", GroovyScriptCodeConverter.asGroovyCode(stack, false, false));
            }
        }
        return "";
    }


    /**
     * Converts the RecipeLayout ingredient groups into an output method for a GroovyScript script.
     * Removes by input {@link FluidStack}, and the input should be unique.
     *
     * @param map ingredients in the RecipeLayout to convert
     * @return a string representing a GrS method to remove the recipe by input
     */
    public static @NotNull String getFromSingleUniqueFluidInput(Map<IIngredientType, IGuiIngredientGroup> map) {
        Map<Integer, IGuiIngredient<?>> ingredientMap = map.get(VanillaTypes.FLUID).getGuiIngredients();
        for (IGuiIngredient<?> slot : ingredientMap.values()) {
            if (slot.isInput() && !slot.getAllIngredients().isEmpty()) {
                FluidStack stack = (FluidStack) slot.getAllIngredients().get(0);
                return String.format("removeByInput(%s)", GroovyScriptCodeConverter.asGroovyCode(stack, false, false));
            }
        }
        return "";
    }

}
