package com.cleanroommc.groovyscript.compat.mods.astralsorcery.crystal;

import com.cleanroommc.groovyscript.compat.mods.astralsorcery.crystal.CrystalHelper;
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

}
