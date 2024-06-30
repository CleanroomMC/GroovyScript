package com.cleanroommc.groovyscript.core.mixin.pneumaticcraft;

import me.desht.pneumaticcraft.common.recipes.PlasticMixerRegistry;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(value = PlasticMixerRegistry.class, remap = false)
public interface PlasticMixerRegistryAccessor {

    @Accessor
    Set<String> getValidFluids();

    @Accessor
    Map<Item, Boolean> getValidItems();

    @Accessor
    List<PlasticMixerRegistry.PlasticMixerRecipe> getRecipes();

}
