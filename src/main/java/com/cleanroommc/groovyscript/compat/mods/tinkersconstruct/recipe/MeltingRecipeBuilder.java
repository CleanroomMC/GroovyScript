package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.recipe;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Comp;
import com.cleanroommc.groovyscript.api.documentation.annotations.Property;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderMethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderRegistrationMethod;
import com.cleanroommc.groovyscript.core.mixin.tconstruct.MeltingRecipeAccessor;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

@Property(property = "input", comp = @Comp(eq = 1))
public class MeltingRecipeBuilder extends AbstractRecipeBuilder<MeltingRecipe> {

    @Property(defaultValue = "300", comp = @Comp(gte = 1))
    private int temp = 300;
    private final StandardListRegistry<MeltingRecipe> registry;
    private final String recipeName;

    public static RecipeMatch recipeMatchFromIngredient(IIngredient ingredient, int amount) {
        return (ingredient instanceof OreDictIngredient oreDictIngredient)
                ? RecipeMatch.of(oreDictIngredient.getOreDict(), amount)
                : RecipeMatch.of(ingredient.getMatchingStacks()[0], amount);
    }

    public static RecipeMatch recipeMatchFromIngredient(IIngredient ingredient) {
        return recipeMatchFromIngredient(ingredient, 1);
    }

    public MeltingRecipeBuilder(StandardListRegistry<MeltingRecipe> registry, String recipeName) {
        this.registry = registry;
        this.recipeName = recipeName;
    }

    @RecipeBuilderMethodDescription(field = "temp")
    public MeltingRecipeBuilder temperature(int temp) {
        this.temp = temp + 300;
        return this;
    }

    @RecipeBuilderMethodDescription(field = "temp")
    public MeltingRecipeBuilder time(int time) {
        int t = fluidOutput.get(0) != null ? fluidOutput.get(0).getFluid().getTemperature() : 300;
        this.temp = MeltingRecipeAccessor.invokeCalcTemperature(t, time);
        return this;
    }

    @Override
    public String getErrorMsg() {
        return "Error adding " + recipeName;
    }

    @Override
    protected int getMaxItemInput() {
        // More than 1 item cannot be placed
        return 1;
    }

    @Override
    public void validate(GroovyLog.Msg msg) {
        validateItems(msg, 1, 1, 0, 0);
        validateFluids(msg, 0, 0, 1, 1);
        msg.add(temp < 1, "Recipe temperature must be at least 1, got " + temp);
    }

    @Override
    @RecipeBuilderRegistrationMethod
    public @Nullable MeltingRecipe register() {
        if (!validate()) return null;
        int amount = fluidOutput.get(0).amount;
        IIngredient input = this.input.get(0);
        RecipeMatch match = recipeMatchFromIngredient(input, amount);
        MeltingRecipe recipe = new MeltingRecipe(match, fluidOutput.get(0), temp);
        registry.add(recipe);
        return recipe;
    }
}
