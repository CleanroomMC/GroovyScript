package com.cleanroommc.groovyscript.compat.mods.factorytech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import dalapo.factech.auxiliary.MachineRecipes;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class Refrigerator extends StandardListRegistry<MachineRecipes.MachineRecipe<FluidStack, ItemStack>> {

    @Override
    public Collection<MachineRecipes.MachineRecipe<FluidStack, ItemStack>> getRecipes() {
        return MachineRecipes.REFRIGERATOR;
    }

    @RecipeBuilderDescription(example = {
            @Example(".fluidInput(fluid('water') * 100).output(item('minecraft:diamond'))"),
            @Example(".fluidInput(fluid('lava') * 30).output(item('minecraft:clay'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(example = @Example("fluid('water')"))
    public void removeByInput(IIngredient input) {
        getRecipes().removeIf(r -> input.test(r.input()) && doAddBackup(r));
    }

    @MethodDescription(example = @Example("item('minecraft:obsidian')"))
    public void removeByOutput(IIngredient output) {
        getRecipes().removeIf(r -> output.test(r.getOutputStack()) && doAddBackup(r));
    }

    @Property(property = "fluidInput", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<MachineRecipes.MachineRecipe<FluidStack, ItemStack>> {

        @Override
        public String getErrorMsg() {
            return "Error adding Factory Tech Refrigerator recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 1, 1);
            validateFluids(msg, 1, 1, 0, 0);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable MachineRecipes.MachineRecipe<FluidStack, ItemStack> register() {
            if (!validate()) return null;
            var recipe = new MachineRecipes.MachineRecipe<>(fluidInput.get(0), output.get(0));
            ModSupport.FACTORY_TECH.get().refrigerator.add(recipe);
            return recipe;
        }
    }
}
