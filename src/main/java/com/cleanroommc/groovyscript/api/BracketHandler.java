package com.cleanroommc.groovyscript.api;

import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.OreDictIngredient;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class BracketHandler {

    private static final Map<String, Function<String, Object>> bracketHandlers = new HashMap<>();
    public static final Function<String, Object> itemStackBracketHandler = s -> {
        String[] parts = s.split(":");
        if (parts.length == 0) return null;
        if (parts.length == 1) {
            if (parts[0].equals("empty")) return IIngredient.EMPTY;
            if (parts[0].equals("any")) return IIngredient.ANY;
            return null;
        }
        try {
            return parseItemStack(parts);
        } catch (Exception e) {
            return null;
        }
    };

    public static void registerBracketHandler(String key, Function<String, Object> handler) {
        if (key == null || key.isEmpty() || handler == null) throw new NullPointerException();
        if (bracketHandlers.containsKey(key)) {
            throw new IllegalArgumentException("Bracket handler already exists for key " + key);
        }
        bracketHandlers.put(key, handler);
    }

    public static Object handleBracket(String s) {
        String[] parts = s.split(":", 2);
        if (parts.length == 0) return s;
        Function<String, Object> bracketHandler = bracketHandlers.get(parts[0]);
        Object result = bracketHandler == null ? itemStackBracketHandler.apply(s) : bracketHandler.apply(parts[1]);
        if (result == null) {
            throw new NoSuchElementException("Could not find game object for <" + s + ">!");
        }
        return result;
    }

    @Nullable
    public static Function<String, Object> getBracketHandler(String key) {
        return bracketHandlers.get(key);
    }

    public static void init() {
        registerBracketHandler("ore", OreDictIngredient::new);
        registerBracketHandler("item", itemStackBracketHandler);
        registerBracketHandler("liquid", BracketHandler::parseFluidStack);
        registerBracketHandler("fluid", BracketHandler::parseFluidStack);
        registerBracketHandler("enchantment", Enchantment::getEnchantmentByLocation);
        if (ModSupport.MEKANISM.isLoaded()) {
            registerBracketHandler("gas", s -> new GasStack(GasRegistry.getGas(s), 1000));
        }
    }

    public static ItemStack parseItemStack(String[] raw) {
        Item item1 = ForgeRegistries.ITEMS.getValue(new ResourceLocation(raw[0], raw[1]));
        if (item1 == null) {
            throw new NoSuchElementException("Can't find item for " + Arrays.toString(raw));
        }
        int meta = 0;
        if (raw.length > 2) {
            try {
                meta = Integer.parseInt(raw[2]);
            } catch (NumberFormatException ignored) {
            }
        }
        return new ItemStack(item1, 1, meta);
    }

    public static FluidStack parseFluidStack(String s) {
        return FluidRegistry.getFluidStack(s, 1000);
    }
}
