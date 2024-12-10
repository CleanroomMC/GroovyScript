package com.cleanroommc.groovyscript.compat.mods.alchemistry;

import al132.alchemistry.recipes.ElectrolyzerRecipe;
import al132.alchemistry.recipes.ModRecipes;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class Electrolyzer extends StandardListRegistry<ElectrolyzerRecipe> {

    @Override
    public Collection<ElectrolyzerRecipe> getRecipes() {
        return ModRecipes.INSTANCE.getElectrolyzerRecipes();
    }

    @RecipeBuilderDescription(example = {
            @Example(".fluidInput(fluid('lava') * 100).output(item('minecraft:clay'))"),
            @Example(".fluidInput(fluid('water') * 100).input(item('minecraft:gold_ingot')).consumptionChance(100).output(item('minecraft:gold_nugget') * 4).output(item('minecraft:gold_nugget') * 4).output(item('minecraft:gold_nugget') * 4).output(item('minecraft:gold_nugget') * 4).chance(50).chance(50)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(example = @Example("element('chlorine')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> {
            for (ItemStack itemstack : r.getOutputs()) {
                if (output.test(itemstack)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription(example = @Example(value = "fluid('water')", commented = true))
    public boolean removeByInput(FluidStack input) {
        return getRecipes().removeIf(r -> {
            if (r.getInput().isFluidEqual(input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("element('calcium_carbonate')"))
    public boolean removeByInput(IIngredient input) {
        return ModRecipes.INSTANCE.getElectrolyzerRecipes().removeIf(r -> {
            for (ItemStack itemstack : r.getElectrolytes()) {
                if (input.test(itemstack)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @Property(property = "input", comp = @Comp(gte = 0, lte = 1))
    @Property(property = "output", comp = @Comp(gte = 1, lte = 4))
    public static class RecipeBuilder extends AbstractRecipeBuilder<ElectrolyzerRecipe> {

        @Property(comp = @Comp(gte = 0, lte = 2))
        private final IntArrayList chance = new IntArrayList();
        @Property(comp = @Comp(gte = 0, lte = 100))
        private int consumptionChance;

        @RecipeBuilderMethodDescription
        public RecipeBuilder input(IIngredient ingredient, int chance) {
            this.input.add(ingredient);
            this.chance.add(chance);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder chance(int chance) {
            this.chance.add(chance);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder chance(int... chances) {
            for (int chance : chances) {
                chance(chance);
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder chance(Collection<Integer> chances) {
            for (int chance : chances) {
                chance(chance);
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder consumptionChance(int consumptionChance) {
            this.consumptionChance = consumptionChance;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Alchemistry Electrolyzer recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 1, 1, 4);
            validateFluids(msg, 1, 1, 0, 0);
            validateCustom(msg, chance, 0, 2, "chance");
            msg.add(!chance.isEmpty() && chance.size() > (output.size() - 2), "chance only applies to output items after the second, cannot have more chance than output items above 2, had {} chance and {} output", chance.size(), output.size());
            msg.add(consumptionChance < 0 || consumptionChance > 100, "consumption chance must be between 0 and 100, yet it was {}", consumptionChance);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable ElectrolyzerRecipe register() {
            if (!validate()) return null;
            ElectrolyzerRecipe recipe = new ElectrolyzerRecipe(
                    fluidInput.get(0),
                    input.size() >= 1 ? input.get(0).toMcIngredient() : Ingredient.EMPTY,
                    consumptionChance,
                    output.get(0),
                    output.getOrEmpty(1),
                    output.getOrEmpty(2),
                    chance.size() >= 1 ? chance.getInt(0) : 0,
                    output.getOrEmpty(3),
                    chance.size() >= 2 ? chance.getInt(1) : 0);
            ModSupport.ALCHEMISTRY.get().electrolyzer.add(recipe);
            return recipe;
        }
    }
}
