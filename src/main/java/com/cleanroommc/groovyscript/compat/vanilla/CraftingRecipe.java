package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.IMarkable;
import com.cleanroommc.groovyscript.core.mixin.InventoryCraftingAccess;
import com.cleanroommc.groovyscript.core.mixin.SlotCraftingAccess;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import groovy.lang.Closure;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
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
    @Nullable
    protected final Closure<ItemStack> recipeFunction;
    @Nullable
    protected final Closure<Void> recipeAction;

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
        ItemStack result = output.copy();
        if (recipeFunction != null) {
            MatchList matchList = getMatchingList(inv);
            Object2ObjectOpenHashMap<String, ItemStack> marks = new Object2ObjectOpenHashMap<>();
            for (SlotMatchResult matchResult : matchList) {
                if (matchResult.getRecipeIngredient() instanceof IMarkable) {
                    IMarkable markable = (IMarkable) matchResult.getRecipeIngredient();
                    if (!input.isEmpty() && markable.hasMark()) {
                        marks.put(markable.getMark(), matchResult.getGivenInput().copy());
                    }
                }
            }
            result = ClosureHelper.call(recipeFunction, result, marks, new CraftingInfo(inv, getPlayerFromInventory(inv)));
            if (result == null) {
                result = ItemStack.EMPTY;
            }
        }
        return result;
    }

    @Override
    public @NotNull ItemStack getRecipeOutput() {
        return output;
    }

    @Nullable
    @Override
    public Closure<Void> getRecipeAction() {
        return recipeAction;
    }

    @Nullable
    @Override
    public Closure<ItemStack> getRecipeFunction() {
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
            ItemStack itemStack = matchResult.getRecipeIngredient().applyTransform(matchResult.getGivenInput().copy());
            result.set(matchResult.getSlotIndex(), itemStack == null ? ItemStack.EMPTY : itemStack);
        }
        return result;
    }

    @Override
    public boolean matches(@NotNull InventoryCrafting inv, @NotNull World worldIn) {
        return !getMatchingList(inv).isEmpty();
    }

    @NotNull
    public abstract MatchList getMatchingList(InventoryCrafting inv);

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
            return givenInput;
        }

        public int getSlotIndex() {
            return slotIndex;
        }
    }

    // TODO
    private static EntityPlayer getPlayerFromInventory(InventoryCrafting inventory) {
        Container eventHandler = ((InventoryCraftingAccess) inventory).getEventHandler();
        if (eventHandler != null) {
            for (Slot slot : eventHandler.inventorySlots) {
                if (slot instanceof SlotCraftingAccess) {
                    return ((SlotCraftingAccess) slot).getPlayer();
                }
            }
        }
        return null;
    }
}
