package com.cleanroommc.groovyscript.brackets;

import com.cleanroommc.groovyscript.api.IBracketHandler;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;

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

    @Nullable
    public static IBracketHandler<?> getBracketHandler(String key) {
        return bracketHandlers.get(key);
    }

    public static void init() {
        registerBracketHandler("ore", OreDictIngredient::new);
        registerBracketHandler("item", ItemBracketHandler.INSTANCE);
        registerBracketHandler("liquid", BracketHandlerManager::parseFluidStack);
        registerBracketHandler("fluid", BracketHandlerManager::parseFluidStack);
        registerBracketHandler("block", Block::getBlockFromName);
        registerBracketHandler("blockstate", BlockStateBracketHandler.INSTANCE);
        registerBracketHandler("enchantment", Enchantment::getEnchantmentByLocation);
        registerBracketHandler("entity", s -> ForgeRegistries.ENTITIES.getValue(new ResourceLocation(s)));
        if (ModSupport.MEKANISM.isLoaded()) {
            registerBracketHandler("gas", s -> new GasStack(GasRegistry.getGas(s), 1));
        }
        if (ModSupport.THAUMCRAFT.isLoaded()) {
            registerBracketHandler("aspect", AspectBracketHandler.INSTANCE);
            registerBracketHandler("crystal", s -> ThaumcraftApiHelper.makeCrystal(Aspect.getAspect(s)));
        }
    }

    public static FluidStack parseFluidStack(String s) {
        return FluidRegistry.getFluidStack(s, 1);
    }
}
