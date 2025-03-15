package com.cleanroommc.groovyscript.compat.mods.thaumcraft.arcane;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.vanilla.ICraftingRecipe;
import com.cleanroommc.groovyscript.compat.vanilla.ShapelessCraftingRecipe;
import groovy.lang.Closure;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.ContainerDummy;
import thaumcraft.api.crafting.IArcaneWorkbench;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;
import thaumcraft.api.items.ItemsTC;

import java.util.List;

/**
 * A thaum shaped recipe wrapping a groovy recipe
 * Yes this needs to be done
 */
public class ShapelessArcaneCR extends ShapelessArcaneRecipe implements ICraftingRecipe {

    private final ShapelessCraftingRecipe craftingRecipe;

    public ShapelessArcaneCR(ItemStack output, List<IIngredient> input, @Nullable Closure<ItemStack> recipeFunction, @Nullable Closure<Void> recipeAction, String research, int vis, AspectList crystals) {
        super(ArcaneWorkbench.DEFAULT, research, vis, crystals, output, new Object[]{
                Ingredient.EMPTY
        });
        this.craftingRecipe = new ShapelessCraftingRecipe(output, input, recipeFunction, recipeAction);
    }

    @Override
    public boolean canFit(int width, int height) {
        return craftingRecipe.canFit(width, height);
    }

    @Override
    public boolean isDynamic() {
        return craftingRecipe.isDynamic();
    }

    @Override
    public @NotNull ItemStack getCraftingResult(@NotNull InventoryCrafting inv) {
        return inv instanceof IArcaneWorkbench ? craftingRecipe.getCraftingResult(inv) : ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack getRecipeOutput() {
        return craftingRecipe.getRecipeOutput();
    }

    @Override
    public @Nullable Closure<Void> getRecipeAction() {
        return craftingRecipe.getRecipeAction();
    }

    @Override
    public @Nullable Closure<ItemStack> getRecipeFunction() {
        return craftingRecipe.getRecipeFunction();
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return craftingRecipe.getIngredients();
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull InventoryCrafting inv) {
        return craftingRecipe.getRemainingItems(inv);
    }

    /**
     * Stolen from {@link thaumcraft.api.crafting.ShapelessArcaneRecipe#matches(InventoryCrafting, World)}
     */
    @Override
    public boolean matches(@NotNull InventoryCrafting inv, @NotNull World world) {
        if (!(inv instanceof IArcaneWorkbench)) return false;

        InventoryCrafting dummy = new InventoryCrafting(new ContainerDummy(), 3, 3);

        for (int a = 0; a < 9; ++a) {
            dummy.setInventorySlotContents(a, inv.getStackInSlot(a));
        }

        if (getCrystals() != null && inv.getSizeInventory() >= 15) {
            Aspect[] var12 = getCrystals().getAspects();
            int var5 = var12.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                Aspect aspect = var12[var6];
                ItemStack cs = ThaumcraftApiHelper.makeCrystal(aspect, getCrystals().getAmount(aspect));
                boolean b = false;

                for (int i = 0; i < 6; ++i) {
                    ItemStack itemstack1 = inv.getStackInSlot(9 + i);
                    if (itemstack1 != null && itemstack1.getItem() == ItemsTC.crystalEssence && itemstack1.getCount() >= cs.getCount() && ItemStack.areItemStackTagsEqual(cs, itemstack1)) {
                        b = true;
                    }
                }

                if (!b) {
                    return false;
                }
            }
        }

        return craftingRecipe.matches(dummy, world);
    }
}
