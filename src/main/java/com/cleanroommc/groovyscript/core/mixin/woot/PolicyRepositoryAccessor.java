package com.cleanroommc.groovyscript.core.mixin.woot;

import ipsis.woot.policy.PolicyRepository;
import ipsis.woot.util.WootMobName;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = PolicyRepository.class, remap = false)
public interface PolicyRepositoryAccessor {

    @Accessor
    List<String> getInternalModBlacklist();

    @Accessor
    List<WootMobName> getInternalEntityBlacklist();

    @Accessor
    List<String> getInternalItemModBlacklist();

    @Accessor
    List<ItemStack> getInternalItemBlacklist();

    @Accessor
    List<String> getExternalModBlacklist();

    @Accessor
    List<WootMobName> getExternalEntityBlacklist();

    @Accessor
    List<String> getExternalItemModBlacklist();

    @Accessor
    List<ItemStack> getExternalItemBlacklist();

    @Accessor
    List<WootMobName> getExternalEntityWhitelist();

    @Accessor
    List<WootMobName> getExternalGenerateOnlyList();

}
