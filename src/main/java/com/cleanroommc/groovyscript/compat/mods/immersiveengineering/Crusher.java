package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.crafting.CrusherRecipe;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.EnergyRecipeBuilder;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Crusher extends VirtualizedRegistry<CrusherRecipe> {

    public Crusher() {
        super("Crusher", "crusher");
    }

    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> CrusherRecipe.recipeList.removeIf(r -> r == recipe));
        CrusherRecipe.recipeList.addAll(restoreFromBackup());
    }

    public void add(CrusherRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            CrusherRecipe.recipeList.add(recipe);
        }
    }

    public CrusherRecipe add(ItemStack output, Object input, int energy) {
        CrusherRecipe recipe = create(output, input, energy);
        addScripted(recipe);
        return recipe;
    }

    public boolean remove(CrusherRecipe recipe) {
        if (CrusherRecipe.recipeList.removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    public void removeByOutput(ItemStack output) {
        List<CrusherRecipe> list = CrusherRecipe.removeRecipesForOutput(output);
        if (list.size() > 0) list.forEach(this::addBackup);
    }

    public void removeByInput(ItemStack input) {
        List<CrusherRecipe> list = CrusherRecipe.removeRecipesForInput(input);
        if (list.size() > 0) list.forEach(this::addBackup);
    }

    public SimpleObjectStream<CrusherRecipe> stream() {
        return new SimpleObjectStream<>(CrusherRecipe.recipeList).setRemover(this::remove);
    }

    public void removeAll() {
        CrusherRecipe.recipeList.forEach(this::addBackup);
        CrusherRecipe.recipeList.clear();
    }

    private static CrusherRecipe create(ItemStack output, Object input, int energy) {
        if (input instanceof IIngredient) input = ((IIngredient) input).getMatchingStacks();
        return CrusherRecipe.addRecipe(output, input, energy);
    }

    public static class RecipeBuilder extends EnergyRecipeBuilder<CrusherRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Engineering Crusher recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            if (energy < 0) energy = 200;
        }

        @Override
        public @Nullable CrusherRecipe register() {
            if (!validate()) return null;
            return ModSupport.IMMERSIVE_ENGINEERING.get().crusher.add(output.get(0), input.get(0), energy);
        }
    }
}
