package com.cleanroommc.groovyscript.helper.recipe;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.IngredientList;
import com.cleanroommc.groovyscript.helper.ItemStackList;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.Collections;

public abstract class AbstractRecipeBuilder<T> implements IRecipeBuilder<T> {

    protected final IngredientList<IIngredient> input = new IngredientList<>();
    protected final ItemStackList output = new ItemStackList();

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
