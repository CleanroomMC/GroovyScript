package com.cleanroommc.groovyscript.compat.mods.jei.removal;

import com.cleanroommc.groovyscript.api.jeiremoval.operations.FluidOperation;
import com.cleanroommc.groovyscript.api.jeiremoval.operations.IOperation;
import com.cleanroommc.groovyscript.api.jeiremoval.operations.ItemOperation;
import com.cleanroommc.groovyscript.compat.mods.jei.JeiPlugin;
import com.cleanroommc.groovyscript.core.mixin.jei.RecipeLayoutAccessor;
import com.google.common.collect.ImmutableList;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.startup.StackHelper;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class OperationHandler {

    private static final String EXACT_METHOD_NAME = "removeByExactInput";
    private static final boolean IS_EXACT_METHOD_ENABLED = false;

    private static StackHelper stackHelper;

    /**
     * Varargs variant of {@link #removalOptions(IRecipeLayout, List)}.
     *
     * @see #removalOptions(IRecipeLayout, List)
     */
    public static List<String> removalOptions(IRecipeLayout layout, IOperation... operations) {
        return removalOptions(layout, Arrays.asList(operations));
    }

    /**
     * Iterates through the provided list of {@link IOperation}s and calls the {@link IOperation#parse(IRecipeLayout, List, List)}
     * method with local lists before sorting them and returning a new immutable list constructed from the temporary lists.
     *
     * @param layout     the recipe layout to be parsed
     * @param operations a list of operations to perform to generate the removal methods
     * @return an immutable list of strings, with each one being a removal method for the given recipe contained by the recipe layout
     */
    public static List<String> removalOptions(IRecipeLayout layout, List<IOperation> operations) {
        var removing = new ArrayList<String>();
        var exactInput = new ArrayList<String>();

        for (var operation : operations) operation.parse(layout, removing, exactInput);

        var builder = ImmutableList.<String>builder();
        if (IS_EXACT_METHOD_ENABLED && exactInput.size() > 1) builder.add(format(EXACT_METHOD_NAME, exactInput));
        builder.addAll(removing.stream().distinct().sorted().collect(Collectors.toList()));
        return builder.build();
    }

    /**
     * @return the default operations to be used, which will get all item and fluid stacks
     * @see ItemOperation#defaultOperation()
     * @see FluidOperation#defaultOperation()
     */
    public static List<IOperation> defaultOperations() {
        return ImmutableList.of(ItemOperation.defaultOperation(), FluidOperation.defaultOperation());
    }

    /**
     * Get the recipe wrapper from the recipe layout. Requires the {@link RecipeLayoutAccessor} mixin.
     *
     * @param layout the recipe layout
     * @return the recipe wrapper the recipe layout contains
     */
    public static IRecipeWrapper getRecipeWrapper(IRecipeLayout layout) {
        return ((RecipeLayoutAccessor) layout).getRecipeWrapper();
    }

    public static StackHelper getStackHelper() {
        if (stackHelper == null) stackHelper = (StackHelper) JeiPlugin.jeiHelpers.getStackHelper();
        return stackHelper;
    }

    /**
     * Check the displayed ingredient first, otherwise try to use the first of all ingredients.
     * If getAllIngredients is empty, return null.
     * Use of this should contain null checks.
     *
     * @param slot the gui ingredient slot the ingredient should be gotten from
     * @param <T>  the type of the gui ingredient
     * @return the displayed ingredient, if there are multiple then the first of the list, otherwise null.
     */
    @Nullable
    public static <T> T getIngredientFromSlot(IGuiIngredient<T> slot) {
        var stack = slot.getDisplayedIngredient();
        if (stack == null && !slot.getAllIngredients().isEmpty()) stack = slot.getAllIngredients().get(0);
        return stack;
    }

    /**
     * @param method name of the method to call
     * @param params one or more parameters of the method
     * @return a string representing a GrS method
     * @see #format(String, List)
     */
    public static String format(String method, String... params) {
        return String.format("%s(%s)", method, String.join(", ", params));
    }

    /**
     * @param method name of the method to call
     * @param params one or more parameters of the method
     * @return a string representing a GrS method
     */
    public static String format(String method, List<String> params) {
        return String.format("%s(%s)", method, String.join(", ", params));
    }
}
