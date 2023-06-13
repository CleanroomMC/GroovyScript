package com.cleanroommc.groovyscript.compat.mods.botania;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.botania.recipe.OrechidRecipe;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import vazkii.botania.api.BotaniaAPI;

import java.util.ArrayList;
import java.util.List;

public class Orechid extends VirtualizedRegistry<OrechidRecipe> {

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(recipe -> BotaniaAPI.oreWeights.remove(recipe.output));
        restoreFromBackup().forEach(recipe -> BotaniaAPI.oreWeights.put(recipe.output, recipe.weight));
    }

    protected List<OrechidRecipe> getAllRecipes() {
        List<OrechidRecipe> recipes = new ArrayList<>(BotaniaAPI.oreWeights.size());
        BotaniaAPI.oreWeights.forEach((ore, weight) -> recipes.add(new OrechidRecipe(ore, weight)));
        return recipes;
    }

    public OrechidRecipe add(String output, int weight) {
        OrechidRecipe recipe = new OrechidRecipe(output, weight);
        add(recipe);
        return recipe;
    }

    public OrechidRecipe add(OreDictIngredient output, int weight) {
        return add(output.getOreDict(), weight);
    }

    public void add(OrechidRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        BotaniaAPI.oreWeights.put(recipe.output, recipe.weight);
    }

    public boolean remove(OrechidRecipe recipe) {
        if (recipe == null) return false;
        if (BotaniaAPI.oreWeights.containsKey(recipe.output)) {
            addBackup(recipe);
            BotaniaAPI.oreWeights.remove(recipe.output);
            return true;
        }
        return false;
    }

    public boolean removeByOutput(String output) {
        if (BotaniaAPI.oreWeights.containsKey(output)) {
            addBackup(new OrechidRecipe(output, BotaniaAPI.getOreWeight(output)));
            BotaniaAPI.oreWeights.remove(output);
            return true;
        }

        GroovyLog.msg("Error removing Botania Orechid recipe")
                .add("could not find recipe for oredict {}", output)
                .error()
                .post();
        return false;
    }

    public boolean removeByOutput(OreDictIngredient output) {
        return removeByOutput(output.getOreDict());
    }

    public void removeAll() {
        getAllRecipes().forEach(this::addBackup);
        BotaniaAPI.oreWeights.clear();
    }

    public SimpleObjectStream<OrechidRecipe> streamRecipes() {
        return new SimpleObjectStream<>(getAllRecipes(), false).setRemover(this::remove);
    }
}
