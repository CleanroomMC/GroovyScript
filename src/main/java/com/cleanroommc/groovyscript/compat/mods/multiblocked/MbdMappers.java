package com.cleanroommc.groovyscript.compat.mods.multiblocked;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect.AspectStack;
import com.cleanroommc.multiblocked.api.recipe.EntityIngredient;
import com.cleanroommc.multiblocked.api.recipe.ItemsIngredient;
import com.cleanroommc.multiblocked.api.recipe.RecipeCondition;
import com.cleanroommc.multiblocked.common.capability.*;
import com.cleanroommc.multiblocked.common.recipe.conditions.*;
import com.cleanroommc.multiblocked.common.recipe.content.Starlight;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import mekanism.api.gas.GasStack;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.EntityEntry;

public class MbdMappers {
    /* Capabilities */
    public MbdCapabilityData<ItemsIngredient> item(IIngredient ingredient) {
        ItemsIngredient ii = new ItemsIngredient(ingredient.getMatchingStacks());
        return new MbdCapabilityData<>(ii, ItemMultiblockCapability.CAP);
    }

    public MbdCapabilityData<FluidStack> fluid(FluidStack ingredient) {
        return new MbdCapabilityData<>(ingredient, FluidMultiblockCapability.CAP);
    }

    public MbdCapabilityData<EntityIngredient> entity(EntityEntry ingredient) {
        EntityIngredient ei = new EntityIngredient();
        ei.type = ingredient;
        return new MbdCapabilityData<>(ei, EntityMultiblockCapability.CAP);
    }

    public MbdCapabilityData<ItemsIngredient> durability(IIngredient ingredient, int durability) {
        if (ingredient.getMatchingStacks().length == 0) {
            GroovyLog.Msg msg = GroovyLog.msg("Invalid item passed to durability() MBD capability");
            msg.warn().post();
            return null;
        }
        ItemsIngredient ii = new ItemsIngredient(durability, ingredient.getMatchingStacks()[0]);
        return new MbdCapabilityData<>(ii, ItemDurabilityMultiblockCapability.CAP);
    }

    public MbdCapabilityData<Integer> fe(int amount) {
        return new MbdCapabilityData<>(amount, FEMultiblockCapability.CAP);
    }

    // The following capabilities require a specific mod to be loaded

    @Optional.Method(modid = "thaumcraft")
    public MbdCapabilityData<com.cleanroommc.multiblocked.common.recipe.content.AspectStack> essentia(AspectStack stack) {
        return new MbdCapabilityData<>(
                new com.cleanroommc.multiblocked.common.recipe.content.AspectStack(stack.getAspect(), stack.getAmount()),
                AspectThaumcraftCapability.CAP);
    }

    @Optional.Method(modid = "naturesaura")
    public MbdCapabilityData<Integer> aura(int amount) {
        return new MbdCapabilityData<>(amount, AuraMultiblockCapability.CAP);
    }

    @Optional.Method(modid = "projecte")
    public MbdCapabilityData<Long> emc(long amount) {
        return new MbdCapabilityData<>(amount, EMCProjectECapability.CAP);
    }

    @Optional.Method(modid = "embers")
    public MbdCapabilityData<Double> ember(double amount) {
        return new MbdCapabilityData<>(amount, EmberEmbersCapability.CAP);
    }

    @Optional.Method(modid = "gregtech")
    public MbdCapabilityData<Long> gteu(long amount) {
        return new MbdCapabilityData<>(amount, EnergyGTCECapability.CAP);
    }

    @Optional.Method(modid = "extrautils2")
    public MbdCapabilityData<Float> gp(float amount) {
        return new MbdCapabilityData<>(amount, GPExtraUtilities2Capability.CAP);
    }

    @Optional.Method(modid = "mekanism")
    public MbdCapabilityData<GasStack> gas(GasStack gas) {
        return new MbdCapabilityData<>(gas, GasMekanismCapability.CAP);
    }

    @Optional.Method(modid = "mekanism")
    public MbdCapabilityData<Double> heat(double amount) {
        return new MbdCapabilityData<>(amount, HeatMekanismCapability.CAP);
    }

    @Optional.Method(modid = "prodigytech")
    public MbdCapabilityData<Integer> hotAir(int amount) {
        return new MbdCapabilityData<>(amount, HotAirProdigyCapability.CAP);
    }

    @Optional.Method(modid = "thaumicaugmentation")
    public MbdCapabilityData<Long> impetus(long amount) {
        return new MbdCapabilityData<>(amount, ImpetusThaumicAugmentationCapability.CAP);
    }

    @Optional.Method(modid = "lightningcraft")
    public MbdCapabilityData<Double> le(double amount) {
        return new MbdCapabilityData<>(amount, LEMultiblockCapability.CAP);
    }

    @Optional.Method(modid = "bloodmagic")
    public MbdCapabilityData<Integer> lp(int amount) {
        return new MbdCapabilityData<>(amount, LPBloodMagicCapability.CAP);
    }

    @Optional.Method(modid = "mekanism")
    public MbdCapabilityData<Double> laser(double amount) {
        return new MbdCapabilityData<>(amount, LaserMekanismCapability.CAP);
    }

    @Optional.Method(modid = "mysticalmechanics")
    public MbdCapabilityData<Double> mechanicalPower(double amount) {
        return new MbdCapabilityData<>(amount, MystMechPowerCapability.CAP);
    }

    // Particle Stack - TODO
    // Can't be done right now because QMD depends on NCO, which crashes with Groovyscript

    @Optional.Method(modid = "pneumaticcraft")
    public MbdCapabilityData<Float> pressure(float amount) {
        return new MbdCapabilityData<>(amount, PneumaticPressureCapability.CAP);
    }

    @Optional.Method(modid = "astralsorcery")
    public MbdCapabilityData<Starlight> starlight(IConstellation constellation, int amount) {
        return new MbdCapabilityData<>(new Starlight(amount, constellation), StarlightAstralCapability.CAP);
    }

    /* Conditions */
    public RecipeCondition biome(Biome b) {
        return new BiomeCondition(b.getRegistryName());
    }

    public RecipeCondition block(IBlockState b, int count) {
        return new BlockCondition(b, count);
    }

    public RecipeCondition dimension(DimensionType dim) {
        return new DimensionCondition(dim.getName());
    }

    public RecipeCondition yRange(int min, int max) {
        return new PositionYCondition(min, max);
    }

    public RecipeCondition rain() {
        return new RainingCondition(0.2f);
    }

    public RecipeCondition thunder() {
        return new ThunderCondition(0.9f);
    }

}
