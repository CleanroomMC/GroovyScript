package com.cleanroommc.groovyscript.compat.mods.betterwithaddons;

import betterwithaddons.crafting.manager.CraftingManagerInfuserTransmutation;
import betterwithaddons.crafting.recipes.infuser.TransmutationRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class Transmutation extends StandardListRegistry<TransmutationRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:clay')).spirits(0)"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay') * 4).spirits(5)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<TransmutationRecipe> getRecipes() {
        return CraftingManagerInfuserTransmutation.getInstance().getRecipes();
    }

    @MethodDescription(example = @Example("item('minecraft:reeds')"))
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

    @MethodDescription(example = @Example("item('betterwithaddons:crop_rice')"))
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
    public static class RecipeBuilder extends AbstractRecipeBuilder<TransmutationRecipe> {

        @Property(comp = @Comp(gte = 0))
        private int spirits;

        @RecipeBuilderMethodDescription
        public RecipeBuilder spirits(int spirits) {
            this.spirits = spirits;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Better With Addons Transmutation recipe";
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(spirits < 0, "spirits must be greater than or equal to 0, yet it was {}", spirits);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable TransmutationRecipe register() {
            if (!validate()) return null;
            TransmutationRecipe recipe = new TransmutationRecipe(BetterWithAddons.FromIngredient.fromIIngredient(input.get(0)), spirits, output.get(0));
            ModSupport.BETTER_WITH_ADDONS.get().transmutation.add(recipe);
            return recipe;
        }
    }
}
