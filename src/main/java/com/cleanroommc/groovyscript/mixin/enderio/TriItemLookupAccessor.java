package com.cleanroommc.groovyscript.mixin.enderio;

import crazypants.enderio.base.recipe.lookup.ItemRecipeLeafNode;
import crazypants.enderio.base.recipe.lookup.ItemRecipeNode;
import crazypants.enderio.base.recipe.lookup.TriItemLookup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = TriItemLookup.class, remap = false)
public interface TriItemLookupAccessor<REC> {

    @Accessor
    ItemRecipeNode<REC, ItemRecipeNode<REC, ItemRecipeLeafNode<REC>>> getRoot();

}
