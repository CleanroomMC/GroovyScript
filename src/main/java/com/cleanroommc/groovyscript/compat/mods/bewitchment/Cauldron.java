package com.cleanroommc.groovyscript.compat.mods.bewitchment;

import com.bewitchment.api.registry.CauldronRecipe;
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

public class Cauldron extends ForgeRegistryWrapper<CauldronRecipe> {
    public Cauldron() {
        super(GameRegistry.findRegistry(CauldronRecipe.class));
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<CauldronRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Bewitchment Cauldron Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 10, 1, 3);
            validateFluids(msg);
            if (name == null || name.getNamespace().isEmpty() || name.getPath().isEmpty()) name = RecipeName.generateRl("cauldron_recipe");
        }

        @Override
        public @Nullable CauldronRecipe register() {
            if (!validate()) return null;
            List<Ingredient> inputs = input.stream().map(IIngredient::toMcIngredient).collect(Collectors.toList());
            CauldronRecipe recipe = new CauldronRecipe(name, inputs, output);
            Bewitchment.cauldron.add(recipe);
            return recipe;
        }
    }
}
