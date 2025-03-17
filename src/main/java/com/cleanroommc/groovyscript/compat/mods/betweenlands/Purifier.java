package com.cleanroommc.groovyscript.compat.mods.betweenlands;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.thebetweenlands.PurifierRecipeAccessor;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.recipes.IPurifierRecipe;
import thebetweenlands.common.recipe.purifier.PurifierRecipeStandard;

import java.util.Collection;

@RegistryDescription
public class Purifier extends StandardListRegistry<IPurifierRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond'))"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<IPurifierRecipe> getRecipes() {
        return PurifierRecipeAccessor.getRecipes();
    }

    @MethodDescription(example = @Example("item('thebetweenlands:items_misc:64')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> r instanceof PurifierRecipeStandard recipe && input.test(recipe.getInput()) && doAddBackup(r));
    }

    @MethodDescription(example = @Example("item('thebetweenlands:cragrock')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> r instanceof PurifierRecipeStandard recipe && output.test(r.getOutput(recipe.getInput())) && doAddBackup(r));
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<IPurifierRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Betweenlands Purifier recipe";
        }

        @Override
        @GroovyBlacklist
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable IPurifierRecipe register() {
            if (!validate()) return null;
            IPurifierRecipe recipe = null;
            for (var stack : input.get(0).getMatchingStacks()) {
                recipe = new PurifierRecipeStandard(output.get(0), stack);
                ModSupport.BETWEENLANDS.get().purifier.add(recipe);
            }
            return recipe;
        }
    }
}
