package com.cleanroommc.groovyscript.compat.mods.jei;

import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.recipe.IIngredientType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface IJEIRemoval {

    @NotNull
    String getCategory();

    @NotNull
    String getRemoval(Map<IIngredientType, IGuiIngredientGroup> map);

}
