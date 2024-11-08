package com.cleanroommc.groovyscript.compat.mods.evilcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import org.cyclops.cyclopscore.recipe.custom.api.IRecipe;
import org.cyclops.evilcraft.Configs;
import org.cyclops.evilcraft.block.EnvironmentalAccumulatorConfig;
import org.cyclops.evilcraft.core.recipe.custom.EnvironmentalAccumulatorRecipeComponent;
import org.cyclops.evilcraft.core.recipe.custom.EnvironmentalAccumulatorRecipeProperties;
import org.cyclops.evilcraft.core.weather.WeatherType;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Locale;

@RegistryDescription
public class EnvironmentalAccumulator extends StandardListRegistry<IRecipe<EnvironmentalAccumulatorRecipeComponent, EnvironmentalAccumulatorRecipeComponent, EnvironmentalAccumulatorRecipeProperties>> {

    @Override
    public boolean isEnabled() {
        return Configs.isEnabled(EnvironmentalAccumulatorConfig.class);
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:clay') * 2).inputWeather(weather('clear')).outputWeather(weather('rain')).processingspeed(1).cooldowntime(1000).duration(10)"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:diamond')).inputWeather(weather('rain')).outputWeather(weather('lightning')).speed(10).cooldown(1)"),
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:clay') * 16).inputWeather(weather('lightning')).outputWeather(weather('lightning'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<IRecipe<EnvironmentalAccumulatorRecipeComponent, EnvironmentalAccumulatorRecipeComponent, EnvironmentalAccumulatorRecipeProperties>> getRecipes() {
        return org.cyclops.evilcraft.block.EnvironmentalAccumulator.getInstance().getRecipeRegistry().allRecipes();
    }

    @MethodDescription(example = @Example("item('evilcraft:exalted_crafter:1')"))
    public boolean removeByInput(ItemStack input) {
        return getRecipes().removeIf(r -> {
            if (r.getInput().getIngredient().test(input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('evilcraft:exalted_crafter:2')"))
    public boolean removeByOutput(ItemStack input) {
        return getRecipes().removeIf(r -> {
            if (r.getOutput().getIngredient().test(input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<IRecipe<EnvironmentalAccumulatorRecipeComponent, EnvironmentalAccumulatorRecipeComponent, EnvironmentalAccumulatorRecipeProperties>> {

        @Property(defaultValue = "EnvironmentalAccumulatorConfig.defaultProcessItemSpeed", comp = @Comp(gte = 0))
        private double processingspeed = EnvironmentalAccumulatorConfig.defaultProcessItemSpeed;
        @Property(defaultValue = "EnvironmentalAccumulatorConfig.defaultTickCooldown", comp = @Comp(gte = 0))
        private int cooldowntime = EnvironmentalAccumulatorConfig.defaultTickCooldown;
        @Property(defaultValue = "EnvironmentalAccumulatorConfig.defaultProcessItemTickCount", comp = @Comp(gte = 0))
        private int duration = EnvironmentalAccumulatorConfig.defaultProcessItemTickCount;
        @Property(comp = @Comp(not = "null"))
        private WeatherType inputWeather;
        @Property(comp = @Comp(not = "null"))
        private WeatherType outputWeather;

        @RecipeBuilderMethodDescription
        public RecipeBuilder cooldowntime(int cooldowntime) {
            this.cooldowntime = cooldowntime;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "cooldowntime")
        public RecipeBuilder cooldown(int cooldowntime) {
            this.cooldowntime = cooldowntime;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder duration(int duration) {
            this.duration = duration;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder processingspeed(double processingspeed) {
            this.processingspeed = processingspeed;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "processingspeed")
        public RecipeBuilder speed(double processingspeed) {
            this.processingspeed = processingspeed;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder inputWeather(String inputWeather) {
            this.inputWeather = WeatherType.valueOf(inputWeather.toUpperCase(Locale.ROOT));
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder inputWeather(WeatherType inputWeather) {
            this.inputWeather = inputWeather;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder outputWeather(String outputWeather) {
            this.outputWeather = WeatherType.valueOf(outputWeather.toUpperCase(Locale.ROOT));
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder outputWeather(WeatherType outputWeather) {
            this.outputWeather = outputWeather;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding EvilCraft Environmental Accumulator Recipe";
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
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
        @RecipeBuilderRegistrationMethod
        public @Nullable IRecipe<EnvironmentalAccumulatorRecipeComponent, EnvironmentalAccumulatorRecipeComponent, EnvironmentalAccumulatorRecipeProperties> register() {
            if (!validate()) return null;
            IRecipe<EnvironmentalAccumulatorRecipeComponent, EnvironmentalAccumulatorRecipeComponent, EnvironmentalAccumulatorRecipeProperties> recipe = org.cyclops.evilcraft.block.EnvironmentalAccumulator.getInstance()
                    .getRecipeRegistry()
                    .registerRecipe(
                            new EnvironmentalAccumulatorRecipeComponent(input.get(0).toMcIngredient(), inputWeather),
                            new EnvironmentalAccumulatorRecipeComponent(output.get(0), outputWeather),
                            new EnvironmentalAccumulatorRecipeProperties(duration, cooldowntime, processingspeed)
                    );
            ModSupport.EVILCRAFT.get().environmentalAccumulator.addScripted(recipe);
            return recipe;
        }
    }
}
