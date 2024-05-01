package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.thermalexpansion.util.managers.dynamo.ReactantManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = ReactantManager.Reaction.class, remap = false)
public interface ReactionAccessor {

    @Invoker("<init>")
    static ReactantManager.Reaction createReaction(ItemStack reactant, Fluid fluid, int energy) {
        throw new UnsupportedOperationException();
    }

}
