package com.cleanroommc.groovyscript.compat.mods.armorplus;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.vanilla.ShapelessCraftingRecipe;
import com.sofodev.armorplus.api.crafting.IRecipe;
import com.sofodev.armorplus.api.crafting.base.BaseShapelessOreRecipe;
import com.sofodev.armorplus.common.container.base.InventoryCraftingImproved;
import groovy.lang.Closure;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class BenchShapelessRecipe extends BaseShapelessOreRecipe implements IRecipe {

    private final ShapelessCraftingRecipe groovyRecipe;
    private final NonNullList<Object> input;

    public BenchShapelessRecipe(ShapelessCraftingRecipe groovyRecipe, NonNullList<Object> input) {
        super(groovyRecipe.getRecipeOutput());
        this.groovyRecipe = groovyRecipe;
        this.input = input;
    }

    public static BenchShapelessRecipe make(ItemStack output, List<IIngredient> input, @Nullable Closure<ItemStack> recipeFunction, @Nullable Closure<Void> recipeAction) {
        ShapelessCraftingRecipe recipe = new ShapelessCraftingRecipe(output, input, recipeFunction, recipeAction);
        NonNullList<Object> jeiInput = NonNullList.create();
        for (var ingredient : recipe.getIngredients()) {
            jeiInput.add(Arrays.asList(ingredient.getMatchingStacks()));
        }
        return new BenchShapelessRecipe(recipe, jeiInput);
    }

    public @NotNull NonNullList<Ingredient> getIngredients() {
        return groovyRecipe.getIngredients();
    }

    @Override
    public boolean matches(@NotNull InventoryCraftingImproved inv, @NotNull World world) {
        return groovyRecipe.matches(inv, world);
    }

    @Override
    public @NotNull ItemStack getCraftingResult(@NotNull InventoryCraftingImproved inv) {
        return groovyRecipe.getCraftingResult(inv);
    }

    @Override
    public int getRecipeSize() {
        return input.size();
    }

    @Override
    public @NotNull ItemStack getRecipeOutput() {
        return groovyRecipe.getRecipeOutput();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCraftingImproved inv) {
        return groovyRecipe.getRemainingItems(inv);
    }

    @Override
    public NonNullList<Object> getInput() {
        return this.input;
    }
}
