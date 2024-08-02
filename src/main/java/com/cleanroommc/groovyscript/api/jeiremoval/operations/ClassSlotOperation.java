package com.cleanroommc.groovyscript.api.jeiremoval.operations;

import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IRecipeLayout;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Class for ingredient groups obtained via the deprecated {@link IRecipeLayout#getIngredientsGroup(Class)} method.
 * <p>
 * Used to read the gui ingredients added via the deprecated format.
 *
 * @see IngredientSlotOperation
 */
@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
public class ClassSlotOperation<T> extends SlotOperation<T> {

    private final Class<T> clazz;

    /**
     * @param clazz         the class that is used to get the gui ingredients from the recipe layout through the deprecated format
     * @param hasExactInput if slots marked as an input should add to the exactInput list in {@link #parse(IRecipeLayout, List, List)}.
     * @param function      a bifunction taking the primary ingredient in the slot and all ingredients in the slot and returning
     *                      a String to represent that slot, typically via an {@link com.cleanroommc.groovyscript.mapper.ObjectMapper ObjectMapper}.
     */
    public ClassSlotOperation(Class<T> clazz, boolean hasExactInput, BiFunction<T, List<T>, String> function) {
        super(hasExactInput, function);
        this.clazz = clazz;
    }

    @Override
    protected Map<Integer, ? extends IGuiIngredient<T>> getGuiIngredients(IRecipeLayout layout) {
        return layout.getIngredientsGroup(clazz).getGuiIngredients();
    }

}
