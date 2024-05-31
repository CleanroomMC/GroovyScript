package com.cleanroommc.groovyscript.compat.mods.bewitchment;

import com.bewitchment.api.registry.DistilleryRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.helper.recipe.RecipeName;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class Distillery extends ForgeRegistryWrapper<DistilleryRecipe> {
    public Distillery() {
        super(GameRegistry.findRegistry(DistilleryRecipe.class));
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<DistilleryRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Bewitchment Distillery Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 6, 1, 6);
            validateFluids(msg);
            if (name == null || name.getNamespace().isEmpty() || name.getPath().isEmpty()) name = RecipeName.generateRl("distillery_recipe");
        }

        @Override
        public @Nullable DistilleryRecipe register() {
            if (!validate()) return null;
            List<Ingredient> inputs = input.stream().map(IIngredient::toMcIngredient).collect(Collectors.toList());
            DistilleryRecipe recipe = new DistilleryRecipe(name, inputs, output);
            Bewitchment.distillery.add(recipe);
            return recipe;
        }
    }
}
