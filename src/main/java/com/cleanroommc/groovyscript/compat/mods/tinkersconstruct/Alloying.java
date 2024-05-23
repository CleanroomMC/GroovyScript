package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.core.mixin.tconstruct.TinkerRegistryAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.smeltery.AlloyRecipe;

import java.util.Arrays;
import java.util.List;

@RegistryDescription
public class Alloying extends VirtualizedRegistry<AlloyRecipe> {

    @RecipeBuilderDescription(example = @Example(".fluidOutput(fluid('iron') * 3).fluidInput(fluid('clay') * 1,fluid('lava') * 2)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(TinkerRegistryAccessor.getAlloyRegistry()::remove);
        restoreFromBackup().forEach(TinkerRegistryAccessor.getAlloyRegistry()::add);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("fluid('lava') * 144, fluid('water') * 500, fluid('iron') * 5, fluid('clay') * 60"))
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

    @MethodDescription(example = @Example("fluid('pigiron')"))
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

    @MethodDescription(description = "groovyscript.wiki.tconstruct.alloying.removeByInputs", example = @Example("fluid('cobalt')*2,fluid('ardite')*2"))
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

    @MethodDescription(example = @Example("fluid('knightslime')*72,fluid('iron')*72,fluid('stone')*144,fluid('purpleslime')*125"))
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

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        TinkerRegistryAccessor.getAlloyRegistry().forEach(this::addBackup);
        TinkerRegistryAccessor.getAlloyRegistry().clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<AlloyRecipe> streamRecipes() {
        return new SimpleObjectStream<>(TinkerRegistryAccessor.getAlloyRegistry()).setRemover(this::remove);
    }

    @Property(property = "fluidInput", valid = {@Comp(value = "2", type = Comp.Type.GTE), @Comp(value = "Integer.MAX_VALUE", type = Comp.Type.LTE)})
    @Property(property = "fluidOutput", valid = @Comp("1"))
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
        @RecipeBuilderRegistrationMethod
        public @Nullable AlloyRecipe register() {
            if (!validate()) return null;
            AlloyRecipe recipe = new AlloyRecipe(fluidOutput.get(0), fluidInput.toArray(new FluidStack[0]));
            add(recipe);
            return recipe;
        }
    }
}
