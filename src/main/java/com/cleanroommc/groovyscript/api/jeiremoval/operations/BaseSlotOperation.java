package com.cleanroommc.groovyscript.api.jeiremoval.operations;

import mezz.jei.api.gui.IGuiIngredient;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Abstract class containing the basic logic for input and output slots, what slots are excluded,
 * and what method name should be used for a given slot.
 */
@ParametersAreNonnullByDefault
public abstract class BaseSlotOperation<T> implements ISlotOperation<BaseSlotOperation<T>> {

    private static final String DEFAULT_INPUT_METHOD_NAME = "removeByInput";
    private static final String DEFAULT_OUTPUT_METHOD_NAME = "removeByOutput";

    protected final List<Integer> included = new ArrayList<>();
    protected final List<Integer> excluded = new ArrayList<>();
    protected final List<Integer> reallyInputs = new ArrayList<>();
    protected final List<Integer> reallyOutputs = new ArrayList<>();
    protected String inputMethodName = DEFAULT_INPUT_METHOD_NAME;
    protected String outputMethodName = DEFAULT_OUTPUT_METHOD_NAME;

    @Override
    public BaseSlotOperation<T> include(int... ints) {
        for (var i : ints) this.included.add(i);
        return this;
    }

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
     * If the slot has no ingredients it is automatically skipped.
     * If {@link #included} is empty, checks the {@link #excluded} list and if the slot contains no ingredients.
     * Otherwise, if the slot is a member of the {@link #included} list the provided slot will be included.
     *
     * @param slot the entry slot being parsed
     * @return if the slot should be ignored
     */
    protected boolean isIgnored(Map.Entry<Integer, ? extends IGuiIngredient<T>> slot) {
        if (slot.getValue().getAllIngredients().isEmpty()) return true;
        return this.included.isEmpty() ? this.excluded.contains(slot.getKey()) : !this.included.contains(slot.getKey());
    }

    /**
     * Checks if the slot should be considered an input slot.
     * By default, checks the {@link #reallyInputs} list for overrides, then if the slot is marked as an input.
     *
     * @param slot the entry slot being parsed
     * @return if the slot is an input
     */
    protected boolean isInput(Map.Entry<Integer, ? extends IGuiIngredient<T>> slot) {
        return this.reallyInputs.contains(slot.getKey()) || slot.getValue().isInput();
    }


    /**
     * Checks if the slot should be considered an output slot.
     * By default, checks the {@link #reallyOutputs} list for overrides, then if the slot is an input via {@link #isInput(Map.Entry)}.
     *
     * @param slot the entry slot being parsed
     * @return if the slot is an output
     */
    protected boolean isOutput(Map.Entry<Integer, ? extends IGuiIngredient<T>> slot) {
        return this.reallyOutputs.contains(slot.getKey()) || !isInput(slot);
    }

    /**
     * @param slot the entry slot being parsed
     * @return the method name used to represent the slot
     */
    protected String getMethod(Map.Entry<Integer, ? extends IGuiIngredient<T>> slot) {
        return isOutput(slot) ? this.outputMethodName : this.inputMethodName;
    }

}
