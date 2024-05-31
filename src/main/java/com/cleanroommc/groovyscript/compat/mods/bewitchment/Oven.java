package com.cleanroommc.groovyscript.compat.mods.bewitchment;

import com.bewitchment.api.registry.OvenRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.helper.recipe.RecipeName;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.Nullable;


public class Oven extends ForgeRegistryWrapper<OvenRecipe> {

    public Oven() {
        super(GameRegistry.findRegistry(OvenRecipe.class));
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

    public static class RecipeBuilder extends AbstractRecipeBuilder<OvenRecipe> {

        private ItemStack byproduct = ItemStack.EMPTY;
        private float byproductChance = 0.0f;
        private boolean requiresJar;

        public RecipeBuilder byproduct(ItemStack byproduct) {
            this.byproduct = byproduct;
            return this;
        }

        public RecipeBuilder byproductChance(float byproductChance) {
            this.byproductChance = byproductChance;
            return this;
        }

        public RecipeBuilder requiresJar(boolean requiresJar) {
            this.requiresJar = requiresJar;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Bewitchment Oven Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1,1,1,1);
            validateFluids(msg);
            if (name == null || name.getNamespace().isEmpty() || name.getPath().isEmpty()) name = RecipeName.generateRl("oven_recipe");
        }

        @Override
        public @Nullable OvenRecipe register() {
            if (!validate()) return null;
            OvenRecipe recipe = new OvenRecipe(name, input.get(0).getMatchingStacks()[0], output.get(0), byproduct, byproductChance, requiresJar);
            Bewitchment.oven.add(recipe);
            return recipe;
        }
    }
}
