package com.cleanroommc.groovyscript.compat.mods.alchemistry;

import al132.alchemistry.recipes.CombinerRecipe;
import al132.alchemistry.recipes.ModRecipes;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RegistryDescription
public class Combiner extends StandardListRegistry<CombinerRecipe> {

    public Combiner() {
        super(Alias.generateOfClass(Combiner.class).andGenerate("ChemicalCombiner"));
    }

    @Override
    public Collection<CombinerRecipe> getRecipes() {
        return ModRecipes.INSTANCE.getCombinerRecipes();
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2).output(item('minecraft:gold_block') * 2)"),
            @Example(".input(ItemStack.EMPTY, ItemStack.EMPTY, item('minecraft:clay')).output(item('minecraft:gold_ingot'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(example = @Example("item('minecraft:glowstone')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> {
            if (output.test(r.getOutput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("element('carbon')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> {
            for (ItemStack itemstack : r.getInputs()) {
                if (input.test(itemstack)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @Property(property = "input", comp = @Comp(gte = 1, lte = 9))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<CombinerRecipe> {

        @Property
        protected String gamestage = "";

        @RecipeBuilderMethodDescription
        public RecipeBuilder gamestage(String gamestage) {
            this.gamestage = gamestage;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Alchemistry Combiner recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            int inputSize = input.getRealSize();
            output.trim();
            msg.add(inputSize < 1 || inputSize > 9, () -> "Must have 1 - 9 inputs, but found " + input.size());
            msg.add(output.size() != 1, () -> "Must have exactly 1 output, but found " + output.size());
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable CombinerRecipe register() {
            if (!validate()) return null;

            List<ItemStack> inputs = input.stream().map(x -> x.isEmpty() ? ItemStack.EMPTY : IngredientHelper.toItemStack(x)).collect(Collectors.toList());
            CombinerRecipe recipe = new CombinerRecipe(output.get(0), inputs, gamestage);
            ModSupport.ALCHEMISTRY.get().combiner.add(recipe);
            return recipe;
        }
    }
}
