package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.crafting.BlastFurnaceRecipe;
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
import java.util.stream.Collectors;

@RegistryDescription
public class BlastFurnaceFuel extends StandardListRegistry<BlastFurnaceRecipe.BlastFurnaceFuel> {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:clay')).time(100)"))
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<BlastFurnaceRecipe.BlastFurnaceFuel> getRecipes() {
        return BlastFurnaceRecipe.blastFuels;
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public BlastFurnaceRecipe.BlastFurnaceFuel add(IIngredient input, int time) {
        BlastFurnaceRecipe.BlastFurnaceFuel recipe = new BlastFurnaceRecipe.BlastFurnaceFuel(ImmersiveEngineering.toIngredientStack(input), time);
        add(recipe);
        return recipe;
    }

    @MethodDescription(example = @Example("item('immersiveengineering:material:6')"))
    public void removeByInput(ItemStack input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Immersive Engineering Blast Furnace recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
            return;
        }
        List<BlastFurnaceRecipe.BlastFurnaceFuel> recipes = getRecipes().stream().filter(r -> r.input.matches(input)).collect(Collectors.toList());
        for (BlastFurnaceRecipe.BlastFurnaceFuel recipe : recipes) {
            remove(recipe);
        }
        if (recipes.isEmpty()) {
            GroovyLog.msg("Error removing Immersive Engineering Blast Furnace recipe")
                    .add("no recipes found for {}", input)
                    .error()
                    .post();
        }
    }

    @Property(property = "input", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<BlastFurnaceRecipe.BlastFurnaceFuel> {

        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int time;

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Engineering Blast Furnace Fuel";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg);
            if (time < 0) time = 200;
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable BlastFurnaceRecipe.BlastFurnaceFuel register() {
            if (!validate()) return null;
            BlastFurnaceRecipe.BlastFurnaceFuel recipe = new BlastFurnaceRecipe.BlastFurnaceFuel(ImmersiveEngineering.toIngredientStack(input.get(0)), time);
            ModSupport.IMMERSIVE_ENGINEERING.get().blastFurnaceFuel.add(recipe);
            return recipe;
        }
    }

}
