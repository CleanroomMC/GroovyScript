package com.cleanroommc.groovyscript.compat.inworldcrafting;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import groovy.lang.Closure;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class FluidToItem extends VirtualizedRegistry<FluidToItem.Recipe> {

    @Override
    public void onReload() {
        getScriptedRecipes().forEach(FluidRecipe::remove);
        getBackupRecipes().forEach(FluidRecipe::add);
    }

    public void add(Recipe recipe) {
        addScripted(recipe);
        FluidRecipe.add(recipe);
    }

    public boolean remove(Recipe recipe) {
        if (FluidRecipe.remove(recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    public boolean removeByInput(FluidStack fluid) {
        if (IngredientHelper.isEmpty(fluid)) {
            GroovyLog.msg("Error removing in world fluid to item recipe")
                    .add("input fluid must not be empty")
                    .error()
                    .post();
            return false;
        }
        if (!FluidRecipe.removeIf(fluid.getFluid(), fluidRecipe -> fluidRecipe.getClass() == Recipe.class, fluidRecipe -> addBackup((Recipe) fluidRecipe))) {
            GroovyLog.msg("Error removing in world fluid to item recipe")
                    .add("no recipes found for {}", fluid.getFluid().getName())
                    .error()
                    .post();
            return false;
        }
        return true;
    }

    public boolean removeByInput(FluidStack fluid, ItemStack... input) {
        if (GroovyLog.msg("Error removing in world fluid to item recipe")
                .add(IngredientHelper.isEmpty(fluid), () -> "input fluid must not be empty")
                .add(IngredientHelper.isEmpty(input), () -> "input ingredients must not be empty")
                .error()
                .postIfNotEmpty()) {
            return false;
        }
        if (!FluidRecipe.removeIf(fluid.getFluid(), fluidRecipe -> fluidRecipe.getClass() == Recipe.class && fluidRecipe.matches(input), fluidRecipe -> addBackup((Recipe) fluidRecipe))) {
            GroovyLog.msg("Error removing in world fluid to item recipe")
                    .add("no recipes found for {}", fluid.getFluid().getName())
                    .error()
                    .post();
            return false;
        }
        return true;
    }

    public boolean removeAll() {
        return FluidRecipe.removeIf(fluidRecipe -> fluidRecipe.getClass() == Recipe.class, fluidRecipe -> addBackup((Recipe) fluidRecipe));
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public SimpleObjectStream<Recipe> streamRecipes() {
        return new SimpleObjectStream<>(FluidRecipe.findRecipesOfType(Recipe.class)).setRemover(this::remove);
    }

    public static class Recipe extends FluidRecipe {

        private final ItemStack output;

        public Recipe(Fluid input, IIngredient[] itemInputs, float[] itemConsumeChance, Closure<Boolean> startCondition, Closure<?> afterRecipe, ItemStack output) {
            super(input, itemInputs, itemConsumeChance, startCondition, afterRecipe);
            this.output = output;
        }

        public ItemStack getOutput() {
            return output;
        }

        @Override
        public void setJeiOutput(IIngredients ingredients) {
            ingredients.setOutput(VanillaTypes.ITEM, getOutput());
        }

        @Override
        public void handleRecipeResult(World world, BlockPos pos) {
            world.setBlockToAir(pos);
            InWorldCrafting.spawnItem(world, pos, getOutput().copy());
        }
    }

    public static class RecipeBuilder extends FluidRecipe.RecipeBuilder<FluidToItem.Recipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding in world fluid to item recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, Recipe.MAX_ITEM_INPUT, 1, 1);
            validateFluids(msg, 1, 1, 0, 0);
            validateChances(msg);
        }

        @Override
        public @Nullable FluidToItem.Recipe register() {
            Recipe recipe = new Recipe(this.fluidInput.get(0).getFluid(), this.input.toArray(new IIngredient[0]), this.chances.toFloatArray(),
                                       this.startCondition, this.afterRecipe, this.output.get(0));
            VanillaModule.inWorldCrafting.fluidToItem.add(recipe);
            return recipe;
        }
    }
}
