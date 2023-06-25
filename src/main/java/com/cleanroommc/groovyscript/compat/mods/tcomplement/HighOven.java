package com.cleanroommc.groovyscript.compat.mods.tcomplement;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.recipe.MeltingRecipeBuilder;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.recipe.MeltingRecipeRegistry;
import com.cleanroommc.groovyscript.core.mixin.tcomplement.TCompRegistryAccessor;
import com.cleanroommc.groovyscript.core.mixin.tconstruct.MeltingRecipeAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.google.common.collect.ImmutableMap;
import knightminer.tcomplement.library.steelworks.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

import java.util.Map;

public class HighOven extends MeltingRecipeRegistry {

    public final Fuel fuel = new Fuel();
    public final Heating heating = new Heating();
    public final Mixing mixing = new Mixing();

    public MeltingRecipeBuilder recipeBuilder() {
        return new MeltingRecipeBuilder(this, "Tinkers Complement High Oven override");
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(TCompRegistryAccessor.getHighOvenOverrides()::remove);
        restoreFromBackup().forEach(TCompRegistryAccessor.getHighOvenOverrides()::add);
        fuel.onReload();
        heating.onReload();
        mixing.onReload();
    }

    public MeltingRecipe add(IIngredient input, FluidStack output, int temp) {
        MeltingRecipe recipe = new MeltingRecipe(MeltingRecipeBuilder.recipeMatchFromIngredient(input, output.amount), output, temp);
        add(recipe);
        return recipe;
    }

    public void add(MeltingRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        TCompRegistryAccessor.getHighOvenOverrides().add(recipe);
    }

    public boolean remove(MeltingRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        TCompRegistryAccessor.getHighOvenOverrides().remove(recipe);
        return true;
    }

