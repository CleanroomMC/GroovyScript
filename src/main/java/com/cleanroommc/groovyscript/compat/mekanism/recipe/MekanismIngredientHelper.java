package com.cleanroommc.groovyscript.compat.mekanism.recipe;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.OreDictIngredient;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
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

    private MekanismIngredientHelper() {
    }

    public static IIngredient optionalIngredient(IIngredient ingredient) {
        return ingredient != null ? ingredient : IIngredient.ANY;
    }

    public static boolean checkNotNull(String name, IIngredient... ingredients) {
        for (IIngredient ingredient : ingredients) {
            if (ingredient == null) {
                GroovyLog.LOG.error(String.format("Required parameters missing for %s Recipe.", name));
                return false;
            }
        }
        return true;
    }

    private static IIngredient getIngredient(Object ingredient) {
        if (ingredient instanceof IIngredient) {
            return (IIngredient) ingredient;
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
        if (input instanceof GasStack) {
            return matches(toMatch, (GasStack) input);
        }
        if ((Object) input instanceof ItemStack) {
            return toMatch != null && toMatch.test((ItemStack) (Object) input);
        }
        if (input instanceof FluidStack) {
            return toMatch != null && toMatch.test((FluidStack) input);
        }
        //TODO: Support other types of things like ore dict
        return false;
    }

    public static boolean matches(Object input, IIngredient toMatch) {
        return matches(getIngredient(input), toMatch);
    }

    public static <INPUT extends MachineInput<INPUT>> boolean matches(INPUT in, IngredientWrapper toMatch) {
        if (in instanceof ItemStackInput) {
            ItemStackInput input = (ItemStackInput) in;
            return matches(input.ingredient, toMatch.getIngredient());
        } else if (in instanceof GasInput) {
            GasInput input = (GasInput) in;
            return matches(input.ingredient, toMatch.getIngredient());
        } else if (in instanceof FluidInput) {
            FluidInput input = (FluidInput) in;
            return matches(input.ingredient, toMatch.getIngredient());
        } else if (in instanceof AdvancedMachineInput) {
            AdvancedMachineInput input = (AdvancedMachineInput) in;
            return matches(input.itemStack, toMatch.getLeft()) && matches(input.gasType, toMatch.getRight());
        } else if (in instanceof ChemicalPairInput) {
            ChemicalPairInput input = (ChemicalPairInput) in;
            return matches(input.leftGas, toMatch.getLeft()) && matches(input.rightGas, toMatch.getRight());
        } else if (in instanceof DoubleMachineInput) {
            DoubleMachineInput input = (DoubleMachineInput) in;
            return matches(input.itemStack, toMatch.getLeft()) && matches(input.extraStack, toMatch.getRight());
        } else if (in instanceof PressurizedInput) {
            PressurizedInput input = (PressurizedInput) in;
            return matches(input.getSolid(), toMatch.getLeft()) && matches(input.getFluid(), toMatch.getMiddle()) && matches(input.getGas(), toMatch.getRight());
        } else if (in instanceof InfusionInput) {
            InfusionInput input = (InfusionInput) in;
            return matches(input.inputStack, toMatch.getIngredient()) && (toMatch.getInfuseType().isEmpty() || toMatch.getInfuseType().equalsIgnoreCase(input.infuse.getType().name));
        } else if (in instanceof IntegerInput) {
            IntegerInput input = (IntegerInput) in;
            return input.ingredient == toMatch.getAmount();
        }
        return false;
    }

    public static <OUTPUT extends MachineOutput<OUTPUT>> boolean matches(OUTPUT out, IngredientWrapper toMatch) {
        if (out instanceof ItemStackOutput) {
            ItemStackOutput output = (ItemStackOutput) out;
            return matches(output.output, toMatch.getIngredient());
        }
        if (out instanceof GasOutput) {
            GasOutput output = (GasOutput) out;
            return matches(output.output, toMatch.getIngredient());
        }
        if (out instanceof FluidOutput) {
            FluidOutput output = (FluidOutput) out;
            return matches(output.output, toMatch.getIngredient());
        }
        if (out instanceof ChanceOutput) {
            ChanceOutput output = (ChanceOutput) out;
            return matches(output.primaryOutput, toMatch.getLeft()) && matches(output.secondaryOutput, toMatch.getRight());
        }
        if (out instanceof ChemicalPairOutput) {
            ChemicalPairOutput output = (ChemicalPairOutput) out;
            return matches(output.leftGas, toMatch.getLeft()) && matches(output.rightGas, toMatch.getRight());
        }
        if (out instanceof PressurizedOutput) {
            PressurizedOutput output = (PressurizedOutput) out;
            return matches(output.getItemOutput(), toMatch.getLeft()) && matches(output.getGasOutput(), toMatch.getRight());
        }
        return false;
    }

    public static IMekanismIngredient<ItemStack> getMekanismIngredient(IIngredient ingredient) {
        if (ingredient instanceof OreDictIngredient) {
            return new OredictMekIngredient(((OreDictIngredient) ingredient).getOreDict());
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
        if (ingredient instanceof GasStack) {
            return ((GasStack) ingredient).isGasEqual(gasStack);
        }
        return false;
    }
}
