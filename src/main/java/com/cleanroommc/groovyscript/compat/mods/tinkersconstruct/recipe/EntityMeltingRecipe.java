package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.recipe;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.Map;

public class EntityMeltingRecipe {
    public final FluidStack result;
    public final ResourceLocation name;

    public EntityMeltingRecipe(Class<? extends Entity> entity, FluidStack result) {
        this.name = EntityList.getKey(entity);
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
