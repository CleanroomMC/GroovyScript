package com.cleanroommc.groovyscript.compat.mods.randomthings;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import lumien.randomthings.recipes.imbuing.ImbuingRecipe;
import lumien.randomthings.recipes.imbuing.ImbuingRecipeHandler;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

@RegistryDescription
public class Imbuing extends StandardListRegistry<ImbuingRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".mainInput(item('minecraft:clay')).input(item('minecraft:clay'), item('minecraft:gold_ingot'), item('minecraft:gold_block')).output(item('minecraft:diamond') * 8)"),
            @Example(".mainInput(item('minecraft:diamond')).input(item('minecraft:clay'), item('minecraft:gold_ingot'), item('minecraft:diamond')).output(item('minecraft:gold_ingot'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<ImbuingRecipe> getRecipes() {
        return ImbuingRecipeHandler.imbuingRecipes;
    }

    @MethodDescription(example = {
            @Example("item('minecraft:cobblestone')"), @Example("item('minecraft:coal')")
    })
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> (input.test(r.toImbue()) || r.getIngredients().stream().anyMatch(input)) && doAddBackup(r));
    }

    @MethodDescription(example = @Example("item('randomthings:imbue:3')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> output.test(r.getResult()) && doAddBackup(r));
    }

    @Property(property = "input", comp = @Comp(eq = 3, unique = "groovyscript.wiki.randomthings.imbuing.input.required"))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<ImbuingRecipe> {

        @Property(comp = @Comp(not = "null"))
        private IIngredient mainInput;

        @RecipeBuilderMethodDescription
        public RecipeBuilder mainInput(IIngredient mainInput) {
            this.mainInput = mainInput;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Random Things Imbuing recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 3, 3, 1, 1);
            validateFluids(msg);
            msg.add(mainInput == null, "mainInput must be defined");
            var uni = input.stream().distinct().count();
            msg.add(uni > 0 && uni < 3, "each input must be unique, yet only {}/3 of the inputs were unique. mainInput is not considered", uni);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable ImbuingRecipe register() {
            if (!validate()) return null;
            ImbuingRecipe recipe = null;
            List<List<ItemStack>> cartesian = IngredientHelper.cartesianProductItemStacks(input);
            for (var toImbue : mainInput.getMatchingStacks()) {
                for (var stacks : cartesian) {
                    recipe = new ImbuingRecipe(toImbue, output.get(0), stacks.toArray(new ItemStack[0]));
                    ModSupport.RANDOM_THINGS.get().imbuing.add(recipe);
                }
            }
            return recipe;
        }
    }
}
