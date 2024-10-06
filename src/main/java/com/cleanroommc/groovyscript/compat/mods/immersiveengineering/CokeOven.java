package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.CokeOvenRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

@RegistryDescription
public class CokeOven extends StandardListRegistry<CokeOvenRecipe> {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:diamond')).output(item('minecraft:clay')).time(100).creosote(50)"))
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<CokeOvenRecipe> getRecipes() {
        return CokeOvenRecipe.recipeList;
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public CokeOvenRecipe add(ItemStack output, IIngredient input, int time, int creosoteOutput) {
        CokeOvenRecipe recipe = new CokeOvenRecipe(output.copy(), ImmersiveEngineering.toIEInput(input), time, creosoteOutput);
        add(recipe);
        return recipe;
    }

    @MethodDescription(example = @Example("item('immersiveengineering:material:6')"))
    public void removeByOutput(ItemStack output) {
        if (IngredientHelper.isEmpty(output)) {
            GroovyLog.msg("Error removing Immersive Engineering Coke Oven recipe")
                    .add("output must not be empty")
                    .error()
                    .post();
            return;
        }
        List<CokeOvenRecipe> list = CokeOvenRecipe.removeRecipes(output);
        if (list.isEmpty()) {
            GroovyLog.msg("Error removing Immersive Engineering Crusher recipe")
                    .add("no recipes found for {}", output)
                    .error()
                    .post();
            return;
        }
        list.forEach(this::addBackup);
    }

    @MethodDescription(example = @Example("item('minecraft:log')"))
    public void removeByInput(ItemStack input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Immersive Engineering Coke Oven recipe")
                    .add("output must not be empty")
                    .error()
                    .post();
            return;
        }
        if (!getRecipes().removeIf(recipe -> {
            if (ApiUtils.stackMatchesObject(input, recipe.input)) {
                addBackup(recipe);
                return true;
            }
            return false;
        })) {
            GroovyLog.msg("Error removing Immersive Engineering Crusher recipe")
                    .add("no recipes found for {}", input)
                    .error()
                    .post();
        }
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<CokeOvenRecipe> {

        @Property(comp = @Comp(gte = 0))
        private int time;
        @Property(comp = @Comp(gte = 0))
        private int creosote;

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder creosote(int creosote) {
            this.creosote = creosote;
            return this;
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Engineering Coke Oven recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            if (time < 0) time = 200;
            if (creosote < 0) creosote = 0;
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable CokeOvenRecipe register() {
            if (!validate()) return null;
            CokeOvenRecipe recipe = new CokeOvenRecipe(output.get(0), ImmersiveEngineering.toIEInput(input.get(0)), time, creosote);
            ModSupport.IMMERSIVE_ENGINEERING.get().cokeOven.add(recipe);
            return recipe;
        }

    }

}
