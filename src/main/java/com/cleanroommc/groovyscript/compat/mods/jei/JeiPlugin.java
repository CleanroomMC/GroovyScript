package com.cleanroommc.groovyscript.compat.mods.jei;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.compat.vanilla.ShapedCraftingRecipe;
import com.cleanroommc.groovyscript.compat.vanilla.ShapelessCraftingRecipe;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.plugins.vanilla.crafting.ShapelessRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@GroovyBlacklist
@JEIPlugin
public class JeiPlugin implements IModPlugin {

    public static IJeiHelpers jeiHelpers;
    public static IIngredientRegistry itemRegistry;
    public static IRecipeRegistry recipeRegistry;
    public static IIngredientHelper<ItemStack> ingredientHelper;
    public static IModRegistry modRegistry;
    public static IJeiRuntime jeiRuntime;

    public static final List<ItemStack> HIDDEN = new ArrayList<>();
    public static final List<FluidStack> HIDDEN_FLUIDS = new ArrayList<>();
    public static final List<String> HIDDEN_CATEGORY = new ArrayList<>();

    public static boolean isLoaded() {
        return jeiRuntime != null;
    }

    public static void reload() {
        HIDDEN.clear();
        HIDDEN_CATEGORY.clear();
    }

    public static void hideItem(ItemStack... stack) {
        Collections.addAll(HIDDEN, stack);
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
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime iJeiRuntime) {
        recipeRegistry = iJeiRuntime.getRecipeRegistry();
        jeiRuntime = iJeiRuntime;

        itemRegistry.removeIngredientsAtRuntime(VanillaTypes.ITEM, ingredientHelper.expandSubtypes(HIDDEN));
        itemRegistry.removeIngredientsAtRuntime(VanillaTypes.FLUID, HIDDEN_FLUIDS);
        for (String category : HIDDEN_CATEGORY) {
            recipeRegistry.hideRecipeCategory(category);
        }
    }
}
