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
import mekanism.common.recipe.inputs.ItemStackInput;
import mekanism.common.recipe.machines.DissolutionRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class DissolutionChamber extends VirtualizedMekanismRegistry<DissolutionRecipe> {

    public DissolutionChamber() {
        super(RecipeHandler.Recipe.CHEMICAL_DISSOLUTION_CHAMBER, Alias.generateOf("Dissolver").andGenerateOfClass(DissolutionChamber.class));
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public DissolutionRecipe add(IIngredient ingredient, GasStack output) {
        GroovyLog.Msg msg = GroovyLog.msg("Error adding Mekanism Dissolution Chamber recipe").error();
        msg.add(IngredientHelper.isEmpty(ingredient), () -> "input must not be empty");
        msg.add(Mekanism.isEmpty(output), () -> "output must not be empty");
        if (msg.postIfNotEmpty()) return null;

        output = output.copy();
        DissolutionRecipe recipe1 = null;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            DissolutionRecipe recipe = new DissolutionRecipe(itemStack.copy(), output);
            if (recipe1 == null) recipe1 = recipe;
            recipeRegistry.put(recipe);
            addScripted(recipe);
        }
        return recipe1;
    }

    public boolean removeByInput(IIngredient ingredient) {
        if (IngredientHelper.isEmpty(ingredient)) {
            removeError("input must not be empty");
            return false;
        }
        boolean found = false;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            DissolutionRecipe recipe = recipeRegistry.get().remove(new ItemStackInput(itemStack));
            if (recipe != null) {
                addBackup(recipe);
                found = true;
            }
        }
        if (!found) {
            removeError("could not find recipe for {}", ingredient);
        }
        return found;
    }

    public static class RecipeBuilder extends GasRecipeBuilder<DissolutionRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Mekanism Dissolution Chamber recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg);
            validateGases(msg, 0, 0, 1, 1);
        }

        @Override
        public @Nullable DissolutionRecipe register() {
            if (!validate()) return null;
            DissolutionRecipe recipe = null;
            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                DissolutionRecipe r = new DissolutionRecipe(itemStack.copy(), gasOutput.get(0));
                if (recipe == null) recipe = r;
                ModSupport.MEKANISM.get().dissolutionChamber.add(r);
            }
            return recipe;
        }
    }
}
