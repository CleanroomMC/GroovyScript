package com.cleanroommc.groovyscript.compat.mods.railcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.api.crafting.IBlastFurnaceCrafter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class BlastFurnace extends StandardListRegistry<IBlastFurnaceCrafter.IRecipe> {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:iron_ingot')).output(item('railcraft:ingot:1')).time(1280).slag(1)"))
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<IBlastFurnaceCrafter.IRecipe> getRecipes() {
        return Crafters.blastFurnace().getRecipes();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public IBlastFurnaceCrafter.IRecipe add(IIngredient input, ItemStack output, int time, int slag) {
        RecipeBuilder builder = recipeBuilder();
        builder.input(input);
        builder.output(output);
        builder.time = time;
        builder.slag = slag;
        return builder.register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public IBlastFurnaceCrafter.IRecipe add(IIngredient input, ItemStack output, int time) {
        return add(input, output, time, 1);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public IBlastFurnaceCrafter.IRecipe add(IIngredient input, ItemStack output) {
        return add(input, output, IBlastFurnaceCrafter.SMELT_TIME, 1);
    }

    @MethodDescription(example = @Example("item('railcraft:ingot:1')"))
    public void removeByOutput(ItemStack output) {
        if (IngredientHelper.isEmpty(output)) {
            GroovyLog.msg("Error removing Railcraft Blast Furnace recipe")
                    .add("output must not be empty")
                    .error()
                    .post();
            return;
        }
        if (!getRecipes().removeIf(recipe -> {
            if (ItemStack.areItemStacksEqual(recipe.getOutput(), output)) {
                addBackup(recipe);
                return true;
            }
            return false;
        })) {
            GroovyLog.msg("Error removing Railcraft Blast Furnace recipe")
                    .add("no recipes found for {}", output)
                    .error()
                    .post();
        }
    }

    @MethodDescription(example = @Example("item('minecraft:iron_ingot')"))
    public void removeByInput(IIngredient input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Railcraft Blast Furnace recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
            return;
        }
        if (!getRecipes().removeIf(recipe -> {
            if (recipe.getInput().test(input.getMatchingStacks()[0])) {
                addBackup(recipe);
                return true;
            }
            return false;
        })) {
            GroovyLog.msg("Error removing Railcraft Blast Furnace recipe")
                    .add("no recipes found for {}", input)
                    .error()
                    .post();
        }
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<IBlastFurnaceCrafter.IRecipe> {

        @Property(comp = @Comp(gte = 0), defaultValue = "IBlastFurnaceCrafter.SMELT_TIME")
        private int time = IBlastFurnaceCrafter.SMELT_TIME;
        @Property(comp = @Comp(gte = 0), defaultValue = "1")
        private int slag = 1;

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder slag(int slag) {
            this.slag = slag;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Railcraft Blast Furnace recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            if (time < 0) time = IBlastFurnaceCrafter.SMELT_TIME;
            if (slag < 0) slag = 1;
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable IBlastFurnaceCrafter.IRecipe register() {
            if (!validate()) return null;
            ItemStack outputStack = output.get(0);
            Ingredient inputIngredient = Railcraft.toIngredient(input.get(0));

            IBlastFurnaceCrafter.IRecipe recipe = new IBlastFurnaceCrafter.IRecipe() {
                @Override
                public net.minecraft.util.ResourceLocation getName() {
                    return new net.minecraft.util.ResourceLocation("groovyscript", "blastfurnace_" + System.currentTimeMillis());
                }

                @Override
                public Ingredient getInput() {
                    return inputIngredient;
                }

                @Override
                public int getTickTime(ItemStack input) {
                    return time;
                }

                @Override
                public ItemStack getOutput() {
                    return outputStack.copy();
                }

                @Override
                public int getSlagOutput() {
                    return slag;
                }
            };

            ModSupport.RAILCRAFT.get().blastFurnace.add(recipe);
            return recipe;
        }
    }
}
