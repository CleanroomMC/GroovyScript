package com.cleanroommc.groovyscript.mapper;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.material.Material;

import java.lang.reflect.Modifier;
import java.util.Locale;
import java.util.Map;

public class ObjectParserHelper {

    public static Map<String, Material> materials;

    public static void init() {
        materials = getMaterials();
    }

    private static ImmutableMap<String, Material> getMaterials() {
        ImmutableMap.Builder<String, Material> materialBuilder = new ImmutableMap.Builder<>();
        for (var field : Material.class.getFields()) {
            if ((field.getModifiers() & Modifier.STATIC) != 0 && field.getType() == Material.class) {
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
