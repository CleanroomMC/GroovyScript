package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.recipe;

import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

public abstract class MeltingRecipeRegistry extends VirtualizedRegistry<MeltingRecipe> {

    public abstract void add(MeltingRecipe recipe);
}
