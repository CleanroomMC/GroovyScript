package com.cleanroommc.groovyscript.core.mixin.industrialforegoing;

import com.buuz135.industrial.tile.block.SludgeRefinerBlock;
import com.buuz135.industrial.utils.ItemStackWeightedItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.ArrayList;

@Mixin(value = SludgeRefinerBlock.class, remap = false)
public interface SludgeRefinerBlockAccessor {

    @Accessor("OUTPUTS")
    static ArrayList<ItemStackWeightedItem> getOutputs() {
        throw new UnsupportedOperationException();
    }

    @Accessor("OUTPUTS")
    static void setOutputs(ArrayList<ItemStackWeightedItem> OUTPUTS) {
        throw new UnsupportedOperationException();
    }
}
