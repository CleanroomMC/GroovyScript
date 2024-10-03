package com.cleanroommc.groovyscript.compat.mods.extendedcrafting;

import com.blakebr0.extendedcrafting.crafting.table.TableRecipeShaped;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.vanilla.ShapedCraftingRecipe;
import groovy.lang.Closure;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShapedTableRecipe extends TableRecipeShaped {

    private final ShapedCraftingRecipe groovyRecipe;

    public static ShapedTableRecipe make(int tier, ItemStack output, List<IIngredient> input, int width, int height, boolean mirrored, @Nullable Closure<ItemStack> recipeFunction, @Nullable Closure<Void> recipeAction) {
        ShapedCraftingRecipe recipe = new ShapedCraftingRecipe(output, input, width, height, mirrored, recipeFunction, recipeAction);
        return new ShapedTableRecipe(tier, recipe);
    }

    public ShapedTableRecipe(int tier, ShapedCraftingRecipe groovyRecipe) {
        super(tier, groovyRecipe.getRecipeOutput(), groovyRecipe.getRecipeWidth(), groovyRecipe.getRecipeHeight(), groovyRecipe.getIngredients());
        this.groovyRecipe = groovyRecipe;
        setMirrored(this.groovyRecipe.isMirrored());
    }

    @Override
    public @NotNull ItemStack getCraftingResult(@NotNull InventoryCrafting inv) {
        return this.groovyRecipe.getCraftingResult(inv);
    }

    @Override
    public boolean matches(@NotNull InventoryCrafting inv, @NotNull World world) {
        return (this.tier == 0 || this.tier == getTierFromGridSize(inv)) && this.groovyRecipe.matches(inv, world);
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull InventoryCrafting inv) {
        return this.groovyRecipe.getRemainingItems(inv);
    }

}
