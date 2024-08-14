package com.cleanroommc.groovyscript.compat.mods.pneumaticcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import me.desht.pneumaticcraft.common.recipes.HeatFrameCoolingRecipe;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class HeatFrameCooling extends VirtualizedRegistry<HeatFrameCoolingRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:gold_ingot'))"),
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:obsidian'))")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        HeatFrameCoolingRecipe.recipes.removeAll(removeScripted());
        HeatFrameCoolingRecipe.recipes.addAll(restoreFromBackup());
    }

    public void add(HeatFrameCoolingRecipe recipe) {
        HeatFrameCoolingRecipe.recipes.add(recipe);
        addScripted(recipe);
    }

    public boolean remove(HeatFrameCoolingRecipe recipe) {
        addBackup(recipe);
        return HeatFrameCoolingRecipe.recipes.remove(recipe);
    }

    @MethodDescription(example = @Example("item('minecraft:obsidian')"))
    public boolean removeByOutput(IIngredient output) {
        return HeatFrameCoolingRecipe.recipes.removeIf(entry -> {
            if (output.test(entry.output)) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:water_bucket')"))
    public boolean removeByInput(IIngredient input) {
        return HeatFrameCoolingRecipe.recipes.removeIf(entry -> {
            if (entry.input.getStacks().stream().anyMatch(input)) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        HeatFrameCoolingRecipe.recipes.forEach(this::addBackup);
        HeatFrameCoolingRecipe.recipes.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<HeatFrameCoolingRecipe> streamRecipes() {
        return new SimpleObjectStream<>(HeatFrameCoolingRecipe.recipes).setRemover(this::remove);
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<HeatFrameCoolingRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding PneumaticCraft Heat Frame Cooling recipe";
        }

        @Override
        protected int getMaxInput() {
            // The recipe correctly requires an increased amount of input items, but only consumes 1
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable HeatFrameCoolingRecipe register() {
            if (!validate()) return null;
            HeatFrameCoolingRecipe recipe = new HeatFrameCoolingRecipe(PneumaticCraft.toItemIngredient(input.get(0)), output.get(0));
            ModSupport.PNEUMATIC_CRAFT.get().heatFrameCooling.add(recipe);
            return recipe;
        }
    }

}
