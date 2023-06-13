package com.cleanroommc.groovyscript.compat.mods.evilcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.cyclops.cyclopscore.recipe.custom.api.IRecipe;
import org.cyclops.evilcraft.block.EnvironmentalAccumulatorConfig;
import org.cyclops.evilcraft.core.recipe.custom.EnvironmentalAccumulatorRecipeComponent;
import org.cyclops.evilcraft.core.recipe.custom.EnvironmentalAccumulatorRecipeProperties;
import org.cyclops.evilcraft.core.weather.WeatherType;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class EnvironmentalAccumulator extends VirtualizedRegistry<IRecipe<EnvironmentalAccumulatorRecipeComponent, EnvironmentalAccumulatorRecipeComponent, EnvironmentalAccumulatorRecipeProperties>> {

    public EnvironmentalAccumulator() {
        super();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(org.cyclops.evilcraft.block.EnvironmentalAccumulator.getInstance().getRecipeRegistry().allRecipes()::remove);
        restoreFromBackup().forEach(org.cyclops.evilcraft.block.EnvironmentalAccumulator.getInstance().getRecipeRegistry().allRecipes()::add);
    }

    public void add(IRecipe<EnvironmentalAccumulatorRecipeComponent, EnvironmentalAccumulatorRecipeComponent, EnvironmentalAccumulatorRecipeProperties> recipe) {
        this.add(recipe, true);
    }

    public void add(IRecipe<EnvironmentalAccumulatorRecipeComponent, EnvironmentalAccumulatorRecipeComponent, EnvironmentalAccumulatorRecipeProperties> recipe, boolean add) {
        if (recipe == null) return;
        addScripted(recipe);
        if (add) org.cyclops.evilcraft.block.EnvironmentalAccumulator.getInstance().getRecipeRegistry().allRecipes().add(recipe);
    }

    public boolean remove(IRecipe<EnvironmentalAccumulatorRecipeComponent, EnvironmentalAccumulatorRecipeComponent, EnvironmentalAccumulatorRecipeProperties> recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        org.cyclops.evilcraft.block.EnvironmentalAccumulator.getInstance().getRecipeRegistry().allRecipes().remove(recipe);
        return true;
    }

    public boolean removeByInput(ItemStack input) {
        return org.cyclops.evilcraft.block.EnvironmentalAccumulator.getInstance().getRecipeRegistry().allRecipes().removeIf(r -> {
            if (r.getInput().getIngredient().test(input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    public boolean removeByOutput(ItemStack input) {
        return org.cyclops.evilcraft.block.EnvironmentalAccumulator.getInstance().getRecipeRegistry().allRecipes().removeIf(r -> {
            if (r.getOutput().getIngredient().test(input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    public void removeAll() {
        org.cyclops.evilcraft.block.EnvironmentalAccumulator.getInstance().getRecipeRegistry().allRecipes().forEach(this::addBackup);
        org.cyclops.evilcraft.block.EnvironmentalAccumulator.getInstance().getRecipeRegistry().allRecipes().clear();
    }

    public SimpleObjectStream<IRecipe<EnvironmentalAccumulatorRecipeComponent, EnvironmentalAccumulatorRecipeComponent, EnvironmentalAccumulatorRecipeProperties>> streamRecipes() {
        return new SimpleObjectStream<>(org.cyclops.evilcraft.block.EnvironmentalAccumulator.getInstance().getRecipeRegistry().allRecipes())
                .setRemover(this::remove);
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<IRecipe<EnvironmentalAccumulatorRecipeComponent, EnvironmentalAccumulatorRecipeComponent, EnvironmentalAccumulatorRecipeProperties>> {

        private double processingspeed = EnvironmentalAccumulatorConfig.defaultProcessItemSpeed;
        private int cooldowntime = EnvironmentalAccumulatorConfig.defaultTickCooldown;
        private int duration = EnvironmentalAccumulatorConfig.defaultProcessItemTickCount;
        private WeatherType inputWeather;
        private WeatherType outputWeather;

        public RecipeBuilder cooldowntime(int cooldowntime) {
            this.cooldowntime = cooldowntime;
            return this;
        }

        public RecipeBuilder cooldown(int cooldowntime) {
            this.cooldowntime = cooldowntime;
            return this;
        }

        public RecipeBuilder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public RecipeBuilder processingspeed(double processingspeed) {
            this.processingspeed = processingspeed;
            return this;
        }

        public RecipeBuilder speed(double processingspeed) {
            this.processingspeed = processingspeed;
            return this;
        }

        public RecipeBuilder inputWeather(String inputWeather) {
            this.inputWeather = WeatherType.valueOf(inputWeather.toUpperCase(Locale.ROOT));
            return this;
        }

        public RecipeBuilder inputWeather(WeatherType inputWeather) {
            this.inputWeather = inputWeather;
            return this;
        }

        public RecipeBuilder outputWeather(String outputWeather) {
            this.outputWeather = WeatherType.valueOf(outputWeather.toUpperCase(Locale.ROOT));
            return this;
        }

        public RecipeBuilder outputWeather(WeatherType outputWeather) {
            this.outputWeather = outputWeather;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding EvilCraft Environmental Accumulator Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(inputWeather == null, "inputWeather must be defined");
            msg.add(outputWeather == null, "outputWeather must be defined");
            msg.add(cooldowntime < 0, "cooldowntime must be a non negative integer, yet it was {}", cooldowntime);
            msg.add(duration < 0, "duration must be a non negative integer, yet it was {}", duration);
            msg.add(processingspeed < 0, "processingspeed must be a non negative integer, yet it was {}", processingspeed);
        }

        @Override
        public @Nullable IRecipe<EnvironmentalAccumulatorRecipeComponent, EnvironmentalAccumulatorRecipeComponent, EnvironmentalAccumulatorRecipeProperties> register() {
            if (!validate()) return null;
            IRecipe<EnvironmentalAccumulatorRecipeComponent, EnvironmentalAccumulatorRecipeComponent, EnvironmentalAccumulatorRecipeProperties> recipe =
                    org.cyclops.evilcraft.block.EnvironmentalAccumulator.getInstance().getRecipeRegistry().registerRecipe(
                            new EnvironmentalAccumulatorRecipeComponent(input.get(0).toMcIngredient(), inputWeather),
                            new EnvironmentalAccumulatorRecipeComponent(output.get(0), outputWeather),
                            new EnvironmentalAccumulatorRecipeProperties(duration, cooldowntime, processingspeed)
                    );
            ModSupport.EVILCRAFT.get().environmentalAccumulator.add(recipe, false);
            return recipe;
        }
    }
}
