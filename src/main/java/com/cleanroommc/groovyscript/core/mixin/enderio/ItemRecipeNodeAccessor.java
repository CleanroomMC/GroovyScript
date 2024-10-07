package com.cleanroommc.groovyscript.core.mixin.enderio;

import com.enderio.core.common.util.NNList;
import crazypants.enderio.base.recipe.lookup.IRecipeNode;
import crazypants.enderio.base.recipe.lookup.ItemRecipeNode;
import crazypants.enderio.util.NNPair;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ItemRecipeNode.class, remap = false)
public interface ItemRecipeNodeAccessor<REC, CHL extends IRecipeNode<?, ?, ?>> {

    @Accessor
    Int2ObjectOpenHashMap<NNPair<NNList<REC>, CHL>> getMap();
}
