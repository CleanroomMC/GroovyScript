package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.crafting.CrusherRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RegistryDescription
public class Crusher extends StandardListRegistry<CrusherRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:clay')).energy(100)"),
            @Example(".input(item('minecraft:diamond_block')).output(item('minecraft:diamond')).secondaryOutput(item('minecraft:gold_ingot')).secondaryOutput(item('minecraft:gold_ingot'), 0.3).energy(100)")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<CrusherRecipe> getRecipes() {
        return CrusherRecipe.recipeList;
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public CrusherRecipe add(ItemStack output, IIngredient input, int energy) {
        CrusherRecipe recipe = new CrusherRecipe(output.copy(), ImmersiveEngineering.toIngredientStack(input), energy);
        add(recipe);
        return recipe;
    }

    @MethodDescription(example = @Example("item('minecraft:sand')"))
    public void removeByOutput(ItemStack output) {
        if (IngredientHelper.isEmpty(output)) {
            GroovyLog.msg("Error removing Immersive Engineering Crusher recipe")
                    .add("output must not be empty")
                    .error()
                    .post();
        }
        List<CrusherRecipe> list = CrusherRecipe.removeRecipesForOutput(output);
        if (list.isEmpty()) {
            GroovyLog.msg("Error removing Immersive Engineering Crusher recipe")
                    .add("no recipes found for {}", output)
                    .error()
                    .post();
            return;
        }
        list.forEach(this::addBackup);
    }

    @MethodDescription(example = @Example("item('immersiveengineering:material:7')"))
    public void removeByInput(ItemStack input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Immersive Engineering Crusher recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
        }
        List<CrusherRecipe> list = CrusherRecipe.removeRecipesForInput(input);
        if (list.isEmpty()) {
            GroovyLog.msg("Error removing Immersive Engineering Crusher recipe")
                    .add("no recipes found for {}", input)
                    .error()
                    .post();
            return;
        }
        list.forEach(this::addBackup);
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<CrusherRecipe> {

        @Property(comp = @Comp(unique = "groovyscript.wiki.immersiveengineering.crusher.secondaryOutputItems.required"))
        private final List<ItemStack> secondaryOutputItems = new ArrayList<>();
        @Property(comp = @Comp(unique = "groovyscript.wiki.immersiveengineering.crusher.secondaryOutputChances.required"))
        private final FloatArrayList secondaryOutputChances = new FloatArrayList();
        @Property(comp = @Comp(gte = 0))
        private int energy;

        @RecipeBuilderMethodDescription(field = {
                "secondaryOutputItems", "secondaryOutputChances"
        })
        public RecipeBuilder secondaryOutput(ItemStack item) {
            return this.secondaryOutput(item, 1);
        }

        @RecipeBuilderMethodDescription(field = {
                "secondaryOutputItems", "secondaryOutputChances"
        })
        public RecipeBuilder secondaryOutput(ItemStack item, float chance) {
            this.secondaryOutputItems.add(item);
            this.secondaryOutputChances.add(chance);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Engineering Crusher recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            secondaryOutputChances.trim();
            msg.add(secondaryOutputItems.size() != secondaryOutputChances.size(), "secondaryOutputItems and secondaryOutputChances must be of equal length, yet secondaryOutputItems was {} and secondaryOutputChances was {}", secondaryOutputItems.size(), secondaryOutputChances.size());
            if (energy < 0) energy = 200;
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable CrusherRecipe register() {
            if (!validate()) return null;
            CrusherRecipe recipe = new CrusherRecipe(output.get(0), ImmersiveEngineering.toIngredientStack(input.get(0)), energy);
            if (!secondaryOutputItems.isEmpty()) {
                recipe.secondaryOutput = secondaryOutputItems.toArray(new ItemStack[0]);
                recipe.secondaryChance = secondaryOutputChances.elements();
                recipe.getItemOutputs().addAll(secondaryOutputItems);
            }
            ModSupport.IMMERSIVE_ENGINEERING.get().crusher.add(recipe);
            return recipe;
        }
    }
}
