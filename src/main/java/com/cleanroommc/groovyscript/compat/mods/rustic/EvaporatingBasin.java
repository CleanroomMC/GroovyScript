package com.cleanroommc.groovyscript.compat.mods.rustic;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import org.jetbrains.annotations.Nullable;
import rustic.common.crafting.IEvaporatingBasinRecipe;
import rustic.common.crafting.Recipes;

import java.util.Collection;

@RegistryDescription
public class EvaporatingBasin extends StandardListRegistry<IEvaporatingBasinRecipe> {

    public EvaporatingBasin() {
        super(Alias.generateOfClass(EvaporatingBasin.class).andGenerate("DryingBasin"));
    }

    @Override
    public Collection<IEvaporatingBasinRecipe> getRegistry() {
        return Recipes.evaporatingRecipes;
    }

    @RecipeBuilderDescription(example = {
            @Example(".fluidInput(fluid('water') * 200).output(item('minecraft:clay'))"),
            @Example(".fluidInput(fluid('lava') * 50).output(item('minecraft:iron_ingot'))")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void afterScriptLoad() {
        Recipes.evaporatingRecipesMap.clear();
        Recipes.evaporatingRecipes.forEach(recipe -> Recipes.evaporatingRecipesMap.put(recipe.getFluid(), recipe));
    }

    @MethodDescription(example = @Example(value = "item('rustic:dust_tiny_iron')", commented = true))
    public boolean removeByOutput(IIngredient output) {
        return Recipes.evaporatingRecipes.removeIf(entry -> {
            if (output.test(entry.getOutput())) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("fluid('ironberryjuice')"))
    public boolean removeByInput(IIngredient input) {
        return Recipes.evaporatingRecipes.removeIf(entry -> {
            if (input.test(entry.getInput())) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @Property(property = "output", valid = @Comp("1"))
    @Property(property = "fluidInput", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<IEvaporatingBasinRecipe> {

        @Property(defaultValue = "fluidInput amount")
        private int time;

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Rustic Evaporating Basin recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 1, 1);
            validateFluids(msg, 1, 1, 0, 0);
            if (time <= 0) time = fluidInput.get(0).amount;
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable IEvaporatingBasinRecipe register() {
            if (!validate()) return null;
            IEvaporatingBasinRecipe recipe = new ExtendedEvaporatingBasinRecipe(output.get(0), fluidInput.get(0), time);
            ModSupport.RUSTIC.get().evaporatingBasin.add(recipe);
            return recipe;
        }
    }

}
