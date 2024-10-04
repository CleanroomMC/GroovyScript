package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.recipe.MeltingRecipeBuilder;
import com.cleanroommc.groovyscript.core.mixin.tconstruct.TinkerRegistryAccessor;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

import java.util.Collection;

@RegistryDescription
public class Melting extends StandardListRegistry<MeltingRecipe> {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:gravel')).fluidOutput(fluid('lava') * 25).time(80)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder(this);
    }

    @Override
    public Collection<MeltingRecipe> getRecipes() {
        return TinkerRegistryAccessor.getMeltingRegistry();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public MeltingRecipe add(IIngredient input, FluidStack output, int temp) {
        MeltingRecipe recipe = new MeltingRecipe(MeltingRecipeBuilder.recipeMatchFromIngredient(input, output.amount), output, temp);
        add(recipe);
        return recipe;
    }

    @MethodDescription
    public boolean removeByInput(IIngredient input) {
        NonNullList<ItemStack> matching = NonNullList.from(ItemStack.EMPTY, input.getMatchingStacks());
        if (getRecipes().removeIf(recipe -> {
            boolean found = recipe.input.matches(matching).isPresent();
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Tinkers Construct Melting recipe")
                .add("could not find recipe with input {}", input)
                .error()
                .post();
        return false;
    }

    @MethodDescription
    public boolean removeByOutput(FluidStack output) {
        if (getRecipes().removeIf(recipe -> {
            boolean found = recipe.getResult().isFluidEqual(output);
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Tinkers Construct Melting recipe")
                .add("could not find recipe with output {}", output)
                .error()
                .post();
        return false;
    }

    @MethodDescription
    public boolean removeByInputAndOutput(IIngredient input, FluidStack output) {
        NonNullList<ItemStack> matching = NonNullList.from(ItemStack.EMPTY, input.getMatchingStacks());
        if (getRecipes().removeIf(recipe -> {
            boolean found = recipe.input.matches(matching).isPresent() && recipe.getResult().isFluidEqual(output);
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Tinkers Construct Melting recipe")
                .add("could not find recipe with input {} and output {}", input, output)
                .error()
                .post();
        return false;
    }

    public static class RecipeBuilder extends MeltingRecipeBuilder {

        public RecipeBuilder(Melting melting) {
            super(melting, "Tinkers Construct Melting recipe");
        }
    }
}
