package com.cleanroommc.groovyscript.core.mixin.tcomplement;

import knightminer.tcomplement.library.IBlacklist;
import knightminer.tcomplement.library.TCompRegistry;
import knightminer.tcomplement.library.steelworks.HighOvenFuel;
import knightminer.tcomplement.library.steelworks.IHeatRecipe;
import knightminer.tcomplement.library.steelworks.IMixRecipe;
import knightminer.tcomplement.library.steelworks.MixAdditive;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import slimeknights.mantle.util.RecipeMatchRegistry;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

import java.util.List;
import java.util.Map;

@Mixin(value = TCompRegistry.class, remap = false)
public interface TCompRegistryAccessor {

    @Accessor
    static List<MeltingRecipe> getMeltingOverrides() {
        throw new AssertionError();
    }

    @Accessor
    static List<IBlacklist> getMeltingBlacklist() {
        throw new AssertionError();
    }

    @Accessor
    static List<IMixRecipe> getMixRegistry() {
        throw new AssertionError();
    }

    @Accessor
    static Map<MixAdditive, RecipeMatchRegistry> getMixAdditives() {
        throw new AssertionError();
    }

    @Accessor
    static List<IHeatRecipe> getHeatRegistry() {
        throw new AssertionError();
    }

    @Accessor
    static List<HighOvenFuel> getHighOvenFuels() {
        throw new AssertionError();
    }

    @Accessor
    static List<MeltingRecipe> getHighOvenOverrides() {
        throw new AssertionError();
    }
}
