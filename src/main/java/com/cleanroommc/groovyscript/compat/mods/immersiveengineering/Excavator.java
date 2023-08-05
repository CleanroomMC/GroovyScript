package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.tool.ExcavatorHandler;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class Excavator extends VirtualizedRegistry<Pair<ExcavatorHandler.MineralMix, Integer>> {

    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> ExcavatorHandler.mineralList.entrySet().removeIf(r -> r.getKey() == recipe.getKey()));
        restoreFromBackup().forEach(recipe -> ExcavatorHandler.mineralList.put(recipe.getKey(), recipe.getValue()));
    }

    public void add(ExcavatorHandler.MineralMix recipe, Integer weight) {
        if (recipe != null) {
            addScripted(Pair.of(recipe, weight));
            ExcavatorHandler.mineralList.put(recipe, weight);
        }
    }

    public ExcavatorHandler.MineralMix add(String name, int mineralWeight, float failChance, String[] ores, float[] chances) {
        ExcavatorHandler.MineralMix recipe = new ExcavatorHandler.MineralMix(name, failChance, ores, chances);
        add(recipe, mineralWeight);
        return recipe;
    }

    public boolean remove(ExcavatorHandler.MineralMix recipe) {
        int weight = ExcavatorHandler.mineralList.get(recipe);
        if (weight > 0) {
            ExcavatorHandler.mineralList.entrySet().removeIf(r -> r.getKey() == recipe);
            addBackup(Pair.of(recipe, weight));
            return true;
        }
        return false;
    }

    public boolean removeByMineral(String key) {
        List<ExcavatorHandler.MineralMix> entries = ExcavatorHandler.mineralList.keySet().stream()
                .filter(r -> r.name.equalsIgnoreCase(key))
                .collect(Collectors.toList());
        for (ExcavatorHandler.MineralMix recipe : entries) {
            remove(recipe);
        }
        if (entries.isEmpty()) {
            GroovyLog.msg("Error removing Immersive Engineering Excavator entry")
                    .add("no entries found for {}", key)
                    .error()
                    .post();
            return false;
        }
        return true;
    }

    public void removeByOres(String... ores) {
        if (ores == null || ores.length == 0) {
            GroovyLog.msg("Error removing Immersive Engineering Excavator entry")
                    .add("ores must not be empty")
                    .error()
                    .post();
            return;
        }
        List<ExcavatorHandler.MineralMix> entries = ExcavatorHandler.mineralList.keySet().stream()
                .filter(r -> Arrays.stream(ores).anyMatch(check -> Arrays.stream(r.ores).anyMatch(target -> target.matches(check))))
                .collect(Collectors.toList());
        for (ExcavatorHandler.MineralMix recipe : entries) {
            remove(recipe);
        }
        if (entries.isEmpty()) {
            GroovyLog.msg("Error removing Immersive Engineering Excavator entry")
                    .add("no entries found for {}", Arrays.toString(ores))
                    .error()
                    .post();
        }
    }

    public void removeByOres(OreDictIngredient... ores) {
        if (IngredientHelper.isEmpty(ores)) {
            GroovyLog.msg("Error removing Immersive Engineering Excavator entry")
                    .add("ores must not be empty")
                    .error()
                    .post();
            return;
        }
        List<ExcavatorHandler.MineralMix> entries = ExcavatorHandler.mineralList.keySet().stream()
                .filter(r -> Arrays.stream(ores).anyMatch(check -> Arrays.stream(r.ores).anyMatch(target -> target.matches(check.getOreDict()))))
                .collect(Collectors.toList());
        for (ExcavatorHandler.MineralMix recipe : entries) {
            remove(recipe);
        }
        if (entries.isEmpty()) {
            GroovyLog.msg("Error removing Immersive Engineering Excavator entry")
                    .add("no entries found for {}", Arrays.toString(ores))
                    .error()
                    .post();
        }
    }

    public SimpleObjectStream<Map.Entry<ExcavatorHandler.MineralMix, Integer>> streamRecipes() {
        return new SimpleObjectStream<>(ExcavatorHandler.mineralList.entrySet()).setRemover(r -> this.remove(r.getKey()));
    }

    public void removeAll() {
        ExcavatorHandler.mineralList.forEach((r, l) -> addBackup(Pair.of(r, l)));
        ExcavatorHandler.mineralList.clear();
    }


    public static class RecipeBuilder extends AbstractRecipeBuilder<ExcavatorHandler.MineralMix> {

        private String name;
        private int weight;
        private float fail;
        private final List<String> ores = new ArrayList<>();
        private final List<Float> chances = new ArrayList<>();
        private final List<Integer> dimensions = new ArrayList<>();
        private boolean blacklist = true;

        public RecipeBuilder name(String name) {
            this.name = name;
            return this;
        }

        public RecipeBuilder weight(int weight) {
            this.weight = weight;
            return this;
        }

        public RecipeBuilder fail(float fail) {
            this.fail = fail;
            return this;
        }

        public RecipeBuilder ore(String ore, float chance) {
            this.ores.add(ore);
            this.chances.add(chance);
            return this;
        }

        public RecipeBuilder ore(OreDictIngredient ore, float chance) {
            this.ores.add(ore.getOreDict());
            this.chances.add(chance);
            return this;
        }

        public RecipeBuilder dimension(int dimension) {
            this.dimensions.add(dimension);
            return this;
        }

        public RecipeBuilder dimension(int... dimensions) {
            for (int dimension : dimensions) {
                dimension(dimension);
            }
            return this;
        }

        public RecipeBuilder dimension(Collection<Integer> dimensions) {
            for (int dimension : dimensions) {
                dimension(dimension);
            }
            return this;
        }

        public RecipeBuilder blacklist(boolean blacklist) {
            this.blacklist = blacklist;
            return this;
        }

        public RecipeBuilder blacklist() {
            this.blacklist = !blacklist;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Engineering Excavator entry";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg);
            msg.add(fail < 0 || fail > 1, "fail must be a float between 0 and 1, yet it was {}", fail);
            msg.add(ores.size() != chances.size(), "ores and chances must be of equal length, yet ores was {} and chances was {}", ores.size(), chances.size());
        }

        @Override
        public @Nullable ExcavatorHandler.MineralMix register() {
            if (!validate()) return null;
            float[] chanceArray = new float[chances.size()];
            for (int i = 0; i < chances.size(); i++) {
                chanceArray[i] = chances.get(i);
            }
            ExcavatorHandler.MineralMix recipe = new ExcavatorHandler.MineralMix(name, fail, ores.toArray(new String[0]), chanceArray);

            int[] dims = dimensions.stream().mapToInt(Integer::intValue).toArray();
            if (dims != null) {
                if (blacklist) recipe.dimensionBlacklist = dims;
                else recipe.dimensionWhitelist = dims;
            }
            recipe.recalculateChances();

            ModSupport.IMMERSIVE_ENGINEERING.get().excavator.add(recipe, weight);
            return recipe;
        }
    }
}
