package com.cleanroommc.groovyscript.compat.inworldcrafting;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import org.jetbrains.annotations.Nullable;

public class FluidToItem extends VirtualizedRegistry<FluidToItem.Recipe> {

    public static final FluidToItem INSTANCE = new FluidToItem();

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

        private final ItemStack output;

        public Recipe(Fluid input, IIngredient[] itemInputs, float[] itemConsumeChance, ItemStack output) {
            super(input, itemInputs, itemConsumeChance);
            this.output = output;
        }

        public ItemStack getOutput() {
            return output;
        }

        @Override
        public void handleRecipeResult(World world, BlockPos pos) {
            world.setBlockToAir(pos);
            world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), getOutput().copy()));
        }
    }

    public static class RecipeBuilder extends FluidRecipe.RecipeBuilder<FluidToItem.Recipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding in world fluid to item recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 9, 1, 1);
            validateFluids(msg, 1, 1, 0, 0);
            validateChances(msg);
        }

        @Override
        public @Nullable FluidToItem.Recipe register() {
            Recipe recipe = new Recipe(this.fluidInput.get(0).getFluid(), this.input.toArray(new IIngredient[0]), this.chances.toFloatArray(), this.output.get(0));
            VanillaModule.inWorldCrafting.fluidToItem.add(recipe);
            return recipe;
        }
    }
}
