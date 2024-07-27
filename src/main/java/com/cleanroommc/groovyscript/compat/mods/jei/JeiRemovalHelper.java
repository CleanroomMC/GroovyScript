package com.cleanroommc.groovyscript.compat.mods.jei;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.INamed;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.core.mixin.jei.RecipeGuiLogicAccessor;
import com.cleanroommc.groovyscript.core.mixin.jei.RecipesGuiAccessor;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import com.google.common.collect.Lists;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IRecipeLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

public class JeiRemovalHelper {

    /**
     * uses the focused category uid to filter
     */
    public static void getRemovalMethod() {
        var state = ((RecipeGuiLogicAccessor) ((RecipesGuiAccessor) JeiPlugin.jeiRuntime.getRecipesGui()).getLogic()).getState();
        if (state == null) return;
        var recipeCategoryUid = state.getRecipeCategories().get(state.getRecipeCategoryIndex()).getUid();
        getRemovalMethod(recipeCategoryUid);
    }

    /**
     * generates a method to remove the targeted recipe in JEI and prints the via {@link #say(String)}.
     * TODO doesnt work with vanilla, or anything not a mod container.
     *
     * @param uid the id of the targeted JEI category
     */
    private static void getRemovalMethod(String uid) {
        var output = getUnderMouse();
        if (output == null) return;

        // TODO why does vanilla have to be special cased, can that be changed?
        for (INamed registry : Lists.newArrayList(VanillaModule.crafting, VanillaModule.furnace)) {
            if (registry.isEnabled() && registry instanceof IJEIRemoval removal && removal.getCategories().contains(uid)) {
                var operation = removal.getRemoval(output);
                if (operation.isEmpty()) continue;
                var message = String.format("%s.%s", registry.getName(), operation);
                say(message);
                return;
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
                var message = String.format("inWorldCrafting.%s.%s", registry.getName(), operation);
                say(message);
                return;
            }
        }

        for (var groovyContainer : ModSupport.getAllContainers()) {
            if (!groovyContainer.isLoaded()) continue;
            for (var registry : groovyContainer.get().getRegistries()) {
                if (registry.isEnabled() && registry instanceof IJEIRemoval removal && removal.getCategories().contains(uid)) {
                    var operation = removal.getRemoval(output);
                    if (operation.isEmpty()) continue;
                    var message = String.format("mods.%s.%s.%s", groovyContainer.getModId(), registry.getName(), operation);
                    say(message);
                    return;
                }
            }
        }
        say(String.format("Couldn't find a way to remove the targeted recipe in category %s", uid));
    }


    /**
     * Get the recipe layout being hovered and return the ingredient groups for further parsing
     *
     * @return ingredient groups attached to targeted recipe layout
     */
    private static IRecipeLayout getUnderMouse() {
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
     * TODO this
     *
     * @param message message to get printed to the log
     */
    private static void say(String message) {
        GroovyLog.get().warn(message);
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(message));
    }

    /**
     * Converts the RecipeLayout into an output method for a GroovyScript script.
     * Removes by input {@link ItemStack}, and the input should be unique.
     *
     * @param layout the RecipeLayout to convert
     * @return a string representing a GrS method to remove the recipe by input
     */
    public static @NotNull String getFromSingleUniqueItemInput(IRecipeLayout layout) {
        var stack = getFirstItemStack(layout);
        return stack.isEmpty() ? "" : formatRemovalString("removeByInput", stack);
    }


    /**
     * Converts the RecipeLayout into an output method for a GroovyScript script.
     * Removes by input {@link FluidStack}, and the input should be unique.
     *
     * @param layout the RecipeLayout to convert
     * @return a string representing a GrS method to remove the recipe by input
     */
    public static @NotNull String getFromSingleUniqueFluidInput(IRecipeLayout layout) {
        var stack = getFirstFluidStack(layout);
        return stack.isEmpty() ? "" : formatRemovalString("removeByInput", stack);
    }

    /**
     * Converts the RecipeLayout into an output method for a GroovyScript script.
     * Gathers all inputs that are either {@link ItemStack} or {@link FluidStack} and generates a function for that.
     * <br>
     * If an input that is not either an {@link ItemStack} or a {@link FluidStack} is required, a custom method should be created.
     *
     * @param layout the RecipeLayout to convert
     * @return a string representing a GrS method to remove the recipe by input
     */
    public static @NotNull String getFromInput(IRecipeLayout layout) {
        List<String> removing = new ArrayList<>();
        for (IGuiIngredient<?> slot : layout.getItemStacks().getGuiIngredients().values()) {
            if (slot.isInput() && !slot.getAllIngredients().isEmpty() && slot.getAllIngredients().get(0) instanceof ItemStack stack) {
                removing.add(GroovyScriptCodeConverter.asGroovyCode(stack, false, false));
            }
        }
        for (IGuiIngredient<?> slot : layout.getFluidStacks().getGuiIngredients().values()) {
            if (slot.isInput() && !slot.getAllIngredients().isEmpty() && slot.getAllIngredients().get(0) instanceof FluidStack stack) {
                removing.add(GroovyScriptCodeConverter.asGroovyCode(stack, false, false));
            }
        }
        // While there are occasionally other types of ingredients, such as Thaumcraft Aspects
        // or Mekanism Gases, those are rare and should be handled by those specific compats.
        // 99% of all recipes will either an ItemStack or a FluidStack.

        if (removing.isEmpty()) return "";
        return formatRemovalString("removeByInput", removing);
    }

    private static @NotNull String getFirstItemStack(IRecipeLayout layout) {
        if (layout.getItemStacks().getGuiIngredients().values().stream().filter(IGuiIngredient::isInput).filter(x -> !x.getAllIngredients().isEmpty()).count() > 1) {
            say("found too many input itemstacks!");
            return "";
        }
        for (IGuiIngredient<?> slot : layout.getItemStacks().getGuiIngredients().values()) {
            if (slot.isInput() && !slot.getAllIngredients().isEmpty() && slot.getAllIngredients().get(0) instanceof ItemStack stack) {
                return GroovyScriptCodeConverter.asGroovyCode(stack, false, false);
            }
        }
        return "";
    }

    private static @NotNull String getFirstFluidStack(IRecipeLayout layout) {
        if (layout.getFluidStacks().getGuiIngredients().values().stream().filter(IGuiIngredient::isInput).filter(x -> !x.getAllIngredients().isEmpty()).count() > 1) {
            say("found too many input fluidstacks!");
            return "";
        }
        for (IGuiIngredient<?> slot : layout.getFluidStacks().getGuiIngredients().values()) {
            if (slot.isInput() && !slot.getAllIngredients().isEmpty() && slot.getAllIngredients().get(0) instanceof FluidStack stack) {
                return GroovyScriptCodeConverter.asGroovyCode(stack, false, false);
            }
        }
        return "";
    }

    /**
     * @param method name of the method to call
     * @param params one or more parameters of the method
     * @return a string representing a GrS method
     * @see #formatRemovalString(String, List)
     */
    public static @NotNull String formatRemovalString(String method, String... params) {
        return String.format("%s(%s)", method, String.join(", ", params));
    }

    /**
     * @param method name of the method to call
     * @param params one or more parameters of the method
     * @return a string representing a GrS method
     */
    public static @NotNull String formatRemovalString(String method, List<String> params) {
        return String.format("%s(%s)", method, String.join(", ", params));
    }

}
