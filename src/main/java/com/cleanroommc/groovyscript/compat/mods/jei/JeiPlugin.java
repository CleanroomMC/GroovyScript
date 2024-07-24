package com.cleanroommc.groovyscript.compat.mods.jei;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.command.GSCommand;
import com.cleanroommc.groovyscript.command.SimpleCommand;
import com.cleanroommc.groovyscript.compat.inworldcrafting.jei.InWorldCraftingJeiPlugin;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.vanilla.ShapedCraftingRecipe;
import com.cleanroommc.groovyscript.compat.vanilla.ShapelessCraftingRecipe;
import mezz.jei.Internal;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.ingredients.IngredientRegistry;
import mezz.jei.plugins.vanilla.crafting.ShapelessRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fluids.FluidStack;

@SuppressWarnings("AssignmentToStaticFieldFromInstanceMethod")
@GroovyBlacklist
@JEIPlugin
public class JeiPlugin implements IModPlugin {

    public static IJeiHelpers jeiHelpers;
    public static IIngredientRegistry itemRegistry;
    public static IRecipeRegistry recipeRegistry;
    public static IIngredientHelper<ItemStack> ingredientHelper;
    public static IModRegistry modRegistry;
    public static IJeiRuntime jeiRuntime;

    public static IIngredientRenderer<FluidStack> fluidRenderer;

    public static void afterRegister() {
        ModSupport.JEI.get().catalyst.applyChanges(modRegistry);
    }

    public static void afterRuntimeAvailable() {
        ModSupport.JEI.get().ingredient.applyChanges(modRegistry.getIngredientRegistry());
        ModSupport.JEI.get().category.applyChanges(jeiRuntime.getRecipeRegistry());
        ModSupport.JEI.get().description.applyRemovals(jeiRuntime.getRecipeRegistry());
    }

    public static boolean isLoaded() {
        return jeiRuntime != null;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IngredientRegistry ingredientRegistry = Internal.getIngredientRegistry();
        fluidRenderer = ingredientRegistry.getIngredientRenderer(VanillaTypes.FLUID);

        InWorldCraftingJeiPlugin.registerCategories(registry);
        ModSupport.JEI.get().category.addCustomRecipeCategories(registry);
    }

    @Override
    public void register(IModRegistry registry) {
        jeiHelpers = registry.getJeiHelpers();
        itemRegistry = registry.getIngredientRegistry();
        ingredientHelper = itemRegistry.getIngredientHelper(VanillaTypes.ITEM);
        modRegistry = registry;

        // jei can't handle custom recipe classes on its own
        registry.handleRecipes(ShapedCraftingRecipe.class, recipe -> new ShapedRecipeWrapper(jeiHelpers, recipe), VanillaRecipeCategoryUid.CRAFTING);
        registry.handleRecipes(ShapelessCraftingRecipe.class, recipe -> new ShapelessRecipeWrapper<>(jeiHelpers, recipe), VanillaRecipeCategoryUid.CRAFTING);

        InWorldCraftingJeiPlugin.register(registry);
        ModSupport.JEI.get().category.applyCustomRecipeCategoryProperties(registry);
        ModSupport.JEI.get().description.applyAdditions(registry);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime iJeiRuntime) {
        recipeRegistry = iJeiRuntime.getRecipeRegistry();
        jeiRuntime = iJeiRuntime;
    }

    public static SimpleCommand getJeiCategoriesCommand() {
        return new SimpleCommand("jeiCategories", (server, sender, args) -> {
            GroovyLog.get().info("All JEI Categories:");
            for (IRecipeCategory<?> category : recipeRegistry.getRecipeCategories()) {
                GroovyLog.get().getWriter().println(" - " + category.getUid());
            }
            sender.sendMessage(new TextComponentString("JEI Categories has been logged to the ")
                                       .appendSibling(GSCommand.getTextForFile("Groovy Log", GroovyLog.get().getLogFilerPath().toString(), new TextComponentString("Click to open GroovyScript log"))));
        });
    }
}
