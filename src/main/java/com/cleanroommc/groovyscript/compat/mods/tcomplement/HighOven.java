package com.cleanroommc.groovyscript.compat.mods.tcomplement;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.recipe.MeltingRecipeBuilder;
import com.cleanroommc.groovyscript.core.mixin.tcomplement.TCompRegistryAccessor;
import com.cleanroommc.groovyscript.core.mixin.tconstruct.MeltingRecipeAccessor;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import com.google.common.collect.ImmutableMap;
import knightminer.tcomplement.library.steelworks.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

import java.util.Collection;
import java.util.Map;

public class HighOven extends StandardListRegistry<MeltingRecipe> {

    public final Fuel fuel = new Fuel();
    public final Heating heating = new Heating();
    public final Mixing mixing = new Mixing();

    public MeltingRecipeBuilder recipeBuilder() {
        return new MeltingRecipeBuilder(this, "Tinkers Complement High Oven override");
    }

    @Override
    public Collection<MeltingRecipe> getRecipes() {
        return TCompRegistryAccessor.getHighOvenOverrides();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        super.onReload();
        fuel.onReload();
        heating.onReload();
        mixing.onReload();
    }

    public MeltingRecipe add(IIngredient input, FluidStack output, int temp) {
        MeltingRecipe recipe = new MeltingRecipe(MeltingRecipeBuilder.recipeMatchFromIngredient(input, output.amount), output, temp);
        add(recipe);
        return recipe;
    }

