package com.cleanroommc.groovyscript.compat.mekanism;

import com.cleanroommc.groovyscript.api.IIngredient;

public class IngredientWrapper {

    private final IIngredient left;
    private final IIngredient middle;
    private final IIngredient right;
    private final String infuseType;

    public IngredientWrapper(IIngredient ingredient) {
        this(ingredient, IIngredient.ANY);
    }

    public IngredientWrapper(IIngredient left, IIngredient right) {
        this(left, IIngredient.ANY, right);
    }

    public IngredientWrapper(IIngredient left, IIngredient middle, IIngredient right) {
        this.left = MekanismIngredientHelper.optionalIngredient(left);
        this.middle = MekanismIngredientHelper.optionalIngredient(middle);
        this.right = MekanismIngredientHelper.optionalIngredient(right);
        this.infuseType = "";
    }

    public IngredientWrapper(IIngredient ingredient, String infuseType) {
        this.left = MekanismIngredientHelper.optionalIngredient(ingredient);
        this.middle = IIngredient.ANY;
        this.right = IIngredient.ANY;
        this.infuseType = infuseType == null ? "" : infuseType;
    }

    public String getInfuseType() {
        return this.infuseType;
    }

    public IIngredient getIngredient() {
        return this.left;
    }

    public IIngredient getLeft() {
        return this.left;
    }

    public IIngredient getMiddle() {
        return this.middle;
    }

    public IIngredient getRight() {
        return this.right;
    }

    public int getAmount() {
        //TODO: Make this method actually do something if we ever need to use IntegerInput as an input type
        return 0;
    }

    @Override
    public String toString() {
        String output = "";
        if (!left.equals(IIngredient.ANY)) {
            output += getDescriptor(left);
        }
        if (!middle.equals(IIngredient.ANY)) {
            output += ", " + getDescriptor(middle);
        }
        if (!right.equals(IIngredient.ANY)) {
            output += ", " + getDescriptor(right);
        }
        if (!infuseType.isEmpty()) {
            output += ", " + infuseType;
        }
        return output;
    }

    public boolean isEmpty() {
        return left.equals(IIngredient.ANY) && middle.equals(IIngredient.ANY) && right.equals(IIngredient.ANY) && infuseType.isEmpty();
    }

    private String getDescriptor(IIngredient ingredient) {
        return ingredient.toString();
    }
}
