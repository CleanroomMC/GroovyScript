package com.cleanroommc.groovyscript.compat.mods.jei.removal;

import com.cleanroommc.groovyscript.compat.mods.jei.JeiPlugin;
import com.cleanroommc.groovyscript.core.mixin.jei.RecipeLayoutAccessor;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import com.google.common.collect.ImmutableList;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.startup.StackHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class OperationHandler {

    private static final String EXACT_METHOD_NAME = "removeByExactInput";
    private static final boolean IS_EXACT_METHOD_ENABLED = false;

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
        if (IS_EXACT_METHOD_ENABLED && exactInput.size() > 1) builder.add(JeiRemovalHelper.format(EXACT_METHOD_NAME, exactInput));
        builder.addAll(removing.stream().distinct().sorted().collect(Collectors.toList()));
        return builder.build();
    }

    /**
     * @return the default operations to be used, which will get all item and fluid stacks
     * @see ItemOperation#defaultItemOperation()
     * @see FluidOperation#defaultFluidOperation()
     */
    public static List<IOperation> defaultOperations() {
        return ImmutableList.of(ItemOperation.defaultItemOperation(), FluidOperation.defaultFluidOperation());
    }

    /**
     * Base operations requirement, merely requires that the implementing classes contain a {@link #parse(IRecipeLayout, List, List)} method.
     */
    public interface IOperation {

        /**
         * @param layout     the recipe layout to be parsed
         * @param removing   a list of strings which should be modified, and any removal methods should be added to
         * @param exactInput if the implementing class gathers input ingredients, an addition list to manage them for a separate method
         */
        void parse(IRecipeLayout layout, List<String> removing, List<String> exactInput);

    }

    /**
     * Interface for a {@link BaseSlotOperation}, requiring the ability to
     * block specific slots from appearing,
     * override the input or output state of a slot (due to the slot being incorrectly listed)
     * set the method name for the input or output.
     *
     * @param <R> the operation class
     */
    public interface ISlotOperation<R> extends IOperation {

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
    public static class WrapperOperation<T> implements IOperation {

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
            var wrapper = ((RecipeLayoutAccessor) layout).getRecipeWrapper();
            if (wrapperClass.isInstance(wrapper)) {
                removing.addAll(function(wrapperClass.cast(wrapper)));
            }
        }

    }

    /**
     * Abstract class containing the basic logic for input and output slots, what slots are excluded,
     * and what method name should be used for a given slot.
     */
    public abstract static class BaseSlotOperation<T> implements ISlotOperation<BaseSlotOperation<T>> {

        private static final String DEFAULT_INPUT_METHOD_NAME = "removeByInput";
        private static final String DEFAULT_OUTPUT_METHOD_NAME = "removeByOutput";

        protected final List<Integer> excluded = new ArrayList<>();
        protected final List<Integer> reallyInputs = new ArrayList<>();
        protected final List<Integer> reallyOutputs = new ArrayList<>();
        protected String inputMethodName = DEFAULT_INPUT_METHOD_NAME;
        protected String outputMethodName = DEFAULT_OUTPUT_METHOD_NAME;

        @Override
        public BaseSlotOperation<T> exclude(int... ints) {
            for (var i : ints) this.excluded.add(i);
            return this;
        }

        @Override
        public BaseSlotOperation<T> input(int... ints) {
            for (var i : ints) this.reallyInputs.add(i);
            return this;
        }

        @Override
        public BaseSlotOperation<T> output(int... ints) {
            for (var i : ints) this.reallyOutputs.add(i);
            return this;
        }

        @Override
        public BaseSlotOperation<T> input(String name) {
            this.inputMethodName = name;
            return this;
        }

        @Override
        public BaseSlotOperation<T> output(String name) {
            this.outputMethodName = name;
            return this;
        }

        /**
         * Checks if the slot should be skipped.
         * By default, checks the {@link #excluded} list and if the slot contains no ingredients.
         *
         * @param slot the entry slot being parsed
         * @return if the slot should be ignored
         */
        protected boolean isIgnored(Map.Entry<Integer, ? extends IGuiIngredient<T>> slot) {
            return this.excluded.contains(slot.getKey()) || slot.getValue().getAllIngredients().isEmpty();
        }

        /**
         * Checks if the slot should be considered an input slot.
         * By default, checks the {@link #reallyInputs} list for overrides, then if the slot is marked as an input.
         *
         * @param slot the entry slot being parsed
         * @return if the slot is an input
         */
        protected boolean isInput(Map.Entry<Integer, ? extends IGuiIngredient<T>> slot) {
            if (this.reallyInputs.contains(slot.getKey())) return true;
            return slot.getValue().isInput();
        }


        /**
         * Checks if the slot should be considered an output slot.
         * By default, checks the {@link #reallyOutputs} list for overrides, then if the slot is an input via {@link #isInput(Map.Entry)}.
         *
         * @param slot the entry slot being parsed
         * @return if the slot is an output
         */
        protected boolean isOutput(Map.Entry<Integer, ? extends IGuiIngredient<T>> slot) {
            if (this.reallyOutputs.contains(slot.getKey())) return true;
            return !isInput(slot);
        }

        /**
         * @param slot the entry slot being parsed
         * @return the method name used to represent the slot
         */
        protected String getMethod(Map.Entry<Integer, ? extends IGuiIngredient<T>> slot) {
            return isOutput(slot) ? this.outputMethodName : this.inputMethodName;
        }

    }

    /**
     * Abstract class containing the core logic of the operation,
     * parsing the provided gui ingredients through a provided function.
     */
    public abstract static class SlotOperation<T> extends BaseSlotOperation<T> {

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

                // check the displayed ingredient first, otherwise try to use the first of all ingredients.
                var stack = slot.getValue().getDisplayedIngredient();
                if (stack == null) {
                    stack = slot.getValue().getAllIngredients().get(0);
                    if (stack == null) continue;
                }

                var identity = function(stack, slot.getValue().getAllIngredients());
                if (this.hasExactInput && !isOutput(slot)) exactInput.add(identity);
                removing.add(JeiRemovalHelper.format(getMethod(slot), identity));
            }
        }
    }

    /**
     * Class for ingredient groups obtained via {@link IRecipeLayout#getIngredientsGroup(IIngredientType)}.
     * <p>
     * Used to read the gui ingredients added via the modern format.
     *
     * @see ClassSlotOperation
     */
    public static class IngredientSlotOperation<T> extends SlotOperation<T> {

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

    /**
     * Class for ingredient groups obtained via the deprecated {@link IRecipeLayout#getIngredientsGroup(Class)} method.\
     * <p>
     * Used to read the gui ingredients added via the deprecated format.
     *
     * @see IngredientSlotOperation
     */
    @SuppressWarnings("deprecation")
    public static class ClassSlotOperation<T> extends SlotOperation<T> {

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

    public static class ItemOperation extends IngredientSlotOperation<ItemStack> {

        protected ItemOperation() {
            super(VanillaTypes.ITEM, true,
                  (stack, all) -> {
                      var oreDict = convertToOreDict(all);
                      return oreDict.isEmpty() ? GroovyScriptCodeConverter.getSingleItemStack(stack, true, false) : oreDict;
                  });
        }

        /**
         * @param ingredients a list of itemstacks to check
         * @return if the list of itemstacks are an oredict, represent them as an oredict
         */
        protected static String convertToOreDict(List<ItemStack> ingredients) {
            if (JeiPlugin.jeiHelpers.getStackHelper() instanceof StackHelper stackHelper) {
                var dict = stackHelper.getOreDictEquivalent(ingredients);
                return dict == null ? "" : GroovyScriptCodeConverter.asGroovyCode(dict, true);
            }
            return "";
        }

        /**
         * @return the base {@link ItemOperation}
         */
        public static ISlotOperation<?> defaultItemOperation() {
            return new ItemOperation();
        }

        /**
         * @return an {@link ItemOperation} that ignores all input slots.
         */
        public static IOperation outputItemOperation() {
            return new ItemOperation() {
                @Override
                public boolean isIgnored(@NotNull Map.Entry<Integer, ? extends IGuiIngredient<ItemStack>> slot) {
                    return slot.getValue().isInput();
                }
            };
        }

    }

    public static class FluidOperation extends IngredientSlotOperation<FluidStack> {

        protected FluidOperation() {
            super(VanillaTypes.FLUID, true, (stack, all) -> GroovyScriptCodeConverter.getSingleFluidStack(stack, true, false));
        }

        /**
         * @return the base {@link FluidOperation}
         */
        public static ISlotOperation<?> defaultFluidOperation() {
            return new FluidOperation();
        }

    }

}
