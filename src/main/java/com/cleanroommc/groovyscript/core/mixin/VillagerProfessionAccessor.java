package com.cleanroommc.groovyscript.core.mixin;

import net.minecraftforge.fml.common.registry.VillagerRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = VillagerRegistry.VillagerProfession.class, remap = false)
public interface VillagerProfessionAccessor {

    @Accessor
    List<VillagerRegistry.VillagerCareer> getCareers();

}
