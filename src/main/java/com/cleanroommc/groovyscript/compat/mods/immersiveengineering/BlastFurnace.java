package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.BlastFurnaceRecipe;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.RecipeStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

public class BlastFurnace extends VirtualizedRegistry<BlastFurnaceRecipe> {

    public BlastFurnace() {
        super("BlastFurnace", "blastfurnace", "blast_furnace");
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

    public BlastFurnaceRecipe add(ItemStack output, Object input, int time, @Nonnull ItemStack slag) {
        BlastFurnaceRecipe recipe = create(output, input, time, slag);
        add(recipe);
        return recipe;
    }

    public void remove(BlastFurnaceRecipe recipe) {
        for (int i = 0; i < BlastFurnaceRecipe.recipeList.size(); i++) {
            BlastFurnaceRecipe rec = BlastFurnaceRecipe.recipeList.get(i);
            if (rec == recipe) {
                addBackup(rec);
                BlastFurnaceRecipe.recipeList.remove(i);
                break;
            }
        }
    }

    public void removeByOutput(ItemStack output) {
        List<BlastFurnaceRecipe> list = BlastFurnaceRecipe.removeRecipes(output);
        if (list.size() > 0) list.forEach(this::addBackup);
    }

    public void removeByInput(ItemStack input) {
        BlastFurnaceRecipe recipe = BlastFurnaceRecipe.findRecipe(input);
        if (recipe != null) {
            remove(recipe);
        }
    }

    public RecipeStream<BlastFurnaceRecipe> stream() {
        return new RecipeStream<>(BlastFurnaceRecipe.recipeList).setRemover(recipe -> {
            BlastFurnaceRecipe recipe1 = BlastFurnaceRecipe.findRecipe(ApiUtils.createIngredientStack(recipe.input).stack);
            if (recipe1 != null) {
                remove(recipe1);
                return true;
            }
            return false;
        });
    }

    public void removeAll() {
        BlastFurnaceRecipe.recipeList.forEach(this::addBackup);
        BlastFurnaceRecipe.recipeList.clear();
    }

    private static BlastFurnaceRecipe create(ItemStack output, Object input, int time, @Nonnull ItemStack slag) {
        if (input instanceof IIngredient) input = ((IIngredient) input).getMatchingStacks();
        return new BlastFurnaceRecipe(output, input, time, slag);
    }

    public static class RecipeBuilder extends TimeRecipeBuilder<BlastFurnaceRecipe> {

        protected ItemStack slag = ItemStack.EMPTY;

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
            validateItems(msg, 1, 1, 1, 2);
            validateFluids(msg);
            if (time < 0) time = 200;
        }

        @Override
        public @Nullable BlastFurnaceRecipe register() {
            if (!validate()) return null;
            return ModSupport.IMMERSIVE_ENGINEERING.get().blastFurnace.add(output.get(0), input.get(0), time, slag);
        }
    }
}
