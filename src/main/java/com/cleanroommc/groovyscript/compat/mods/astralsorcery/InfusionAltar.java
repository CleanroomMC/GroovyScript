package com.cleanroommc.groovyscript.compat.mods.astralsorcery;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.infusion.AbstractInfusionRecipe;
import hellfirepvp.astralsorcery.common.crafting.infusion.InfusionRecipeRegistry;
import hellfirepvp.astralsorcery.common.crafting.infusion.recipes.BasicInfusionRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class InfusionAltar extends VirtualizedRegistry<BasicInfusionRecipe> {

    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(r -> InfusionRecipeRegistry.recipes.removeIf(rec -> rec.getUniqueRecipeId() == r.getUniqueRecipeId()));
        restoreFromBackup().forEach(InfusionRecipeRegistry::registerInfusionRecipe);
    }

    public void afterScriptLoad() {
        InfusionRecipeRegistry.compileRecipes();
    }

    private void add(BasicInfusionRecipe recipe) {
        addScripted(recipe);
        InfusionRecipeRegistry.registerInfusionRecipe(recipe);
    }

    public BasicInfusionRecipe add(ItemStack output, IIngredient input, float consumption) {
        return add(output, AstralSorcery.toItemHandle(input), consumption);
    }

    public BasicInfusionRecipe add(ItemStack output, ItemHandle input, float consumption) {
        BasicInfusionRecipe recipe = new BasicInfusionRecipe(output, input);
        recipe.setLiquidStarlightConsumptionChance(consumption);
        InfusionRecipeRegistry.registerInfusionRecipe(recipe);

        addScripted(recipe);

        return recipe;
    }

    private boolean remove(AbstractInfusionRecipe recipe) {
        if (recipe instanceof BasicInfusionRecipe) addBackup((BasicInfusionRecipe) recipe);
        return InfusionRecipeRegistry.recipes.removeIf(rec -> rec.getUniqueRecipeId() == recipe.getUniqueRecipeId());
    }

    public void removeByInput(ItemStack input) {
        InfusionRecipeRegistry.recipes.removeIf(r -> {
            if (r instanceof BasicInfusionRecipe && r.getInput().matchCrafting(input)) {
                addBackup((BasicInfusionRecipe) r);
                return true;
            }
            return false;
        });
    }

    public void removeByOutput(ItemStack output) {
        addBackup((BasicInfusionRecipe) InfusionRecipeRegistry.removeFindRecipeByOutput(output));
    }

    public SimpleObjectStream<AbstractInfusionRecipe> streamRecipes() {
        return new SimpleObjectStream<>(InfusionRecipeRegistry.recipes)
                .setRemover(this::remove);
    }

    public void removeAll() {
        InfusionRecipeRegistry.recipes.removeIf(r -> {
            if (r instanceof BasicInfusionRecipe) {
                addBackup((BasicInfusionRecipe) r);
                return true;
            }
            return false;
        });
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<BasicInfusionRecipe> {

        private float consumption = 0.05F;
        private boolean chalice = true;
        private boolean consumeMultiple = false;
        private int time = 200;


        public RecipeBuilder consumption(float chance) {
            this.consumption = chance;
            return this;
        }

        public RecipeBuilder chalice(boolean chalice) {
            this.chalice = chalice;
            return this;
        }

        public RecipeBuilder chalice() {
            this.chalice = !chalice;
            return this;
        }

        public RecipeBuilder consumeMultiple(boolean consumeMultiple) {
            this.consumeMultiple = consumeMultiple;
            return this;
        }

        public RecipeBuilder consumeMultiple() {
            this.consumeMultiple = !consumeMultiple;
            return this;
        }

        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Astral Infusion recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(consumption < 0 || consumption > 1, "consumption must be a float between 0 and 1, yet it was {}", consumption);
            msg.add(time <= 0, "time must be an integer greater than 0, yet it was {}", time);
        }

        @Override
        public @Nullable BasicInfusionRecipe register() {
            if (!validate()) return null;
            BasicInfusionRecipe recipe = new BasicInfusionRecipe(output.get(0), AstralSorcery.toItemHandle(input.get(0))) {
                public int craftingTickTime() {
                    return time;
                }
            };
            recipe.setLiquidStarlightConsumptionChance(consumption);
            recipe.setCanBeSupportedByChalices(chalice);
            if (consumeMultiple) recipe.setConsumeMultiple();
            recipe.craftingTickTime();
            ModSupport.ASTRAL_SORCERY.get().infusionAltar.add(recipe);
            return recipe;
        }

    }

}
