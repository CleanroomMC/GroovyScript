package com.cleanroommc.groovyscript.compat.mods.prodigytech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import lykrast.prodigytech.common.recipe.SoldererManager;
import lykrast.prodigytech.common.util.Config;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class Solderer extends VirtualizedRegistry<SoldererManager.SoldererRecipe> {

    @RecipeBuilderDescription(example = {
        @Example(".pattern(item('minecraft:clay')).input(item('minecraft:gold_ingot')).output(item('minecraft:diamond')).gold(5).time(100)"),
        @Example(".pattern(item('minecraft:coal_block')).output(item('minecraft:nether_star')).gold(75)"),
    })
    public Solderer.RecipeBuilder recipeBuilder() {
        return new Solderer.RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(x -> SoldererManager.removeRecipe(x.getPattern(), x.getAdditive(), 9999));
        restoreFromBackup().forEach(SoldererManager::addRecipe);
    }

    public void add(SoldererManager.SoldererRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        SoldererManager.addRecipe(recipe);
    }

    public boolean remove(SoldererManager.SoldererRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        return SoldererManager.removeRecipe(recipe.getPattern(), recipe.getAdditive(), 9999) != null;
    }

    @MethodDescription(example = @Example("item('prodigytech:pattern_circuit_refined')"))
    public boolean removeByPattern(IIngredient pattern) {
        SoldererManager.SoldererRecipe recipe = SoldererManager.RECIPES.stream()
                .filter(r -> pattern.test(r.getPattern()))
                .findFirst().orElse(null);
        return remove(recipe);
    }

    @MethodDescription(example = @Example("item('minecraft:iron_ingot')"))
    public boolean removeByAdditive(IIngredient additive) {
        SoldererManager.SoldererRecipe recipe = SoldererManager.RECIPES.stream()
                .filter(r -> r.requiresAdditive() && additive.test(r.getAdditive()))
                .findFirst().orElse(null);
        return remove(recipe);
    }

    @MethodDescription(example = @Example("item('prodigytech:circuit_refined')"))
    public boolean removeByOutput(IIngredient output) {
        SoldererManager.SoldererRecipe recipe = SoldererManager.RECIPES.stream()
                .filter(r -> output.test(r.getOutput()))
                .findFirst().orElse(null);
        return remove(recipe);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        SoldererManager.RECIPES.forEach(this::addBackup);
        SoldererManager.removeAll();
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeWithoutAdditive() {
        SoldererManager.RECIPES.removeIf(r -> {
            if (r.requiresAdditive()) return false;
            addBackup(r);
            return true;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<SoldererManager.SoldererRecipe> streamRecipes() {
        return new SimpleObjectStream<>(SoldererManager.RECIPES)
                .setRemover(this::remove);
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<SoldererManager.SoldererRecipe> {

        @Property(valid = @Comp(value = "1", type = Comp.Type.GTE))
        private int time = -1;

        @Property(valid = @Comp(value = "1", type = Comp.Type.GTE))
        private int gold;

        @Property(valid = @Comp("1"))
        private IIngredient pattern;

        @RecipeBuilderMethodDescription
        public Solderer.RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @RecipeBuilderMethodDescription
        public Solderer.RecipeBuilder gold(int gold) {
            this.gold = gold;
            return this;
        }

        @RecipeBuilderMethodDescription
        public Solderer.RecipeBuilder pattern(IIngredient pattern) {
            this.pattern = pattern;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding ProdigyTech Solderer Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 1, 1, 1);
            validateFluids(msg);
            msg.add(gold <= 0, "gold must be greater than or equal to 1, yet it was {}", gold);
            msg.add(pattern == null, "pattern cannot be empty");
            int capacity = Config.soldererMaxGold;
            msg.add(gold > capacity, "gold must be less than or equal to the Solderer's capacity {}, yet it was {}", capacity, gold);
            if (time <= 0) {
                time = Config.soldererProcessTime;
            }
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable SoldererManager.SoldererRecipe register() {
            if (!validate()) return null;
            SoldererManager.SoldererRecipe recipe = null;
            for (ItemStack pat : pattern.getMatchingStacks()) {
                if (input.isEmpty()) {
                    SoldererManager.SoldererRecipe theRecipe = new SoldererManager.SoldererRecipe(pat, ItemStack.EMPTY, output.get(0), gold, time);
                    ModSupport.PRODIGY_TECH.get().solderer.add(theRecipe);
                    if (recipe == null) recipe = theRecipe;
                } else {
                    for (ItemStack additive : input.get(0).getMatchingStacks()) {
                        SoldererManager.SoldererRecipe theRecipe = new SoldererManager.SoldererRecipe(pat, additive, output.get(0), gold, time);
                        ModSupport.PRODIGY_TECH.get().solderer.add(theRecipe);
                        if (recipe == null) recipe = theRecipe;
                    }
                }
            }

            return recipe;
        }
    }
}
