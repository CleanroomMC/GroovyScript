package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.crafting.FermenterRecipe;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.EnergyRecipeBuilder;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Iterator;

public class Fermenter extends VirtualizedRegistry<FermenterRecipe> {

    public Fermenter() {
        super("Fermenter", "fermenter");
    }

    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> FermenterRecipe.recipeList.removeIf(r -> r == recipe));
        FermenterRecipe.recipeList.addAll(restoreFromBackup());
    }

    public void add(FermenterRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            FermenterRecipe.recipeList.add(recipe);
        }
    }

    public FermenterRecipe add(FluidStack fluidOutput, @Nonnull ItemStack itemOutput, Object input, int energy) {
        FermenterRecipe recipe = create(fluidOutput, itemOutput, input, energy);
        addScripted(recipe);
        return recipe;
    }

    public void remove(FermenterRecipe recipe) {
        if (FermenterRecipe.recipeList.removeIf(r -> r == recipe)) addBackup(recipe);
    }

    public void removeByOutput(FluidStack fluidOutput) {
        for (Iterator<FermenterRecipe> iterator = FermenterRecipe.recipeList.iterator(); iterator.hasNext(); ) {
            FermenterRecipe recipe = iterator.next();
            if (recipe.fluidOutput.isFluidEqual(fluidOutput)) {
                addBackup(recipe);
                iterator.remove();
            }
        }
    }

    public void removeByInput(ItemStack input) {
        for (Iterator<FermenterRecipe> iterator = FermenterRecipe.recipeList.iterator(); iterator.hasNext(); ) {
            FermenterRecipe recipe = iterator.next();
            if (recipe.input.matches(input)) {
                addBackup(recipe);
                iterator.remove();
            }
        }
    }

    public SimpleObjectStream<FermenterRecipe> stream() {
        return new SimpleObjectStream<>(FermenterRecipe.recipeList).setRemover(r -> {
            FermenterRecipe recipe = FermenterRecipe.findRecipe(r.input.stack);
            if (recipe != null) {
                remove(recipe);
                return true;
            }
            return false;
        });
    }

    public void removeAll() {
        FermenterRecipe.recipeList.forEach(this::addBackup);
        FermenterRecipe.recipeList.clear();
    }

    private static FermenterRecipe create(FluidStack fluidOutput, @Nonnull ItemStack itemOutput, Object input, int energy) {
        if (input instanceof IIngredient) input = ((IIngredient) input).getMatchingStacks();
        return FermenterRecipe.addRecipe(fluidOutput, itemOutput, input, energy);
    }

    public static class RecipeBuilder extends EnergyRecipeBuilder<FermenterRecipe> {

        protected ItemStack out = ItemStack.EMPTY;

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Engineering Fermenter recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 1);
            validateFluids(msg, 0, 0, 1, 1);
            if (output.size() > 0) out = output.get(0);
        }

        @Override
        public @Nullable FermenterRecipe register() {
            if (!validate()) return null;
            return ModSupport.IMMERSIVE_ENGINEERING.get().fermenter.add(fluidOutput.get(0), out, input.get(0), energy);
        }
    }
}
