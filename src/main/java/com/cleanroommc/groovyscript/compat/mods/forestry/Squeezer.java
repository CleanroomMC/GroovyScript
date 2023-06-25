package com.cleanroommc.groovyscript.compat.mods.forestry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.core.mixin.forestry.SqueezerRecipeManagerAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import forestry.api.recipes.ISqueezerRecipe;
import forestry.factory.recipes.SqueezerRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class Squeezer extends ForestryRegistry<ISqueezerRecipe> {

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        if (!isEnabled()) return;
        removeScripted().forEach(SqueezerRecipeManagerAccessor.getRecipes()::remove);
        restoreFromBackup().forEach(SqueezerRecipeManagerAccessor.getRecipes()::add);
    }

    public ISqueezerRecipe add(FluidStack output, IIngredient remnant, int time, int remnantChance, IIngredient... inputs) {
        ISqueezerRecipe recipe = new SqueezerRecipe(time, NonNullList.from(ItemStack.EMPTY, Arrays.stream(inputs).map(i -> i.getMatchingStacks()[0]).toArray(ItemStack[]::new)), output, remnant.getMatchingStacks()[0], remnantChance);
        add(recipe);
        return recipe;
    }

    public void add(ISqueezerRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        SqueezerRecipeManagerAccessor.getRecipes().add(recipe);
    }

    public boolean remove(ISqueezerRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        return SqueezerRecipeManagerAccessor.getRecipes().remove(recipe);
    }

    public boolean removeByOutput(FluidStack output) {
        if (SqueezerRecipeManagerAccessor.getRecipes().removeIf(recipe -> {
            boolean found = recipe.getFluidOutput().isFluidEqual(output);
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Squeezer recipe")
                .add("could not find recipe with output {}", output)
                .error()
                .post();
        return false;
    }

    public boolean removeByInputs(IIngredient... input) {
        Set<ItemStack> inputs = Arrays.stream(input).map(i -> i.getMatchingStacks()[0]).collect(Collectors.toSet());
        if (SqueezerRecipeManagerAccessor.getRecipes().removeIf(recipe -> {
            boolean found = inputs.containsAll(recipe.getResources());
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Squeezer recipe")
                .add("could not find recipe with inputs {}", inputs)
                .error()
                .post();
        return false;
    }

    public boolean removeByInput(IIngredient... input) {
        return removeByInputs(input);
    }

    public void removeAll() {
        SqueezerRecipeManagerAccessor.getRecipes().forEach(this::addBackup);
        SqueezerRecipeManagerAccessor.getRecipes().clear();
    }

    public SimpleObjectStream<ISqueezerRecipe> streamRecipes() {
        return new SimpleObjectStream<>(SqueezerRecipeManagerAccessor.getRecipes()).setRemover(this::remove);
    }

    public class RecipeBuilder extends AbstractRecipeBuilder<ISqueezerRecipe> {

        protected int time = 20;
        protected float chance = 1.0F;

        public RecipeBuilder time(int time) {
            this.time = Math.max(time, 1);
            return this;
        }

        public RecipeBuilder chance(float chance) {
            this.chance = Math.max(chance, 0.01F);
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Forestry Squeezer recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateFluids(msg, 0, 0, 1, 1);
            validateItems(msg, 1, 9, 0, 1);
        }

        @Override
        public @Nullable ISqueezerRecipe register() {
            if (!validate()) return null;
            NonNullList<ItemStack> list = NonNullList.from(ItemStack.EMPTY, input.stream().map(i -> i.getMatchingStacks()[0]).toArray(ItemStack[]::new));
            ISqueezerRecipe recipe = new SqueezerRecipe(time, list, fluidOutput.get(0), output.getOrEmpty(0), chance);
            add(recipe);
            return recipe;
        }
    }
}
