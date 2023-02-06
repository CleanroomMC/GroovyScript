package com.cleanroommc.groovyscript.core.mixin.astralsorcery;

import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin( value = TraitRecipe.class, remap = false )
public interface TraitRecipeAccessor {

    @Accessor("additionallyRequiredStacks")
    public List<ItemHandle> getOuterTraitItems();

    @Accessor("matchTraitStacks")
    public Map<TraitRecipe.TraitRecipeSlot, ItemHandle> getTraitStackMap();

}
