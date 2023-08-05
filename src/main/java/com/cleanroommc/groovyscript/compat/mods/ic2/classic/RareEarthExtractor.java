package com.cleanroommc.groovyscript.compat.mods.ic2.classic;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import ic2.api.classic.recipe.ClassicRecipes;
import ic2.api.classic.recipe.machine.IRareEarthExtractorRecipeList;
import net.minecraft.item.ItemStack;

public class RareEarthExtractor extends VirtualizedRegistry<IRareEarthExtractorRecipeList.EarthEntry> {

    public RareEarthExtractor() {
        super();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> ClassicRecipes.earthExtractor.removeEntry(recipe.getItem()));
        restoreFromBackup().forEach(recipe -> ClassicRecipes.earthExtractor.registerValue(recipe.getEarthValue(), recipe.getItem()));
    }

    public void add(IRareEarthExtractorRecipeList.EarthEntry entry) {
        ClassicRecipes.earthExtractor.registerValue(entry.getEarthValue(), entry.getItem());
        addScripted(entry);
    }

    public void add(IIngredient input, float value) {
        if (GroovyLog.msg("Error adding Industrialcraft 2 Rare Earth Extractor recipe")
                .add(IngredientHelper.isEmpty(input), () -> "input must not be empty")
                .add(value <= 0, () -> "value must be higher than zero")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (ItemStack stack : input.getMatchingStacks()) {
            IRareEarthExtractorRecipeList.EarthEntry entry = new IRareEarthExtractorRecipeList.EarthEntry(value, stack);
            add(entry);
        }
    }

    public SimpleObjectStream<IRareEarthExtractorRecipeList.EarthEntry> streamRecipes() {
        return new SimpleObjectStream<>(ClassicRecipes.earthExtractor.getRecipeList()).setRemover(r -> this.remove(r.getItem()));
    }

    public boolean remove(ItemStack input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Rare Earth Extractor recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
            return false;
        }
        for (IRareEarthExtractorRecipeList.EarthEntry entry : ClassicRecipes.earthExtractor.getRecipeList()) {
            if (ItemStack.areItemStacksEqual(entry.getItem(), input)) {
                remove(entry);
                return true;
            }
        }
        GroovyLog.msg("Error removing Rare Earth Extractor recipe")
                .add("no recipes found for {}", input)
                .error()
                .post();
        return false;
    }

    public void removeAll() {
        for (IRareEarthExtractorRecipeList.EarthEntry entry : ClassicRecipes.earthExtractor.getRecipeList()) {
            remove(entry);
        }
    }

    public void remove(IRareEarthExtractorRecipeList.EarthEntry entry) {
        ClassicRecipes.earthExtractor.removeEntry(entry.getItem());
        addBackup(entry);
    }
}
