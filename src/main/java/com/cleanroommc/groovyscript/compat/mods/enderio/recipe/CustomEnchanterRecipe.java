package com.cleanroommc.groovyscript.compat.mods.enderio.recipe;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.stackable.Things;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.RecipeLevel;
import crazypants.enderio.base.recipe.enchanter.EnchanterRecipe;
import crazypants.enderio.util.Prep;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class CustomEnchanterRecipe extends EnchanterRecipe {

    private final Things lapis;
    private final Things book;

    public CustomEnchanterRecipe(@NotNull IIngredient input, int stackSizePerLevel, @NotNull Enchantment enchantment, double costMultiplier, IIngredient lapis, IIngredient book) {
        super(RecipeLevel.IGNORE, RecipeUtils.toThings(input), stackSizePerLevel, enchantment, costMultiplier);
        this.lapis = RecipeUtils.toThings(lapis);
        this.book = RecipeUtils.toThings(book);
    }

    @NotNull
    @Override
    public Things getLapis() {
        return lapis;
    }

    @NotNull
    @Override
    public Things getBook() {
        return book;
    }

    @Override
    public boolean isRecipe(@Nonnull RecipeLevel machineLevel, @Nonnull NNList<MachineRecipeInput> inputs) {
        ItemStack slot0 = MachineRecipeInput.getInputForSlot(0, inputs);
        ItemStack slot1 = MachineRecipeInput.getInputForSlot(1, inputs);
        ItemStack slot2 = MachineRecipeInput.getInputForSlot(2, inputs);
        if (getBook().contains(slot0) && this.getInput().contains(slot1) && getLapis().contains(slot2)) {
            int level = this.getLevelForStackSize(slot1.getCount());
            if (level < 1) {
                return false;
            } else {
                return slot2.getCount() >= this.getLapizForLevel(level);
            }
        } else {
            return false;
        }

    }

    private int getLevelForStackSize(int size) {
        return Math.min(size / this.getItemsPerLevel(), this.getEnchantment().getMaxLevel());
    }

    @Override
    public boolean isValidInput(@Nonnull RecipeLevel machineLevel, @Nonnull MachineRecipeInput inputs) {
        ItemStack slot0 = MachineRecipeInput.getInputForSlot(0, inputs);
        ItemStack slot1 = MachineRecipeInput.getInputForSlot(1, inputs);
        ItemStack slot2 = MachineRecipeInput.getInputForSlot(2, inputs);
        return (Prep.isInvalid(slot0) || getBook().contains(slot0)) && (Prep.isInvalid(slot1) || this.getInput().contains(slot1)) && (Prep.isInvalid(slot2) || getLapis().contains(slot2));
    }
}
