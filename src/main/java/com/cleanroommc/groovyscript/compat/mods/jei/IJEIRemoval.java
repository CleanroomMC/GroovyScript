package com.cleanroommc.groovyscript.compat.mods.jei;

import com.google.common.collect.Lists;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.recipe.IIngredientType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface IJEIRemoval {

    @NotNull
    String getCategory();

    @NotNull
    default List<String> getCategories() {
        return Lists.newArrayList(getCategory());
    }

    @NotNull
    String getRemoval(Map<IIngredientType, IGuiIngredientGroup> map);

}
