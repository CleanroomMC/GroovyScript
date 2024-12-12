package com.cleanroommc.groovyscript.compat.mods.alchemistry;

import al132.alchemistry.recipes.LiquifierRecipe;
import al132.alchemistry.recipes.ModRecipes;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class Liquifier extends StandardListRegistry<LiquifierRecipe> {

    @Override
    public Collection<LiquifierRecipe> getRecipes() {
        return ModRecipes.INSTANCE.getLiquifierRecipes();
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(element('carbon') * 32).fluidOutput(fluid('water') * 1000)"),
            @Example(".input(item('minecraft:magma')).fluidOutput(fluid('lava') * 750)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public LiquifierRecipe add(IIngredient input, FluidStack output) {
        return recipeBuilder().input(input).fluidOutput(output).register();
    }

    @MethodDescription(example = @Example(value = "fluid('water')", commented = true))
    public boolean removeByOutput(FluidStack output) {
        return getRecipes().removeIf(r -> {
            if (r.getOutput().isFluidEqual(output)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("element('water')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> {
            if (input.test(r.getInput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "fluidOutput", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<LiquifierRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Alchemistry Liquifier recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg, 0, 0, 1, 1);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable LiquifierRecipe register() {
            if (!validate()) return null;
            LiquifierRecipe recipe = new LiquifierRecipe(IngredientHelper.toItemStack(input.get(0)), fluidOutput.get(0));
            ModSupport.ALCHEMISTRY.get().liquifier.add(recipe);
            return recipe;
        }
    }
}
