package com.cleanroommc.groovyscript.compat.mods.futuremc;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import org.jetbrains.annotations.Nullable;
import thedarkcolour.futuremc.recipe.smithing.SmithingRecipe;
import thedarkcolour.futuremc.recipe.smithing.SmithingRecipes;

import java.util.Arrays;
import java.util.Collection;

@RegistryDescription(admonition = @Admonition(value = "groovyscript.wiki.futuremc.smithing.note0", type = Admonition.Type.WARNING))
public class Smithing extends StandardListRegistry<SmithingRecipe> {

    @Override
    public Collection<SmithingRecipe> getRecipes() {
        return SmithingRecipes.INSTANCE.getRecipes();
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay'), item('minecraft:gold_ingot')).output(item('minecraft:diamond'))"),
            @Example(".input(item('minecraft:gold_ingot') * 4, item('minecraft:clay')).output(item('minecraft:clay') * 8)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(example = {
            @Example("item('minecraft:diamond_hoe')"), @Example(value = "item('futuremc:netherite_ingot')", commented = true),
    })
    public void removeByInput(IIngredient input) {
        getRecipes().removeIf(r -> Arrays.stream(r.getInput().getMatchingStacks()).anyMatch(input) && doAddBackup(r));
    }

    @MethodDescription(example = @Example("item('futuremc:netherite_pickaxe')"))
    public void removeByOutput(IIngredient output) {
        getRecipes().removeIf(r -> output.test(r.getOutput()) && doAddBackup(r));
    }

    @Property(property = "input", comp = @Comp(eq = 2))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<SmithingRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding FutureMC Smithing recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 2, 2, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable SmithingRecipe register() {
            if (!validate()) return null;
            SmithingRecipe recipe = new SmithingRecipe(input.get(0).toMcIngredient(), input.get(1).toMcIngredient(), output.get(0));
            ModSupport.FUTURE_MC.get().smithing.add(recipe);
            return recipe;
        }

    }

}
