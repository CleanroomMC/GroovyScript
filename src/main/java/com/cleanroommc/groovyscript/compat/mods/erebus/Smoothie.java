package com.cleanroommc.groovyscript.compat.mods.erebus;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.IOreDicts;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.erebus.SmoothieMakerRecipeAccessor;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import erebus.recipes.SmoothieMakerRecipe;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@RegistryDescription
public class Smoothie extends StandardListRegistry<SmoothieMakerRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".container(item('minecraft:diamond')).output(item('minecraft:gold_ingot'))"),
            @Example(".container(item('minecraft:clay')).input(item('minecraft:clay')).output(item('minecraft:gold_ingot'))"),
            @Example(".container(item('minecraft:gold_block')).fluidInput(fluid('water') * 5000).output(item('minecraft:gold_ingot'))"),
            @Example(".container(item('minecraft:stone')).input(item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')).fluidInput(fluid('lava') * 500, fluid('formic_acid') * 500, fluid('honey') * 500, fluid('milk') * 500).output(item('minecraft:clay') * 5)")
    })
    public Smoothie.RecipeBuilder recipeBuilder() {
        return new Smoothie.RecipeBuilder();
    }

    @Override
    public Collection<SmoothieMakerRecipe> getRecipes() {
        return SmoothieMakerRecipeAccessor.getRecipes();
    }

    @MethodDescription(example = {
            @Example("item('erebus:materials', 18)"), @Example("fluid('honey')")
    })
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> {
            for (FluidStack fluid : r.getFluids()) {
                if (input.test(fluid)) return doAddBackup(r);
            }
            for (Object object : r.getInputs()) {
                if (object instanceof ItemStack stack && input.test(stack)) return doAddBackup(r);
                else if (object instanceof String s) {
                    if (input instanceof IOreDicts d && d.getOreDicts().contains(s)) {
                        return true;
                    } else if (OreDictionary.getOres(s, false).stream().anyMatch(input)) {
                        return true;
                    }
                }
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:bucket')"))
    public boolean removeByContainer(IIngredient container) {
        return getRecipes().removeIf(r -> container.test(r.getContainer()) && doAddBackup(r));
    }

    @MethodDescription(example = @Example("item('erebus:materials', 21)"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> output.test(r.getOutput()) && doAddBackup(r));
    }

    @Property(property = "input", comp = @Comp(gte = 0, lte = 4))
    @Property(property = "fluidInput", comp = @Comp(gte = 0, lte = 4, unique = "groovyscript.wiki.erebus.smoothie.fluidInput.required"))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<SmoothieMakerRecipe> {

        private static final int MAX_FLUID_CAPACITY = 16_000;
        @Property(comp = @Comp(not = "null"))
        private ItemStack container;

        @RecipeBuilderMethodDescription
        public RecipeBuilder container(ItemStack container) {
            this.container = container;
            return this;
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Erebus Smoothie Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 4, 1, 1);
            validateFluids(msg, 0, 4, 0, 0);
            msg.add(IngredientHelper.isEmpty(container), "container must not be empty");
            validateStackSize(msg, getMaxItemInput(), "container", IngredientHelper.toIIngredient(container));
            validateStackSize(msg, getMaxItemInput(), "output", IngredientHelper.toIIngredient(output.getOrEmpty(0)));
            validateStackSize(msg, MAX_FLUID_CAPACITY, "fluidInput", fluidInput.asIIngredientList());

            // ensure no duplicate fluids
            Set<Fluid> set = new ObjectOpenHashSet<>();
            for (var fluidStack : fluidInput) {
                if (!set.add(fluidStack.getFluid())) {
                    msg.add("duplicate fluids cannot be handled correctly, and duplicate fluid {} was detected", fluidStack.getFluid());
                    break;
                }
            }
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable SmoothieMakerRecipe register() {
            if (!validate()) return null;
            SmoothieMakerRecipe recipe = null;
            var inputs = IngredientHelper.cartesianProductOres(input);
            for (List<Object> objects : inputs) {
                recipe = SmoothieMakerRecipeAccessor.createSmoothieMakerRecipe(output.get(0), container, fluidInput.toArray(new FluidStack[0]), objects.toArray());
                ModSupport.EREBUS.get().smoothie.add(recipe);
            }
            return recipe;
        }
    }
}
