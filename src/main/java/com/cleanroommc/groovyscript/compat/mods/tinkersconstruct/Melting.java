package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.recipe.MeltingRecipeBuilder;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.recipe.MeltingRecipeRegistry;
import com.cleanroommc.groovyscript.core.mixin.tconstruct.TinkerRegistryAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

@RegistryDescription
public class Melting extends MeltingRecipeRegistry {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:gravel')).fluidOutput(fluid('lava') * 25).time(80)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder(this);
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(TinkerRegistryAccessor.getMeltingRegistry()::remove);
        restoreFromBackup().forEach(TinkerRegistryAccessor.getMeltingRegistry()::add);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public MeltingRecipe add(IIngredient input, FluidStack output, int temp) {
        MeltingRecipe recipe = new MeltingRecipe(MeltingRecipeBuilder.recipeMatchFromIngredient(input, output.amount), output, temp);
        add(recipe);
        return recipe;
    }

    public void add(MeltingRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        TinkerRegistryAccessor.getMeltingRegistry().add(recipe);
    }

    public boolean remove(MeltingRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        TinkerRegistryAccessor.getMeltingRegistry().remove(recipe);
        return true;
    }

    @MethodDescription
    public boolean removeByInput(IIngredient input) {
        NonNullList<ItemStack> matching = NonNullList.from(ItemStack.EMPTY, input.getMatchingStacks());
        if (TinkerRegistryAccessor.getMeltingRegistry().removeIf(recipe -> {
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
        if (TinkerRegistryAccessor.getMeltingRegistry().removeIf(recipe -> {
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
        if (TinkerRegistryAccessor.getMeltingRegistry().removeIf(recipe -> {
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

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        TinkerRegistryAccessor.getMeltingRegistry().forEach(this::addBackup);
        TinkerRegistryAccessor.getMeltingRegistry().clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<MeltingRecipe> streamRecipes() {
        return new SimpleObjectStream<>(TinkerRegistryAccessor.getMeltingRegistry()).setRemover(this::remove);
    }

    public static class RecipeBuilder extends MeltingRecipeBuilder {

        public RecipeBuilder(Melting melting) {
            super(melting, "Tinkers Construct Melting recipe");
        }
    }
}
