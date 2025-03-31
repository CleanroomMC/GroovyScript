package com.cleanroommc.groovyscript.compat.mods.betterwithaddons;

import betterwithaddons.crafting.manager.CraftingManagerSpindle;
import betterwithaddons.crafting.recipes.SpindleRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class Spindle extends StandardListRegistry<SpindleRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:clay'))"),
            @Example(".input(item('minecraft:clay') * 3).output(item('minecraft:diamond')).popoff()"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay') * 4)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<SpindleRecipe> getRecipes() {
        return CraftingManagerSpindle.getInstance().getRecipes();
    }

    @MethodDescription(example = @Example("item('minecraft:vine')"))
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

    @MethodDescription(example = @Example("item('betterwithaddons:bolt')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> {
            for (var itemstack : r.getOutput()) {
                if (output.test(itemstack)) {
                    return doAddBackup(r);
                }
            }
            return false;
        });
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(gte = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<SpindleRecipe> {

        @Property
        private boolean popoff;

        @RecipeBuilderMethodDescription
        public RecipeBuilder popoff(boolean popoff) {
            this.popoff = popoff;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder popoff() {
            return this.popoff(!popoff);
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Better With Addons Spindle recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, Integer.MAX_VALUE);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable SpindleRecipe register() {
            if (!validate()) return null;
            SpindleRecipe recipe = new SpindleRecipe(popoff, BetterWithAddons.FromIngredient.fromIIngredient(input.get(0)), output.toArray(new ItemStack[0]));
            ModSupport.BETTER_WITH_ADDONS.get().spindle.add(recipe);
            return recipe;
        }
    }
}
