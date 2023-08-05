package com.cleanroommc.groovyscript.compat.mods.enderio;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.enderio.recipe.EnderIORecipeBuilder;
import com.cleanroommc.groovyscript.compat.mods.enderio.recipe.RecipeInput;
import com.cleanroommc.groovyscript.compat.mods.enderio.recipe.SagRecipe;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import crazypants.enderio.base.recipe.Recipe;
import crazypants.enderio.base.recipe.RecipeBonusType;
import crazypants.enderio.base.recipe.RecipeLevel;
import crazypants.enderio.base.recipe.RecipeOutput;
import crazypants.enderio.base.recipe.sagmill.SagMillRecipeManager;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SagMill extends VirtualizedRegistry<Recipe> {

    public SagMill() {
        super("SAGMill", "Sag", "sag");
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void add(Recipe recipe) {
        SagMillRecipeManager.getInstance().addRecipe(recipe);
        addScripted(recipe);
    }

    public boolean remove(Recipe recipe) {
        if (recipe == null) return false;
        SagMillRecipeManager.getInstance().getRecipes().remove(recipe);
        addBackup(recipe);
        return true;
    }

    public void removeByInput(ItemStack input) {
        Recipe recipe = (Recipe) SagMillRecipeManager.getInstance().getRecipeForInput(RecipeLevel.IGNORE, input);
        if (recipe == null) {
            GroovyLog.get().error("Can't find EnderIO Sag Mill recipe for input " + input);
        } else {
            SagMillRecipeManager.getInstance().getRecipes().remove(recipe);
            addBackup(recipe);
        }
    }

    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(SagMillRecipeManager.getInstance().getRecipes()::remove);
        restoreFromBackup().forEach(SagMillRecipeManager.getInstance().getRecipes()::add);
    }

    public SimpleObjectStream<Recipe> streamRecipes() {
        return new SimpleObjectStream<>(SagMillRecipeManager.getInstance().getRecipes())
                .setRemover(this::remove);
    }

    public void removeAll() {
        SagMillRecipeManager.getInstance().getRecipes().forEach(this::addBackup);
        SagMillRecipeManager.getInstance().getRecipes().clear();
    }

    public static class RecipeBuilder extends EnderIORecipeBuilder<Recipe> {

        private final FloatList chances = new FloatArrayList();
        private RecipeBonusType bonusType = RecipeBonusType.NONE;

        public RecipeBuilder output(ItemStack itemStack, float chance) {
            this.output.add(itemStack);
            this.chances.add(Math.max(0, chance));
            return this;
        }

        @Override
        public AbstractRecipeBuilder<Recipe> output(ItemStack output) {
            return output(output, 1f);
        }

        public RecipeBuilder bonusTypeNone() {
            this.bonusType = RecipeBonusType.NONE;
            return this;
        }

        public RecipeBuilder bonusTypeMultiply() {
            this.bonusType = RecipeBonusType.MULTIPLY_OUTPUT;
            return this;
        }

        public RecipeBuilder bonusTypeChance() {
            this.bonusType = RecipeBonusType.CHANCE_ONLY;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding EnderIO Sag Mill recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 4);
            validateFluids(msg);
            if (energy <= 0) energy = 5000;
        }

        @Override
        public @Nullable Recipe register() {
            if (!validate()) return null;
            RecipeOutput[] outputs = new RecipeOutput[output.size()];
            for (int i = 0; i < outputs.length; i++) {
                outputs[i] = new RecipeOutput(output.get(i), chances.get(i));
            }
            Recipe recipe = new SagRecipe(new RecipeInput(input.get(0)), energy, bonusType, level, outputs);
            ModSupport.ENDER_IO.get().sagMill.add(recipe);
            return recipe;
        }
    }
}
