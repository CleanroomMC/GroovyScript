package com.cleanroommc.groovyscript.compat.mods.betterwithmods;

import betterwithmods.common.registry.anvil.ShapelessAnvilRecipe;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.vanilla.ShapelessCraftingRecipe;
import groovy.lang.Closure;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AnvilShapelessRecipe extends ShapelessAnvilRecipe {

    private final ShapelessCraftingRecipe groovyRecipe;

    public AnvilShapelessRecipe(ResourceLocation resourceLocation, ShapelessCraftingRecipe groovyRecipe) {
        super(resourceLocation, groovyRecipe.getIngredients(), groovyRecipe.getRecipeOutput());
        this.groovyRecipe = groovyRecipe;
    }

//    public static AnvilShapelessRecipe make(ItemStack output, List<IIngredient> input, @Nullable Closure<ItemStack> recipeFunction, @Nullable Closure<Void> recipeAction) {
//        return make(null, output, input, recipeFunction, recipeAction);
//    }

    public static AnvilShapelessRecipe make(ResourceLocation resourceLocation, ItemStack output, List<IIngredient> input, @Nullable Closure<ItemStack> recipeFunction, @Nullable Closure<Void> recipeAction) {
        ShapelessCraftingRecipe recipe = new ShapelessCraftingRecipe(output, input, recipeFunction, recipeAction);
        return new AnvilShapelessRecipe(resourceLocation, recipe);
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
