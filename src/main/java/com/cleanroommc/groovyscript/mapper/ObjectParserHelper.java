package com.cleanroommc.groovyscript.mapper;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import net.minecraft.block.material.Material;

public class ObjectParserHelper {

    public static BiMap<String, Material> materials;

    public static void init() {
        materials = getMaterials();
    }

    private static BiMap<String, Material> getMaterials() {
        return new ImmutableBiMap.Builder<String, Material>()
                .put("air", Material.AIR)
                .put("grass", Material.GRASS)
                .put("ground", Material.GROUND)
                .put("wood", Material.WOOD)
                .put("rock", Material.ROCK)
                .put("iron", Material.IRON)
                .put("anvil", Material.ANVIL)
                .put("water", Material.WATER)
                .put("lava", Material.LAVA)
                .put("leaves", Material.LEAVES)
                .put("plants", Material.PLANTS)
                .put("vine", Material.VINE)
                .put("sponge", Material.SPONGE)
                .put("cloth", Material.CLOTH)
                .put("fire", Material.FIRE)
                .put("sand", Material.SAND)
                .put("circuits", Material.CIRCUITS)
                .put("carpet", Material.CARPET)
                .put("glass", Material.GLASS)
                .put("redstone_light", Material.REDSTONE_LIGHT)
                .put("tnt", Material.TNT)
                .put("coral", Material.CORAL)
                .put("ice", Material.ICE)
                .put("packed_ice", Material.PACKED_ICE)
                .put("snow", Material.SNOW)
                .put("crafted_snow", Material.CRAFTED_SNOW)
                .put("cactus", Material.CACTUS)
                .put("clay", Material.CLAY)
                .put("gourd", Material.GOURD)
                .put("dragon_egg", Material.DRAGON_EGG)
                .put("portal", Material.PORTAL)
                .put("cake", Material.CAKE)
                .put("web", Material.WEB)
                .put("piston", Material.PISTON)
                .put("barrier", Material.BARRIER)
                .put("structure_void", Material.STRUCTURE_VOID)
                .build();
    }
}
