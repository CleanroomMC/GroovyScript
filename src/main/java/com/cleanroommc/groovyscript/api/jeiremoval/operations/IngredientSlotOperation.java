package com.cleanroommc.groovyscript.api.jeiremoval.operations;

import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IIngredientType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Class for ingredient groups obtained via {@link IRecipeLayout#getIngredientsGroup(IIngredientType)}.
 * <p>
 * Used to read the gui ingredients added via the modern format.
 *
 * @see ClassSlotOperation
 */
@ParametersAreNonnullByDefault
public class IngredientSlotOperation<T> extends SlotOperation<T> {

    private final IIngredientType<T> type;

    /**
     * @param type          the {@link IIngredientType} that is used to get the gui ingredients from the recipe layout
     * @param hasExactInput if slots marked as an input should add to the exactInput list in {@link #parse(IRecipeLayout, List, List)}.
     * @param function      a bifunction taking the primary ingredient in the slot and all ingredients in the slot and returning
     *                      a String to represent that slot, typically via an {@link com.cleanroommc.groovyscript.mapper.ObjectMapper ObjectMapper}.
     */
    public IngredientSlotOperation(IIngredientType<T> type, boolean hasExactInput, BiFunction<T, List<T>, String> function) {
        super(hasExactInput, function);
        this.type = type;
    }

    @Override
    protected Map<Integer, ? extends IGuiIngredient<T>> getGuiIngredients(IRecipeLayout layout) {
        return layout.getIngredientsGroup(type).getGuiIngredients();
    }

}
