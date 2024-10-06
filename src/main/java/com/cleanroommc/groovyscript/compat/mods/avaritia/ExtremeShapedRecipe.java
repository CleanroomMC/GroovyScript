package com.cleanroommc.groovyscript.compat.mods.avaritia;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.vanilla.ShapedCraftingRecipe;
import groovy.lang.Closure;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExtremeShapedRecipe extends morph.avaritia.recipe.extreme.ExtremeShapedRecipe {

    private final ShapedCraftingRecipe groovyRecipe;

    public static ExtremeShapedRecipe make(ItemStack output, List<IIngredient> input, int width, int height, boolean mirrored, @Nullable Closure<ItemStack> recipeFunction, @Nullable Closure<Void> recipeAction) {
        ShapedCraftingRecipe recipe = new ShapedCraftingRecipe(output, input, width, height, mirrored, recipeFunction, recipeAction);
        CraftingHelper.ShapedPrimer primer = new CraftingHelper.ShapedPrimer();
        primer.width = width;
        primer.height = height;
        primer.mirrored = mirrored;
        primer.input = recipe.getIngredients();
        return new ExtremeShapedRecipe(recipe, primer);
    }

    private ExtremeShapedRecipe(ShapedCraftingRecipe groovyRecipe, CraftingHelper.ShapedPrimer primer) {
        super(groovyRecipe.getRecipeOutput(), primer);
        this.groovyRecipe = groovyRecipe;
        setMirrored(this.groovyRecipe.isMirrored());
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
