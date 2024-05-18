package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.crafting.SqueezerRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

@RegistryDescription
public class Squeezer extends VirtualizedRegistry<SqueezerRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:clay')).fluidOutput(fluid('lava')).energy(100)"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay')).energy(100)"),
            @Example(".input(item('minecraft:clay')).fluidOutput(fluid('water')).energy(100)")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> SqueezerRecipe.recipeList.removeIf(r -> r == recipe));
        SqueezerRecipe.recipeList.addAll(restoreFromBackup());
    }

    public void add(SqueezerRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            SqueezerRecipe.recipeList.add(recipe);
        }
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public SqueezerRecipe add(FluidStack fluidOutput, @Nonnull ItemStack itemOutput, IIngredient input, int energy) {
        SqueezerRecipe recipe = new SqueezerRecipe(fluidOutput, itemOutput, ImmersiveEngineering.toIngredientStack(input), energy);
        add(recipe);
        return recipe;
    }

    public boolean remove(SqueezerRecipe recipe) {
        if (SqueezerRecipe.recipeList.removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(example = @Example("fluid('plantoil')"))
    public void removeByOutput(FluidStack fluidOutput) {
        if (IngredientHelper.isEmpty(fluidOutput)) {
            GroovyLog.msg("Error removing Immersive Engineering Squeezer recipe")
                    .add("fluid output must not be empty")
                    .error()
                    .post();
            return;
        }
        if (!SqueezerRecipe.recipeList.removeIf(recipe -> {
            if (fluidOutput.isFluidEqual(recipe.fluidOutput)) {
                addBackup(recipe);
                return true;
            }
            return false;
        })) {
            GroovyLog.msg("Error removing Immersive Engineering Squeezer recipe")
                    .add("no recipes found for {}", fluidOutput)
                    .error()
                    .post();
        }
    }

    @MethodDescription
    public void removeByOutput(FluidStack fluidOutput, ItemStack itemOutput) {
        if (GroovyLog.msg("Error removing Immersive Engineering Squeezer recipe")
                .add(IngredientHelper.isEmpty(fluidOutput), () -> "fluid output must not be empty")
                .add(IngredientHelper.isEmpty(itemOutput), () -> "item input must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        List<SqueezerRecipe> recipes = SqueezerRecipe.recipeList.stream().filter(r -> fluidOutput.isFluidEqual(r.fluidOutput) && r.itemOutput.isItemEqual(itemOutput)).collect(Collectors.toList());
        for (SqueezerRecipe recipe : recipes) {
            remove(recipe);
        }
        if (recipes.isEmpty()) {
            GroovyLog.msg("Error removing Immersive Engineering Squeezer recipe")
                    .add("no recipes found for {} and {}", fluidOutput, itemOutput)
                    .error()
                    .post();
        }
    }

    @MethodDescription(example = @Example("item('immersiveengineering:material:18')"))
    public void removeByOutput(ItemStack itemOutput) {
        if (GroovyLog.msg("Error removing Immersive Engineering Squeezer recipe")
                .add(IngredientHelper.isEmpty(itemOutput), () -> "item input must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        // "Condition 'r.itemOutput != null' is always 'true'" is a lie. It can be null, and if it is it *will* throw an NPE if we don't check against it.
        @SuppressWarnings("ConstantValue")
        List<SqueezerRecipe> recipes = SqueezerRecipe.recipeList.stream().filter(r -> r != null && r.itemOutput != null && r.itemOutput.isItemEqual(itemOutput)).collect(Collectors.toList());
        for (SqueezerRecipe recipe : recipes) {
            remove(recipe);
        }
        if (recipes.isEmpty()) {
            GroovyLog.msg("Error removing Immersive Engineering Squeezer recipe")
                    .add("no recipes found for {}", itemOutput)
                    .error()
                    .post();
        }
    }

    @MethodDescription(example = @Example("item('minecraft:wheat_seeds')"))
    public void removeByInput(ItemStack input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Immersive Engineering Squeezer recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
            return;
        }
        SqueezerRecipe recipe = SqueezerRecipe.findRecipe(input);
        if (recipe == null || !remove(recipe)) {
            GroovyLog.msg("Error removing Immersive Engineering Squeezer recipe")
                    .add("no recipes found for {}", input)
                    .error()
                    .post();
        }
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<SqueezerRecipe> streamRecipes() {
        return new SimpleObjectStream<>(SqueezerRecipe.recipeList).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        SqueezerRecipe.recipeList.forEach(this::addBackup);
        SqueezerRecipe.recipeList.clear();
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = {@Comp(value = "0", type = Comp.Type.GTE), @Comp(value = "1", type = Comp.Type.LTE)})
    @Property(property = "fluidOutput", valid = {@Comp(value = "0", type = Comp.Type.GTE), @Comp(value = "1", type = Comp.Type.LTE)})
    private static class RecipeBuilder extends AbstractRecipeBuilder<SqueezerRecipe> {

        @Property
        private int energy;

        @RecipeBuilderMethodDescription
        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Engineering Refinery recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 1);
            validateFluids(msg, 0, 0, 0, 1);
            msg.add(fluidOutput.isEmpty() && output.isEmpty(), "Either a fluid output or an item output must be defined");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable SqueezerRecipe register() {
            if (!validate()) return null;
            SqueezerRecipe recipe = new SqueezerRecipe(fluidOutput.getOrEmpty(0), output.getOrEmpty(0), ImmersiveEngineering.toIngredientStack(input.get(0)), energy);
            ModSupport.IMMERSIVE_ENGINEERING.get().squeezer.add(recipe);
            return recipe;
        }
    }
}
