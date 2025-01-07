package com.cleanroommc.groovyscript.compat.inworldcrafting;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import groovy.lang.Closure;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.Optional;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class FluidRecipe {

    public static final int MAX_ITEM_INPUT = 9;

    private static final Map<String, List<FluidRecipe>> fluidRecipes = new Object2ObjectOpenHashMap<>();

    public static void add(FluidRecipe fluidRecipe) {
        fluidRecipes.computeIfAbsent(fluidRecipe.input.getName(), key -> new ArrayList<>()).add(fluidRecipe);
    }

    public static boolean remove(FluidRecipe fluidRecipe) {
        List<FluidRecipe> fluidRecipes1 = fluidRecipes.get(fluidRecipe.input.getName());
        if (fluidRecipes1 != null) {
            return fluidRecipes1.remove(fluidRecipe);
        }
        return false;
    }

    public static <T extends FluidRecipe> List<T> findRecipesOfType(Class<T> clazz) {
        List<T> recipes = new ArrayList<>();
        fluidRecipes.values().forEach(fluidRecipes1 -> fluidRecipes1.forEach(fluidRecipe -> {
            if (fluidRecipe.getClass() == clazz) {
                recipes.add((T) fluidRecipe);
            }
        }));
        return recipes;
    }

    public static boolean removeIf(Fluid fluid, Predicate<FluidRecipe> fluidRecipePredicate, Consumer<FluidRecipe> removedConsumer) {
        List<FluidRecipe> recipes = fluidRecipes.get(fluid.getName());
        return recipes != null && recipes.removeIf(fluidRecipe -> {
            if (fluidRecipePredicate.test(fluidRecipe)) {
                removedConsumer.accept(fluidRecipe);
                return true;
            }
            return false;
        });
    }

    public static boolean removeIf(Predicate<FluidRecipe> fluidRecipePredicate, Consumer<FluidRecipe> removedConsumer) {
        AtomicBoolean successful = new AtomicBoolean(false);
        fluidRecipes.forEach((fluid, fluidRecipes1) -> {
            if (fluidRecipes1.removeIf(fluidRecipe -> {
                if (fluidRecipePredicate.test(fluidRecipe)) {
                    removedConsumer.accept(fluidRecipe);
                    return true;
                }
                return false;
            })) {
                successful.set(true);
            }
        });
        return successful.get();
    }

    public static void forEach(Consumer<FluidRecipe> consumer) {
        fluidRecipes.values().forEach(list -> list.forEach(consumer));
    }

    /**
     * Tries to find a fluid conversion recipe for a fluid at a position in the world
     *
     * @return fluid recipe or null if non is found
     */
    @GroovyBlacklist
    public static boolean findAndRunRecipe(Fluid fluid, World world, BlockPos pos, IBlockState blockState) {
        List<FluidRecipe> candidates = fluidRecipes.get(fluid.getName());
        if (candidates == null || candidates.isEmpty()) return false;
        AxisAlignedBB aabb = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
        // get all items in the fluid block space
        List<EntityItem> entitiesInFluid = world.getEntitiesWithinAABB(EntityItem.class, aabb, Entity::isEntityAlive);
        List<ItemContainer> itemsInFluid = new ArrayList<>();
        for (EntityItem item : entitiesInFluid) {
            itemsInFluid.add(new ItemContainer(item));
        }
        // search for a recipe using those items
        for (FluidRecipe recipe : candidates) {
            if (recipe.tryRecipe(world, pos, itemsInFluid)) {
                return true;
            }
        }
        return false;
    }

    private final Fluid input;
    private final IIngredient[] itemInputs;
    private final float[] itemConsumeChance;
    private final Closure<Boolean> startCondition;
    private final Closure<?> afterRecipe;

    public FluidRecipe(Fluid input, IIngredient[] itemInputs, float[] itemConsumeChance, Closure<Boolean> startCondition, Closure<?> afterRecipe) {
        this.input = input;
        this.itemInputs = itemInputs;
        this.itemConsumeChance = itemConsumeChance;
        this.startCondition = startCondition;
        this.afterRecipe = afterRecipe;
    }

    public Fluid getFluidInput() {
        return input;
    }

    public IIngredient[] getItemInputs() {
        return itemInputs;
    }

    public float[] getItemConsumeChance() {
        return itemConsumeChance;
    }

    @Optional.Method(modid = "jei")
    public abstract void setJeiOutput(IIngredients ingredients);

    public boolean matches(ItemStack[] input) {
        if (input.length != this.itemInputs.length) return false;
        IntSet used = new IntOpenHashSet();
        main:
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input.length; j++) {
                if (used.contains(j)) continue;
                if (this.itemInputs[i].test(input[j])) {
                    used.add(j);
                    continue main;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Tries a recipe and also kills the input items if this recipe matches
     *
     * @param itemsInFluid all items that are in the fluid block space
     * @return if this recipe matched the input
     */
    @GroovyBlacklist
    private boolean tryRecipe(World world, BlockPos pos, List<ItemContainer> itemsInFluid) {
        IntSet matchedItems = new IntOpenHashSet();
        main:
        for (int j = 0; j < this.itemInputs.length; j++) {
            IIngredient input = this.itemInputs[j];
            int remaining = input.getAmount();
            for (int i = 0; i < itemsInFluid.size(); i++) {
                if (matchedItems.contains(i)) continue; // this item already is used
                ItemContainer itemInFluid = itemsInFluid.get(i);
                if (input.test(itemInFluid.item)) { // found matching item
                    // calculate how many items should be killed and what's left of the ingredient
                    int count = itemInFluid.item.getCount() - itemInFluid.amountToKill;
                    int amountToKill;
                    if (count < input.getAmount()) {
                        remaining -= count;
                        amountToKill = count;
                    } else {
                        amountToKill = remaining;
                        remaining = 0;
                        matchedItems.add(i);
                    }
                    // applies the amount to kill
                    float chance = this.itemConsumeChance[j];
                    if (chance > 0 && (chance >= 1 || GroovyScript.RND.nextFloat() < chance)) {
                        itemInFluid.amountToKill += amountToKill;
                    }
                    if (remaining == 0) continue main;
                }
            }
            return false;
        }
        if (this.startCondition != null && !ClosureHelper.call(true, this.startCondition, world, pos)) {
            return false;
        }
        // kill all items with the before calculated amount
        itemsInFluid.forEach(ItemContainer::killItems);
        // handle the output of the recipe
        handleRecipeResult(world, pos);
        if (this.afterRecipe != null) {
            ClosureHelper.call(this.afterRecipe, world, pos);
        }
        return true;
    }

    /**
     * Called after a valid recipe has been found and the input items are killed.
     *
     * @param world world
     * @param pos   pos of fluid
     */
    public abstract void handleRecipeResult(World world, BlockPos pos);

    public static @Nullable Fluid getFluid(IBlockState state) {
        Block block = state.getBlock();

        if (block instanceof IFluidBlock iFluidBlock) {
            return iFluidBlock.getFluid();
        }
        if (block instanceof BlockLiquid) {
            if (state.getMaterial() == Material.WATER) {
                return FluidRegistry.WATER;
            }
            if (state.getMaterial() == Material.LAVA) {
                return FluidRegistry.LAVA;
            }
        }
        return null;
    }

    public static boolean isSourceBlock(IBlockState blockState) {
        return blockState.getValue(BlockFluidBase.LEVEL) == 0;
    }

    private static class ItemContainer {

        private final EntityItem entityItem;
        private final ItemStack item;
        private int amountToKill;

        private ItemContainer(EntityItem entityItem) {
            this.entityItem = entityItem;
            this.item = entityItem.getItem();
        }

        private void killItems() {
            if (amountToKill > 0) {
                if (amountToKill >= item.getCount()) {
                    entityItem.setDead();
                } else {
                    item.shrink(amountToKill);
                    entityItem.setItem(item);
                }
            }
        }
    }

    public abstract static class RecipeBuilder<T extends FluidRecipe> extends AbstractRecipeBuilder<T> {

        protected final FloatList chances = new FloatArrayList();
        protected Closure<Boolean> startCondition;
        protected Closure<?> afterRecipe;

        public RecipeBuilder<T> input(IIngredient ingredient, float consumeChance) {
            this.input.add(ingredient);
            this.chances.add(MathHelper.clamp(consumeChance, 0.0f, 1.0f));
            return this;
        }

        @Override
        public AbstractRecipeBuilder<T> input(IIngredient ingredient) {
            return input(ingredient, 1.0f);
        }

        public RecipeBuilder<T> startCondition(Closure<Boolean> startCondition) {
            this.startCondition = startCondition;
            return this;
        }

        public RecipeBuilder<T> afterRecipe(Closure<Boolean> afterRecipe) {
            this.afterRecipe = afterRecipe;
            return this;
        }

        protected void validateChances(GroovyLog.Msg msg) {
            if (this.input.size() != this.chances.size()) {
                msg.add("input amount and input chances amount are not equal");
            }
        }
    }
}
