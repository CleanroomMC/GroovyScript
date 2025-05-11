package com.cleanroommc.groovyscript.compat.mods.factorytech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import dalapo.factech.auxiliary.MachineRecipes;
import dalapo.factech.tileentity.specialized.TileEntityAgitator;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class Agitator extends StandardListRegistry<TileEntityAgitator.AgitatorRecipe> {

    @Override
    public Collection<TileEntityAgitator.AgitatorRecipe> getRecipes() {
        return MachineRecipes.AGITATOR;
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).fluidInput(fluid('water') * 100).output(item('minecraft:diamond'))"),
            @Example(".fluidInput(fluid('ftglowstone') * 100).output(item('minecraft:clay'))"),
            @Example(".fluidInput(fluid('lava') * 100, fluid('water') * 100).output(item('minecraft:clay'))"),
            @Example(".fluidInput(fluid('lava') * 100, fluid('ftglowstone') * 100).fluidOutput(fluid('ftglowstone') * 100)"),
            @Example(".input(item('minecraft:gold_ingot')).fluidInput(fluid('water') * 100).output(item('minecraft:diamond') * 5)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(example = {
            @Example("fluid('lava')"), @Example("fluid('ftglowstone')"), @Example("item('minecraft:sand')")
    })
    public void removeByInput(IIngredient input) {
        getRecipes().removeIf(r -> (input.test(r.getInputItem()) || input.test(r.getInputFluid(0)) || input.test(r.getInputFluid(1))) && doAddBackup(r));
    }

    @MethodDescription(example = {
            @Example("fluid('h2so4')"), @Example("item('minecraft:stone')")
    })
    public void removeByOutput(IIngredient output) {
        getRecipes().removeIf(r -> (output.test(r.getOutputStack()) || output.test(r.getOutputFluid())) && doAddBackup(r));
    }

    @Property(property = "input", comp = @Comp(gte = 0, lte = 1))
    @Property(property = "fluidInput", comp = @Comp(gte = 1, lte = 2))
    @Property(property = "output", comp = @Comp(gte = 0, lte = 1))
    @Property(property = "fluidOutput", comp = @Comp(gte = 0, lte = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<TileEntityAgitator.AgitatorRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Factory Tech Agitator recipe";
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 1, 0, 1);
            validateFluids(msg, 1, 2, 0, 1);
            msg.add(output.isEmpty() && fluidOutput.isEmpty(), "either output or fluidOutput must have an entry, yet both were empty");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable TileEntityAgitator.AgitatorRecipe register() {
            if (!validate()) return null;
            TileEntityAgitator.AgitatorRecipe recipe = null;
            if (input.isEmpty()) {
                recipe = new TileEntityAgitator.AgitatorRecipe(null, output.getOrEmpty(0), fluidOutput.getOrEmpty(0), fluidInput.getOrEmpty(0), fluidInput.getOrEmpty(1));
                ModSupport.FACTORY_TECH.get().agitator.add(recipe);
            } else {
                for (var stack : input.get(0).getMatchingStacks()) {
                    recipe = new TileEntityAgitator.AgitatorRecipe(stack, output.getOrEmpty(0), fluidOutput.getOrEmpty(0), fluidInput.getOrEmpty(0), fluidInput.getOrEmpty(1));
                    ModSupport.FACTORY_TECH.get().agitator.add(recipe);
                }
            }
            return recipe;
        }
    }
}
