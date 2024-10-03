package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.recipe;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.EntityEntry;

import java.util.Map;

public class EntityMeltingRecipe {

    public final FluidStack result;
    public final ResourceLocation name;

    public EntityMeltingRecipe(EntityEntry entity, FluidStack result) {
        this.name = entity.getRegistryName();
        this.result = result;
    }

    public EntityMeltingRecipe(ResourceLocation name, FluidStack result) {
        this.name = name;
        this.result = result;
    }

    public static EntityMeltingRecipe fromMapEntry(Map.Entry<ResourceLocation, FluidStack> entry) {
        return new EntityMeltingRecipe(entry.getKey(), entry.getValue());
    }

}
