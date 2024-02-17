package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.KilnPitRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class Kiln extends ForgeRegistryWrapper<KilnPitRecipe> {


    public Kiln() {
        super(ModuleTechBasic.Registries.KILN_PIT_RECIPE, Alias.generateOfClass(Kiln.class));
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public boolean remove(KilnPitRecipe recipe) {
        if (recipe == null) return false;
        remove(recipe.getRegistryName());
        return true;
    }

    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing pit kiln recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (KilnPitRecipe recipe : getRegistry()) {
            if (recipe.getInput().test(input)) {
                remove(recipe);
            }
        }
    }

    public void removeByOutput(IIngredient output) {
        if (GroovyLog.msg("Error removing pit kiln recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (KilnPitRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        }
    }


    public static class RecipeBuilder extends AbstractRecipeBuilder<KilnPitRecipe> {

        private int burnTime;
        private float failureChance;
        private ItemStack[] failureOutputs;

        public RecipeBuilder burnTime(int time) {
            this.burnTime = time;
            return this;
        }

        public RecipeBuilder failureChance(float chance) {
            this.failureChance = chance;
            return this;
        }

        public RecipeBuilder failureOutputs(ItemStack[] failureOutputs) {
            this.failureOutputs = failureOutputs;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Pit Kiln Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(burnTime < 0, "burnTime must be a non negative integer, yet it was {}", burnTime);
            msg.add(failureChance < 0, "failureChance must be a non negative float, yet it was {}", failureChance);
            msg.add(name == null, "name cannot be null.");
            msg.add(ModuleTechBasic.Registries.KILN_PIT_RECIPE.getValue(name) != null, "tried to register {}, but it already exists.", name);

        }

        @Override
        public @Nullable KilnPitRecipe register() {
            if (!validate()) return null;
            KilnPitRecipe recipe = new KilnPitRecipe(output.get(0), input.get(0).toMcIngredient(), burnTime, failureChance, failureOutputs).setRegistryName(name);
            PyroTech.kiln.add(recipe);
            return recipe;
        }
    }
}
