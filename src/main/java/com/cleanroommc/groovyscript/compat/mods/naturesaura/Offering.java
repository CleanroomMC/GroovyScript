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
import de.ellpeck.naturesaura.api.recipes.OfferingRecipe;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@RegistryDescription(admonition = @Admonition("groovyscript.wiki.naturesaura.offering.note0"))
public class Offering extends VirtualizedRegistry<OfferingRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".name('demo').input(item('minecraft:diamond')).catalyst(item('minecraft:clay')).output(item('minecraft:gold_ingot') * 8)"),
            @Example(".name(resource('example:demo')).input(item('minecraft:clay')).catalyst(item('minecraft:gold_ingot')).output(item('minecraft:diamond') * 8)"),
            @Example(".input(item('minecraft:gold_ingot') * 10).catalyst(item('minecraft:diamond')).output(item('minecraft:clay'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(x -> NaturesAuraAPI.OFFERING_RECIPES.remove(x.name));
        restoreFromBackup().forEach(x -> NaturesAuraAPI.OFFERING_RECIPES.put(x.name, x));
    }

    public void add(OfferingRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        NaturesAuraAPI.OFFERING_RECIPES.put(recipe.name, recipe);
    }

    public boolean remove(OfferingRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        return NaturesAuraAPI.OFFERING_RECIPES.remove(recipe.name) != null;
    }

    @MethodDescription(example = @Example("resource('naturesaura:token_euphoria')"))
    public boolean removeByName(ResourceLocation name) {
        if (name == null) return false;
        var recipe = NaturesAuraAPI.OFFERING_RECIPES.remove(name);
        if (recipe == null) return false;
        addBackup(recipe);
        return true;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('minecraft:nether_star')"))
    public boolean removeByInput(IIngredient input) {
        return NaturesAuraAPI.OFFERING_RECIPES.entrySet().removeIf(r -> {
            for (var item : r.getValue().input.getMatchingStacks()) {
                if (input.test(item)) {
                    addBackup(r.getValue());
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeByCatalyst", example = @Example(value = "item('naturesaura:calling_spirit')", commented = true))
    public boolean removeByCatalyst(IIngredient catalyst) {
        return NaturesAuraAPI.OFFERING_RECIPES.entrySet().removeIf(r -> {
            for (var x : r.getValue().startItem.getMatchingStacks()) {
                if (catalyst.test(x)) {
                    addBackup(r.getValue());
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example("item('naturesaura:sky_ingot')"))
    public boolean removeByOutput(IIngredient output) {
        return NaturesAuraAPI.OFFERING_RECIPES.entrySet().removeIf(r -> {
            if (output.test(r.getValue().output)) {
                addBackup(r.getValue());
                return true;
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        NaturesAuraAPI.OFFERING_RECIPES.values().forEach(this::addBackup);
        NaturesAuraAPI.OFFERING_RECIPES.entrySet().clear();
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Map.Entry<ResourceLocation, OfferingRecipe>> streamRecipes() {
        return new SimpleObjectStream<>(NaturesAuraAPI.OFFERING_RECIPES.entrySet()).setRemover(x -> remove(x.getValue()));
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<OfferingRecipe> {

        @Property(valid = @Comp(value = "null", type = Comp.Type.NOT))
        private IIngredient catalyst;

        @RecipeBuilderMethodDescription
        public RecipeBuilder catalyst(IIngredient catalyst) {
            this.catalyst = catalyst;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Nature's Aura Offering Recipe";
        }

        public String getRecipeNamePrefix() {
            return "groovyscript_offering_";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateName();
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(IngredientHelper.isEmpty(catalyst), "catalyst must not be empty");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable OfferingRecipe register() {
            if (!validate()) return null;
            OfferingRecipe recipe = new OfferingRecipe(name, input.get(0).toMcIngredient(), catalyst.toMcIngredient(), output.get(0));
            ModSupport.NATURES_AURA.get().offering.add(recipe);
            return recipe;
        }
    }
}
