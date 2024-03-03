package com.cleanroommc.groovyscript.compat.mods.actuallyadditions;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.api.recipe.WeightedOre;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class NetherMiningLens extends VirtualizedRegistry<WeightedOre> {

    @RecipeBuilderDescription(example = {
            @Example(".ore(ore('blockDiamond')).weight(100)"),
            @Example(".ore('blockGold').weight(100)")
    })
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

    @MethodDescription(description = "groovyscript.wiki.removeByOre", example = @Example("ore('oreQuartz')"))
    public boolean removeByOre(OreDictIngredient ore) {
        return this.removeByOre(ore.getOreDict());
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOre", example = @Example("'oreQuartz'"))
    public boolean removeByOre(String oreName) {
        return ActuallyAdditionsAPI.NETHERRACK_ORES.removeIf(recipe -> {
            boolean found = oreName.equals(recipe.name);
            if (found) {
                addBackup(recipe);
            }
            return found;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ActuallyAdditionsAPI.NETHERRACK_ORES.forEach(this::addBackup);
        ActuallyAdditionsAPI.NETHERRACK_ORES.clear();
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<WeightedOre> streamRecipes() {
        return new SimpleObjectStream<>(ActuallyAdditionsAPI.NETHERRACK_ORES)
                .setRemover(this::remove);
    }


    public static class RecipeBuilder extends AbstractRecipeBuilder<WeightedOre> {

        @Property(valid = @Comp(type = Comp.Type.NOT, value = "null"))
        private String ore;
        @Property(valid = @Comp(type = Comp.Type.GTE, value = "0"))
        private int weight;

        @RecipeBuilderMethodDescription
        public RecipeBuilder ore(String ore) {
            this.ore = ore;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "ore")
        public RecipeBuilder ore(OreDictIngredient ore) {
            this.ore = ore.getOreDict();
            return this;
        }

        @RecipeBuilderMethodDescription
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
        @RecipeBuilderRegistrationMethod
        public @Nullable WeightedOre register() {
            if (!validate()) return null;
            WeightedOre recipe = new WeightedOre(ore, weight);
            ModSupport.ACTUALLY_ADDITIONS.get().netherMiningLens.add(recipe);
            return recipe;
        }
    }
}
