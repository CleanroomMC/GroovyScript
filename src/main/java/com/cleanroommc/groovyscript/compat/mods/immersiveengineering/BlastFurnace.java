package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.BlastFurnaceRecipe;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.IngredientHelper;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
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
        List<BlastFurnaceRecipe> list = BlastFurnaceRecipe.removeRecipes(output);
        if (list.size() > 0) list.forEach(this::addBackup);
    }

    public void removeByInput(ItemStack input) {
        BlastFurnaceRecipe recipe = BlastFurnaceRecipe.findRecipe(input);
        if (recipe != null) {
            remove(recipe);
        }
    }

    public SimpleObjectStream<BlastFurnaceRecipe> stream() {
        return new SimpleObjectStream<>(BlastFurnaceRecipe.recipeList).setRemover(this::remove);
    }

    public void removeAll() {
        BlastFurnaceRecipe.recipeList.forEach(this::addBackup);
        BlastFurnaceRecipe.recipeList.clear();
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
