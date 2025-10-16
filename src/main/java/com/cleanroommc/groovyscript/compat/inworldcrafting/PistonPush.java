package com.cleanroommc.groovyscript.compat.inworldcrafting;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.inworldcrafting.jei.PistonPushRecipeCategory;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import groovy.lang.Closure;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.Optional;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RegistryDescription
public class PistonPush extends StandardListRegistry<PistonPush.PistonPushRecipe> {

    private final List<PistonPushRecipe> pistonPushRecipes = new ArrayList<>();

    @Optional.Method(modid = "jei")
    @GroovyBlacklist
    public List<PistonPushRecipeCategory.RecipeWrapper> getRecipeWrappers() {
        return this.pistonPushRecipes.stream().map(PistonPushRecipeCategory.RecipeWrapper::new).collect(Collectors.toList());
    }

    @Override
    public Collection<PistonPushRecipe> getRecipes() {
        return this.pistonPushRecipes;
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:diamond')).minHarvestLevel(2).maxConversionsPerPush(3)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public static class PistonPushRecipe {

        private final IIngredient input;
        private final ItemStack output;
        private final int maxConversionsPerPush;
        private final int minHarvestLevel;
        private final Closure<Boolean> startCondition;

        public PistonPushRecipe(IIngredient input, ItemStack output, int maxConversionsPerPush, int minHarvestLevel, Closure<Boolean> startCondition) {
            this.input = input;
            this.output = output;
            this.maxConversionsPerPush = maxConversionsPerPush;
            this.minHarvestLevel = minHarvestLevel;
            this.startCondition = startCondition;
        }

        public IIngredient getInput() {
            return input;
        }

        public ItemStack getOutput() {
            return output;
        }

        public int getMaxConversionsPerPush() {
            return maxConversionsPerPush;
        }

        public int getMinHarvestLevel() {
            return minHarvestLevel;
        }

        private boolean tryRecipe(Consumer<EntityItem> entitySpawner, EntityItem entityItem, ItemStack itemStack, IBlockState pushingAgainst) {
            if (!this.input.test(itemStack)) return false;
            if (this.startCondition != null && !ClosureHelper.call(true, this.startCondition, entityItem, itemStack, pushingAgainst)) return false;
            if (this.minHarvestLevel >= 0 && this.minHarvestLevel > pushingAgainst.getBlock().getHarvestLevel(pushingAgainst)) return false;
            ItemStack newStack = this.output.copy();
            if (this.maxConversionsPerPush < itemStack.getCount()) {
                itemStack.shrink(this.maxConversionsPerPush);
                newStack.setCount(this.maxConversionsPerPush);
                entityItem.setItem(itemStack);
                entitySpawner.accept(new EntityItem(entityItem.world, entityItem.posX, entityItem.posY, entityItem.posZ, newStack));
            } else {
                newStack.setCount(itemStack.getCount());
                entityItem.setItem(newStack);
            }
            return true;
        }
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<PistonPushRecipe> {

        @Property(comp = @Comp(gt = 0, lte = 64), defaultValue = "64")
        private int maxConversionsPerPush = 64;
        @Property(defaultValue = "-1")
        private int minHarvestLevel = -1;
        @Property
        private Closure<Boolean> startCondition;

        @RecipeBuilderMethodDescription
        public RecipeBuilder maxConversionsPerPush(int maxConversionsPerPush) {
            this.maxConversionsPerPush = maxConversionsPerPush;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder minHarvestLevel(int minHarvestLevel) {
            this.minHarvestLevel = minHarvestLevel;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder startCondition(Closure<Boolean> beforeRecipe) {
            this.startCondition = beforeRecipe;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding in world piston push recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            if (this.maxConversionsPerPush <= 0 || this.maxConversionsPerPush > 64) {
                GroovyLog.get().warn("Piston push recipe chance should be greater than 0 and equal or less than 64.");
                this.maxConversionsPerPush = MathHelper.clamp(this.maxConversionsPerPush, 1, 64);
            }
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable PistonPush.PistonPushRecipe register() {
            if (!validate()) return null;
            PistonPushRecipe pistonPushRecipe = new PistonPushRecipe(this.input.get(0), this.output.get(0), this.maxConversionsPerPush, this.minHarvestLevel, this.startCondition);
            VanillaModule.INSTANCE.inWorldCrafting.pistonPush.add(pistonPushRecipe);
            return null;
        }
    }

    @GroovyBlacklist
    public void findAndRunRecipe(Consumer<EntityItem> entitySpawner, EntityItem entityItem, IBlockState pushingAgainst) {
        ItemStack itemStack = entityItem.getItem();
        for (PistonPushRecipe pistonPushRecipe : this.pistonPushRecipes) {
            if (pistonPushRecipe.tryRecipe(entitySpawner, entityItem, itemStack, pushingAgainst)) {
                return;
            }
        }
    }
}
