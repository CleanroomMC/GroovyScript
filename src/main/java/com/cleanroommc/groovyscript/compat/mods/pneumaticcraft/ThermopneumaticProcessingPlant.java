package com.cleanroommc.groovyscript.compat.mods.pneumaticcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import me.desht.pneumaticcraft.api.recipe.IThermopneumaticProcessingPlantRecipe;
import me.desht.pneumaticcraft.common.recipes.BasicThermopneumaticProcessingPlantRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class ThermopneumaticProcessingPlant extends VirtualizedRegistry<IThermopneumaticProcessingPlantRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay') * 3).fluidInput(fluid('water') * 100).fluidOutput(fluid('kerosene') * 100).pressure(4).requiredTemperature(323)"),
            @Example(".fluidInput(fluid('water') * 100).fluidOutput(fluid('lava') * 100).pressure(4).requiredTemperature(323)")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        BasicThermopneumaticProcessingPlantRecipe.recipes.removeAll(removeScripted());
        BasicThermopneumaticProcessingPlantRecipe.recipes.addAll(restoreFromBackup());
    }

    public void add(IThermopneumaticProcessingPlantRecipe recipe) {
        BasicThermopneumaticProcessingPlantRecipe.recipes.add(recipe);
        addScripted(recipe);
    }

    public boolean remove(IThermopneumaticProcessingPlantRecipe recipe) {
        addBackup(recipe);
        return BasicThermopneumaticProcessingPlantRecipe.recipes.remove(recipe);
    }

    @MethodDescription(example = @Example("fluid('lpg')"))
    public boolean removeByOutput(IIngredient output) {
        return BasicThermopneumaticProcessingPlantRecipe.recipes.removeIf(entry -> {
            if (entry instanceof BasicThermopneumaticProcessingPlantRecipe recipe && output.test(recipe.getOutputLiquid())) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = {@Example("item('minecraft:coal')"), @Example("fluid('diesel')")})
    public boolean removeByInput(IIngredient input) {
        return BasicThermopneumaticProcessingPlantRecipe.recipes.removeIf(entry -> {
            if (entry instanceof BasicThermopneumaticProcessingPlantRecipe recipe && (input.test(recipe.getInputLiquid()) || input.test(recipe.getInputItem()))) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        BasicThermopneumaticProcessingPlantRecipe.recipes.forEach(this::addBackup);
        BasicThermopneumaticProcessingPlantRecipe.recipes.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<IThermopneumaticProcessingPlantRecipe> streamRecipes() {
        return new SimpleObjectStream<>(BasicThermopneumaticProcessingPlantRecipe.recipes).setRemover(this::remove);
    }

    @Property(property = "input", comp = @Comp(types = {Comp.Type.GTE, Comp.Type.LTE}, lte = 1))
    @Property(property = "fluidInput", comp = @Comp(types = Comp.Type.EQ, eq = 1))
    @Property(property = "fluidOutput", comp = @Comp(types = Comp.Type.EQ, eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<IThermopneumaticProcessingPlantRecipe> {

        @Property
        private float pressure;
        @Property
        private double requiredTemperature;

        @RecipeBuilderMethodDescription
        public RecipeBuilder pressure(float pressure) {
            this.pressure = pressure;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder requiredTemperature(double requiredTemperature) {
            this.requiredTemperature = requiredTemperature;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding PneumaticCraft Thermopneumatic Processing Plant recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 1, 0, 0);
            validateFluids(msg, 1, 1, 1, 1);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable IThermopneumaticProcessingPlantRecipe register() {
            if (!validate()) return null;
            IThermopneumaticProcessingPlantRecipe recipe = null;
            if (input.isEmpty()) {
                recipe = new BasicThermopneumaticProcessingPlantRecipe(fluidInput.get(0), ItemStack.EMPTY, fluidOutput.get(0), requiredTemperature, pressure);
                ModSupport.PNEUMATIC_CRAFT.get().thermopneumaticProcessingPlant.add(recipe);
            } else {
                for (ItemStack stack : input.get(0).getMatchingStacks()) {
                    IThermopneumaticProcessingPlantRecipe recipe1 = new BasicThermopneumaticProcessingPlantRecipe(fluidInput.get(0), stack, fluidOutput.get(0), requiredTemperature, pressure);
                    ModSupport.PNEUMATIC_CRAFT.get().thermopneumaticProcessingPlant.add(recipe1);
                    if (recipe == null) recipe = recipe1;
                }
            }
            return recipe;
        }
    }

}
