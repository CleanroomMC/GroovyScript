package com.cleanroommc.groovyscript.compat.mods.rustic;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import rustic.common.crafting.EvaporatingBasinRecipe;

public class ExtendedEvaporatingBasinRecipe extends EvaporatingBasinRecipe {

    private final int time;

    public ExtendedEvaporatingBasinRecipe(ItemStack out, FluidStack in, int time) {
        super(out, in);
        this.time = time;
    }

    public int getTime() {
        return time;
    }

}
