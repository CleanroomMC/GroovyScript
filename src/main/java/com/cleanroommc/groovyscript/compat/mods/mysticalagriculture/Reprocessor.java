package com.cleanroommc.groovyscript.compat.mods.mysticalagriculture;

import com.blakebr0.mysticalagriculture.crafting.ReprocessorManager;
import com.blakebr0.mysticalagriculture.crafting.ReprocessorRecipe;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class Reprocessor extends VirtualizedRegistry<ReprocessorRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond') * 3)"),
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:gold_ingot'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        ReprocessorManager.getRecipes().removeAll(removeScripted());
        ReprocessorManager.getRecipes().addAll(restoreFromBackup());
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

    public void add(ReprocessorRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        ReprocessorManager.getRecipes().add(recipe);
    }

    public boolean remove(ReprocessorRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        ReprocessorManager.getRecipes().remove(recipe);
        return true;
    }

    @MethodDescription(example = @Example("item('mysticalagriculture:stone_seeds')"))
    public boolean removeByInput(IIngredient input) {
        return ReprocessorManager.getRecipes().removeIf(recipe -> {
            if (input.test(recipe.getInput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('mysticalagriculture:dirt_essence')"))
    public boolean removeByOutput(IIngredient output) {
        return ReprocessorManager.getRecipes().removeIf(recipe -> {
            if (output.test(recipe.getOutput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ReprocessorManager.getRecipes().forEach(this::addBackup);
        ReprocessorManager.getRecipes().clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<ReprocessorRecipe> streamRecipes() {
        return new SimpleObjectStream<>(ReprocessorManager.getRecipes())
                .setRemover(this::remove);
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = {@Comp(type = Comp.Type.GTE, value = "1"), @Comp(type = Comp.Type.LTE, value = "2")})
    public static class RecipeBuilder extends AbstractRecipeBuilder<ReprocessorRecipe> {

//        @Property(valid = @Comp(type = Comp.Type.GT, value = "0"), defaultValue = "1")
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
