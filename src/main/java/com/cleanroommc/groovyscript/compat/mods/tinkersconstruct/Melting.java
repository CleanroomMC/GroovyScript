package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.core.mixin.tconstruct.MeltingRecipeAccessor;
import com.cleanroommc.groovyscript.core.mixin.tconstruct.TinkerRegistryAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

public class Melting extends VirtualizedRegistry<MeltingRecipe> {
    public Melting() {
        super();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(TinkerRegistryAccessor.getMeltingRegistry()::remove);
        restoreFromBackup().forEach(TinkerRegistryAccessor.getMeltingRegistry()::add);
    }

    public MeltingRecipe add(String oreDict, FluidStack output, int temp) {
        MeltingRecipe recipe = new MeltingRecipe(RecipeMatch.of(oreDict), output, temp);
        add(recipe);
        return recipe;
    }

    public MeltingRecipe add(IIngredient input, FluidStack output, int temp) {
        MeltingRecipe recipe = new MeltingRecipe(RecipeMatch.of(input.getMatchingStacks()[0]), output, temp);
        add(recipe);
        return recipe;
    }

    public void add(MeltingRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        TinkerRegistryAccessor.getMeltingRegistry().add(recipe);
    }

    public boolean remove(MeltingRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        TinkerRegistryAccessor.getMeltingRegistry().remove(recipe);
        return true;
    }

    public boolean removeByInput(IIngredient input) {
        if (TinkerRegistryAccessor.getMeltingRegistry().removeIf(recipe -> {
            boolean found = input.test(recipe.input.getInputs().get(0));
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Tinkers Construct Melting recipe")
                .add("could not find recipe with input %s", input)
                .error()
                .post();
        return false;
    }

    public boolean removeByOutput(FluidStack output) {
        if (TinkerRegistryAccessor.getMeltingRegistry().removeIf(recipe -> {
            boolean found = recipe.getResult().isFluidEqual(output);
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Tinkers Construct Melting recipe")
                .add("could not find recipe with output %s", output)
                .error()
                .post();
        return false;
    }

    public boolean removeByInputAndOutput(IIngredient input, FluidStack output) {
        if (TinkerRegistryAccessor.getMeltingRegistry().removeIf(recipe -> {
            boolean found = input.test(recipe.input.getInputs().get(0)) && recipe.getResult().isFluidEqual(output);
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Tinkers Construct Melting recipe")
                .add("could not find recipe with input %s and output %s", input, output)
                .error()
                .post();
        return false;
    }

    public void removeAll() {
        TinkerRegistryAccessor.getMeltingRegistry().forEach(this::addBackup);
        TinkerRegistryAccessor.getMeltingRegistry().forEach(TinkerRegistryAccessor.getMeltingRegistry()::remove);
    }

    public SimpleObjectStream<MeltingRecipe> streamRecipes() {
        return new SimpleObjectStream<>(TinkerRegistryAccessor.getMeltingRegistry()).setRemover(this::remove);
    }

    public class RecipeBuilder extends AbstractRecipeBuilder<MeltingRecipe> {
        private int temp = 300;
        private String oreDict;

        public RecipeBuilder temperature(int temp) {
            this.temp = temp + 300;
            return this;
        }

        public RecipeBuilder time(int time) {
            int t = fluidOutput.get(0) != null ? fluidOutput.get(0).getFluid().getTemperature() : 300;
            this.temp = MeltingRecipeAccessor.invokeCalcTemperature(t, time);
            return this;
        }

        public RecipeBuilder input(String oreDict) {
            this.oreDict = oreDict;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Tinkers Construct Melting recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg, 0, 0, 1, 1);
            msg.add(temp < 1, "Recipe temperature must be at least 1, got " + temp);
        }

        @Override
        public @Nullable MeltingRecipe register() {
            if (!validate()) return null;
            int amount = fluidOutput.get(0).amount;
            RecipeMatch match = oreDict != null && !oreDict.isEmpty() ? RecipeMatch.of(oreDict, amount) : RecipeMatch.of(input.get(0).getMatchingStacks()[0], amount);
            MeltingRecipe recipe = new MeltingRecipe(match, fluidOutput.get(0), temp);
            add(recipe);
            return recipe;
        }
    }
}
