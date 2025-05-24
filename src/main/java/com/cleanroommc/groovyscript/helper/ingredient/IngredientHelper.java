package com.cleanroommc.groovyscript.helper.ingredient;

import com.cleanroommc.groovyscript.GroovyScriptConfig;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.IOreDicts;
import com.cleanroommc.groovyscript.compat.vanilla.ItemStackMixinExpansion;
import com.cleanroommc.groovyscript.sandbox.expand.LambdaClosure;
import com.google.common.collect.Lists;
import groovy.lang.Closure;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class IngredientHelper {

    public static final Closure<Object> MATCH_ANY = new LambdaClosure<>(args -> true);
    public static final Closure<Object> MATCH_NONE = new LambdaClosure<>(args -> false);
    public static final Closure<Object> REUSE = new LambdaClosure<>(args -> args[0]);
    public static final Closure<Object> NO_RETURN = new LambdaClosure<>(args -> ItemStack.EMPTY);
    public static final Closure<Object> MATCH_NBT = new LambdaClosure<>(args -> ItemStack.EMPTY);

    public static ItemStack damageItem(ItemStack stack, int damage) {
        // Short.MAX_VALUE is meta wildcard
        // Items.DIAMOND.getDamage(stack) is guaranteed to return the value of the damage field of stack
        return ItemStackMixinExpansion.of(stack).withMeta(Math.min(Short.MAX_VALUE - 1, Items.DIAMOND.getDamage(stack) + damage));
    }

    public static boolean isFluid(IIngredient ingredient) {
        return ingredient instanceof FluidStack;
    }

    @SuppressWarnings("all")
    public static boolean isItem(IIngredient ingredient) {
        return (Object) ingredient instanceof ItemStack;
    }

    @SuppressWarnings("all")
    public static ItemStack toItemStack(IIngredient ingredient) {
        return (ItemStack) (Object) ingredient;
    }

    public static ItemStack toItemStack(IBlockState state) {
        return toItemStack(state, 1);
    }

    public static ItemStack toItemStack(IBlockState state, int amount) {
        var block = state.getBlock();
        return new ItemStack(block, amount, block.getMetaFromState(state));
    }

    public static FluidStack toFluidStack(IIngredient ingredient) {
        return (FluidStack) ingredient;
    }

    public static IIngredient toIIngredient(ItemStack itemStack) {
        return (IIngredient) (Object) itemStack;
    }

    public static IIngredient toIIngredient(FluidStack fluidStack) {
        return (IIngredient) fluidStack;
    }

    public static @NotNull NonNullList<IIngredient> toNonNullList(IngredientList<IIngredient> list) {
        NonNullList<IIngredient> ingredients = NonNullList.create();
        for (IIngredient i : list) {
            if (i == null) ingredients.add(IIngredient.EMPTY);
            else ingredients.add(i);
        }
        return ingredients;
    }

    public static @NotNull NonNullList<Ingredient> toIngredientNonNullList(Collection<IIngredient> list) {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        for (IIngredient i : list) {
            if (i == null) ingredients.add(Ingredient.EMPTY);
            else ingredients.add(i.toMcIngredient());
        }
        return ingredients;
    }

    /**
     * Converts a List of IIngredients into every combination of oredict
     * (if the IIngredient represents one or more oredicts)
     * and matching ItemStacks
     *
     * @param inputs a list of IIngredients
     * @return a list of cartesian product of the oredicts (if relevant) and matching stacks
     */
    public static @NotNull List<List<Object>> cartesianProductOres(@NotNull List<IIngredient> inputs) {
        List<List<?>> entries = new ArrayList<>();
        for (var input : inputs) {
            if (input instanceof IOreDicts ore) entries.add(new ArrayList<>(ore.getOreDicts()));
            else entries.add(Arrays.asList(input.getMatchingStacks()));
        }
        return Lists.cartesianProduct(entries);
    }

    /**
     * Converts a List of IIngredients into every combination of matching ItemStacks
     *
     * @param inputs a list of IIngredients
     * @return a list of cartesian product of the matching stacks
     */
    public static @NotNull List<List<ItemStack>> cartesianProductItemStacks(@NotNull List<IIngredient> inputs) {
        List<List<ItemStack>> entries = new ArrayList<>();
        for (var input : inputs) {
            entries.add(Arrays.asList(input.getMatchingStacks()));
        }
        return Lists.cartesianProduct(entries);
    }


    public static boolean isEmpty(@Nullable IIngredient ingredient) {
        return ingredient == null || ingredient.isEmpty();
    }

    public static boolean isEmpty(@Nullable ItemStack itemStack) {
        return itemStack == null || itemStack.isEmpty();
    }

    public static boolean isEmpty(@Nullable FluidStack itemStack) {
        return itemStack == null || itemStack.amount <= 0;
    }

    public static boolean isEmpty(@Nullable NBTTagCompound nbt) {
        return nbt == null || nbt.isEmpty();
    }

    public static boolean overMaxSize(@Nullable IIngredient ingredient, int maxSize) {
        return GroovyScriptConfig.compat.checkInputStackCounts && ingredient != null && ingredient.getAmount() > maxSize;
    }

    public static boolean overMaxSize(Collection<IIngredient> ingredient, int maxSize) {
        return ingredient.stream().anyMatch(a -> overMaxSize(a, maxSize));
    }

    public static boolean overMaxSize(@Nullable ItemStack ingredient, int maxSize) {
        return GroovyScriptConfig.compat.checkInputStackCounts && ingredient != null && ingredient.getCount() > maxSize;
    }

    /**
     * Determines whether the list or all elements in the list are considered empty
     *
     * @param itemStacks collection of item stacks
     * @return true if the collection or the elements are empty
     */
    public static boolean isEmptyItems(@Nullable Collection<ItemStack> itemStacks) {
        if (itemStacks == null || itemStacks.isEmpty())
            return true;
        for (ItemStack item : itemStacks)
            if (!isEmpty(item)) return false;
        return true;
    }

    /**
     * Determines whether the list or all elements in the list are considered empty
     *
     * @param fluidStacks collection of fluid stacks
     * @return true if the collection or the elements are empty
     */
    public static boolean isEmptyFluids(@Nullable Collection<FluidStack> fluidStacks) {
        if (fluidStacks == null || fluidStacks.isEmpty())
            return true;
        for (FluidStack fluid : fluidStacks)
            if (!isEmpty(fluid)) return false;
        return true;
    }

    /**
     * Determines whether the list or all elements in the list are considered empty
     *
     * @param ingredients collection of ingredients
     * @return true if the collection or all elements are empty
     */
    public static boolean isEmpty(@Nullable Collection<IIngredient> ingredients) {
        if (ingredients == null || ingredients.isEmpty())
            return true;
        for (IIngredient item : ingredients)
            if (!isEmpty(item)) return false;
        return true;
    }

    /**
     * Determines whether the list or all elements in the list are considered empty
     *
     * @param ingredients collection of ingredients
     * @return true if the collection or one element is empty
     */
    public static boolean isAnyEmpty(@Nullable Collection<IIngredient> ingredients) {
        if (ingredients == null || ingredients.isEmpty())
            return true;
        for (IIngredient item : ingredients)
            if (isEmpty(item)) return true;
        return false;
    }

    /**
     * Determines whether the list or all elements in the array are considered empty
     *
     * @param itemStacks array of item stacks
     * @return true if the array or the elements are empty
     */
    public static boolean isEmpty(@Nullable ItemStack[] itemStacks) {
        if (itemStacks == null)
            return true;
        for (ItemStack item : itemStacks)
            if (!isEmpty(item)) return false;
        return true;
    }

    public static boolean isEmpty(@Nullable FluidStack[] fluidStacks) {
        if (fluidStacks == null)
            return true;
        for (FluidStack fluid : fluidStacks)
            if (!isEmpty(fluid)) return false;
        return true;
    }

    public static boolean isEmpty(@Nullable IIngredient[] ingredients) {
        if (ingredients == null)
            return true;
        for (IIngredient item : ingredients)
            if (!isEmpty(item)) return false;
        return true;
    }

    public static @NotNull Collection<IIngredient> trim(@Nullable Collection<IIngredient> ingredients) {
        if (ingredients == null) return Collections.emptyList();
        if (ingredients.isEmpty()) return ingredients;
        ingredients.removeIf(IngredientHelper::isEmpty);
        return ingredients;
    }

    public static @NotNull Collection<ItemStack> trimItems(@Nullable Collection<ItemStack> ingredients) {
        if (ingredients == null) return Collections.emptyList();
        if (ingredients.isEmpty()) return ingredients;
        ingredients.removeIf(IngredientHelper::isEmpty);
        return ingredients;
    }

    public static @NotNull Collection<FluidStack> trimFluids(@Nullable Collection<FluidStack> ingredients) {
        if (ingredients == null) return Collections.emptyList();
        if (ingredients.isEmpty()) return ingredients;
        ingredients.removeIf(IngredientHelper::isEmpty);
        return ingredients;
    }

    public static int getRealSize(@Nullable Collection<IIngredient> ingredients) {
        if (ingredients == null || ingredients.isEmpty())
            return 0;
        int size = 0;
        for (IIngredient ingredient : ingredients)
            if (!isEmpty(ingredient))
                size++;
        return size;
    }

    public static int getRealSizeItems(@Nullable Collection<ItemStack> ingredients) {
        if (ingredients == null || ingredients.isEmpty())
            return 0;
        int size = 0;
        for (ItemStack ingredient : ingredients)
            if (!isEmpty(ingredient))
                size++;
        return size;
    }

    public static int getRealSizeFluids(@Nullable Collection<FluidStack> ingredients) {
        if (ingredients == null || ingredients.isEmpty())
            return 0;
        int size = 0;
        for (FluidStack ingredient : ingredients)
            if (!isEmpty(ingredient))
                size++;
        return size;
    }

    /**
     * Useful when the item can be empty or null, but only want to copy non empty items
     */
    public static @NotNull ItemStack copy(@Nullable ItemStack item) {
        return item == null || item == ItemStack.EMPTY ? ItemStack.EMPTY : item.copy();
    }

    @Contract("null -> null")
    public static FluidStack copy(FluidStack fluid) {
        return fluid == null ? null : fluid.copy();
    }

    @Deprecated
    public static String asGroovyCode(ItemStack itemStack, boolean colored) {
        return GroovyScriptCodeConverter.asGroovyCode(itemStack, colored);
    }

    @Deprecated
    public static String asGroovyCode(ItemStack itemStack, boolean colored, boolean prettyNbt) {
        return GroovyScriptCodeConverter.asGroovyCode(itemStack, colored, prettyNbt);
    }

    @Deprecated
    public static String asGroovyCode(FluidStack fluidStack, boolean colored) {
        return GroovyScriptCodeConverter.asGroovyCode(fluidStack, colored);
    }

    @Deprecated
    public static String asGroovyCode(FluidStack fluidStack, boolean colored, boolean prettyNbt) {
        return GroovyScriptCodeConverter.asGroovyCode(fluidStack, colored, prettyNbt);
    }

    @Deprecated
    public static String asGroovyCode(String oreDict, boolean colored) {
        return GroovyScriptCodeConverter.asGroovyCode(oreDict, colored);
    }

    @Deprecated
    public static String asGroovyCode(IBlockState state, boolean colored) {
        return GroovyScriptCodeConverter.asGroovyCode(state, colored);
    }
}
