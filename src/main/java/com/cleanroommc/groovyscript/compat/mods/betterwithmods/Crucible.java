package com.cleanroommc.groovyscript.compat.mods.betterwithmods;

import betterwithmods.common.BWRegistry;
import betterwithmods.common.registry.bulk.recipes.CookingPotRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.stream.Collectors;

@RegistryDescription
public class Crucible extends StandardListRegistry<CookingPotRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond')).heat(2)"),
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:gold_ingot') * 16).ignoreHeat()")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<CookingPotRecipe> getRecipes() {
        return BWRegistry.CRUCIBLE.getRecipes();
    }

    @MethodDescription(example = @Example("item('minecraft:gunpowder')"))
    public boolean removeByOutput(ItemStack output) {
        return getRecipes().removeIf(r -> {
            for (ItemStack itemstack : r.getOutputs()) {
                if (ItemHandlerHelper.canItemStacksStack(itemstack, output)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:gunpowder')"))
    public boolean removeByInput(ItemStack input) {
        return getRecipes().removeIf(r -> {
            for (Ingredient ingredient : r.getInputs()) {
                if (ingredient.test(input)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription
    public boolean removeByInput(IIngredient input) {
        return removeByInput(IngredientHelper.toItemStack(input));
    }

    @Property(property = "input", comp = @Comp(gte = 1, lte = 9))
    @Property(property = "output", comp = @Comp(gte = 1, lte = 9))
    public static class RecipeBuilder extends AbstractRecipeBuilder<CookingPotRecipe> {

        @Property(defaultValue = "1")
        private int heat = 1;
        @Property
        private boolean ignoreHeat;
        @Property(defaultValue = "1")
        private int priority = 1;

        @RecipeBuilderMethodDescription
        public RecipeBuilder heat(int heat) {
            this.heat = heat;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder ignoreHeat(boolean ignoreHeat) {
            this.ignoreHeat = ignoreHeat;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder ignoreHeat() {
            this.ignoreHeat = !ignoreHeat;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder priority(int priority) {
            this.priority = priority;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Better With Mods Cauldron recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 9, 1, 9);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable CookingPotRecipe register() {
            if (!validate()) return null;

            CookingPotRecipe recipe = new CookingPotRecipe(input.stream().map(IIngredient::toMcIngredient).collect(Collectors.toList()), output, heat);
            recipe.setIgnoreHeat(ignoreHeat);
            recipe.setPriority(priority);
            ModSupport.BETTER_WITH_MODS.get().crucible.add(recipe);
            return recipe;
        }
    }
}
