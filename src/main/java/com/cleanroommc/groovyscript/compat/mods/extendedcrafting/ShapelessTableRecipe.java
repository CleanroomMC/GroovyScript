package com.cleanroommc.groovyscript.compat.mods.extendedcrafting;

import com.blakebr0.extendedcrafting.crafting.table.TableRecipeShapeless;
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

public class ShapelessTableRecipe extends TableRecipeShapeless {

    private final ShapelessCraftingRecipe groovyRecipe;

    public static ShapelessTableRecipe make(int tier, ItemStack output, List<IIngredient> input, @Nullable Closure<ItemStack> recipeFunction, @Nullable Closure<Void> recipeAction) {
        ShapelessCraftingRecipe recipe = new ShapelessCraftingRecipe(output, input, recipeFunction, recipeAction);
        return new ShapelessTableRecipe(tier, recipe);
    }

    public ShapelessTableRecipe(int tier, ShapelessCraftingRecipe groovyRecipe) {
        super(tier, groovyRecipe.getRecipeOutput(), groovyRecipe.getIngredients());
        this.groovyRecipe = groovyRecipe;
    }

    @Override
    public @NotNull ItemStack getCraftingResult(@NotNull InventoryCrafting inv) {
        return this.groovyRecipe.getCraftingResult(inv);
    }

    @Override
    public boolean matches(@NotNull InventoryCrafting inv, @NotNull World world) {
        return (this.tier == 0 || this.tier == getTierFromSize(inv.getSizeInventory())) && this.groovyRecipe.matches(inv, world);
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull InventoryCrafting inv) {
        return this.groovyRecipe.getRemainingItems(inv);
    }

}
