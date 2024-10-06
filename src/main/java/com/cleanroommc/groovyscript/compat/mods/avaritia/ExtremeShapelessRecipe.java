package com.cleanroommc.groovyscript.compat.mods.avaritia;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.vanilla.ShapelessCraftingRecipe;
import groovy.lang.Closure;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExtremeShapelessRecipe extends morph.avaritia.recipe.extreme.ExtremeShapelessRecipe {

    private final ShapelessCraftingRecipe groovyRecipe;

    public static ExtremeShapelessRecipe make(ItemStack output, List<IIngredient> input, @Nullable Closure<ItemStack> recipeFunction, @Nullable Closure<Void> recipeAction) {
        ShapelessCraftingRecipe recipe = new ShapelessCraftingRecipe(output, input, recipeFunction, recipeAction);
        return new ExtremeShapelessRecipe(recipe);
    }

    public ExtremeShapelessRecipe(ShapelessCraftingRecipe groovyRecipe) {
        super(groovyRecipe.getIngredients(), groovyRecipe.getRecipeOutput());
        this.groovyRecipe = groovyRecipe;
    }

    @Override
    public @NotNull ItemStack getCraftingResult(@NotNull InventoryCrafting inv) {
        return this.groovyRecipe.getCraftingResult(inv);
    }

    @Override
    public boolean matches(@NotNull InventoryCrafting inv, @NotNull World world) {
        return this.groovyRecipe.matches(inv, world);
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull InventoryCrafting inv) {
        return this.groovyRecipe.getRemainingItems(inv);
    }
}
