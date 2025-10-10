package com.cleanroommc.groovyscript.mapper;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import net.minecraft.block.material.Material;

import java.lang.reflect.Modifier;
import java.util.Locale;

public class ObjectParserHelper {

    public static BiMap<String, Material> materials;

    public static void init() {
        materials = getMaterials();
    }

    private static BiMap<String, Material> getMaterials() {
        ImmutableBiMap.Builder<String, Material> materialBuilder = new ImmutableBiMap.Builder<>();
        for (var field : Material.class.getFields()) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType() == Material.class) {
                try {
                    var material = (Material) field.get(null);
                    materialBuilder.put(field.getName(), material);
                    materialBuilder.put(field.getName().toLowerCase(Locale.ROOT), material);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return materialBuilder.build();
    }
}
