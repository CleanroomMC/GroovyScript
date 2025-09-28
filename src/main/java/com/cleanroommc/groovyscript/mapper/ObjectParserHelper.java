package com.cleanroommc.groovyscript.mapper;

import com.cleanroommc.groovyscript.core.mixin.VillagerProfessionAccessor;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.util.Map;

public class ObjectParserHelper {

    public static final Map<String, Material> MATERIALS;

    public static final Map<String, VillagerRegistry.VillagerCareer> VILLAGER_CAREERS;

    static {
        MATERIALS = new ImmutableMap.Builder<String, Material>()
                .put("AIR", Material.AIR)
                .put("GRASS", Material.GRASS)
                .put("GROUND", Material.GROUND)
                .put("WOOD", Material.WOOD)
                .put("ROCK", Material.ROCK)
                .put("IRON", Material.IRON)
                .put("ANVIL", Material.ANVIL)
                .put("WATER", Material.WATER)
                .put("LAVA", Material.LAVA)
                .put("LEAVES", Material.LEAVES)
                .put("PLANTS", Material.PLANTS)
                .put("VINE", Material.VINE)
                .put("SPONGE", Material.SPONGE)
                .put("CLOTH", Material.CLOTH)
                .put("FIRE", Material.FIRE)
                .put("SAND", Material.SAND)
                .put("CIRCUITS", Material.CIRCUITS)
                .put("CARPET", Material.CARPET)
                .put("GLASS", Material.GLASS)
                .put("REDSTONE_LIGHT", Material.REDSTONE_LIGHT)
                .put("TNT", Material.TNT)
                .put("CORAL", Material.CORAL)
                .put("ICE", Material.ICE)
                .put("PACKED_ICE", Material.PACKED_ICE)
                .put("SNOW", Material.SNOW)
                .put("CRAFTED_SNOW", Material.CRAFTED_SNOW)
                .put("CACTUS", Material.CACTUS)
                .put("CLAY", Material.CLAY)
                .put("GOURD", Material.GOURD)
                .put("DRAGON_EGG", Material.DRAGON_EGG)
                .put("PORTAL", Material.PORTAL)
                .put("CAKE", Material.CAKE)
                .put("WEB", Material.WEB)
                .put("PISTON", Material.PISTON)
                .put("BARRIER", Material.BARRIER)
                .put("STRUCTURE_VOID", Material.STRUCTURE_VOID)
                .build();

        var careers = new ImmutableMap.Builder<String, VillagerRegistry.VillagerCareer>();
        for (var profession : ForgeRegistries.VILLAGER_PROFESSIONS) {
            if (profession == null) continue;
            for (var career : ((VillagerProfessionAccessor) profession).getCareers()) {
                if (career == null) continue;
                careers.put(career.getName(), career);
            }
        }
        VILLAGER_CAREERS = careers.build();
    }
}
