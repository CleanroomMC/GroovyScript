package com.cleanroommc.groovyscript.compat.mods.botania.recipe;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.core.mixin.botania.BotaniaAPIAccessor;
import net.minecraft.block.Block;

public class MagnetSubject {

    public final IIngredient item;
    public final Block block;
    public final int meta;

    public MagnetSubject(IIngredient item) {
        this.item = item;
        this.meta = 0;
        this.block = null;
    }

    public MagnetSubject(Block block, int meta) {
        this.meta = meta;
        this.block = block;
        this.item = null;
    }

    public boolean isBlock() {
        return block != null;
    }

    public String getMagnetKey() {
        return isBlock() ? BotaniaAPIAccessor.invokeGetMagnetKey(block, meta) : BotaniaAPIAccessor.invokeGetMagnetKey(item.getMatchingStacks()[0]);
    }
}
