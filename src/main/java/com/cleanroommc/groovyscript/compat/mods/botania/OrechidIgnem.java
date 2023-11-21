package com.cleanroommc.groovyscript.compat.mods.botania;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.botania.recipe.OrechidRecipe;
import vazkii.botania.api.BotaniaAPI;

import java.util.ArrayList;
import java.util.List;

public class OrechidIgnem extends Orechid {

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(recipe -> BotaniaAPI.oreWeightsNether.remove(recipe.output));
        restoreFromBackup().forEach(recipe -> BotaniaAPI.oreWeightsNether.put(recipe.output, recipe.weight));
    }

    @Override
    protected List<OrechidRecipe> getAllRecipes() {
        List<OrechidRecipe> list = new ArrayList<>(BotaniaAPI.oreWeightsNether.size());
        BotaniaAPI.oreWeightsNether.forEach((ore, weight) -> list.add(new OrechidRecipe(ore, weight)));
        return list;
    }

    @Override
    public void add(OrechidRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        BotaniaAPI.oreWeightsNether.put(recipe.output, recipe.weight);
    }

    @Override
    public boolean remove(OrechidRecipe recipe) {
        if (recipe == null) return false;
        if (BotaniaAPI.oreWeightsNether.containsKey(recipe.output)) {
            addBackup(recipe);
            BotaniaAPI.oreWeightsNether.remove(recipe.output);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeByOutput(String output) {
        if (BotaniaAPI.oreWeightsNether.containsKey(output)) {
            addBackup(new OrechidRecipe(output, BotaniaAPI.getOreWeightNether(output)));
            BotaniaAPI.oreWeightsNether.remove(output);
            return true;
        }

        GroovyLog.msg("Error removing Botania OrechidIgnem recipe")
                .add("could not find recipe for oredict {}", output)
                .error()
                .post();
        return false;
    }

    @Override
    public void removeAll() {
        getAllRecipes().forEach(this::addBackup);
        BotaniaAPI.oreWeightsNether.clear();
    }
}
