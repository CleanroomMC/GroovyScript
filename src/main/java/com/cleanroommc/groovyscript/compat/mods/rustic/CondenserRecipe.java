package com.cleanroommc.groovyscript.compat.mods.rustic;

import com.cleanroommc.groovyscript.api.IIngredient;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import rustic.common.crafting.ICondenserRecipe;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CondenserRecipe implements ICondenserRecipe {

    private static final List<ItemStack> EMPTY_LIST = Collections.singletonList(ItemStack.EMPTY);

    private final boolean advanced;
    private final int time;
    private final ItemStack output;
    private final FluidStack fluid;
    private final IIngredient bottle;
    private final IIngredient modifier;
    private final List<IIngredient> inputs;

    public CondenserRecipe(@Nonnull ItemStack output, List<IIngredient> inputs) {
        this(output, inputs, null);
    }

    public CondenserRecipe(@Nonnull ItemStack output, List<IIngredient> inputs, IIngredient modifier) {
        this(output, inputs, modifier, (IIngredient) (Object) new ItemStack(Items.GLASS_BOTTLE));
    }

    public CondenserRecipe(@Nonnull ItemStack output, List<IIngredient> inputs, IIngredient modifier, IIngredient bottle) {
        this(output, inputs, modifier, bottle, new FluidStack(FluidRegistry.WATER, 125));
    }

    public CondenserRecipe(@Nonnull ItemStack output, List<IIngredient> inputs, IIngredient modifier, IIngredient bottle, @Nonnull FluidStack fluid) {
        this(output, inputs, modifier, bottle, fluid, 400);
    }

    public CondenserRecipe(@Nonnull ItemStack output, List<IIngredient> inputs, IIngredient modifier, IIngredient bottle, @Nonnull FluidStack fluid, int time) {
        this(output, inputs, modifier, bottle, fluid, time, modifier == null && inputs.size() <= 2);
    }

    public CondenserRecipe(
            @Nonnull ItemStack output, List<IIngredient> inputs, IIngredient modifier, IIngredient bottle,
            @Nonnull FluidStack fluid, int time, boolean advanced) {
        this.output = output;
        this.fluid = fluid;
        this.bottle = bottle;
        this.modifier = modifier;
        this.inputs = inputs;
        this.time = Math.max(0, time);
        this.advanced = advanced;
    }

    public boolean matches(Fluid fluid, ItemStack modifier, ItemStack bottle, ItemStack[] inputs) {
        if (fluid == this.fluid.getFluid() &&
            (this.modifier == null || this.modifier.test(modifier)) &&
            (this.modifier != null || modifier.isEmpty()) &&
            (this.bottle == null || this.bottle.test(bottle))) {
            List<IIngredient> tempInputs = new ArrayList<>(this.inputs);
            for (ItemStack stack : inputs) {
                if (stack != null && !stack.isEmpty()) {
                    boolean stackNotInput = true;

                    for (IIngredient in : tempInputs) {
                        if (in == null || in.isEmpty()) {
                            tempInputs.remove(in);
                        } else if (in.test(stack)) {
                            stackNotInput = false;
                            tempInputs.remove(in);
                            break;
                        }
                    }

                    if (stackNotInput) {
                        return false;
                    }
                }
            }
            return tempInputs.isEmpty();
        }
        return false;
    }

    public boolean isBasic() {
        return !this.advanced;
    }

    public boolean isAdvanced() {
        return this.advanced;
    }

    public FluidStack getFluid() {
        return this.fluid;
    }

    public List<ItemStack> getModifiers() {
        return modifier == null ? EMPTY_LIST : Arrays.asList(modifier.getMatchingStacks());
    }

    public List<ItemStack> getBottles() {
        return bottle == null ? EMPTY_LIST : Arrays.asList(bottle.getMatchingStacks());
    }

    public List<List<ItemStack>> getInputs() {
        return this.inputs.stream().map(x -> Arrays.asList(x.getMatchingStacks())).collect(Collectors.toList());
    }

    public int getTime() {
        return this.time;
    }

    public int getModifierConsumption(ItemStack modifier) {
        return this.modifier != null && this.modifier.test(modifier) ? modifier.getCount() : 0;
    }

    public int getBottleConsumption(ItemStack bottle) {
        return this.bottle != null && this.bottle.test(bottle) ? bottle.getCount() : 0;
    }

    public int[] getInputConsumption(ItemStack[] inputs) {
        int[] consume = new int[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            consume[i] = 0;
            if (inputs[i] != null) {
                for (IIngredient in : this.inputs) {
                    if (in.test(inputs[i])) {
                        consume[i] = in.getAmount();
                        break;
                    }
                }
            }
        }
        return consume;
    }

    public ItemStack getResult() {
        return this.output.copy();
    }
}
