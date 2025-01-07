package com.cleanroommc.groovyscript.compat.mods.alchemistry;

import al132.alchemistry.recipes.EvaporatorRecipe;
import al132.alchemistry.recipes.ModRecipes;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class Evaporator extends StandardListRegistry<EvaporatorRecipe> {

    @Override
    public Collection<EvaporatorRecipe> getRecipes() {
        return ModRecipes.INSTANCE.getEvaporatorRecipes();
    }

    @RecipeBuilderDescription(example = {
            @Example(".fluidInput(fluid('lava') * 100).output(item('minecraft:redstone') * 8)"),
            @Example(".fluidInput(fluid('water') * 10).output(item('minecraft:clay'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public EvaporatorRecipe add(FluidStack input, ItemStack output) {
        return recipeBuilder().fluidInput(input).output(output).register();
    }

    @MethodDescription(example = @Example("item('alchemistry:mineral_salt')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> {
            if (output.test(r.getOutput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("fluid('lava')"))
    public boolean removeByInput(FluidStack input) {
        return getRecipes().removeIf(r -> {
            if (r.getInput().isFluidEqual(input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @Property(property = "fluidInput", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<EvaporatorRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Alchemistry Evaporator recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 1, 1);
            validateFluids(msg, 1, 1, 0, 0);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable EvaporatorRecipe register() {
            if (!validate()) return null;
            EvaporatorRecipe recipe = new EvaporatorRecipe(fluidInput.get(0), output.get(0));
            ModSupport.ALCHEMISTRY.get().evaporator.add(recipe);
            return recipe;
        }
    }
}
