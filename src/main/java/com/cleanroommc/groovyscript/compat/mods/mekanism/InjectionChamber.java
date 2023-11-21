package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.GasRecipeBuilder;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import mekanism.api.gas.GasStack;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.AdvancedMachineInput;
import mekanism.common.recipe.machines.InjectionRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class InjectionChamber extends VirtualizedMekanismRegistry<InjectionRecipe> {

    public InjectionChamber() {
        super(RecipeHandler.Recipe.CHEMICAL_INJECTION_CHAMBER, Alias.generateOf("Injector").andGenerateOfClass(InjectionChamber.class));
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public InjectionRecipe add(IIngredient ingredient, GasStack gasInput, ItemStack output) {
        GroovyLog.Msg msg = GroovyLog.msg("Error adding Mekanism Injection Chamber recipe").error();
        msg.add(IngredientHelper.isEmpty(ingredient), () -> "input must not be empty");
        msg.add(Mekanism.isEmpty(gasInput), () -> "gas input must not be empty");
        msg.add(IngredientHelper.isEmpty(output), () -> "output must not be empty");
        if (msg.postIfNotEmpty()) return null;

        output = output.copy();
        InjectionRecipe recipe1 = null;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            InjectionRecipe recipe = new InjectionRecipe(itemStack.copy(), gasInput.getGas(), output);
            if (recipe1 == null) recipe1 = recipe;
            recipeRegistry.put(recipe);
            addScripted(recipe);
        }
        return recipe1;
    }

    public boolean removeByInput(IIngredient ingredient, GasStack gasInput) {
        GroovyLog.Msg msg = GroovyLog.msg("Error removing Mekanism Injection Chamber recipe").error();
        msg.add(IngredientHelper.isEmpty(ingredient), () -> "input must not be empty");
        msg.add(Mekanism.isEmpty(gasInput), () -> "gas input must not be empty");
        if (msg.postIfNotEmpty()) return false;

        boolean found = false;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            InjectionRecipe recipe = recipeRegistry.get().remove(new AdvancedMachineInput(itemStack, gasInput.getGas()));
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

    public static class RecipeBuilder extends GasRecipeBuilder<InjectionRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Mekanism Injection Chamber recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            validateGases(msg, 1, 1, 0, 0);
        }

        @Override
        public @Nullable InjectionRecipe register() {
            if (!validate()) return null;
            InjectionRecipe recipe = null;
            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                InjectionRecipe r = new InjectionRecipe(itemStack.copy(), gasInput.get(0).getGas(), output.get(0));
                if (recipe == null) recipe = r;
                ModSupport.MEKANISM.get().injectionChamber.add(r);
            }
            return recipe;
        }
    }
}
