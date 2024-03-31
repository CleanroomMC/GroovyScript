package com.cleanroommc.groovyscript.compat.mods.rustic;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import rustic.common.crafting.CrushingTubRecipe;
import rustic.common.crafting.ICrushingTubRecipe;
import rustic.common.crafting.Recipes;

@RegistryDescription
public class CrushingTub extends VirtualizedRegistry<ICrushingTubRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:stone')).fluidOutput(fluid('lava') * 50)"),
            @Example(".input(item('minecraft:clay')).fluidOutput(fluid('lava') * 20).byproduct(item('minecraft:gold_ingot') * 4)"),
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        Recipes.crushingTubRecipes.removeAll(removeScripted());
        Recipes.crushingTubRecipes.addAll(restoreFromBackup());
    }

    public void add(ICrushingTubRecipe recipe) {
        Recipes.crushingTubRecipes.add(recipe);
        addScripted(recipe);
    }

    public boolean remove(ICrushingTubRecipe recipe) {
        addBackup(recipe);
        return Recipes.crushingTubRecipes.remove(recipe);
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = {@Example("fluid('ironberryjuice')"), @Example("item('minecraft:sugar')")})
    public boolean removeByOutput(IIngredient output) {
        return Recipes.crushingTubRecipes.removeIf(entry -> {
            if (output.test(entry.getResult()) || output.test(entry.getByproduct())) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('rustic:wildberries')"))
    public boolean removeByInput(IIngredient input) {
        return Recipes.crushingTubRecipes.removeIf(entry -> {
            if (input.test(entry.getInput())) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        Recipes.crushingTubRecipes.forEach(this::addBackup);
        Recipes.crushingTubRecipes.clear();
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<ICrushingTubRecipe> streamRecipes() {
        return new SimpleObjectStream<>(Recipes.crushingTubRecipes).setRemover(this::remove);
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "fluidOutput", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<ICrushingTubRecipe> {

        @Property
        private ItemStack byproduct = ItemStack.EMPTY;

        @RecipeBuilderMethodDescription
        public RecipeBuilder byproduct(ItemStack byproduct) {
            this.byproduct = byproduct;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Rustic Crushing Tub recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg, 0, 0, 1, 1);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable ICrushingTubRecipe register() {
            if (!validate()) return null;
            ICrushingTubRecipe recipe = null;
            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                ICrushingTubRecipe recipe1 = new CrushingTubRecipe(fluidOutput.get(0), itemStack, byproduct);
                ModSupport.RUSTIC.get().crushingTub.add(recipe1);
                if (recipe == null) recipe = recipe1;
            }
            return recipe;
        }
    }

}
