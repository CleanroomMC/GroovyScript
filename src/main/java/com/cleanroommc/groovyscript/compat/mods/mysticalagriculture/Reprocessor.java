package com.cleanroommc.groovyscript.compat.mods.mysticalagriculture;

import com.blakebr0.mysticalagriculture.crafting.ReprocessorManager;
import com.blakebr0.mysticalagriculture.crafting.ReprocessorRecipe;
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
public class Reprocessor extends StandardListRegistry<ReprocessorRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond') * 3)"),
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:gold_ingot'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<ReprocessorRecipe> getRecipes() {
        return ReprocessorManager.getRecipes();
    }

//    public ReprocessorRecipe add(IIngredient input, ItemStack output, int amount) {
//        return add(input, output, amount, false);
//    }

    public ReprocessorRecipe add(IIngredient input, ItemStack output/*, int amount, boolean exact*/) {
        return recipeBuilder()
//                .exact(exact)
//                .amount(amount)
                .input(input)
                .output(output)
                .register();
    }

    @MethodDescription(example = @Example("item('mysticalagriculture:stone_seeds')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(recipe -> {
            if (input.test(recipe.getInput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('mysticalagriculture:dirt_essence')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(recipe -> {
            if (output.test(recipe.getOutput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(gte = 1, lte = 2))
    public static class RecipeBuilder extends AbstractRecipeBuilder<ReprocessorRecipe> {

//        @Property(comp = @Comp(gt = 0), defaultValue = "1")
//        private int amount = 1;
//        @Property
//        private boolean exact;
//
//        @RecipeBuilderMethodDescription
//        public RecipeBuilder amount(int amount) {
//            this.amount = amount;
//            return this;
//        }
//
//        @RecipeBuilderMethodDescription
//        public RecipeBuilder exact() {
//            this.exact = !exact;
//            return this;
//        }
//
//        @RecipeBuilderMethodDescription
//        public RecipeBuilder exact(boolean exact) {
//            this.exact = exact;
//            return this;
//        }

        @Override
        public String getErrorMsg() {
            return "Error adding Mystical Agriculture Reprocessor recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
//            msg.add(amount <= 0, "amount must be greater than 0, yet it was {}", amount);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable ReprocessorRecipe register() {
            if (!validate()) return null;
            ReprocessorRecipe recipe = null;
            for (ItemStack matchingStack : input.get(0).getMatchingStacks()) {
//                recipe = new ReprocessorRecipe(output.get(0), amount, matchingStack, exact);
                recipe = new ReprocessorRecipe(output.get(0), 1, matchingStack, false);
                ModSupport.MYSTICAL_AGRICULTURE.get().reprocessor.add(recipe);
            }
            return recipe;
        }
    }
}
