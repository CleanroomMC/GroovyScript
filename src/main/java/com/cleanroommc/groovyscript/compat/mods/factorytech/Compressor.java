package com.cleanroommc.groovyscript.compat.mods.factorytech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import dalapo.factech.auxiliary.MachineRecipes;
import dalapo.factech.tileentity.specialized.TileEntityCompressionChamber;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class Compressor extends StandardListRegistry<TileEntityCompressionChamber.CompressorRecipe> {

    @Override
    public Collection<TileEntityCompressionChamber.CompressorRecipe> getRecipes() {
        return MachineRecipes.COMPRESSOR;
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond'))"),
            @Example(".input(item('minecraft:diamond')).fluidInput(fluid('lava') * 100).output(item('minecraft:clay'))"),
            @Example(".input(item('minecraft:gold_ingot')).fluidInput(fluid('water') * 100).output(item('minecraft:diamond') * 5)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(example = {
            @Example("fluid('water')"), @Example("item('factorytech:machinepart:60')")
    })
    public void removeByInput(IIngredient input) {
        getRecipes().removeIf(r -> (input.test(r.getItemIn()) || input.test(r.getFluidIn())) && doAddBackup(r));
    }

    @MethodDescription(example = @Example("item('factorytech:machinepart:141')"))
    public void removeByOutput(IIngredient output) {
        getRecipes().removeIf(r -> output.test(r.getOutputStack()) && doAddBackup(r));
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "fluidInput", comp = @Comp(gte = 0, lte = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<TileEntityCompressionChamber.CompressorRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Factory Tech Compressor recipe";
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg, 0, 1, 0, 0);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable TileEntityCompressionChamber.CompressorRecipe register() {
            if (!validate()) return null;
            TileEntityCompressionChamber.CompressorRecipe recipe = null;
            for (var stack : input.get(0).getMatchingStacks()) {
                recipe = new TileEntityCompressionChamber.CompressorRecipe(stack, fluidInput.getOrEmpty(0), output.get(0));
                ModSupport.FACTORY_TECH.get().compressor.add(recipe);
            }
            return recipe;
        }
    }
}
