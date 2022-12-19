package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.api.GroovyLog;
import mekanism.api.infuse.InfuseRegistry;
import mekanism.api.infuse.InfuseType;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.InfusionInput;
import mekanism.common.recipe.machines.MetallurgicInfuserRecipe;
import net.minecraft.item.ItemStack;

public class MetallurgicInfuser extends VirtualizedMekanismRegistry<MetallurgicInfuserRecipe> {

    public MetallurgicInfuser() {
        super(RecipeHandler.Recipe.METALLURGIC_INFUSER);
    }

    public MetallurgicInfuserRecipe add(IIngredient ingredient, String infuseType, int infuseAmount, ItemStack output) {
        InfuseType infuseType1 = InfuseRegistry.get(infuseType);
        GroovyLog.Msg msg = GroovyLog.msg("Error adding Mekanism Metallurgic Infuser recipe").error();
        msg.add(IngredientHelper.isEmpty(ingredient), () -> "input must not be empty");
        msg.add(IngredientHelper.isEmpty(output), () -> "output must not be empty");
        msg.add(infuseType1 == null, () -> infuseType + " is not a valid infuse type");
        if (infuseAmount <= 0) infuseAmount = 40;
        if (msg.postIfNotEmpty()) return null;

        output = output.copy();
        MetallurgicInfuserRecipe recipe1 = null;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            MetallurgicInfuserRecipe recipe = new MetallurgicInfuserRecipe(new InfusionInput(infuseType1, infuseAmount, itemStack.copy()), output);
            if (recipe1 == null) recipe1 = recipe;
            recipeRegistry.put(recipe);
            addScripted(recipe);
        }
        return recipe1;
    }

    public boolean removeByInput(IIngredient ingredient, String infuseType) {
        InfuseType infuseType1 = InfuseRegistry.get(infuseType);
        GroovyLog.Msg msg = GroovyLog.msg("Error removing Mekanism Metallurgic Infuser recipe").error();
        msg.add(IngredientHelper.isEmpty(ingredient), () -> "input must not be empty");
        msg.add(infuseType1 == null, () -> infuseType + " is not a valid infuse type");
        if (msg.postIfNotEmpty()) return false;

        boolean found = false;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            // infuse amount is not hashed so we don't need it
            MetallurgicInfuserRecipe recipe = recipeRegistry.get().remove(new InfusionInput(infuseType1, 1, itemStack));
            if (recipe != null) {
                addBackup(recipe);
                found = true;
            }
        }
        if (!found) {
            removeError("could not find recipe for %s", ingredient);
        }
        return found;
    }
}
