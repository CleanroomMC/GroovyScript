package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.IngredientWrapper;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.MekanismIngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.ItemStackInput;
import mekanism.common.recipe.machines.CrusherRecipe;
import net.minecraft.item.ItemStack;

import java.util.Iterator;
import java.util.Map.Entry;

public class Crusher extends VirtualizedRegistry<CrusherRecipe> {

    public Crusher() {
        super("Crusher", "crusher");
    }

    public void add(IIngredient ingredient, ItemStack output) {
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            CrusherRecipe recipe = new CrusherRecipe(itemStack, output);
            RecipeHandler.Recipe.CRUSHER.put(recipe);
            addScripted(recipe);
        }
    }

    public void remove(ItemStack input, ItemStack output) {
        IngredientWrapper inputIngredient = new IngredientWrapper((IIngredient) (Object) input);
        IngredientWrapper outputIngredient = new IngredientWrapper((IIngredient) (Object) output);
        Iterator<Entry<ItemStackInput, CrusherRecipe>> iter = RecipeHandler.Recipe.CRUSHER.get().entrySet().iterator();
        while (iter.hasNext()) {
            Entry<ItemStackInput, CrusherRecipe> entry = iter.next();
            if (MekanismIngredientHelper.matches(entry.getKey(), inputIngredient) && MekanismIngredientHelper.matches(entry.getValue().recipeOutput, outputIngredient)) {
                addBackup(entry.getValue());
                iter.remove();
            }
        }
    }

    @Override
    public void onReload() {

    }

}
