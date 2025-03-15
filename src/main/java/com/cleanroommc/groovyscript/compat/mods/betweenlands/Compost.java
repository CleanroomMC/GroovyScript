package com.cleanroommc.groovyscript.compat.mods.betweenlands;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.recipes.ICompostBinRecipe;
import thebetweenlands.common.recipe.misc.CompostRecipe;

import java.util.Collection;

@RegistryDescription
public class Compost extends StandardListRegistry<ICompostBinRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).amount(20).time(30)"),
            @Example(".input(item('minecraft:gold_ingot')).amount(1).time(5)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<ICompostBinRecipe> getRecipes() {
        return CompostRecipe.RECIPES;
    }

    @MethodDescription(example = @Example("item('thebetweenlands:items_misc:13')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> r instanceof CompostRecipe recipe && input.test(recipe.getInput()) && doAddBackup(r));
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<ICompostBinRecipe> {

        @Property(comp = @Comp(gte = 1))
        private int amount;
        @Property(comp = @Comp(gte = 1))
        private int time;

        @RecipeBuilderMethodDescription
        public RecipeBuilder amount(int amount) {
            this.amount = amount;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Betweenlands Compost recipe";
        }

        @Override
        @GroovyBlacklist
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg);
            msg.add(amount <= 0, "amount must be a positive integer greater than 0, yet it was {}", amount);
            msg.add(time <= 0, "time must be a positive integer greater than 0, yet it was {}", time);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable ICompostBinRecipe register() {
            if (!validate()) return null;
            ICompostBinRecipe recipe = null;
            for (var stack : input.get(0).getMatchingStacks()) {
                recipe = new CompostRecipe(amount, time, stack);
                ModSupport.BETWEENLANDS.get().compost.add(recipe);
            }
            return recipe;
        }
    }
}
