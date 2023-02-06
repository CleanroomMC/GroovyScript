package com.cleanroommc.groovyscript.compat.mods.astralsorcery;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import hellfirepvp.astralsorcery.common.crafting.infusion.InfusionRecipeRegistry;
import hellfirepvp.astralsorcery.common.crafting.infusion.recipes.BasicInfusionRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class InfusionAltar extends VirtualizedRegistry<BasicInfusionRecipe> {

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(this::remove);
        removeScripted().forEach(this::add);
    }

    private void add(BasicInfusionRecipe recipe) {
        InfusionRecipeRegistry.registerInfusionRecipe(recipe);
        InfusionRecipeRegistry.compileRecipes();
    }

    public BasicInfusionRecipe add(ItemStack output, ItemStack input, float consumption) {
        BasicInfusionRecipe recipe = new BasicInfusionRecipe(output, input);
        recipe.setLiquidStarlightConsumptionChance(consumption);
        InfusionRecipeRegistry.registerInfusionRecipe(recipe);
        InfusionRecipeRegistry.compileRecipes();

        addScripted(recipe);

        return recipe;
    }

    private void remove(BasicInfusionRecipe recipe) {
        InfusionRecipeRegistry.recipes.removeIf(rec -> rec.getUniqueRecipeId() == recipe.getUniqueRecipeId());
        InfusionRecipeRegistry.compileRecipes();
    }

    public void removeByOutput(ItemStack output) {
        addBackup((BasicInfusionRecipe) InfusionRecipeRegistry.removeFindRecipeByOutput(output));
        InfusionRecipeRegistry.compileRecipes();
    }

    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<BasicInfusionRecipe> {

        private float consumption = 0.05F;

        public RecipeBuilder consumption(float chance) {
            this.consumption = chance;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Astral Infusion recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 64, 1, 64);
            msg.add(this.consumption <= 0.0F, () -> "Consumption must be a positive value.");
        }

        @Override
        public @Nullable BasicInfusionRecipe register() {
            if (!validate()) return null;
            return ModSupport.ASTRAL_SORCERY.get().infusionAltar.add(output.get(0), input.get(0).getMatchingStacks()[0], consumption);
        }

    }

}
