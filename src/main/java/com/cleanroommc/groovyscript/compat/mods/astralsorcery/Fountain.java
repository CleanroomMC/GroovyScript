package com.cleanroommc.groovyscript.compat.mods.astralsorcery;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.astralsorcery.FluidRarityRegistryAccessor;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import hellfirepvp.astralsorcery.common.base.FluidRarityRegistry;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.apache.logging.log4j.Level;
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
        ((FluidRarityRegistryAccessor) FluidRarityRegistry.INSTANCE).getRarityList().removeIf(fluidRarityEntry -> {
            if (fluidRarityEntry.fluid.equals(entry)) {
                this.addBackup(fluidRarityEntry);
                return true;
            }
            return false;
        });
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
            GroovyLog.Msg out = GroovyLog.msg("Error adding fluid to Astral Sorcery Fountain").warn();

            if (this.fluid == null) {
                out.add("No fluid specified.").error();
            }
            if (this.rarity < 0) {
                out.add("Rarity cannot be negative, defaulting to 0.");
                this.rarity = 0;
            }
            if (this.minimumAmount < 0) {
                out.add("Minimum amount cannot be negative, defaulting to 0.");
                this.minimumAmount = 0;
            }
            if (this.variance < 0) {
                out.add("Variance cannot be negative, defaulting to 0.");
                this.variance = 0;
            }

            out.postIfNotEmpty();
            return out.getLevel() != Level.ERROR;
        }

        public void register() {
            if (validate()) {
                ModSupport.ASTRAL_SORCERY.get().fountain.add(this.fluid, this.rarity, this.minimumAmount, this.variance);
            }
        }

    }

}
