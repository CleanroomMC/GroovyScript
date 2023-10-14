package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.core.mixin.tconstruct.TinkerRegistryAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.smeltery.AlloyRecipe;

import java.util.Arrays;
import java.util.List;

public class Alloying extends VirtualizedRegistry<AlloyRecipe> {

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(TinkerRegistryAccessor.getAlloyRegistry()::remove);
        restoreFromBackup().forEach(TinkerRegistryAccessor.getAlloyRegistry()::add);
    }

    public AlloyRecipe add(FluidStack output, FluidStack... inputs) {
        AlloyRecipe recipe = new AlloyRecipe(output, inputs);
        add(recipe);
        return recipe;
    }

    public void add(AlloyRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        TinkerRegistryAccessor.getAlloyRegistry().add(recipe);
    }

    public boolean remove(AlloyRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        TinkerRegistryAccessor.getAlloyRegistry().remove(recipe);
        return true;
    }

    public boolean removeByOutput(FluidStack output) {
        if (TinkerRegistryAccessor.getAlloyRegistry().removeIf(recipe -> {
            boolean found = recipe.getResult().isFluidEqual(output);
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Tinkers Construct Alloying recipe")
                .add("could not find recipe with output {}", output)
                .error()
                .post();
        return false;
    }

    public boolean removeByInputs(FluidStack... inputs) {
        List<FluidStack> list = Arrays.asList(inputs);
        if (TinkerRegistryAccessor.getAlloyRegistry().removeIf(recipe -> {
            boolean found = recipe.matches(list) > 0;
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Tinkers Construct Alloying recipe")
                .add("could not find recipe with inputs {}", Arrays.asList(inputs))
                .error()
                .post();
        return false;
    }

    public boolean removeByInputsAndOutput(FluidStack output, FluidStack... inputs) {
        List<FluidStack> list = Arrays.asList(inputs);
        if (TinkerRegistryAccessor.getAlloyRegistry().removeIf(recipe -> {
            boolean found = recipe.getResult().isFluidEqual(output) && recipe.matches(list) > 0;
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Tinkers Construct Alloying recipe")
                .add("could not find recipe with inputs {} and output {}", Arrays.asList(inputs), output)
                .error()
                .post();
        return false;
    }

    public void removeAll() {
        TinkerRegistryAccessor.getAlloyRegistry().forEach(this::addBackup);
        TinkerRegistryAccessor.getAlloyRegistry().forEach(TinkerRegistryAccessor.getAlloyRegistry()::remove);
    }

    public SimpleObjectStream<AlloyRecipe> streamRecipes() {
        return new SimpleObjectStream<>(TinkerRegistryAccessor.getAlloyRegistry()).setRemover(this::remove);
    }

    public class RecipeBuilder extends AbstractRecipeBuilder<AlloyRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Tinkers Construct Alloying recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateFluids(msg, 2, fluidInput.size() + 2, 1, 1);
        }

        @Override
        public @Nullable AlloyRecipe register() {
            if (!validate()) return null;
            AlloyRecipe recipe = new AlloyRecipe(fluidOutput.get(0), fluidInput.toArray(new FluidStack[0]));
            add(recipe);
            return recipe;
        }
    }
}
