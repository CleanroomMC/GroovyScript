package com.cleanroommc.groovyscript.compat.mods.enderio.recipe;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.ArrayUtils;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.stackable.Things;
import crazypants.enderio.base.recipe.IRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.RecipeOutput;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.List;

public class RecipeUtils {

    public static NNList<MachineRecipeInput> getMachineInputs(Collection<ItemStack> itemStacks) {
        NNList<MachineRecipeInput> inputs = new NNList<>();
        for (ItemStack itemStack : itemStacks) {
            inputs.add(new MachineRecipeInput(-1, itemStack));
        }
        return inputs;
    }

    public static IRecipeInput[] toEIOInputs(IIngredient[] inputs) {
        return ArrayUtils.map(inputs, RecipeInput::new, new IRecipeInput[0]);
    }

    public static NNList<IRecipeInput> toEIOInputsNN(List<IIngredient> inputs) {
        NNList<IRecipeInput> ret = new NNList<>();
        for (IIngredient input : inputs) {
            ret.add(new RecipeInput(input));
        }
        return ret;
    }

    public static RecipeOutput[] toEIOOutputs(ItemStack[] inputs, float[] chances, float[] xp) {
        RecipeOutput[] ret = new RecipeOutput[inputs.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = new RecipeOutput(inputs[i], chances[i], xp[i]);
        }
        return ret;
    }

    public static String getDisplayString(IIngredient... ings) {
        StringBuilder sb = new StringBuilder("[");
        for (IIngredient i : ings)
            sb.append(i == null ? i : i + ",");
        sb.replace(sb.length() - 1, sb.length(), "");
        return sb.append("]").toString();
    }

    public static Things toThings(IIngredient ingredient) {
        Things thing = new Things();
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            thing.add(itemStack);
        }
        return thing;
    }

}
