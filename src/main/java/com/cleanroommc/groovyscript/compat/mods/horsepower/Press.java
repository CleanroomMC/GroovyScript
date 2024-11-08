package com.cleanroommc.groovyscript.compat.mods.horsepower;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import se.gory_moon.horsepower.recipes.HPRecipes;
import se.gory_moon.horsepower.recipes.PressRecipe;

import java.util.Collection;

@RegistryDescription
public class Press extends StandardListRegistry<PressRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond') * 5)"),
            @Example(".input(item('minecraft:diamond')).fluidOutput(fluid('lava') * 500)")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    // have to override onReload and add to add recipes to the registry due to the actual setup being a map
    @Override
    public Collection<PressRecipe> getRecipes() {
        return HPRecipes.instance().getPressRecipes();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        var recipes = getRecipes();
        recipes.removeAll(removeScripted());
        for (var recipe : restoreFromBackup()) {
            HPRecipes.instance().addPressRecipe(recipe);
        }
    }

    @Override
    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.add_to_list", priority = 500)
    public boolean add(PressRecipe recipe) {
        HPRecipes.instance().addPressRecipe(recipe);
        return recipe != null && doAddScripted(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public PressRecipe add(IIngredient input, ItemStack output) {
        return recipeBuilder()
                .output(output)
                .input(input)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public PressRecipe add(IIngredient input, FluidStack output) {
        return recipeBuilder()
                .fluidOutput(output)
                .input(input)
                .register();
    }

    @MethodDescription(example = @Example("item('minecraft:wheat_seeds')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(entry -> input.test(entry.getInput()) && doAddBackup(entry));
    }

    @MethodDescription(example = {
            @Example(value = "item('minecraft:dirt')", commented = true),
            @Example("fluid('water')")
    })
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(entry -> (output.test(entry.getOutput()) || output.test(entry.getOutputFluid())) && doAddBackup(entry));
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(gte = 0, lte = 1))
    @Property(property = "fluidOutput", comp = @Comp(gte = 0, lte = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<PressRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Horse Power Press recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 1);
            validateFluids(msg, 0, 0, 0, 1);
            msg.add(output.isEmpty() && fluidOutput.isEmpty(), "either output or fluidOutput must have have an entry, yet both were empty");
            msg.add(!output.isEmpty() && !fluidOutput.isEmpty(), "either output or fluidOutput must have have an entry, yet both had an entry");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable PressRecipe register() {
            if (!validate()) return null;
            PressRecipe recipe = null;
            if (fluidOutput.isEmpty()) {
                for (var stack : input.get(0).getMatchingStacks()) {
                    recipe = new PressRecipe(stack, output.get(0), ItemStack.EMPTY, 0, 0);
                    ModSupport.HORSE_POWER.get().press.add(recipe);
                }
            } else {
                for (var stack : input.get(0).getMatchingStacks()) {
                    recipe = new PressRecipe(stack, fluidOutput.get(0));
                    ModSupport.HORSE_POWER.get().press.add(recipe);
                }
            }
            return recipe;
        }
    }
}
