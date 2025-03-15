package com.cleanroommc.groovyscript.compat.mods.astralsorcery.crystal;

import com.cleanroommc.groovyscript.api.GroovyLog;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.base.ItemTunedCrystalBase;
import net.minecraft.item.ItemStack;

public class CrystalHelper {

    public static ItemStack setSize(ItemStack crystal, int size) {
        if (CrystalProperties.getCrystalProperties(crystal) == null)
            CrystalProperties.applyCrystalProperties(crystal, new CrystalProperties(size, 0, 0, 0, -1));
        else {
            CrystalProperties oldProps = CrystalProperties.getCrystalProperties(crystal);
            CrystalProperties newProps = new CrystalProperties(size, oldProps.getPurity(), oldProps.getCollectiveCapability(), oldProps.getFracturation(), oldProps.getSizeOverride());
            CrystalProperties.applyCrystalProperties(crystal, newProps);
        }
        return crystal;
    }

    public static ItemStack setPurity(ItemStack crystal, int purity) {
        if (CrystalProperties.getCrystalProperties(crystal) == null)
            CrystalProperties.applyCrystalProperties(crystal, new CrystalProperties(0, purity, 0, 0, -1));
        else {
            CrystalProperties oldProps = CrystalProperties.getCrystalProperties(crystal);
            CrystalProperties newProps = new CrystalProperties(oldProps.getSize(), purity, oldProps.getCollectiveCapability(), oldProps.getFracturation(), oldProps.getSizeOverride());
            CrystalProperties.applyCrystalProperties(crystal, newProps);
        }
        return crystal;
    }

    public static ItemStack setCollectiveCapability(ItemStack crystal, int collectiveCapability) {
        if (CrystalProperties.getCrystalProperties(crystal) == null)
            CrystalProperties.applyCrystalProperties(crystal, new CrystalProperties(0, 0, collectiveCapability, 0, -1));
        else {
            CrystalProperties oldProps = CrystalProperties.getCrystalProperties(crystal);
            CrystalProperties newProps = new CrystalProperties(oldProps.getSize(), oldProps.getPurity(), collectiveCapability, oldProps.getFracturation(), oldProps.getSizeOverride());
            CrystalProperties.applyCrystalProperties(crystal, newProps);
        }
        return crystal;
    }

    public static ItemStack setFracturation(ItemStack crystal, int fracturation) {
        if (CrystalProperties.getCrystalProperties(crystal) == null)
            CrystalProperties.applyCrystalProperties(crystal, new CrystalProperties(0, 0, 0, fracturation, -1));
        else {
            CrystalProperties oldProps = CrystalProperties.getCrystalProperties(crystal);
            CrystalProperties newProps = new CrystalProperties(oldProps.getSize(), oldProps.getPurity(), oldProps.getCollectiveCapability(), fracturation, oldProps.getSizeOverride());
            CrystalProperties.applyCrystalProperties(crystal, newProps);
        }
        return crystal;
    }

    public static ItemStack setSizeOverride(ItemStack crystal, int size) {
        if (CrystalProperties.getCrystalProperties(crystal) == null)
            CrystalProperties.applyCrystalProperties(crystal, new CrystalProperties(0, 0, 0, 0, size));
        else {
            CrystalProperties oldProps = CrystalProperties.getCrystalProperties(crystal);
            CrystalProperties newProps = new CrystalProperties(oldProps.getSize(), oldProps.getPurity(), oldProps.getCollectiveCapability(), oldProps.getFracturation(), size);
            CrystalProperties.applyCrystalProperties(crystal, newProps);
        }
        return crystal;
    }

    public static ItemStack tuneTo(ItemStack crystal, IConstellation constellation) {
        if (constellation instanceof IWeakConstellation iWeakConstellation)
            ItemTunedCrystalBase.applyMainConstellation(crystal, iWeakConstellation);
        else
            GroovyLog.msg("Main constellation must be Major or Weak (Bright or Dim)").error().post();
        return crystal;
    }

    public static ItemStack setTrait(ItemStack crystal, IConstellation constellation) {
        if (constellation instanceof IMinorConstellation iMinorConstellation)
            ItemTunedCrystalBase.applyTrait(crystal, iMinorConstellation);
        else
            GroovyLog.msg("Trait constellation must be Minor (Faint)").error().post();
        return crystal;
    }
}
