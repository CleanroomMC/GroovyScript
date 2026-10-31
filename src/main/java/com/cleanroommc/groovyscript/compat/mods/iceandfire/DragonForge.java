package com.cleanroommc.groovyscript.compat.mods.iceandfire;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import com.github.alexthe666.iceandfire.recipe.DragonForgeRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public abstract class DragonForge extends StandardListRegistry<DragonForgeRecipe> {

    private final String type;
    private final Collection<DragonForgeRecipe> recipes;

    protected DragonForge(String type, Collection<DragonForgeRecipe> recipes) {
        this.type = type;
        this.recipes = recipes;
    }

    @Override
    public final Collection<DragonForgeRecipe> getRecipes() {
        return recipes;
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> (input.test(r.getInput()) || input.test(r.getBlood())) && doAddBackup(r));
    }

    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> output.test(r.getOutput()) && doAddBackup(r));
    }

    @Property(property = "input", comp = @Comp(eq = 2))
    @Property(property = "output", comp = @Comp(eq = 1))
    public final class RecipeBuilder extends AbstractRecipeBuilder<DragonForgeRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Ice And Fire " + DragonForge.this.type + " Forge recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 2, 2, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable DragonForgeRecipe register() {
            if (!validate()) return null;
            DragonForgeRecipe recipe = null;
            for (var inputStack : input.get(0).getMatchingStacks()) {
                for (var blood : input.get(1).getMatchingStacks()) {
                    recipe = new DragonForgeRecipe(inputStack, blood, output.get(0));
                    DragonForge.this.add(recipe);
                }
            }
            return recipe;
        }
    }
}