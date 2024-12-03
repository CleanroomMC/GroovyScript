package com.cleanroommc.groovyscript.compat.mods.mekanism.recipe;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.common.recipe.ingredients.IMekanismIngredient;
import mekanism.common.recipe.ingredients.IngredientMekIngredientWrapper;
import mekanism.common.recipe.ingredients.ItemStackMekIngredient;
import mekanism.common.recipe.ingredients.OredictMekIngredient;
import mekanism.common.recipe.inputs.*;
import mekanism.common.recipe.outputs.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class MekanismIngredientHelper {

    private MekanismIngredientHelper() {}

    public static IIngredient optionalIngredient(IIngredient ingredient) {
        return ingredient != null ? ingredient : IIngredient.ANY;
    }

    public static boolean checkNotNull(String name, IIngredient... ingredients) {
        for (IIngredient ingredient : ingredients) {
            if (ingredient == null) {
                GroovyLog.get().error("Required parameters missing for {} Recipe.", name);
                return false;
            }
        }
        return true;
    }

    private static IIngredient getIngredient(Object ingredient) {
        if (ingredient instanceof IIngredient iIngredient) {
            return iIngredient;
        }

        if (ingredient instanceof Gas) {
            return (IIngredient) new GasStack((Gas) ingredient, 1);
        }
        if (ingredient instanceof Fluid) {
            return (IIngredient) new FluidStack((Fluid) ingredient, 1);
        }
        //TODO: Support other types of things like ore dict
        return IIngredient.ANY;
    }

    public static boolean matches(IIngredient input, IIngredient toMatch) {
        if (input instanceof GasStack gasStack) {
            return matches(toMatch, gasStack);
        }
        if (IngredientHelper.isItem(input)) {
            return toMatch != null && toMatch.test(IngredientHelper.toItemStack(input));
        }
        if (input instanceof FluidStack fluidStack) {
            return toMatch != null && toMatch.test(fluidStack);
        }
        //TODO: Support other types of things like ore dict
        return false;
    }

    public static boolean matches(Object input, IIngredient toMatch) {
        return matches(getIngredient(input), toMatch);
    }

    public static <INPUT extends MachineInput<INPUT>> boolean matches(INPUT in, IngredientWrapper toMatch) {
        if (in instanceof ItemStackInput input) {
            return matches(input.ingredient, toMatch.getIngredient());
        } else if (in instanceof GasInput input) {
            return matches(input.ingredient, toMatch.getIngredient());
        } else if (in instanceof FluidInput input) {
            return matches(input.ingredient, toMatch.getIngredient());
        } else if (in instanceof AdvancedMachineInput input) {
            return matches(input.itemStack, toMatch.getLeft()) && matches(input.gasType, toMatch.getRight());
        } else if (in instanceof ChemicalPairInput input) {
            return matches(input.leftGas, toMatch.getLeft()) && matches(input.rightGas, toMatch.getRight());
        } else if (in instanceof DoubleMachineInput input) {
            return matches(input.itemStack, toMatch.getLeft()) && matches(input.extraStack, toMatch.getRight());
        } else if (in instanceof PressurizedInput input) {
            return matches(input.getSolid(), toMatch.getLeft()) && matches(input.getFluid(), toMatch.getMiddle()) && matches(input.getGas(), toMatch.getRight());
        } else if (in instanceof InfusionInput input) {
            return matches(input.inputStack, toMatch.getIngredient()) && (toMatch.getInfuseType().isEmpty() || toMatch.getInfuseType().equalsIgnoreCase(input.infuse.getType().name));
        } else if (in instanceof IntegerInput input) {
            return input.ingredient == toMatch.getAmount();
        }
        return false;
    }

    public static <OUTPUT extends MachineOutput<OUTPUT>> boolean matches(OUTPUT out, IngredientWrapper toMatch) {
        if (out instanceof ItemStackOutput output) {
            return matches(output.output, toMatch.getIngredient());
        }
        if (out instanceof GasOutput output) {
            return matches(output.output, toMatch.getIngredient());
        }
        if (out instanceof FluidOutput output) {
            return matches(output.output, toMatch.getIngredient());
        }
        if (out instanceof ChanceOutput output) {
            return matches(output.primaryOutput, toMatch.getLeft()) && matches(output.secondaryOutput, toMatch.getRight());
        }
        if (out instanceof ChemicalPairOutput output) {
            return matches(output.leftGas, toMatch.getLeft()) && matches(output.rightGas, toMatch.getRight());
        }
        if (out instanceof PressurizedOutput output) {
            return matches(output.getItemOutput(), toMatch.getLeft()) && matches(output.getGasOutput(), toMatch.getRight());
        }
        return false;
    }

    public static IMekanismIngredient<ItemStack> getMekanismIngredient(IIngredient ingredient) {
        if (ingredient instanceof OreDictIngredient oreDictIngredient) {
            return new OredictMekIngredient(oreDictIngredient.getOreDict());
        }
        if (IngredientHelper.isItem(ingredient)) {
            return new ItemStackMekIngredient(IngredientHelper.toItemStack(ingredient));
        }
        return new IngredientMekIngredientWrapper(ingredient.toMcIngredient());
    }

    public static boolean matches(IIngredient ingredient, GasStack gasStack) {
        if (ingredient == null) {
            return false;
        }
        if (ingredient == IIngredient.ANY) {
            return true;
        }
        if (ingredient instanceof GasStack stack) {
            return stack.isGasEqual(gasStack);
        }
        return false;
    }
}
