package com.cleanroommc.groovyscript.compat.mods.armorplus;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderDescription;
import com.cleanroommc.groovyscript.core.mixin.armorplus.BaseCraftingManagerAccessor;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import com.sofodev.armorplus.api.crafting.IRecipe;
import com.sofodev.armorplus.api.crafting.base.BaseCraftingManager;

import java.util.Collection;

public abstract class AbstractBenchRegistry extends StandardListRegistry<IRecipe> {

    public abstract BaseCraftingManager getInstance();

    @Override
    public Collection<IRecipe> getRecipes() {
        return getInstance().getRecipeList();
    }

    private int size() {
        return ((BaseCraftingManagerAccessor) getInstance()).getSize();
    }

    @RecipeBuilderDescription
    public BenchRecipeBuilder.Shaped shapedBuilder() {
        return new BenchRecipeBuilder.Shaped(this, size());
    }

    @RecipeBuilderDescription
    public BenchRecipeBuilder.Shapeless shapelessBuilder() {
        return new BenchRecipeBuilder.Shapeless(this, size());
    }

    // so the <init> of BaseCraftingManager does this sorting... before any recipes are added to the list. and never again.
//    @Override
//    public void afterScriptLoad() {
//        getInstance().getRecipeList().sort((left, right) -> Integer.compare(right.getRecipeSize(), left.getRecipeSize()));
//    }

    @MethodDescription
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(x -> output.test(x.getRecipeOutput()));
    }
}