    public boolean removeByOutput(FluidStack output) {
        if (TCompRegistryAccessor.getHighOvenOverrides().removeIf(recipe -> {
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
        if (TCompRegistryAccessor.getHighOvenOverrides().removeIf(recipe -> {
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
        if (TCompRegistryAccessor.getHighOvenOverrides().removeIf(recipe -> {
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

    public void removeAll() {
        TCompRegistryAccessor.getHighOvenOverrides().forEach(this::addBackup);
        TCompRegistryAccessor.getHighOvenOverrides().forEach(TCompRegistryAccessor.getHighOvenOverrides()::remove);
    }

    public SimpleObjectStream<MeltingRecipe> streamRecipes() {
        return new SimpleObjectStream<>(TCompRegistryAccessor.getHighOvenOverrides()).setRemover(this::remove);
    }

    public static class Mixing extends VirtualizedRegistry<IMixRecipe> {

        public RecipeBuilder recipeBuilder() {
            return new RecipeBuilder();
        }

        @Override
        public void onReload() {
            removeScripted().forEach(TCompRegistryAccessor.getMixRegistry()::remove);
            restoreFromBackup().forEach(TCompRegistryAccessor.getMixRegistry()::add);
        }

        public void add(IMixRecipe recipe) {
            if (recipe == null) return;
            addScripted(recipe);
            TCompRegistryAccessor.getMixRegistry().add(recipe);
        }

        public boolean remove(IMixRecipe recipe) {
            if (recipe == null) return false;
            addBackup(recipe);
            TCompRegistryAccessor.getMixRegistry().remove(recipe);
            return true;
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
            if (TCompRegistryAccessor.getMixRegistry().removeIf(recipe -> {
                HighOvenFilter recipe1 = (recipe instanceof HighOvenFilter) ? (HighOvenFilter) recipe : null;
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
            if (TCompRegistryAccessor.getMixRegistry().removeIf(recipe -> {
                HighOvenFilter recipe1 = (recipe instanceof HighOvenFilter) ? (HighOvenFilter) recipe : null;
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
            if (TCompRegistryAccessor.getMixRegistry().removeIf(recipe -> {
                MixRecipe recipe1 = (recipe instanceof MixRecipe) ? (MixRecipe) recipe : null;
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

        public void removeAll() {
            TCompRegistryAccessor.getMixRegistry().forEach(this::addBackup);
            TCompRegistryAccessor.getMixRegistry().forEach(TCompRegistryAccessor.getMixRegistry()::remove);
        }

        public SimpleObjectStream<IMixRecipe> streamRecipes() {
            return new SimpleObjectStream<>(TCompRegistryAccessor.getMixRegistry()).setRemover(this::remove);
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

    public static class Heating extends VirtualizedRegistry<IHeatRecipe> {

        public RecipeBuilder recipeBuilder() {
            return new RecipeBuilder();
        }

        @Override
        public void onReload() {
            removeScripted().forEach(TCompRegistryAccessor.getHeatRegistry()::remove);
            restoreFromBackup().forEach(TCompRegistryAccessor.getHeatRegistry()::add);
        }

        public IHeatRecipe add(FluidStack input, FluidStack output, int temp) {
            HeatRecipe recipe = new HeatRecipe(input, output, temp);
            add(recipe);
            return recipe;
        }

        public void add(IHeatRecipe recipe) {
            if (recipe == null) return;
            addScripted(recipe);
            TCompRegistryAccessor.getHeatRegistry().add(recipe);
        }

        public boolean remove(IHeatRecipe recipe) {
            if (recipe == null) return false;
            addBackup(recipe);
            TCompRegistryAccessor.getHeatRegistry().remove(recipe);
            return true;
        }

        public boolean removeByInput(FluidStack input) {
            if (TCompRegistryAccessor.getHeatRegistry().removeIf(recipe -> {
                boolean found = recipe.getInput().isFluidEqual(input);
                if (found) addBackup(recipe);
                return found;
            })) return true;

            GroovyLog.msg("Error removing Tinkers Complement High Oven Heating recipe")
                    .add("could not find recipe with input %s", input)
                    .error()
                    .post();
            return false;
        }

        public boolean removeByOutput(FluidStack output) {
            if (TCompRegistryAccessor.getHeatRegistry().removeIf(recipe -> {
                boolean found = recipe.getOutput().isFluidEqual(output);
                if (found) addBackup(recipe);
                return found;
            })) return true;

            GroovyLog.msg("Error removing Tinkers Complement High Oven Heating recipe")
                    .add("could not find recipe with output %s", output)
                    .error()
                    .post();
            return false;
        }

        public boolean removeByInputAndOutput(FluidStack input, FluidStack output) {
            if (TCompRegistryAccessor.getHeatRegistry().removeIf(recipe -> {
                boolean found = recipe.getInput().isFluidEqual(input) && recipe.getOutput().isFluidEqual(output);
                if (found) addBackup(recipe);
                return found;
            })) return true;

            GroovyLog.msg("Error removing Tinkers Complement High Oven Heating recipe")
                    .add("could not find recipe with input %s and output %s", input, output)
                    .error()
                    .post();
            return false;
        }

        public void removeAll() {
            TCompRegistryAccessor.getHeatRegistry().forEach(this::addBackup);
            TCompRegistryAccessor.getHeatRegistry().forEach(TCompRegistryAccessor.getHeatRegistry()::remove);
        }

        public SimpleObjectStream<IHeatRecipe> streamRecipes() {
            return new SimpleObjectStream<>(TCompRegistryAccessor.getHeatRegistry()).setRemover(this::remove);
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

    public static class Fuel extends VirtualizedRegistry<HighOvenFuel> {

        public RecipeBuilder recipeBuilder() {
            return new RecipeBuilder();
        }

        @Override
        @GroovyBlacklist
        public void onReload() {
            removeScripted().forEach(TCompRegistryAccessor.getHighOvenFuels()::remove);
            restoreFromBackup().forEach(TCompRegistryAccessor.getHighOvenFuels()::add);
        }

        public HighOvenFuel add(IIngredient item, int time, int rate) {
            HighOvenFuel fuel = new HighOvenFuel(MeltingRecipeBuilder.recipeMatchFromIngredient(item), time, rate);
            add(fuel);
            return fuel;
        }

        public void add(HighOvenFuel fuel) {
            if (fuel == null) return;
            addScripted(fuel);
            TCompRegistryAccessor.getHighOvenFuels().add(fuel);
        }

        public boolean remove(HighOvenFuel fuel) {
            if (fuel == null) return false;
            addBackup(fuel);
            TCompRegistryAccessor.getHighOvenFuels().remove(fuel);
            return true;
        }

        public boolean removeByItem(IIngredient item) {
            if (TCompRegistryAccessor.getHighOvenFuels().removeIf(recipe -> {
                boolean found = recipe.matches(item.getMatchingStacks()[0]);
                if (found) addBackup(recipe);
                return found;
            })) return true;

            GroovyLog.msg("Error removing Tinkers Complement High Oven fuel")
                    .add("could not find override with item %s", item)
                    .error()
                    .post();
            return false;
        }

        public void removeAll() {
            TCompRegistryAccessor.getHighOvenFuels().forEach(this::addBackup);
            TCompRegistryAccessor.getHighOvenFuels().forEach(TCompRegistryAccessor.getHighOvenFuels()::remove);
        }

        public SimpleObjectStream<HighOvenFuel> streamFuels() {
            return new SimpleObjectStream<>(TCompRegistryAccessor.getHighOvenFuels()).setRemover(this::remove);
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
