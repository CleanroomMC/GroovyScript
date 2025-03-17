package com.cleanroommc.groovyscript.compat.mods.betterwithaddons;

import betterwithaddons.block.EriottoMod.BlockCherryBox;
import betterwithaddons.crafting.manager.CraftingManagerDryingBox;
import betterwithaddons.crafting.recipes.CherryBoxRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class DryingBox extends StandardListRegistry<CherryBoxRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:clay'))"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay') * 4)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<CherryBoxRecipe> getRecipes() {
        return CraftingManagerDryingBox.instance().getRecipes();
    }

    @MethodDescription(example = @Example("item('betterwithaddons:japanmat:2')"))
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

    @MethodDescription(example = @Example("item('minecraft:sponge')"))
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
    public static class RecipeBuilder extends AbstractRecipeBuilder<CherryBoxRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Better With Addons Drying Box recipe";
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
        public @Nullable CherryBoxRecipe register() {
            if (!validate()) return null;
            CherryBoxRecipe recipe = new CherryBoxRecipe(BlockCherryBox.CherryBoxType.DRYING, BetterWithAddons.FromIngredient.fromIIngredient(input.get(0)), output.get(0));
            ModSupport.BETTER_WITH_ADDONS.get().dryingBox.add(recipe);
            return recipe;
        }
    }
}