    public boolean removeByOutput(FluidStack output) {
        if (getRecipes().removeIf(recipe -> {
            boolean found = recipe.output.isFluidEqual(output);
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Tinkers Complement High Oven override")
                .add("could not find override with output {}", output)
                .error()
                .post();
        return false;
    }

    public boolean removeByInput(IIngredient input) {
        NonNullList<ItemStack> list = NonNullList.from(ItemStack.EMPTY, input.getMatchingStacks());
        if (getRecipes().removeIf(recipe -> {
            boolean found = recipe.input.matches(list).isPresent();
            if (found) addBackup(recipe);
            list.clear();
            return found;
        })) return true;

        list.clear();
        GroovyLog.msg("Error removing Tinkers Complement High Oven override")
                .add("could not find override with input {}", input)
                .error()
                .post();
        return false;
    }

    public boolean removeByInputAndOutput(IIngredient input, FluidStack output) {
        NonNullList<ItemStack> list = NonNullList.from(ItemStack.EMPTY, input.getMatchingStacks());
        if (getRecipes().removeIf(recipe -> {
            boolean found = recipe.input.matches(list).isPresent() && recipe.output.isFluidEqual(output);
            if (found) addBackup(recipe);
            list.clear();
            return found;
        })) return true;

        list.clear();
        GroovyLog.msg("Error removing Tinkers Complement High Oven override")
                .add("could not find override with input {} and output {]", input, output)
                .error()
                .post();
        return false;
    }

    public static class Mixing extends StandardListRegistry<IMixRecipe> {

        public RecipeBuilder recipeBuilder() {
            return new RecipeBuilder();
        }

        @Override
        public Collection<IMixRecipe> getRecipes() {
            return TCompRegistryAccessor.getMixRegistry();
        }

        public boolean removeByOutput(FluidStack output) {
            if (TCompRegistryAccessor.getMixRegistry().removeIf(recipe -> {
                boolean found = recipe.getOutput().isFluidEqual(output);
                if (found) addBackup(recipe);
                return found;
            })) return true;

            GroovyLog.msg("Error removing Tinkers Complement High Oven Mixing recipe")
                    .add("could not find recipe with output {}", output)
                    .error()
                    .post();
            return false;
        }

        public boolean removeByInput(FluidStack input) {
            if (getRecipes().removeIf(recipe -> {
                HighOvenFilter recipe1 = (recipe instanceof HighOvenFilter highOvenFilter) ? highOvenFilter : null;
                boolean found = recipe1 != null && recipe1.getInput().isFluidEqual(input);
                if (found) addBackup(recipe);
                return found;
            })) return true;

            GroovyLog.msg("Error removing Tinkers Complement High Oven Mixing recipe")
                    .add("could not find recipe with input {}", input)
                    .error()
                    .post();
            return false;
        }

        public boolean removeByInputAndOutput(FluidStack input, FluidStack output) {
            if (getRecipes().removeIf(recipe -> {
                HighOvenFilter recipe1 = (recipe instanceof HighOvenFilter highOvenFilter) ? highOvenFilter : null;
                boolean found = recipe1 != null && recipe1.getInput().isFluidEqual(input) && recipe1.getOutput().isFluidEqual(output);
                if (found) addBackup(recipe);
                return found;
            })) return true;

            GroovyLog.msg("Error removing Tinkers Complement High Oven Mixing recipe")
                    .add("could not find recipe with input {} and output {}", input, output)
                    .error()
                    .post();
            return false;
        }

        public boolean removeByAdditives(Map<MixAdditive, IIngredient> additives) {
            if (getRecipes().removeIf(recipe -> {
                MixRecipe recipe1 = (recipe instanceof MixRecipe mixRecipe) ? mixRecipe : null;
                if (recipe1 != null) {
                    for (Map.Entry<MixAdditive, IIngredient> entry : additives.entrySet()) {
                        if (recipe1.getAdditives(entry.getKey()).contains(entry.getValue().getMatchingStacks()[0])) {
                            addBackup(recipe);
                            return true;
                        }
                    }
                }
                return false;
            })) return true;

            GroovyLog.msg("Error removing Tinkers Complement High Oven Mixing recipe")
                    .add("could not find override with additives {}", additives.values())
                    .error()
                    .post();
            return false;
        }

        public boolean removeByAdditive(MixAdditive type, IIngredient item) {
            return removeByAdditives(ImmutableMap.of(type, item));
        }

        public class RecipeBuilder extends AbstractRecipeBuilder<MixRecipe> {

            private int temp = 300;
            private RecipeMatch oxidizer;
            private RecipeMatch reducer;
            private RecipeMatch purifier;

            public RecipeBuilder temperature(int temp) {
                this.temp = temp + 300;
                return this;
            }

            public RecipeBuilder time(int time) {
                int ti = fluidInput.get(0) != null ? fluidInput.get(0).getFluid().getTemperature() : 300;
                int to = fluidOutput.get(0) != null ? fluidOutput.get(0).getFluid().getTemperature() : 300;
                this.temp = MeltingRecipeAccessor.invokeCalcTemperature(Math.max(ti, to), time);
                return this;
            }

            public RecipeBuilder oxidizer(IIngredient ingredient, int chance) {
                this.oxidizer = MeltingRecipeBuilder.recipeMatchFromIngredient(ingredient, chance);
                return this;
            }

            public RecipeBuilder reducer(IIngredient ingredient, int chance) {
                this.reducer = MeltingRecipeBuilder.recipeMatchFromIngredient(ingredient, chance);
                return this;
            }

            public RecipeBuilder purifier(IIngredient ingredient, int chance) {
                this.purifier = MeltingRecipeBuilder.recipeMatchFromIngredient(ingredient, chance);
                return this;
            }

            public RecipeBuilder oxidizer(IIngredient ingredient) {
                return oxidizer(ingredient, 0);
            }

            public RecipeBuilder reducer(IIngredient ingredient) {
                return reducer(ingredient, 0);
            }

            public RecipeBuilder purifier(IIngredient ingredient) {
                return purifier(ingredient, 0);
            }

            @Override
            public String getErrorMsg() {
                return "Error adding Tinkers Complement High Oven Mixing recipe";
            }

            @Override
            public void validate(GroovyLog.Msg msg) {
                validateFluids(msg, 1, 1, 1, 1);
                msg.add(oxidizer == null && reducer == null && purifier == null, "Expected at least one additive, but found none!");
            }

            @Override
            public @Nullable MixRecipe register() {
                if (!validate()) return null;
                MixRecipe recipe = new MixRecipe(fluidInput.get(0), fluidOutput.get(0), temp);
                if (oxidizer != null) recipe.addAdditive(MixAdditive.OXIDIZER, oxidizer);
                if (reducer != null) recipe.addAdditive(MixAdditive.REDUCER, reducer);
                if (purifier != null) recipe.addAdditive(MixAdditive.PURIFIER, purifier);
                add(recipe);
                return recipe;
            }
        }
    }

    public static class Heating extends StandardListRegistry<IHeatRecipe> {

        public RecipeBuilder recipeBuilder() {
            return new RecipeBuilder();
        }

        @Override
        public Collection<IHeatRecipe> getRecipes() {
            return TCompRegistryAccessor.getHeatRegistry();
        }

        public IHeatRecipe add(FluidStack input, FluidStack output, int temp) {
            HeatRecipe recipe = new HeatRecipe(input, output, temp);
            add(recipe);
            return recipe;
        }

        public boolean removeByInput(FluidStack input) {
            if (getRecipes().removeIf(recipe -> {
                boolean found = recipe.getInput().isFluidEqual(input);
                if (found) addBackup(recipe);
                return found;
            })) return true;

            GroovyLog.msg("Error removing Tinkers Complement High Oven Heating recipe")
                    .add("could not find recipe with input {}", input)
                    .error()
                    .post();
            return false;
        }

        public boolean removeByOutput(FluidStack output) {
            if (getRecipes().removeIf(recipe -> {
                boolean found = recipe.getOutput().isFluidEqual(output);
                if (found) addBackup(recipe);
                return found;
            })) return true;

            GroovyLog.msg("Error removing Tinkers Complement High Oven Heating recipe")
                    .add("could not find recipe with output {}", output)
                    .error()
                    .post();
            return false;
        }

        public boolean removeByInputAndOutput(FluidStack input, FluidStack output) {
            if (getRecipes().removeIf(recipe -> {
                boolean found = recipe.getInput().isFluidEqual(input) && recipe.getOutput().isFluidEqual(output);
                if (found) addBackup(recipe);
                return found;
            })) return true;

            GroovyLog.msg("Error removing Tinkers Complement High Oven Heating recipe")
                    .add("could not find recipe with input {} and output {}", input, output)
                    .error()
                    .post();
            return false;
        }

        public class RecipeBuilder extends AbstractRecipeBuilder<IHeatRecipe> {

            private int temp = 300;

            public RecipeBuilder temperature(int temp) {
                this.temp = temp + 300;
                return this;
            }

            public RecipeBuilder time(int time) {
                int t = fluidOutput.get(0) != null ? fluidOutput.get(0).getFluid().getTemperature() : 300;
                this.temp = MeltingRecipeAccessor.invokeCalcTemperature(t, time);
                return this;
            }

            @Override
            public String getErrorMsg() {
                return "Error adding Tinkers Complement High Oven Heating recipe";
            }

            @Override
            public void validate(GroovyLog.Msg msg) {
                validateFluids(msg, 1, 1, 1, 1);
            }

            @Override
            public @Nullable IHeatRecipe register() {
                if (!validate()) return null;
                HeatRecipe recipe = new HeatRecipe(fluidInput.get(0), fluidOutput.get(0), temp);
                add(recipe);
                return recipe;
            }
        }
    }

    public static class Fuel extends StandardListRegistry<HighOvenFuel> {

        public RecipeBuilder recipeBuilder() {
            return new RecipeBuilder();
        }

        @Override
        public Collection<HighOvenFuel> getRecipes() {
            return TCompRegistryAccessor.getHighOvenFuels();
        }

        public HighOvenFuel add(IIngredient item, int time, int rate) {
            HighOvenFuel fuel = new HighOvenFuel(MeltingRecipeBuilder.recipeMatchFromIngredient(item), time, rate);
            add(fuel);
            return fuel;
        }

        public boolean removeByItem(IIngredient item) {
            if (getRecipes().removeIf(recipe -> {
                boolean found = recipe.matches(item.getMatchingStacks()[0]);
                if (found) addBackup(recipe);
                return found;
            })) return true;

            GroovyLog.msg("Error removing Tinkers Complement High Oven fuel")
                    .add("could not find override with item {}", item)
                    .error()
                    .post();
            return false;
        }

        public class RecipeBuilder extends AbstractRecipeBuilder<HighOvenFuel> {

            private int time = 1;
            private int rate = 1;

            public RecipeBuilder time(int time) {
                this.time = Math.max(time, 1);
                return this;
            }

            public RecipeBuilder rate(int rate) {
                this.rate = Math.max(rate, 1);
                return this;
            }

            @Override
            public String getErrorMsg() {
                return "Error adding Tinkers Complement High Oven fuel";
            }

            @Override
            public void validate(GroovyLog.Msg msg) {
                validateItems(msg, 1, 1, 0, 0);
            }

            @Override
            public @Nullable HighOvenFuel register() {
                if (!validate()) return null;
                HighOvenFuel fuel = new HighOvenFuel(MeltingRecipeBuilder.recipeMatchFromIngredient(input.get(0)), time, rate);
                add(fuel);
                return fuel;
            }
        }
    }
}
