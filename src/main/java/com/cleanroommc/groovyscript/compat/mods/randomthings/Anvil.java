package com.cleanroommc.groovyscript.compat.mods.randomthings;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import lumien.randomthings.recipes.anvil.AnvilRecipe;
import lumien.randomthings.recipes.anvil.AnvilRecipeHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription(admonition = {
        @Admonition(value = "groovyscript.wiki.randomthings.anvil.note0", type = Admonition.Type.TIP),
        @Admonition(value = "groovyscript.wiki.randomthings.anvil.note1", type = Admonition.Type.WARNING),
        @Admonition(value = "groovyscript.wiki.randomthings.anvil.note2", type = Admonition.Type.BUG, format = Admonition.Format.STANDARD),
})
public class Anvil extends StandardListRegistry<AnvilRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond_sword'), item('minecraft:boat')).output(item('minecraft:diamond')).cost(1)"),
            @Example(".input(item('minecraft:iron_sword'), item('minecraft:boat')).output(item('minecraft:gold_ingot') * 16).cost(50)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<AnvilRecipe> getRecipes() {
        return AnvilRecipeHandler.getAllRecipes();
    }

    @MethodDescription(example = @Example("item('randomthings:obsidianskull')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> (input.test(r.getFirst()) || input.test(r.getSecond())) && doAddBackup(r));
    }

    @MethodDescription(example = @Example("item('randomthings:lavawader')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> output.test(r.getOutput()) && doAddBackup(r));
    }

    @Property(property = "input", comp = @Comp(eq = 2))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<AnvilRecipe> {

        @Property(comp = @Comp(gt = 0))
        private int cost;

        @RecipeBuilderMethodDescription
        public RecipeBuilder cost(int cost) {
            this.cost = cost;
            return this;
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Random Things Anvil recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 2, 2, 1, 1);
            validateFluids(msg);
            msg.add(cost <= 0, "cost must be greater than 0, yet it was {}", cost);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable AnvilRecipe register() {
            if (!validate()) return null;
            AnvilRecipe recipe = null;
            for (var input1 : input.get(0).getMatchingStacks()) {
                for (var input2 : input.get(1).getMatchingStacks()) {
                    recipe = new AnvilRecipe(input1, input2, output.get(0), cost);
                    ModSupport.RANDOM_THINGS.get().anvil.add(recipe);
                }
            }
            return recipe;
        }
    }
}
