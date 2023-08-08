package com.cleanroommc.groovyscript.compat.inworldcrafting;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class FluidRecipe {

    private static final Map<Fluid, List<FluidRecipe>> fluidRecipes = new Object2ObjectOpenHashMap<>();

    public static void add(FluidRecipe fluidRecipe) {
        fluidRecipes.computeIfAbsent(fluidRecipe.input, key -> new ArrayList<>()).add(fluidRecipe);
    }

    public static boolean remove(FluidRecipe fluidRecipe) {
        List<FluidRecipe> fluidRecipes1 = fluidRecipes.get(fluidRecipe.input);
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

    @Nullable
    public static FluidRecipe find(Fluid fluid, World world, BlockPos pos) {
        List<FluidRecipe> candidates = fluidRecipes.get(fluid);
        if (candidates == null || candidates.isEmpty()) return null;
        AxisAlignedBB aabb = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
        List<EntityItem> entitiesInFluid = world.getEntitiesWithinAABB(EntityItem.class, aabb, Entity::isEntityAlive);
        List<ItemContainer> itemsInFluid = new ArrayList<>();
        for (EntityItem item : entitiesInFluid) {
            itemsInFluid.add(new ItemContainer(item));
        }
        for (FluidRecipe recipe : candidates) {
            if (recipe.tryRecipe(itemsInFluid)) {
                return recipe;
            }
        }
        return null;
    }

    private final Fluid input;
    private final IIngredient[] itemInputs;
    private final float[] itemConsumeChance;

    public FluidRecipe(Fluid input, IIngredient[] itemInputs, float[] itemConsumeChance) {
        this.input = input;
        this.itemInputs = itemInputs;
        this.itemConsumeChance = itemConsumeChance;
    }

    private boolean tryRecipe(List<ItemContainer> itemsInFluid) {
        IntSet matchedItems = new IntOpenHashSet();
        main:
        for (int j = 0; j < this.itemInputs.length; j++) {
            IIngredient input = this.itemInputs[j];
            int remaining = input.getAmount();
            for (int i = 0; i < itemsInFluid.size(); i++) {
                if (matchedItems.contains(i)) continue;
                ItemContainer itemInFluid = itemsInFluid.get(i);
                if (input.test(itemInFluid.item)) {
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
                    float chance = this.itemConsumeChance[j];
                    if (chance > 0 && (chance >= 1 || GroovyScript.RND.nextFloat() < chance)) {
                        itemInFluid.amountToKill += amountToKill;
                    }
                    if (remaining == 0) continue main;
                }
            }
            return false;
        }
        itemsInFluid.forEach(ItemContainer::killItems);
        return true;
    }

    public abstract void handleRecipeResult(World world, BlockPos pos);

    @Nullable
    public static Fluid getFluid(IBlockState state) {
        Block block = state.getBlock();

        if (block instanceof IFluidBlock) {
            return ((IFluidBlock) block).getFluid();
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
        private int amountToKill = 0;

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

        public RecipeBuilder<T> input(IIngredient ingredient, float consumeChance) {
            this.input.add(ingredient);
            this.chances.add(MathHelper.clamp(consumeChance, 0f, 1f));
            return this;
        }

        @Override
        public AbstractRecipeBuilder<T> input(IIngredient ingredient) {
            return input(ingredient, 1f);
        }

        protected void validateChances(GroovyLog.Msg msg) {
            if (this.input.size() != this.chances.size()) {
                msg.add("input amount and input chances amount are not equal");
            }
        }
    }
}
