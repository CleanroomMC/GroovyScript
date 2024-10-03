package com.cleanroommc.groovyscript.compat.mods.actuallyadditions;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.api.recipe.WeightedOre;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class StoneMiningLens extends StandardListRegistry<WeightedOre> {

    @RecipeBuilderDescription(example = {
            @Example(".ore(ore('blockDiamond')).weight(100)"),
            @Example(".ore('blockGold').weight(100)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<WeightedOre> getRecipes() {
        return ActuallyAdditionsAPI.STONE_ORES;
    }

    public WeightedOre add(String oreName, int weight) {
        WeightedOre recipe = new WeightedOre(oreName, weight);
        add(recipe);
        return recipe;
    }

    @MethodDescription(example = @Example("ore('oreCoal')"))
    public boolean removeByOre(OreDictIngredient ore) {
        return this.removeByOre(ore.getOreDict());
    }

    @MethodDescription(example = @Example("'oreLapis'"))
    public boolean removeByOre(String oreName) {
        return getRecipes().removeIf(recipe -> {
            boolean found = oreName.equals(recipe.name);
            if (found) {
                addBackup(recipe);
            }
            return found;
        });
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<WeightedOre> {

        @Property(comp = @Comp(not = "null"))
        private String ore;
        @Property(comp = @Comp(gte = 0))
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
            return "Error adding Actually Additions Stone Mining Lens recipe";
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
            ModSupport.ACTUALLY_ADDITIONS.get().stoneMiningLens.add(recipe);
            return recipe;
        }

    }

}
