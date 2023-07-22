package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.ItemStackInput;
import mekanism.common.recipe.machines.EnrichmentRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class EnrichmentChamber extends VirtualizedMekanismRegistry<EnrichmentRecipe> {

    public EnrichmentChamber() {
        super(RecipeHandler.Recipe.ENRICHMENT_CHAMBER, VirtualizedRegistry.generateAliases("Enricher"));
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public EnrichmentRecipe add(IIngredient ingredient, ItemStack output) {
        GroovyLog.Msg msg = GroovyLog.msg("Error adding Mekanism Enrichment Chamber recipe").error();
        msg.add(IngredientHelper.isEmpty(ingredient), () -> "input must not be empty");
        msg.add(IngredientHelper.isEmpty(output), () -> "output must not be empty");
        if (msg.postIfNotEmpty()) return null;

        output = output.copy();
        EnrichmentRecipe recipe1 = null;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            EnrichmentRecipe recipe = new EnrichmentRecipe(itemStack.copy(), output);
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
            EnrichmentRecipe recipe = recipeRegistry.get().remove(new ItemStackInput(itemStack));
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

    public static class RecipeBuilder extends AbstractRecipeBuilder<EnrichmentRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Mekanism Enrichment Chamber recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
        }

        @Override
        public @Nullable EnrichmentRecipe register() {
            if (!validate()) return null;
            EnrichmentRecipe recipe = null;
            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                EnrichmentRecipe r = new EnrichmentRecipe(itemStack.copy(), output.get(0));
                if (recipe == null) recipe = r;
                ModSupport.MEKANISM.get().enrichmentChamber.add(r);
            }
            return recipe;
        }
    }
}
