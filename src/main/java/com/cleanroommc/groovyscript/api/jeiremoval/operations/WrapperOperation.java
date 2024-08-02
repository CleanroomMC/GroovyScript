package com.cleanroommc.groovyscript.api.jeiremoval.operations;

import com.cleanroommc.groovyscript.compat.mods.jei.removal.OperationHandler;
import mezz.jei.api.gui.IRecipeLayout;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Function;

/**
 * Checks that the wrapper is an instance of the given class, then calls the provided function parameter
 * to generate a list of methods to remove the recipe based on the recipe wrapper.
 * <p>
 * Note that in most cases, using {@link BaseSlotOperation} and some combination of the
 * {@link ISlotOperation#exclude(int...)}, {@link ISlotOperation#input(int...)}, and {@link ISlotOperation#output(int...)}
 * methods will be able to generate all the removal methods needed.
 * <p>
 * {@link WrapperOperation} should generally only be used if it isn't possible to access
 * the desired information through a {@link BaseSlotOperation}.
 *
 * @param <T> wrapper type
 */
@ParametersAreNonnullByDefault
public class WrapperOperation<T> implements IOperation {

    private final Class<T> wrapperClass;
    private final Function<T, List<String>> function;

    /**
     * @param wrapperClass the wrapper class that is being parsed
     * @param function     a function that accepts an instance of the wrapper class and returns a list of strings
     *                     to generate output methods for the given recipe
     */
    public WrapperOperation(Class<T> wrapperClass, Function<T, List<String>> function) {
        this.wrapperClass = wrapperClass;
        this.function = function;
    }

    protected List<String> function(T wrapper) {
        return this.function.apply(wrapper);
    }

    @Override
    public void parse(IRecipeLayout layout, List<String> removing, List<String> exactInput) {
        var wrapper = OperationHandler.getRecipeWrapper(layout);
        if (wrapperClass.isInstance(wrapper)) {
            removing.addAll(function(wrapperClass.cast(wrapper)));
        }
    }

}
