package com.cleanroommc.groovyscript.compat.inworldcrafting;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import org.jetbrains.annotations.Nullable;

public class FluidToFluid extends VirtualizedRegistry<FluidToFluid.Recipe> {

    public static final FluidToFluid INSTANCE = new FluidToFluid();

    @Override
    public void onReload() {
        getScriptedRecipes().forEach(FluidRecipe::remove);
        getBackupRecipes().forEach(FluidRecipe::remove);
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

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public SimpleObjectStream<Recipe> streamRecipes() {
        return new SimpleObjectStream<>(FluidRecipe.findRecipesOfType(Recipe.class)).setRemover(this::remove);
    }

    public static class Recipe extends FluidRecipe {

        private final Fluid output;

        public Recipe(Fluid input, IIngredient[] itemInputs, float[] itemConsumeChance, Fluid output) {
            super(input, itemInputs, itemConsumeChance);
            this.output = output;
        }

        public Fluid getOutput() {
            return output;
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
            validateItems(msg, 1, 9, 0, 0);
            validateFluids(msg, 1, 1, 1, 1);
            validateChances(msg);
        }

        @Override
        public @Nullable Recipe register() {
            Recipe recipe = new Recipe(this.fluidInput.get(0).getFluid(), this.input.toArray(new IIngredient[0]), this.chances.toFloatArray(), this.fluidOutput.get(0).getFluid());
            VanillaModule.inWorldCrafting.fluidToFluid.add(recipe);
            return recipe;
        }
    }
}
