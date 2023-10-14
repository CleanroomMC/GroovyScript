package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.GasRecipeBuilder;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import mekanism.api.gas.GasStack;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.AdvancedMachineInput;
import mekanism.common.recipe.machines.PurificationRecipe;
import mekanism.common.recipe.outputs.ItemStackOutput;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class PurificationChamber extends VirtualizedMekanismRegistry<PurificationRecipe> {

    public PurificationChamber() {
        super(RecipeHandler.Recipe.PURIFICATION_CHAMBER, VirtualizedRegistry.generateAliases("Purifier"));
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public PurificationRecipe add(IIngredient ingredient, GasStack gasInput, ItemStack output) {
        GroovyLog.Msg msg = GroovyLog.msg("Error adding Mekanism Purification Chamber recipe").error();
        msg.add(IngredientHelper.isEmpty(ingredient), () -> "input must not be empty");
        msg.add(IngredientHelper.isEmpty(output), () -> "output must not be empty");
        if (msg.postIfNotEmpty()) return null;

        output = output.copy();
        PurificationRecipe recipe1 = null;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            PurificationRecipe recipe = new PurificationRecipe(new AdvancedMachineInput(itemStack.copy(), gasInput.getGas()), new ItemStackOutput(output));
            if (recipe1 == null) recipe1 = recipe;
            recipeRegistry.put(recipe);
            addScripted(recipe);
        }
        return recipe1;
    }

    public boolean removeByInput(IIngredient ingredient, GasStack gasInput) {
        GroovyLog.Msg msg = GroovyLog.msg("Error removing Mekanism Purification Chamber recipe").error();
        msg.add(IngredientHelper.isEmpty(ingredient), () -> "input must not be empty");
        msg.add(Mekanism.isEmpty(gasInput), () -> "gas input must not be empty");
        if (msg.postIfNotEmpty()) return false;

        boolean found = false;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            PurificationRecipe recipe = recipeRegistry.get().remove(new AdvancedMachineInput(itemStack, gasInput.getGas()));
            if (recipe != null) {
                addBackup(recipe);
                found = true;
            }
        }
        if (!found) {
            removeError("could not find recipe for {} and {}", ingredient, gasInput);
        }
        return found;
    }

    public static class RecipeBuilder extends GasRecipeBuilder<PurificationRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Mekanism Purification Chamber recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            validateGases(msg, 1, 1, 0, 0);
        }

        @Override
        public @Nullable PurificationRecipe register() {
            if (!validate()) return null;
            PurificationRecipe recipe = null;
            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                PurificationRecipe r = new PurificationRecipe(new AdvancedMachineInput(itemStack.copy(), gasInput.get(0).getGas()), new ItemStackOutput(output.get(0)));
                if (recipe == null) recipe = r;
                ModSupport.MEKANISM.get().purificationChamber.add(r);
            }
            return recipe;
        }
    }
}
