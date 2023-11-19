package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import mekanism.api.infuse.InfuseRegistry;
import mekanism.api.infuse.InfuseType;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.InfusionInput;
import mekanism.common.recipe.machines.MetallurgicInfuserRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class MetallurgicInfuser extends VirtualizedMekanismRegistry<MetallurgicInfuserRecipe> {

    public MetallurgicInfuser() {
        super(RecipeHandler.Recipe.METALLURGIC_INFUSER);
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public MetallurgicInfuserRecipe add(IIngredient ingredient, InfuseType infuseType, int infuseAmount, ItemStack output) {
        GroovyLog.Msg msg = GroovyLog.msg("Error adding Mekanism Metallurgic Infuser recipe").error();
        msg.add(IngredientHelper.isEmpty(ingredient), () -> "input must not be empty");
        msg.add(IngredientHelper.isEmpty(output), () -> "output must not be empty");
        msg.add(infuseType == null, () -> "invalid infusion type");
        if (infuseAmount <= 0) infuseAmount = 40;
        if (msg.postIfNotEmpty()) return null;

        output = output.copy();
        MetallurgicInfuserRecipe recipe1 = null;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            MetallurgicInfuserRecipe recipe = new MetallurgicInfuserRecipe(new InfusionInput(infuseType, infuseAmount, itemStack.copy()), output);
            if (recipe1 == null) recipe1 = recipe;
            recipeRegistry.put(recipe);
            addScripted(recipe);
        }
        return recipe1;
    }

    public MetallurgicInfuserRecipe add(IIngredient ingredient, String infuseType, int infuseAmount, ItemStack output) {
        return add(ingredient, InfuseRegistry.get(infuseType), infuseAmount, output);
    }

    public boolean removeByInput(IIngredient ingredient, InfuseType infuseType) {
        GroovyLog.Msg msg = GroovyLog.msg("Error removing Mekanism Metallurgic Infuser recipe").error();
        msg.add(IngredientHelper.isEmpty(ingredient), () -> "input must not be empty");
        msg.add(infuseType == null, () -> "invalid infusion type");
        if (msg.postIfNotEmpty()) return false;

        boolean found = false;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            // infuse amount is not hashed so we don't need it
            MetallurgicInfuserRecipe recipe = recipeRegistry.get().remove(new InfusionInput(infuseType, 1, itemStack));
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

    public boolean removeByInput(IIngredient ingredient, String infuseType) {
        return removeByInput(ingredient, InfuseRegistry.get(infuseType));
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<MetallurgicInfuserRecipe> {

        private InfuseType infuse;
        private int amount;

        public RecipeBuilder infuse(InfuseType infuse) {
            this.infuse = infuse;
            return this;
        }

        public RecipeBuilder infuse(String infuse) {
            return infuse(InfuseRegistry.get(infuse));
        }

        public RecipeBuilder amount(int amount) {
            this.amount = amount;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Mekanism Metallurgic Infuser recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(infuse == null, "infuse must be defined");
            msg.add(amount <= 0, "amount must be an integer greater than 0, yet it was {}", amount);
        }

        @Override
        public @Nullable MetallurgicInfuserRecipe register() {
            if (!validate()) return null;
            MetallurgicInfuserRecipe recipe = null;
            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                MetallurgicInfuserRecipe r = new MetallurgicInfuserRecipe(new InfusionInput(infuse, amount, itemStack.copy()), output.get(0));
                if (recipe == null) recipe = r;
                ModSupport.MEKANISM.get().metallurgicInfuser.add(r);
            }
            return recipe;
        }
    }
}
