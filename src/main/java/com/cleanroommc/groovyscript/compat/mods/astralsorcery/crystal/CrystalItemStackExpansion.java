package com.cleanroommc.groovyscript.compat.mods.astralsorcery.crystal;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import net.minecraft.item.ItemStack;

public class CrystalItemStackExpansion {

    public static ItemStack setSize(ItemStack crystal, int size) {
        return CrystalHelper.setSize(crystal, size);
    }

    public static ItemStack setPurity(ItemStack crystal, int purity) {
        return CrystalHelper.setPurity(crystal, purity);
    }

    public static ItemStack setCutting(ItemStack crystal, int cutting) {
        return CrystalHelper.setCollectiveCapability(crystal, cutting);
    }

    public static ItemStack setFracturation(ItemStack crystal, int fracturation) {
        return CrystalHelper.setFracturation(crystal, fracturation);
    }

    public static ItemStack setSizeOverride(ItemStack crystal, int size) {
        return CrystalHelper.setSizeOverride(crystal, size);
    }

    public static ItemStack tuneTo(ItemStack crystal, IConstellation constellation) {
        return CrystalHelper.tuneTo(crystal, constellation);
    }

    public static ItemStack setTrait(ItemStack crystal, IConstellation constellation) {
        return CrystalHelper.setTrait(crystal, constellation);
    }

}
