package com.cleanroommc.groovyscript.compat.mods.astralsorcery.crystal;

import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
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

}
