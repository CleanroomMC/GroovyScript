package com.cleanroommc.groovyscript.compat.mods.betterwithaddons;

import betterwithaddons.crafting.manager.CraftingManagerTatara;
import betterwithaddons.crafting.recipes.SmeltingRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class Tatara extends StandardListRegistry<SmeltingRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:clay'))"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay') * 4)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<SmeltingRecipe> getRecipes() {
        return CraftingManagerTatara.instance().getRecipes();
    }

    @MethodDescription(example = @Example("item('betterwithaddons:japanmat:20')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> {
            for (var itemstack : r.getRecipeInputs()) {
                if (input.test(itemstack)) {
                    return doAddBackup(r);
                }
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('betterwithaddons:kera')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> {
            for (var itemstack : r.getRecipeOutputs()) {
                if (output.test(itemstack)) {
                    return doAddBackup(r);
                }
            }
            return false;
        });
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<SmeltingRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Better With Addons Tatara recipe";
        }

        @Override
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
        public @Nullable SmeltingRecipe register() {
            if (!validate()) return null;
            SmeltingRecipe recipe = new SmeltingRecipe(BetterWithAddons.FromIngredient.fromIIngredient(input.get(0)), output.get(0));
            ModSupport.BETTER_WITH_ADDONS.get().tatara.add(recipe);
            return recipe;
        }
    }
}
