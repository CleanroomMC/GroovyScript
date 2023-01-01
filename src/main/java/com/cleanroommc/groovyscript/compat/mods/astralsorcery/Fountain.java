package com.cleanroommc.groovyscript.compat.mods.astralsorcery;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.astralsorcery.FluidRarityRegistryAccessor;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import hellfirepvp.astralsorcery.common.base.FluidRarityRegistry;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;

public class Fountain extends VirtualizedRegistry<FluidRarityRegistry.FluidRarityEntry> {

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(this::remove);
        restoreFromBackup().forEach(this::add);
    }

    public void add(FluidRarityRegistry.FluidRarityEntry entry) {
        ((FluidRarityRegistryAccessor) FluidRarityRegistry.INSTANCE).getRarityList().add(entry);
    }

    public void add(FluidStack fluid, int rarity, int guaranteedAmt, int addRand) {
        this.add(fluid.getFluid(), rarity, guaranteedAmt, addRand);
    }

    public void add(Fluid fluid, int rarity, int guaranteedAmt, int addRand) {
        FluidRarityRegistry.FluidRarityEntry newFRE = Utils.createNewFRE(fluid, rarity, guaranteedAmt, addRand);
        this.addScripted(newFRE);
        this.add(newFRE);
    }

    public void remove(FluidRarityRegistry.FluidRarityEntry entry) {
        ((FluidRarityRegistryAccessor) FluidRarityRegistry.INSTANCE).getRarityList().removeIf(fluidRarityEntry -> fluidRarityEntry.fluid.equals(entry.fluid));
    }

    public void remove(FluidStack entry) {
        this.remove(entry.getFluid());
    }

    public void remove(Fluid entry) {
        ((FluidRarityRegistryAccessor) FluidRarityRegistry.INSTANCE).getRarityList().forEach(fluidRarityEntry -> {
            if (fluidRarityEntry.fluid.equals(entry)) this.addBackup(fluidRarityEntry);
        });
        ((FluidRarityRegistryAccessor) FluidRarityRegistry.INSTANCE).getRarityList().removeIf(fluidRarityEntry -> fluidRarityEntry.fluid.equals(entry));
    }

    public FountainChanceHelper chanceHelper() {
        return new FountainChanceHelper();
    }

    public static class FountainChanceHelper {

        private Fluid fluid = null;
        private int rarity = 0;
        private int minimumAmount = 0;
        private int variance = 0;

        public FountainChanceHelper fluid(Fluid fluid) {
            this.fluid = fluid;
            return this;
        }

        public FountainChanceHelper fluid(FluidStack fluid) {
            return this.fluid(fluid.getFluid());
        }

        public FountainChanceHelper rarity(int rarity) {
            this.rarity = rarity;
            return this;
        }

        public FountainChanceHelper minimumAmount(int amount) {
            this.minimumAmount = amount;
            return this;
        }

        public FountainChanceHelper variance(int variance) {
            this.variance = variance;
            return this;
        }

        private boolean validate() {
            if (this.rarity < 0) {
                this.rarity = 0;
                GroovyLog.msg("Error adding fluid to Astral Sorcery Founain. rarity cannot be negative.");
            }
            if (this.minimumAmount < 0) {
                this.minimumAmount = 0;
                GroovyLog.msg("Error adding fluid to Astral Sorcery Founain. minimumAmount cannot be negative.");
            }
            if (this.variance < 0) {
                this.variance = 0;
                GroovyLog.msg("Error adding fluid to Astral Sorcery Founain. variance cannot be negative.");
            }
            if (this.fluid == null) {
                GroovyLog.msg("Error adding fluid to Astral Sorcery Founain. No Fluid provided.");
                return false;
            }
            return true;
        }

        public void register() {
            if (validate()) {
                ModSupport.ASTRAL_SORCERY.get().fountain.add(this.fluid, this.rarity, this.minimumAmount, this.variance);
            }
        }

    }

}
