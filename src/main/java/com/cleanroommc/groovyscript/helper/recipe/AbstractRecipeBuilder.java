package com.cleanroommc.groovyscript.helper.recipe;

import com.cleanroommc.groovyscript.api.IIngredient;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class AbstractRecipeBuilder<T> implements IRecipeBuilder<T> {

    protected final List<IIngredient> input = new ArrayList<>();
    protected final List<ItemStack> output = new ArrayList<>();

    public AbstractRecipeBuilder<T> input(IIngredient ingredient) {
        this.input.add(ingredient);
        return this;
    }

    public AbstractRecipeBuilder<T> input(IIngredient... ingredients) {
        Collections.addAll(input, ingredients);
        return this;
    }

    public AbstractRecipeBuilder<T> input(Collection<IIngredient> ingredients) {
        this.input.addAll(ingredients);
        return this;
    }

    public AbstractRecipeBuilder<T> output(ItemStack output) {
        this.output.add(output);
        return this;
    }

    public AbstractRecipeBuilder<T> output(ItemStack... outputs) {
        Collections.addAll(output, outputs);
        return this;
    }

    public AbstractRecipeBuilder<T> output(Collection<ItemStack> outputs) {
        this.output.addAll(outputs);
        return this;
    }
}
