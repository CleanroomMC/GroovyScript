package com.cleanroommc.groovyscript.api.jeiremoval.operations;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Interface for a {@link BaseSlotOperation}, requiring the ability to
 * block specific slots from appearing,
 * override the input or output state of a slot (due to the slot being incorrectly listed)
 * set the method name for the input or output.
 *
 * @param <R> the operation class
 */
@ParametersAreNonnullByDefault
public interface ISlotOperation<R> extends IOperation {

    /**
     * Include all provided slot numbers to be evaluated. If this is called, {@link #exclude(int...)} should be ignored.
     *
     * @param ints all slots to be included
     */
    ISlotOperation<R> include(int... ints);

    /**
     * Excludes all provided slot numbers from being evaluated.
     * Some slots cannot be parsed properly, are superfluous, contain helper items unrelated to the recipe,
     * or otherwise should be ignored when generating methods to remove the recipe.
     *
     * @param ints all slots to be excluded
     */
    ISlotOperation<R> exclude(int... ints);

    /**
     * Sets all provided slot numbers as an input slot.
     * Some slots incorrectly list themselves as an input or an output.
     *
     * @param ints all slots to be set as an input
     */
    ISlotOperation<R> input(int... ints);

    /**
     * Sets all provided slot numbers as an output slot.
     * Some slots incorrectly list themselves as an input or an output.
     *
     * @param ints all slots to be set as an output
     */
    ISlotOperation<R> output(int... ints);

    /**
     * @param name the name of the method used to remove inputs
     */
    ISlotOperation<R> input(String name);

    /**
     * @param name the name of the method used to remove outputs
     */
    ISlotOperation<R> output(String name);

}
