package com.cleanroommc.groovyscript.core.mixin.tconstruct;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import slimeknights.tconstruct.library.DryingRecipe;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.smeltery.AlloyRecipe;
import slimeknights.tconstruct.library.smeltery.ICastingRecipe;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import slimeknights.tconstruct.library.traits.ITrait;

import java.util.List;
import java.util.Map;

@Mixin(value = TinkerRegistry.class, remap = false)
public interface TinkerRegistryAccessor {

    @Accessor
    static List<DryingRecipe> getDryingRegistry() {
        throw new AssertionError();
    }

    @Accessor
    static List<MeltingRecipe> getMeltingRegistry() {
        throw new AssertionError();
    }

    @Accessor
    static List<AlloyRecipe> getAlloyRegistry() {
        throw new AssertionError();
    }

    @Accessor
    static List<ICastingRecipe> getTableCastRegistry() {
        throw new AssertionError();
    }

    @Accessor
    static List<ICastingRecipe> getBasinCastRegistry() {
        throw new AssertionError();
    }

    @Accessor
    static Map<FluidStack, Integer> getSmelteryFuels() {
        throw new AssertionError();
    }

    @Accessor
    static Map<ResourceLocation, FluidStack> getEntityMeltingRegistry() {
        throw new AssertionError();
    }

    @Accessor
    static Map<String, Material> getMaterials() {
        throw new AssertionError();
    }

    @Accessor
    static Map<String, ITrait> getTraits() {
        throw new AssertionError();
    }
}
