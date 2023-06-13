package com.cleanroommc.groovyscript.compat.mods.actuallyadditions;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.api.recipe.WeightedOre;
import org.jetbrains.annotations.Nullable;

public class NetherMiningLens extends VirtualizedRegistry<WeightedOre> {

    public NetherMiningLens() {
        super();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(ActuallyAdditionsAPI.NETHERRACK_ORES::remove);
        ActuallyAdditionsAPI.NETHERRACK_ORES.addAll(restoreFromBackup());
    }

    public WeightedOre add(String oreName, int weight) {
        WeightedOre recipe = new WeightedOre(oreName, weight);
        add(recipe);
        return recipe;
    }

    public void add(WeightedOre recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        ActuallyAdditionsAPI.NETHERRACK_ORES.add(recipe);
    }

    public boolean remove(WeightedOre recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        ActuallyAdditionsAPI.NETHERRACK_ORES.remove(recipe);
        return true;
    }

    public boolean removeByOre(OreDictIngredient ore) {
        return this.removeByOre(ore.getOreDict());
    }

    public boolean removeByOre(String oreName) {
        return ActuallyAdditionsAPI.NETHERRACK_ORES.removeIf(recipe -> {
            boolean found = oreName.equals(recipe.name);
            if (found) {
                addBackup(recipe);
            }
            return found;
        });
    }

    public void removeAll() {
        ActuallyAdditionsAPI.NETHERRACK_ORES.forEach(this::addBackup);
        ActuallyAdditionsAPI.NETHERRACK_ORES.clear();
    }

    public SimpleObjectStream<WeightedOre> streamRecipes() {
        return new SimpleObjectStream<>(ActuallyAdditionsAPI.NETHERRACK_ORES)
                .setRemover(this::remove);
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<WeightedOre> {

        private String ore;
        private int weight;

        public RecipeBuilder ore(String ore) {
            this.ore = ore;
            return this;
        }

        public RecipeBuilder ore(OreDictIngredient ore) {
            this.ore = ore.getOreDict();
            return this;
        }

        public RecipeBuilder weight(int weight) {
            this.weight = weight;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Actually Additions Nether Mining Lens recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg);
            msg.add(ore == null, "ore must be defined");
            msg.add(weight < 0, "weight must be a non negative integer, yet it was {}", weight);
        }

        @Override
        public @Nullable WeightedOre register() {
            if (!validate()) return null;
            WeightedOre recipe = new WeightedOre(ore, weight);
            ModSupport.ACTUALLY_ADDITIONS.get().netherMiningLens.add(recipe);
            return recipe;
        }
    }
}
