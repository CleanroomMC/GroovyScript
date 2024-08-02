package com.cleanroommc.groovyscript.api.jeiremoval.operations;

import com.cleanroommc.groovyscript.compat.mods.jei.removal.OperationHandler;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IRecipeLayout;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Abstract class containing the core logic of the operation,
 * parsing the provided gui ingredients through a provided function.
 */
@ParametersAreNonnullByDefault
public abstract class SlotOperation<T> extends BaseSlotOperation<T> {

    protected final boolean hasExactInput;
    protected final BiFunction<T, List<T>, String> function;

    /**
     * @param hasExactInput if slots marked as an input should add to the exactInput list in {@link #parse(IRecipeLayout, List, List)}.
     * @param function      a bifunction taking the primary ingredient in the slot and all ingredients in the slot and returning
     *                      a String to represent that slot, typically via an {@link com.cleanroommc.groovyscript.mapper.ObjectMapper ObjectMapper}.
     */
    public SlotOperation(boolean hasExactInput, BiFunction<T, List<T>, String> function) {
        this.hasExactInput = hasExactInput;
        this.function = function;
    }

    /**
     * @param layout the recipe layout being parsed
     * @return the ingredients for the target recipe layout
     */
    abstract Map<Integer, ? extends IGuiIngredient<T>> getGuiIngredients(IRecipeLayout layout);

    protected String function(T stack, List<T> list) {
        return this.function.apply(stack, list);
    }

    @Override
    public void parse(IRecipeLayout layout, List<String> removing, List<String> exactInput) {
        for (var slot : getGuiIngredients(layout).entrySet()) {
            if (isIgnored(slot)) continue;

            var stack = OperationHandler.getIngredientFromSlot(slot.getValue());
            if (stack == null) continue;

            var identity = function(stack, slot.getValue().getAllIngredients());
            if (this.hasExactInput && !isOutput(slot)) exactInput.add(identity);
            removing.add(OperationHandler.format(getMethod(slot), identity));
        }
    }
}
