package com.cleanroommc.groovyscript.compat.mods.naturesaura;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.recipes.TreeRitualRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@RegistryDescription(admonition = @Admonition(value = "groovyscript.wiki.naturesaura.ritual.note0", type = Admonition.Type.WARNING))
public class Ritual extends VirtualizedRegistry<TreeRitualRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".name('demo').input(item('minecraft:clay')).output(item('minecraft:diamond')).time(100).sapling(item('minecraft:sapling:1'))"),
            @Example(".name(resource('example:demo')).input(item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay')).output(item('minecraft:gold_ingot')).time(15).sapling(item('minecraft:sapling:1'))"),
            @Example(".input(item('minecraft:gold_ingot'), item('minecraft:clay'), item('minecraft:gold_ingot'), item('minecraft:clay'), item('minecraft:gold_ingot'), item('minecraft:clay'), item('minecraft:gold_ingot'), item('minecraft:clay')).output(item('minecraft:diamond') * 16).time(20).sapling(item('minecraft:sapling:3'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(x -> NaturesAuraAPI.TREE_RITUAL_RECIPES.remove(x.name));
        restoreFromBackup().forEach(x -> NaturesAuraAPI.TREE_RITUAL_RECIPES.put(x.name, x));
    }

    public void add(TreeRitualRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        NaturesAuraAPI.TREE_RITUAL_RECIPES.put(recipe.name, recipe);
    }

    public boolean remove(TreeRitualRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        return NaturesAuraAPI.TREE_RITUAL_RECIPES.remove(recipe.name) != null;
    }

    @MethodDescription(example = @Example("resource('naturesaura:eye_improved')"))
    public boolean removeByName(ResourceLocation name) {
        if (name == null) return false;
        var recipe = NaturesAuraAPI.TREE_RITUAL_RECIPES.remove(name);
        if (recipe == null) return false;
        addBackup(recipe);
        return true;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('naturesaura:infused_stone')"))
    public boolean removeByInput(IIngredient input) {
        return NaturesAuraAPI.TREE_RITUAL_RECIPES.entrySet().removeIf(r -> {
            for (var ingredient : r.getValue().ingredients) {
                for (var item : ingredient.getMatchingStacks()) {
                    if (input.test(item)) {
                        addBackup(r.getValue());
                        return true;
                    }
                }
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:sapling:3')"))
    public boolean removeBySapling(IIngredient catalyst) {
        return NaturesAuraAPI.TREE_RITUAL_RECIPES.entrySet().removeIf(r -> {
            for (var x : r.getValue().saplingType.getMatchingStacks()) {
                if (catalyst.test(x)) {
                    addBackup(r.getValue());
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example("item('naturesaura:eye')"))
    public boolean removeByOutput(IIngredient output) {
        return NaturesAuraAPI.TREE_RITUAL_RECIPES.entrySet().removeIf(r -> {
            if (output.test(r.getValue().result)) {
                addBackup(r.getValue());
                return true;
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        NaturesAuraAPI.TREE_RITUAL_RECIPES.values().forEach(this::addBackup);
        NaturesAuraAPI.TREE_RITUAL_RECIPES.entrySet().clear();
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Map.Entry<ResourceLocation, TreeRitualRecipe>> streamRecipes() {
        return new SimpleObjectStream<>(NaturesAuraAPI.TREE_RITUAL_RECIPES.entrySet()).setRemover(x -> remove(x.getValue()));
    }

    @Property(property = "input", valid = {@Comp(type = Comp.Type.GTE, value = "1"), @Comp(type = Comp.Type.LTE, value = "8")})
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<TreeRitualRecipe> {

        @Property(valid = @Comp(value = "null", type = Comp.Type.NOT))
        private IIngredient sapling;
        @Property(valid = @Comp(value = "1", type = Comp.Type.GTE))
        private int time;

        @RecipeBuilderMethodDescription
        public RecipeBuilder sapling(IIngredient sapling) {
            this.sapling = sapling;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Nature's Aura Ritual Recipe";
        }

        public String getRecipeNamePrefix() {
            return "groovyscript_ritual_";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateName();
            validateItems(msg, 1, 8, 1, 1);
            validateFluids(msg);
            msg.add(IngredientHelper.isEmpty(sapling), "sapling must not be empty");
            msg.add(time <= 0, "time must be greater than or equal to 1, yet it was {}", time);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable TreeRitualRecipe register() {
            if (!validate()) return null;
            TreeRitualRecipe recipe = new TreeRitualRecipe(name, sapling.toMcIngredient(), output.get(0), time, input.stream().map(IIngredient::toMcIngredient).toArray(Ingredient[]::new));
            ModSupport.NATURES_AURA.get().ritual.add(recipe);
            return recipe;
        }
    }
}
