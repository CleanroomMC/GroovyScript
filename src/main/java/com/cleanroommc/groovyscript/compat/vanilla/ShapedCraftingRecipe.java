package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.api.IIngredient;
import groovy.lang.Closure;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.crafting.IShapedRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShapedCraftingRecipe extends CraftingRecipe implements IShapedRecipe {

    private final int width, height;
    private final boolean mirrored;

    public ShapedCraftingRecipe(ItemStack output, List<IIngredient> input, int width, int height, boolean mirrored, @Nullable Closure<ItemStack> recipeFunction, @Nullable Closure<Void> recipeAction) {
        super(output, input, recipeFunction, recipeAction);
        this.width = width;
        this.height = height;
        this.mirrored = mirrored;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width >= this.width && height >= this.height;
    }

    @Override
    public int getRecipeWidth() {
        return width;
    }

    @Override
    public int getRecipeHeight() {
        return height;
    }

    public boolean isMirrored() {
        return mirrored;
    }

    @Override
    public @NotNull MatchList getMatchingList(InventoryCrafting inv) {
        for (int x = 0; x <= inv.getWidth() - width; x++) {
            for (int y = 0; y <= inv.getHeight() - height; ++y) {
                MatchList matches = checkMatch(inv, x, y, false);
                if (!matches.isEmpty()) return matches;
                if (mirrored) {
                    matches = checkMatch(inv, x, y, true);
                    if (!matches.isEmpty()) return matches;
                }
            }
        }

        return MatchList.EMPTY;
    }

    /**
     * Based on {@link net.minecraft.item.crafting.ShapedRecipes#checkMatch(InventoryCrafting, int, int, boolean)}
     */
    @SuppressWarnings("JavadocReference")
    protected MatchList checkMatch(InventoryCrafting inv, int startX, int startY, boolean mirror) {
        MatchList matches = new MatchList();
        for (int x = 0; x < inv.getWidth(); x++) {
            for (int y = 0; y < inv.getHeight(); y++) {
                int subX = x - startX;
                int subY = y - startY;
                IIngredient target = IIngredient.EMPTY;

                if (subX >= 0 && subY >= 0 && subX < width && subY < height) {
                    if (mirror) {
                        target = input.get(width - subX - 1 + subY * width);
                    } else {
                        target = input.get(subX + subY * width);
                    }
                }

                ItemStack itemStack = inv.getStackInRowAndColumn(x, y);
                if (target.test(itemStack)) {
                    if (!itemStack.isEmpty()) {
                        matches.addMatch(target, itemStack, x + y * inv.getWidth());
                    }
                } else {
                    return MatchList.EMPTY;
                }
            }
        }

        return matches;
    }
}
