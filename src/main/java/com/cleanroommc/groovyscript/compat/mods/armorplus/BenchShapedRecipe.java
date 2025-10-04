package com.cleanroommc.groovyscript.compat.mods.armorplus;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.vanilla.ShapedCraftingRecipe;
import com.sofodev.armorplus.api.crafting.IRecipe;
import com.sofodev.armorplus.api.crafting.IShapedRecipe;
import com.sofodev.armorplus.api.crafting.base.BaseShapedOreRecipe;
import com.sofodev.armorplus.common.container.base.InventoryCraftingImproved;
import groovy.lang.Closure;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class BenchShapedRecipe extends BaseShapedOreRecipe implements IRecipe, IShapedRecipe {

    private final int size;
    private final ShapedCraftingRecipe groovyRecipe;
    private final Object[] input;

    private BenchShapedRecipe(int size, ShapedCraftingRecipe groovyRecipe, Object[] shapeDummy, Object[] input) {
        super(size, groovyRecipe.getRecipeOutput(), shapeDummy);
        this.size = size;
        this.groovyRecipe = groovyRecipe;
        this.input = input;
    }

    public static BenchShapedRecipe make(int size, ItemStack output, List<IIngredient> input, int width, int height, boolean mirrored, @Nullable Closure<ItemStack> recipeFunction, @Nullable Closure<Void> recipeAction) {
        ShapedCraftingRecipe recipe = new ShapedCraftingRecipe(output, input, width, height, mirrored, recipeFunction, recipeAction);
        Object[] jeiInput = new Object[width * height];
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            jeiInput[i] = Arrays.asList(recipe.getIngredients().get(i).getMatchingStacks());
        }
        Object[] shapeDummy = new Object[height + 2];
        var row = StringUtils.repeat(' ', width);
        int i = 0;
        while (i < height) {
            shapeDummy[i++] = row;
        }
        shapeDummy[i++] = ' ';
        shapeDummy[i] = ItemStack.EMPTY;

        return new BenchShapedRecipe(size, recipe, shapeDummy, jeiInput);
    }

    public @NotNull NonNullList<Ingredient> getIngredients() {
        return groovyRecipe.getIngredients();
    }

    @Override
    public int getRecipeWidth() {
        return size;
    }

    @Override
    public int getRecipeHeight() {
        return size;
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
        return size * size;
    }

    @Override
    public @NotNull ItemStack getRecipeOutput() {
        return groovyRecipe.getRecipeOutput();
    }

    @Override
    public Object[] getInput() {
        return this.input;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCraftingImproved inv) {
        return groovyRecipe.getRemainingItems(inv);
    }
}
