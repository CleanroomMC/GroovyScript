package com.cleanroommc.groovyscript.compat.mods.bewitchment;

import com.bewitchment.api.registry.FrostfireRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.helper.recipe.RecipeName;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.Nullable;

public class Frostfire extends ForgeRegistryWrapper<FrostfireRecipe> {
    public Frostfire() {
        super(GameRegistry.findRegistry(FrostfireRecipe.class));
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void removeByOutput(IIngredient output) {
        this.getRegistry().getValuesCollection().forEach(recipe -> {
            if (output.test(recipe.output)) {
                remove(recipe);
            }
        });
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<FrostfireRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Bewitchment Frostfire recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            if (name == null || name.getNamespace().isEmpty() || name.getPath().isEmpty()) name = RecipeName.generateRl("oven_recipe");
        }

        @Override
        public @Nullable FrostfireRecipe register() {
            if (!validate()) return null;
            FrostfireRecipe recipe = new FrostfireRecipe(name, input.get(0).toMcIngredient(), output.get(0));
            Bewitchment.frostfire.add(recipe);
            return recipe;
        }
    }
}
