package com.cleanroommc.groovyscript.brackets;

import com.cleanroommc.groovyscript.api.IBracketHandler;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class BracketHandlerManager {

    private static final Map<String, IBracketHandler<?>> bracketHandlers = new Object2ObjectOpenHashMap<>();
    public static final String ANY = "any", EMPTY = "empty", WILDCARD = "*", SPLITTER = ":";

    public static void registerBracketHandler(String key, IBracketHandler<?> handler) {
        if (StringUtils.isEmpty(key) || handler == null) throw new NullPointerException();
        if (bracketHandlers.containsKey(key)) {
            throw new IllegalArgumentException("Bracket handler already exists for key " + key);
        }
        bracketHandlers.put(key, handler);
    }

    public static Object handleBracket(String s) {
        String[] parts = s.split(SPLITTER, 2);
        if (parts.length == 0) return s;
        IBracketHandler<?> bracketHandler = bracketHandlers.get(parts[0]);
        if (bracketHandler != null) {
            return bracketHandler.parse(parts[1]);
        }
        String main = parts[1];
        if (ANY.equals(main)) return IIngredient.ANY;
        if (EMPTY.equals(main)) return IIngredient.EMPTY;
        return ItemBracketHandler.INSTANCE.parse(main);
    }

    @Nullable
    public static IBracketHandler<?> getBracketHandler(String key) {
        return bracketHandlers.get(key);
    }

    public static void init() {
        registerBracketHandler("ore", OreDictIngredient::new);
        registerBracketHandler("item", ItemBracketHandler.INSTANCE);
        registerBracketHandler("liquid", BracketHandlerManager::parseFluidStack);
        registerBracketHandler("fluid", BracketHandlerManager::parseFluidStack);
        registerBracketHandler("blockstate", BlockStateBracketHandler.INSTANCE);
        registerBracketHandler("enchantment", Enchantment::getEnchantmentByLocation);
        if (ModSupport.MEKANISM.isLoaded()) {
            registerBracketHandler("gas", s -> new GasStack(GasRegistry.getGas(s), 1));
        }
    }

    public static FluidStack parseFluidStack(String s) {
        return FluidRegistry.getFluidStack(s, 1);
    }
}
