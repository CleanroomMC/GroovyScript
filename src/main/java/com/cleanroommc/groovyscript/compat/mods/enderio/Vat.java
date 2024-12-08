package com.cleanroommc.groovyscript.compat.mods.enderio;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.enderio.recipe.RecipeInput;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientList;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.helper.recipe.IRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.enderio.core.common.util.NNList;
import crazypants.enderio.base.recipe.*;
import crazypants.enderio.base.recipe.vat.VatRecipe;
import crazypants.enderio.base.recipe.vat.VatRecipeManager;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RegistryDescription
public class Vat extends VirtualizedRegistry<VatRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(fluid('lava')).output(fluid('hootch')).baseMultiplier(2).itemInputLeft(item('minecraft:clay'), 2).itemInputLeft(item('minecraft:clay_ball'), 0.5).itemInputRight(item('minecraft:diamond'), 5).itemInputRight(item('minecraft:diamond_block'), 50).itemInputRight(item('minecraft:gold_block'), 10).itemInputRight(item('minecraft:gold_ingot'), 1).itemInputRight(item('minecraft:gold_nugget'), 0.1).energy(1000).tierEnhanced()"),
            @Example(".input(fluid('hootch') * 100).output(fluid('water') * 50).itemInputLeft(item('minecraft:clay_ball'), 1).itemInputRight(item('minecraft:diamond'), 1).energy(1000).tierNormal()"),
            @Example(".input(fluid('water')).output(fluid('hootch')).itemInputLeft(item('minecraft:clay'), 2).itemInputLeft(item('minecraft:clay_ball'), 0.5).itemInputRight(item('minecraft:diamond'), 5).itemInputRight(item('minecraft:gold_ingot'), 1).energy(1000).tierAny()")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void add(Recipe recipe) {
        VatRecipeManager instance = VatRecipeManager.getInstance();
        instance.addRecipe(recipe);
        addScripted((VatRecipe) instance.getRecipes().get(instance.getRecipes().size() - 1));
    }

    public boolean remove(VatRecipe recipe) {
        if (recipe == null) return false;
        VatRecipeManager.getInstance().getRecipes().remove(recipe);
        addBackup(recipe);
        return true;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example("fluid('nutrient_distillation')"))
    public void remove(FluidStack output) {
        if (IngredientHelper.isEmpty(output)) {
            GroovyLog.get().error("Error removing EnderIO Vat recipe for empty output!");
            return;
        }
        int oldSize = VatRecipeManager.getInstance().getRecipes().size();
        VatRecipeManager.getInstance().getRecipes().removeIf(recipe -> {
            FluidStack recipeOutput = recipe.getOutputs()[0].getFluidOutput();
            if (output.isFluidEqual(recipeOutput)) addBackup((VatRecipe) recipe);
            return output.isFluidEqual(recipeOutput);
        });
        if (oldSize == VatRecipeManager.getInstance().getRecipes().size()) {
            GroovyLog.get().error("Could not find EnderIO Vat recipes with fluid output {}", output.getFluid().getName());
        }
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        NNList<IRecipe> recipes = VatRecipeManager.getInstance().getRecipes();
        removeScripted().forEach(recipes::remove);
        recipes.addAll(restoreFromBackup());
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<VatRecipe> streamRecipes() {
        return new SimpleObjectStream<>(VatRecipeManager.getInstance().getRecipes().stream().map(r -> (VatRecipe) r).collect(Collectors.toList()))
                .setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        VatRecipeManager.getInstance().getRecipes().forEach(r -> addBackup((VatRecipe) r));
        VatRecipeManager.getInstance().getRecipes().clear();
    }

    public static class RecipeBuilder implements IRecipeBuilder<Recipe> {

        @Property
        private final IngredientList<IIngredient> itemInputs1 = new IngredientList<>();
        @Property
        private final IngredientList<IIngredient> itemInputs2 = new IngredientList<>();
        @Property
        private final FloatList multipliers1 = new FloatArrayList();
        @Property
        private final FloatList multipliers2 = new FloatArrayList();
        @Property(value = "groovyscript.wiki.enderio.level.value", defaultValue = "RecipeLevel.IGNORE")
        protected RecipeLevel level = RecipeLevel.IGNORE;
        @Property(ignoresInheritedMethods = true, comp = @Comp(not = "null"))
        private FluidStack output;
        @Property(ignoresInheritedMethods = true, comp = @Comp(not = "null"))
        private FluidStack input;
        @Property(defaultValue = "1", comp = @Comp(gt = 0))
        private float baseMultiplier = 1;
        @Property(comp = @Comp(gt = 0))
        private int energy;

        @RecipeBuilderMethodDescription
        public RecipeBuilder input(FluidStack input) {
            this.input = input;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder output(FluidStack output) {
            this.output = output;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder baseMultiplier(float baseMultiplier) {
            this.baseMultiplier = baseMultiplier;
            return this;
        }

        @RecipeBuilderMethodDescription(field = {
                "itemInputs1", "multipliers1"
        })
        public RecipeBuilder itemInputLeft(IIngredient ingredient, float multiplier) {
            itemInputs1.add(ingredient);
            multipliers1.add(multiplier);
            return this;
        }

        @RecipeBuilderMethodDescription(field = {
                "itemInputs2", "multipliers2"
        })
        public RecipeBuilder itemInputRight(IIngredient ingredient, float multiplier) {
            itemInputs2.add(ingredient);
            multipliers2.add(multiplier);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "level")
        public RecipeBuilder tierNormal() {
            this.level = RecipeLevel.NORMAL;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "level")
        public RecipeBuilder tierEnhanced() {
            this.level = RecipeLevel.ADVANCED;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "level")
        public RecipeBuilder tierAny() {
            this.level = RecipeLevel.IGNORE;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @Override
        public boolean validate() {
            GroovyLog.Msg msg = GroovyLog.msg("Error adding EnderIO Vat recipe").error();
            msg.add(IngredientHelper.isEmpty(input), () -> "fluid input must not be empty");
            msg.add(IngredientHelper.isEmpty(output), () -> "fluid output must not be empty");
            AbstractRecipeBuilder.validateStackSize(msg, 1, "first slot input", itemInputs1);
            AbstractRecipeBuilder.validateStackSize(msg, 1, "second slot input", itemInputs2);
            if (energy <= 0) energy = 5000;
            if (baseMultiplier <= 0) baseMultiplier = 1;

            return !msg.postIfNotEmpty();
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable Recipe register() {
            if (!validate()) return null;
            List<IRecipeInput> inputs = new ArrayList<>();
            for (int i = 0; i < itemInputs1.size(); i++) {
                IIngredient ingredient = itemInputs1.get(i);
                if (!IngredientHelper.isEmpty(ingredient)) {
                    inputs.add(new VatRecipeInput(ingredient, 0, multipliers1.get(i)));
                }
            }
            for (int i = 0; i < itemInputs2.size(); i++) {
                IIngredient ingredient = itemInputs2.get(i);
                if (!IngredientHelper.isEmpty(ingredient)) {
                    inputs.add(new VatRecipeInput(ingredient, 1, multipliers2.get(i)));
                }
            }
            inputs.add(new crazypants.enderio.base.recipe.RecipeInput(input, baseMultiplier));

            Recipe recipe = new Recipe(new RecipeOutput(output), energy, RecipeBonusType.NONE, level, inputs.toArray(new IRecipeInput[0]));
            ModSupport.ENDER_IO.get().vat.add(recipe);
            return recipe;
        }
    }

    public static class VatRecipeInput extends RecipeInput {

        private final float multiplier;

        public VatRecipeInput(IIngredient ing, int slot, float multiplier) {
            super(ing, slot);
            this.multiplier = multiplier;
        }

        @Override
        public float getMulitplier() {
            return multiplier;
        }
    }
}
