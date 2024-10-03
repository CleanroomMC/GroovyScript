package com.cleanroommc.groovyscript.core.mixin.roots;


import epicsquid.roots.init.ModRecipes;
import epicsquid.roots.recipe.ChrysopoeiaRecipe;
import epicsquid.roots.recipe.MortarRecipe;
import epicsquid.roots.recipe.SummonCreatureRecipe;
import epicsquid.roots.recipe.TransmutationRecipe;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = ModRecipes.class, remap = false)
public interface ModRecipesAccessor {

    @Accessor
    static Map<ResourceLocation, ChrysopoeiaRecipe> getChrysopoeiaRecipes() {
        throw new AssertionError();
    }

    @Accessor
    static Map<ResourceLocation, TransmutationRecipe> getTransmutationRecipes() {
        throw new AssertionError();
    }

    @Accessor("summonCreatureRecipes")
    static Map<ResourceLocation, SummonCreatureRecipe> getSummonCreatureEntries() {
        throw new AssertionError();
    }

    @Accessor
    static Map<Class<? extends EntityLivingBase>, SummonCreatureRecipe> getSummonCreatureClasses() {
        throw new AssertionError();
    }

    @Accessor
    static Map<ResourceLocation, MortarRecipe> getMortarRecipes() {
        throw new AssertionError();
    }

}
