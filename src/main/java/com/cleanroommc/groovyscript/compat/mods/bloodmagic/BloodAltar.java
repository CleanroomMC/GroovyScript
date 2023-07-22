package com.cleanroommc.groovyscript.compat.mods.bloodmagic;

import WayofTime.bloodmagic.altar.AltarTier;
import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.api.impl.recipe.RecipeBloodAltar;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.bloodmagic.BloodMagicRecipeRegistrarAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public class BloodAltar extends VirtualizedRegistry<RecipeBloodAltar> {

    public BloodAltar() {
        super();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAltarRecipes()::remove);
        restoreFromBackup().forEach(((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAltarRecipes()::add);
    }

    public RecipeBloodAltar add(Ingredient input, ItemStack output, int minimumTier, int syphon, int consumeRate, int drainRate) {
        RecipeBloodAltar recipe = new RecipeBloodAltar(input, output, minimumTier, syphon, consumeRate, drainRate);
        add(recipe);
        return recipe;
    }

    public void add(RecipeBloodAltar recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        ((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAltarRecipes().add(recipe);
    }

    public boolean remove(RecipeBloodAltar recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        ((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAltarRecipes().remove(recipe);
        return true;
    }

    public boolean removeByInput(IIngredient input) {
        if (((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAltarRecipes().removeIf(recipe -> {
            boolean removeRecipe = recipe.getInput().test(IngredientHelper.toItemStack(input));
            if (removeRecipe) {
                addBackup(recipe);
            }
            return removeRecipe;
        })) {
            return true;
        }

        GroovyLog.msg("Error removing Blood Magic Blood Altar recipe")
                .add("could not find recipe with input {}", input)
                .error()
                .post();
        return false;
    }

    public boolean removeByOutput(ItemStack output) {
        if (((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAltarRecipes().removeIf(recipe -> {
            boolean matches = ItemStack.areItemStacksEqual(recipe.getOutput(), output);
            if (matches) {
                addBackup(recipe);
            }
            return matches;
        })) {
            return true;
        }

        GroovyLog.msg("Error removing Blood Magic Blood Altar recipe")
                .add("could not find recipe with output {}", output)
                .error()
                .post();
        return false;
    }

    public void removeAll() {
        ((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAltarRecipes().forEach(this::addBackup);
        ((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAltarRecipes().clear();
    }

    public SimpleObjectStream<RecipeBloodAltar> streamRecipes() {
        return new SimpleObjectStream<>(((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAltarRecipes())
                .setRemover(this::remove);
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<RecipeBloodAltar> {

        private int minimumTier;
        private int syphon;
        private int consumeRate;
        private int drainRate;

        public RecipeBuilder minimumTier(int minimumTier) {
            this.minimumTier = minimumTier;
            return this;
        }

        public RecipeBuilder tier(int tier) {
            return minimumTier(tier);
        }

        public RecipeBuilder syphon(int syphon) {
            this.syphon = syphon;
            return this;
        }

        public RecipeBuilder consumeRate(int consumeRate) {
            this.consumeRate = consumeRate;
            return this;
        }

        public RecipeBuilder drainRate(int drainRate) {
            this.drainRate = drainRate;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Blood Magic Blood Altar recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(minimumTier < 0, "minimumTier must be a nonnegative integer, yet it was {}", minimumTier);
            msg.add(minimumTier > AltarTier.MAXTIERS, "minimumTier must be less than the max tier (which is {}), yet it was {}", AltarTier.MAXTIERS, minimumTier);
            msg.add(syphon < 0, "syphon must be a nonnegative integer, yet it was {}", syphon);
            msg.add(consumeRate < 0, "consumeRate must be a nonnegative integer, yet it was {}", consumeRate);
            msg.add(drainRate < 0, "drainRate must be a nonnegative integer, yet it was {}", drainRate);
        }

        @Override
        public @Nullable RecipeBloodAltar register() {
            if (!validate()) return null;
            RecipeBloodAltar recipe = ModSupport.BLOOD_MAGIC.get().bloodAltar.add(input.get(0).toMcIngredient(), output.get(0), minimumTier, syphon, consumeRate, drainRate);
            return recipe;
        }
    }
}
