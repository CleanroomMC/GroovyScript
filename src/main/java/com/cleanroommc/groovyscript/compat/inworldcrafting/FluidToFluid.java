package com.cleanroommc.groovyscript.compat.inworldcrafting;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
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

@RegistryDescription
public class FluidToFluid extends VirtualizedRegistry<FluidToFluid.Recipe> {

    @Override
    public void onReload() {
        getScriptedRecipes().forEach(FluidRecipe::remove);
        getBackupRecipes().forEach(FluidRecipe::add);
    }

    @RecipeBuilderDescription(example = {
            @Example(".fluidInput(fluid('water')).input(item('minecraft:diamond'), item('minecraft:dirt')).fluidOutput(fluid('lava'))"),
            @Example(".fluidInput(fluid('water')).input(item('minecraft:diamond'), item('minecraft:gold_nugget')).fluidOutput(fluid('lava'))"),
            @Example(".fluidInput(fluid('water')).input(item('minecraft:diamond'), item('minecraft:diamond_block')).fluidOutput(fluid('lava')).startCondition({ world, pos -> pos.getY() < 50 })"),
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.add_to_list", priority = 500)
    public void add(Recipe recipe) {
        addScripted(recipe);
        FluidRecipe.add(recipe);
    }

    @MethodDescription(description = "groovyscript.wiki.remove_from_list", priority = 500)
    public boolean remove(Recipe recipe) {
        if (FluidRecipe.remove(recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(example = @Example(value = "fluid('water')", commented = true))
    public boolean removeByInput(FluidStack fluid) {
        if (IngredientHelper.isEmpty(fluid)) {
            GroovyLog.msg("Error removing in world fluid to fluid recipe")
                    .add("input fluid must not be empty")
                    .error()
                    .post();
            return false;
        }
        if (!FluidRecipe.removeIf(fluid.getFluid(), fluidRecipe -> fluidRecipe.getClass() == Recipe.class, fluidRecipe -> addBackup((Recipe) fluidRecipe))) {
            GroovyLog.msg("Error removing in world fluid to fluid recipe")
                    .add("no recipes found for {}", fluid.getFluid().getName())
                    .error()
                    .post();
            return false;
        }
        return true;
    }

    @MethodDescription(example = @Example(value = "fluid('water'), item('minecraft:clay')", commented = true))
    public boolean removeByInput(FluidStack fluid, ItemStack... input) {
        if (GroovyLog.msg("Error removing in world fluid to fluid recipe")
                .add(IngredientHelper.isEmpty(fluid), () -> "input fluid must not be empty")
                .add(IngredientHelper.isEmpty(input), () -> "input ingredients must not be empty")
                .error()
                .postIfNotEmpty()) {
            return false;
        }
        if (!FluidRecipe.removeIf(fluid.getFluid(), fluidRecipe -> fluidRecipe.getClass() == Recipe.class && fluidRecipe.matches(input), fluidRecipe -> addBackup((Recipe) fluidRecipe))) {
            GroovyLog.msg("Error removing in world fluid to fluid recipe")
                    .add("no recipes found for {}", fluid.getFluid().getName())
                    .error()
                    .post();
            return false;
        }
        return true;
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public boolean removeAll() {
        return FluidRecipe.removeIf(fluidRecipe -> fluidRecipe.getClass() == Recipe.class, fluidRecipe -> addBackup((Recipe) fluidRecipe));
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Recipe> streamRecipes() {
        return new SimpleObjectStream<>(FluidRecipe.findRecipesOfType(Recipe.class)).setRemover(this::remove);
    }

    public static class Recipe extends FluidRecipe {

        private final Fluid output;

        public Recipe(Fluid input, IIngredient[] itemInputs, float[] itemConsumeChance, Closure<Boolean> startCondition, Closure<?> afterRecipe, Fluid output) {
            super(input, itemInputs, itemConsumeChance, startCondition, afterRecipe);
            this.output = output;
        }

        public Fluid getOutput() {
            return output;
        }

        @Override
        public void setJeiOutput(IIngredients ingredients) {
            ingredients.setOutput(VanillaTypes.FLUID, new FluidStack(getOutput(), 1000));
        }

        @Override
        public void handleRecipeResult(World world, BlockPos pos) {
            world.setBlockState(pos, getOutput().getBlock().getDefaultState());
        }
    }

    public static class RecipeBuilder extends FluidRecipe.RecipeBuilder<Recipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding in world fluid to fluid recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, Recipe.MAX_ITEM_INPUT, 0, 0);
            validateFluids(msg, 1, 1, 1, 1);
            validateChances(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable Recipe register() {
            Recipe recipe = new Recipe(
                    this.fluidInput.get(0).getFluid(),
                    this.input.toArray(new IIngredient[0]),
                    this.chances.toFloatArray(),
                    this.startCondition,
                    this.afterRecipe,
                    this.fluidOutput.get(0).getFluid());
            VanillaModule.INSTANCE.inWorldCrafting.fluidToFluid.add(recipe);
            return recipe;
        }
    }
}
