package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.crafting.BlastFurnaceRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

public class BlastFurnace extends VirtualizedRegistry<BlastFurnaceRecipe> {

    public BlastFurnace() {
        super();
    }

    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> BlastFurnaceRecipe.recipeList.removeIf(r -> r == recipe));
        BlastFurnaceRecipe.recipeList.addAll(restoreFromBackup());
    }

    public void add(BlastFurnaceRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            BlastFurnaceRecipe.recipeList.add(recipe);
        }
    }

    public BlastFurnaceRecipe add(ItemStack output, IIngredient input, int time, @Nonnull ItemStack slag) {
        BlastFurnaceRecipe recipe = new BlastFurnaceRecipe(output.copy(), ImmersiveEngineering.toIEInput(input), time, IngredientHelper.copy(slag));
        add(recipe);
        return recipe;
    }

    public boolean remove(BlastFurnaceRecipe recipe) {
        if (BlastFurnaceRecipe.recipeList.removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    public void removeByOutput(ItemStack output) {
        if (IngredientHelper.isEmpty(output)) {
            GroovyLog.msg("Error removing Immersive Engineering Blast Furnace recipe")
                    .add("output must not be empty")
                    .error()
                    .post();
            return;
        }
        List<BlastFurnaceRecipe> list = BlastFurnaceRecipe.removeRecipes(output);
        if (list.isEmpty()) {
            GroovyLog.msg("Error removing Immersive Engineering Blast Furnace recipe")
                    .add("no recipes found for {}", output)
                    .error()
                    .post();
            return;
        }
        list.forEach(this::addBackup);
    }

    public void removeByInput(ItemStack input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Immersive Engineering Blast Furnace recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
            return;
        }
        BlastFurnaceRecipe recipe = BlastFurnaceRecipe.findRecipe(input);
        if (recipe != null) {
            remove(recipe);
        } else {
            GroovyLog.msg("Error removing Immersive Engineering Blast Furnace recipe")
                    .add("no recipes found for {}", input)
                    .error()
                    .post();
        }
    }

    public SimpleObjectStream<BlastFurnaceRecipe> streamRecipes() {
        return new SimpleObjectStream<>(BlastFurnaceRecipe.recipeList).setRemover(this::remove);
    }

    public void removeAll() {
        BlastFurnaceRecipe.recipeList.forEach(this::addBackup);
        BlastFurnaceRecipe.recipeList.clear();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<BlastFurnaceRecipe> {

        private int time;
        private ItemStack slag;

        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        public RecipeBuilder slag(ItemStack slag) {
            this.slag = slag;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Engineering Blast Furnace recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            if (time < 0) time = 200;
            if (slag == null) slag = ItemStack.EMPTY;
        }

        @Override
        public @Nullable BlastFurnaceRecipe register() {
            if (!validate()) return null;
            return ModSupport.IMMERSIVE_ENGINEERING.get().blastFurnace.add(output.get(0), input.get(0), time, slag);
        }
    }
}
