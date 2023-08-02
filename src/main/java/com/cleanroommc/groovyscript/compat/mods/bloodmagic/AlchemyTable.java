package com.cleanroommc.groovyscript.compat.mods.bloodmagic;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.api.impl.recipe.RecipeAlchemyTable;
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
import net.minecraft.util.NonNullList;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class AlchemyTable extends VirtualizedRegistry<RecipeAlchemyTable> {

    public AlchemyTable() {
        super();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAlchemyRecipes()::remove);
        restoreFromBackup().forEach(((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAlchemyRecipes()::add);
    }

    public RecipeAlchemyTable add(NonNullList<Ingredient> input, ItemStack output, int syphon, int ticks, int minimumTier) {
        RecipeAlchemyTable recipe = new RecipeAlchemyTable(input, output, syphon, ticks, minimumTier);
        add(recipe);
        return recipe;
    }

    public void add(RecipeAlchemyTable recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        ((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAlchemyRecipes().add(recipe);
    }

    public boolean remove(RecipeAlchemyTable recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        ((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAlchemyRecipes().remove(recipe);
        return true;
    }

    public boolean removeByInput(IIngredient... input) {
        NonNullList<IIngredient> inputs = NonNullList.create();
        Collections.addAll(inputs, input);
        return removeByInput(inputs);
    }

    public boolean removeByInput(NonNullList<IIngredient> input) {
        // Filters down to only recipes which have inputs that match all the input IIngredients (NOTE: a recipe with ABCD would match an input of AB)
        if (((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAlchemyRecipes().removeIf(recipe -> {
            boolean removeRecipe = false;
            for (IIngredient match : input) {
                boolean foundInputMatch = false;
                for (Ingredient target : recipe.getInput()) {
                    if (target.test(IngredientHelper.toItemStack(match))) foundInputMatch = true;
                }
                removeRecipe = foundInputMatch;
            }
            if (removeRecipe) {
                addBackup(recipe);
            }
            return removeRecipe;
        })) {
            return true;
        }

        GroovyLog.msg("Error removing Blood Magic Alchemy Table recipe")
                .add("could not find recipe with inputs including all of {}", input)
                .error()
                .post();
        return false;
    }

    public boolean removeByOutput(ItemStack output) {
        if (((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAlchemyRecipes().removeIf(recipe -> {
            boolean matches = ItemStack.areItemStacksEqual(recipe.getOutput(), output);
            if (matches) {
                addBackup(recipe);
            }
            return matches;
        })) {
            return true;
        }
        GroovyLog.msg("Error removing Blood Magic Alchemy Table recipe")
                .add("could not find recipe with output {}", output)
                .error()
                .post();
        return false;
    }

    public void removeAll() {
        ((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAlchemyRecipes().forEach(this::addBackup);
        ((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAlchemyRecipes().clear();
    }

    public SimpleObjectStream<RecipeAlchemyTable> streamRecipes() {
        return new SimpleObjectStream<>(((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAlchemyRecipes())
                .setRemover(this::remove);
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<RecipeAlchemyTable> {

        private int syphon;
        private int ticks;
        private int minimumTier;

        public RecipeBuilder syphon(int syphon) {
            this.syphon = syphon;
            return this;
        }

        public RecipeBuilder drain(int drain) {
            return syphon(drain);
        }

        public RecipeBuilder ticks(int ticks) {
            this.ticks = ticks;
            return this;
        }

        public RecipeBuilder time(int time) {
            return ticks(time);
        }

        public RecipeBuilder minimumTier(int minimumTier) {
            this.minimumTier = minimumTier;
            return this;
        }

        public RecipeBuilder tier(int tier) {
            return minimumTier(tier);
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Blood Magic Alchemy Table recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 6, 1, 1);
            msg.add(syphon < 0, "syphon must be a nonnegative integer, yet it was {}", syphon);
            msg.add(ticks <= 0, "ticks must be a positive integer greater than 0, yet it was {}", ticks);
            msg.add(minimumTier < 0, "minimumTier must be a nonnegative integer, yet it was {}", minimumTier);
        }

        @Override
        public @Nullable RecipeAlchemyTable register() {
            if (!validate()) return null;
            RecipeAlchemyTable recipe = ModSupport.BLOOD_MAGIC.get().alchemyTable.add(IngredientHelper.toIngredientNonNullList(input), output.get(0), syphon, ticks, minimumTier);
            return recipe;
        }
    }
}
