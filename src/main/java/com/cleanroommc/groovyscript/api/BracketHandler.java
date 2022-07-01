package com.cleanroommc.groovyscript.api;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class BracketHandler {

    public static final List<Function<String, Object>> handlers = new ArrayList<>();

    public static void registerBracketHandler(Function<String, Object> handler) {
        handlers.add(handler);
    }

    public static Object handleBracket(String s) {
        for (Function<String, Object> handler : handlers) {
            Object result = handler.apply(s);
            if (result != null) return result;
        }
        return s;
    }

    public static void init() {
        registerBracketHandler(s -> {
            try {
                return new com.cleanroommc.groovyscript.api.wrapper.ItemStack(getItemStack(s));
            } catch (Exception e) {
                return null;
            }
        });
    }

    public static ItemStack getItemStack(String item) {
        String[] parts = item.split(":");
        if (parts.length < 2) throw new IllegalArgumentException();
        Item item1 = ForgeRegistries.ITEMS.getValue(new ResourceLocation(parts[0], parts[1]));
        if (item1 == null) {
            throw new NoSuchElementException("Can't find item for " + item);
        }
        int meta = 0;
        if (parts.length > 2) {
            try {
                meta = Integer.parseInt(parts[2]);
            } catch (NumberFormatException ignored) {
            }
        }
        return new ItemStack(item1, 1, meta);
    }
}
