package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.crafting.IngredientStack;
import blusunrize.immersiveengineering.api.crafting.MixerRecipe;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.EnergyRecipeBuilder;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;

public class Mixer extends VirtualizedRegistry<MixerRecipe> {

    public Mixer() {
        super("Mixer", "mixer");
    }

    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> MixerRecipe.recipeList.removeIf(r -> r == recipe));
        MixerRecipe.recipeList.addAll(restoreFromBackup());
    }

    public void add(MixerRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            MixerRecipe.recipeList.add(recipe);
        }
    }

    public MixerRecipe add(FluidStack fluidOutput, FluidStack fluidInput, int energy, Object... itemInput) {
        MixerRecipe recipe = create(fluidOutput, fluidInput, itemInput, energy);
        addScripted(recipe);
        return recipe;
    }

    public void remove(MixerRecipe recipe) {
        if (MixerRecipe.recipeList.removeIf(r -> r == recipe)) addBackup(recipe);
    }

    public void removeByOutput(FluidStack fluidOutput) {
         for (Iterator<MixerRecipe> iterator = MixerRecipe.recipeList.iterator(); iterator.hasNext(); ) {
            MixerRecipe recipe = iterator.next();
            if (recipe.fluidOutput.isFluidEqual(fluidOutput)) {
                addBackup(recipe);
                iterator.remove();
            }
        }
    }

    public void removeByInput(ItemStack... itemInputs) {
        for (Iterator<MixerRecipe> iterator = MixerRecipe.recipeList.iterator(); iterator.hasNext(); ) {
            MixerRecipe recipe = iterator.next();

            if (recipe.itemInputs.length != itemInputs.length) continue;

            int i;
            for (i = 0; i < itemInputs.length; i++) {
                if (!recipe.itemInputs[i].matches(itemInputs[i])) break;
            }

            if (i == itemInputs.length) {
                addBackup(recipe);
                iterator.remove();
            }
        }
    }
    
    public void removeByInput(FluidStack fluidInput, ItemStack... itemInput) {
        NonNullList<ItemStack> inputs = NonNullList.create();
        inputs.addAll(Arrays.asList(itemInput));
        
        MixerRecipe recipe = MixerRecipe.findRecipe(fluidInput, inputs);
        remove(recipe);
    }
    
    public SimpleObjectStream<MixerRecipe> stream() {
        return new SimpleObjectStream<>(MixerRecipe.recipeList).setRemover(recipe -> {
            NonNullList<ItemStack> list = NonNullList.create();
            for (IngredientStack ing : recipe.itemInputs) list.add(ing.stack);

            MixerRecipe r = MixerRecipe.findRecipe(recipe.fluidInput, list);
            if (r != null) {
                remove(r);
                return true;
            }
            return false;
        });
    }
    
    public void removeAll() {
        MixerRecipe.recipeList.forEach(this::addBackup);
        MixerRecipe.recipeList.clear();
    }

    private static MixerRecipe create(FluidStack fluidOutput, FluidStack fluidInput, Object[] itemInput, int energy) {
        for (int i = 0; i < itemInput.length; i++) {
            if (itemInput[i] instanceof IIngredient) itemInput[i] = ((IIngredient) itemInput[i]).getMatchingStacks();
        }

        return MixerRecipe.addRecipe(fluidOutput, fluidInput, itemInput, energy);
    }

    public static class RecipeBuilder extends EnergyRecipeBuilder<MixerRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Engineering Mixer recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 8, 0, 0);
            validateFluids(msg, 1, 1, 1, 1);
        }

        @Override
        public @Nullable MixerRecipe register() {
            if (!validate()) return null;
            return ModSupport.IMMERSIVE_ENGINEERING.get().mixer.add(fluidOutput.get(0), fluidInput.get(0), energy, input.toArray());
        }
    }
}
