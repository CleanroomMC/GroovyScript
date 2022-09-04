package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.ComparableItemStack;
import blusunrize.immersiveengineering.api.crafting.MetalPressRecipe;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.EnergyRecipeBuilder;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class MetalPress extends VirtualizedRegistry<MetalPressRecipe> {

    public MetalPress() {
        super("MetalPress", "metalpress");
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> {
            if (MetalPressRecipe.recipeList.containsValue(recipe))
                MetalPressRecipe.recipeList.remove(recipe.mold, recipe);
        });
        restoreFromBackup().forEach(recipe -> MetalPressRecipe.recipeList.put(recipe.mold, recipe));
    }

    public void add(MetalPressRecipe recipe) {
        if (recipe != null) {
            MetalPressRecipe.recipeList.put(recipe.mold, recipe);
            addScripted(recipe);
        }
    }

    public MetalPressRecipe add(ItemStack output, Object input, ItemStack mold, int energy) {
        MetalPressRecipe recipe = create(output, input, mold, energy);
        addScripted(recipe);
        return recipe;
    }

    public boolean remove(MetalPressRecipe recipe) {
        if (recipe != null && MetalPressRecipe.recipeList.get(recipe.mold).removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    public void removeByOutput(ItemStack output) {
        List<MetalPressRecipe> list = MetalPressRecipe.removeRecipes(output);
        if (list.size() > 0) list.forEach(this::addBackup);
    }

    public void removeByOutput(ItemStack mold, ItemStack output) {
        ComparableItemStack comparable = ApiUtils.createComparableItemStack(mold, false);
        if (MetalPressRecipe.recipeList.containsKey(comparable)) {
            for (MetalPressRecipe recipe : MetalPressRecipe.recipeList.get(comparable)) {
                if (ApiUtils.stackMatchesObject(output, recipe.output)) {
                    addBackup(recipe);
                    MetalPressRecipe.recipeList.remove(comparable, recipe);
                    break;
                }
            }
        }
    }

    public void removeByInput(ItemStack mold, ItemStack input) {
        ComparableItemStack comparable = ApiUtils.createComparableItemStack(mold, false);
        if (MetalPressRecipe.recipeList.containsKey(comparable)) {
            for (MetalPressRecipe recipe : MetalPressRecipe.recipeList.get(comparable)) {
                if (ApiUtils.stackMatchesObject(input, recipe.input)) {
                    addBackup(recipe);
                    MetalPressRecipe.recipeList.remove(comparable, recipe);
                    break;
                }
            }
        }
    }

    public void removeByInput(ItemStack input) {
        for (Iterator<MetalPressRecipe> iterator = MetalPressRecipe.recipeList.values().iterator(); iterator.hasNext(); ) {
            MetalPressRecipe recipe = iterator.next();
            if (recipe.input.matches(input)) {
                addBackup(recipe);
                iterator.remove();
            }
        }
    }

    public void removeByMold(ItemStack mold) {
        List<MetalPressRecipe> list = MetalPressRecipe.recipeList.removeAll(ApiUtils.createComparableItemStack(mold, false));
        if (list.size() > 0) list.forEach(this::addBackup);
    }

    public void removeAll() {
        MetalPressRecipe.recipeList.values().forEach(this::addBackup);
        MetalPressRecipe.recipeList.clear();
    }

    public SimpleObjectStream<MetalPressRecipe> streamRecipes() {
        List<MetalPressRecipe> recipes = new ArrayList<>(MetalPressRecipe.recipeList.values());
        return new SimpleObjectStream<>(recipes).setRemover(this::remove);
    }

    private static MetalPressRecipe create(ItemStack output, Object input, ItemStack mold, int energy) {
        if (input instanceof IIngredient) input = ((IIngredient) input).getMatchingStacks();
        return MetalPressRecipe.addRecipe(output, input, mold, energy);
    }

    public static class RecipeBuilder extends EnergyRecipeBuilder<MetalPressRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Engineering Metal Press recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 2, 2, 1, 1);
            validateFluids(msg);
        }

        @Override
        public @Nullable MetalPressRecipe register() {
            if (!validate()) return null;
            for (ItemStack stack : input.get(1).getMatchingStacks()) {
                return ModSupport.IMMERSIVE_ENGINEERING.get().metalPress.add(output.get(0), input.get(0), stack, energy);
            }
            return null;
        }
    }
}
