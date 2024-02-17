package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.CampfireRecipe;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.ChoppingBlockRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class Campfire extends ForgeRegistryWrapper<CampfireRecipe> {

    public Campfire() {
        super(ModuleTechBasic.Registries.CAMPFIRE_RECIPE, Alias.generateOfClass(Campfire.class));
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public boolean remove(CampfireRecipe recipe) {
        if (recipe == null) return false;
        remove(recipe.getRegistryName());
        return true;
    }

    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing campfire recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (CampfireRecipe recipe : getRegistry()) {
            if (recipe.getInput().test(input)) {
                remove(recipe);
            }
        }
    }

    public void removeByOutput(IIngredient output) {
        if (GroovyLog.msg("Error removing campfire recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (CampfireRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        }
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<CampfireRecipe> {

        private int duration;

        public RecipeBuilder duration(int time) {
            this.duration = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Campfire Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(duration < 0, "duration must be a non negative integer, yet it was {}", duration);
            msg.add(name == null, "name cannot be null.");
            msg.add(ModuleTechBasic.Registries.CAMPFIRE_RECIPE.getValue(name) != null, "tried to register {}, but it already exists.", name);

        }

        @Override
        public @Nullable CampfireRecipe register() {
            if (!validate()) return null;
            CampfireRecipe recipe = new CampfireRecipe(output.get(0), input.get(0).toMcIngredient(), duration).setRegistryName(name);
            PyroTech.campfire.add(recipe);
            return recipe;
        }
    }
}
