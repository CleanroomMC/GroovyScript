package com.cleanroommc.groovyscript.compat.mods.betweenlands;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.thebetweenlands.PestleAndMortarRecipeAccessor;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.recipes.IPestleAndMortarRecipe;
import thebetweenlands.common.recipe.mortar.PestleAndMortarRecipe;

import java.util.Collection;

@RegistryDescription
public class PestleAndMortar extends StandardListRegistry<IPestleAndMortarRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond'))"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<IPestleAndMortarRecipe> getRecipes() {
        return PestleAndMortarRecipeAccessor.getRecipes();
    }

    @MethodDescription(example = @Example("item('thebetweenlands:limestone')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> input.test(r.getInputs()) && doAddBackup(r));
    }

    @MethodDescription(example = @Example("item('thebetweenlands:fish_bait')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> output.test(r.getOutput(r.getInputs(), ItemStack.EMPTY)) && doAddBackup(r));
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<IPestleAndMortarRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Betweenlands Pestle And Mortar recipe";
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
        public @Nullable IPestleAndMortarRecipe register() {
            if (!validate()) return null;
            IPestleAndMortarRecipe recipe = null;
            for (var stack : input.get(0).getMatchingStacks()) {
                recipe = new PestleAndMortarRecipe(output.get(0), stack);
                ModSupport.BETWEENLANDS.get().pestleAndMortar.add(recipe);
            }
            return recipe;
        }
    }
}
