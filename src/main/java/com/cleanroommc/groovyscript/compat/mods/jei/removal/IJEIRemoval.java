package com.cleanroommc.groovyscript.compat.mods.jei.removal;

import mezz.jei.api.gui.IRecipeLayout;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * Interface that should be implemented on {@link com.cleanroommc.groovyscript.api.INamed} registries,
 * primarily those that would be iterated through as part of {@link com.cleanroommc.groovyscript.compat.mods.ModSupport#getAllContainers()},
 * and contains methods indicating what JEI Categories the given registry represents
 * and how to remove those recipes.
 * <p>
 * Primarily interacted with by {@link JeiRemovalHelper#getRemovalMethod(String, IRecipeLayout)}.
 * <p>
 * In most cases, classes should implement {@link Default} instead of directly implementing {@link IJEIRemoval}.
 *
 * @see Default
 */
public interface IJEIRemoval {

    /**
     * A list containing any number of strings which
     * are the UIDs of a JEI recipe category {@link mezz.jei.api.recipe.IRecipeCategory#getUid()}.
     * <br>
     * This list is typically immutable.
     *
     * @return a list of all UIDs associated with the target recipe category
     */
    @NotNull
    Collection<String> getCategories();

    /**
     * Generates one or more removal methods for the targeted recipe, if possible,
     * using the information from JEI to do so.
     * <p>
     * If possible, the first removal method should only remove a single recipe,
     * but this is not required and may not be possible.
     * <p>
     * The path to the recipe will automatically be generated before each entry, only the method name and its parameters should be returned.
     * <p>
     * Any empty list should be returned if generating a removal method is not possible.
     *
     * @param layout a map of type to group of slots displayed in JEI
     * @return a collection of all generated method to remove the targeted recipe
     */
    @NotNull
    List<String> getRemoval(IRecipeLayout layout);

    /**
     * Has a default removal method, {@link #getRemoval(IRecipeLayout)},
     * which returns a method to remove the recipe for each input and output.
     * <p>
     * If changing the operations interacted with is required, implementations should generally override
     * the {@link #getJEIOperations()} method instead of {@link #getRemoval(IRecipeLayout)}.
     */
    interface Default extends IJEIRemoval {

        /**
         * A shorthand method that modifies the default operations so each
         * of the default operations excludes the provided slots.
         * <p>
         * Should be used inside {@link #getJEIOperations()} to provide the return value.
         *
         * @param excluded all slots to be excluded
         * @return a list if operations with the given slots excluded
         * @see OperationHandler#defaultOperations()
         */
        static List<OperationHandler.IOperation> excludeSlots(int... excluded) {
            var operations = OperationHandler.defaultOperations();
            for (var operation : operations) {
                if (operation instanceof OperationHandler.ISlotOperation<?> op) op.exclude(excluded);
            }
            return operations;
        }

        /**
         * Calls {@link OperationHandler#removalOptions(IRecipeLayout, List)} with the output of {@link #getJEIOperations()}.\
         * <p>
         * In almost all cases, this should not be overridden and
         * {@link #getJEIOperations()} should be manipulated instead.
         *
         * @see OperationHandler#removalOptions(IRecipeLayout, List)
         */
        @Override
        default @NotNull List<String> getRemoval(IRecipeLayout layout) {
            return OperationHandler.removalOptions(layout, getJEIOperations());
        }

        /**
         * Gets the desired operations that will be parsed through with the target {@link IRecipeLayout}.
         * <p>
         * Should be overridden to modify the excluded slots, override incorrectly labeled input or output slots,
         * or to override the names of the methods.
         *
         * @return a list of operations to process
         * @see OperationHandler#defaultOperations()
         * @see OperationHandler#removalOptions(IRecipeLayout, List)
         */
        default @NotNull List<OperationHandler.IOperation> getJEIOperations() {
            return OperationHandler.defaultOperations();
        }

    }

}
