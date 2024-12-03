package com.cleanroommc.groovyscript.compat.vanilla;


import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.core.mixin.InventoryCraftingAccess;
import com.cleanroommc.groovyscript.core.mixin.SlotCraftingAccess;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import groovy.lang.Closure;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class CraftingRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe, ICraftingRecipe {

    protected final ItemStack output;
    protected final List<IIngredient> input;
    private final NonNullList<Ingredient> ingredients;
    protected final @Nullable Closure<ItemStack> recipeFunction;
    protected final @Nullable Closure<Void> recipeAction;

    public CraftingRecipe(ItemStack output, List<IIngredient> input, @Nullable Closure<ItemStack> recipeFunction, @Nullable Closure<Void> recipeAction) {
        this.output = output;
        this.input = input;
        this.recipeFunction = recipeFunction;
        this.recipeAction = recipeAction;
        this.ingredients = NonNullList.create();
        for (int i = 0; i < this.input.size(); i++) {
            if (this.input.get(i) == null) this.input.set(i, IIngredient.EMPTY);
        }
        for (IIngredient ingredient : this.input) {
            this.ingredients.add(ingredient.toMcIngredient());
        }
    }

    @Override
    public @NotNull ItemStack getCraftingResult(@NotNull InventoryCrafting inv) {
        ItemStack output = this.output.copy();

        if (recipeFunction == null || input.isEmpty()) return output;

        InputList inputs = new InputList();
        for (SlotMatchResult slotMatchResult : getMatchingList(inv)) {
            ItemStack givenInput = slotMatchResult.getGivenInput();
            inputs.add(givenInput);
        }

        // Call recipe function
        ItemStack recipeFunctionResult = ClosureHelper.call(recipeFunction, output, inputs, new CraftingInfo(inv, getPlayerFromInventory(inv)));
        return recipeFunctionResult == null ? output : recipeFunctionResult;
    }

    @Override
    public @NotNull ItemStack getRecipeOutput() {
        return output;
    }

    @Override
    public @Nullable Closure<Void> getRecipeAction() {
        return recipeAction;
    }

    @Override
    public @Nullable Closure<ItemStack> getRecipeFunction() {
        return recipeFunction;
    }

    public boolean matches(@Nullable IIngredient expectedInput, ItemStack givenInput) {
        return expectedInput == null || expectedInput.test(givenInput);
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull InventoryCrafting inv) {
        NonNullList<ItemStack> result = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        for (SlotMatchResult matchResult : getMatchingList(inv)) {
            ItemStack input = matchResult.getGivenInput();
            ItemStack remainder = matchResult.getRecipeIngredient().applyTransform(input.copy());
            if (remainder == null) remainder = ItemStack.EMPTY;
            else if (input.getCount() > 1 && !remainder.isEmpty() && ItemStack.areItemsEqual(input, remainder) && ItemStack.areItemStackTagsEqual(input, remainder)) {
                remainder.setCount(1);
            }
            result.set(matchResult.getSlotIndex(), remainder);
        }
        return result;
    }

    @Override
    public boolean matches(@NotNull InventoryCrafting inv, @NotNull World worldIn) {
        return !getMatchingList(inv).isEmpty();
    }

    public abstract @NotNull MatchList getMatchingList(InventoryCrafting inv);

    /**
     * Contains information about a inventory that was matched against a recipe.
     * The triples contain:
     * 1. The ingredient defined by the recipe
     * 2. The given input from a inventory
     * 3. The slot index the input was found
     */
    public static class MatchList extends ArrayList<SlotMatchResult> {

        public static final MatchList EMPTY = new MatchList() {

            @Override
            public boolean add(SlotMatchResult slotMatchResult) {
                throw new UnsupportedOperationException();
            }
        };

        public void addMatch(IIngredient ingredient, ItemStack itemStack, int itemSlotIndex) {
            add(new SlotMatchResult(ingredient, itemStack, itemSlotIndex));
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    public static class SlotMatchResult {

        private final IIngredient recipeIngredient;
        private final ItemStack givenInput;
        private final int slotIndex;

        public SlotMatchResult(IIngredient recipeIngredient, ItemStack givenInput, int slotIndex) {
            this.recipeIngredient = recipeIngredient;
            this.givenInput = givenInput;
            this.slotIndex = slotIndex;
        }

        public IIngredient getRecipeIngredient() {
            return recipeIngredient;
        }

        public ItemStack getGivenInput() {
            // Copy mark from recipeIngredient to givenInput
            ItemStackMixinExpansion itemStack = ItemStackMixinExpansion.of(givenInput);
            String mark = recipeIngredient.getMark();
            if (mark != null) itemStack.setMark(mark);
            return itemStack.grs$getItemStack();
        }

        public int getSlotIndex() {
            return slotIndex;
        }
    }

    public static class InputList extends ArrayList<ItemStack> {

        // groovy [] operator
        public @Nullable ItemStack getAt(String mark) {
            return findMarked(mark);
        }

        public @Nullable ItemStack findMarked(String mark) {
            if (isEmpty()) return null;
            for (ItemStack itemStack : this) {
                if (mark.equals(ItemStackMixinExpansion.of(itemStack).getMark())) {
                    return itemStack;
                }
            }
            return null;
        }

        public ItemStack findMarkedOrEmpty(String mark) {
            if (isEmpty()) return ItemStack.EMPTY;
            for (ItemStack itemStack : this) {
                if (mark.equals(ItemStackMixinExpansion.of(itemStack).getMark())) return itemStack;
            }
            return ItemStack.EMPTY;
        }
    }

    // TODO
    private static EntityPlayer getPlayerFromInventory(InventoryCrafting inventory) {
        Container eventHandler = ((InventoryCraftingAccess) inventory).getEventHandler();
        if (eventHandler != null) {
            for (Slot slot : eventHandler.inventorySlots) {
                if (slot instanceof SlotCraftingAccess slotCraftingAccess) {
                    return slotCraftingAccess.getPlayer();
                }
            }
        }
        return null;
    }
}
