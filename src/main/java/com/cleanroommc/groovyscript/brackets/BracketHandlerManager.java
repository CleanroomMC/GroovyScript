package com.cleanroommc.groovyscript.brackets;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IBracketHandler;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictWildcardIngredient;
import com.cleanroommc.groovyscript.network.NetworkUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

public class BracketHandlerManager {

    private static final Map<String, BracketHandler<?>> bracketHandlers = new Object2ObjectOpenHashMap<>();
    public static final String ANY = "any", EMPTY = "empty", WILDCARD = "*", SPLITTER = ":";

    public static <T> void registerBracketHandler(@Nullable String mod, String key, IBracketHandler<T> handler, Supplier<T> defaultValue) {
        if (StringUtils.isEmpty(key) || handler == null) throw new NullPointerException();
        if (bracketHandlers.containsKey(key)) {
            throw new IllegalArgumentException("Bracket handler already exists for key " + key);
        }
        bracketHandlers.put(key, new BracketHandler<>(key, mod, handler, defaultValue));
    }

    public static <T> void registerBracketHandler(@Nullable String mod, String key, IBracketHandler<T> handler, T defaultValue) {
        registerBracketHandler(mod, key, handler, (Supplier<T>) () -> defaultValue);
    }

    public static <T> void registerBracketHandler(@Nullable String mod, String key, IBracketHandler<T> handler) {
        registerBracketHandler(mod, key, handler, (Supplier<T>) () -> null);
    }

    public static <T> void registerBracketHandler(String key, IBracketHandler<T> handler, Supplier<T> defaultValue) {
        registerBracketHandler(null, key, handler, defaultValue);
    }

    public static <T> void registerBracketHandler(String key, IBracketHandler<T> handler) {
        registerBracketHandler(null, key, handler);
    }

    @Nullable
    public static BracketHandler<?> getBracketHandler(String key) {
        return bracketHandlers.get(key);
    }

    public static void init() {
        registerBracketHandler("resource", ResourceLocationBracketHandler.INSTANCE);
        registerBracketHandler("ore", s -> s.contains("*") ? OreDictWildcardIngredient.of(s) : new OreDictIngredient(s));
        registerBracketHandler("item", ItemBracketHandler.INSTANCE, () -> ItemStack.EMPTY);
        registerBracketHandler("liquid", BracketHandlerManager::parseFluidStack);
        registerBracketHandler("fluid", BracketHandlerManager::parseFluidStack);
        registerBracketHandler("block", Block::getBlockFromName);
        registerBracketHandler("blockstate", BlockStateBracketHandler.INSTANCE);
        registerBracketHandler("enchantment", Enchantment::getEnchantmentByLocation);
        registerBracketHandler("potion", Potion::getPotionFromResourceLocation);
        registerBracketHandler("sound", s -> ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(s)));
        registerBracketHandler("entity", s -> ForgeRegistries.ENTITIES.getValue(new ResourceLocation(s)));
        registerBracketHandler("creativeTab", s -> {
            if (!NetworkUtils.isDedicatedClient()) {
                GroovyLog.get().error("Creative tabs can't be obtained from server side!");
                return CreativeTabs.SEARCH;
            }
            for (CreativeTabs tab : CreativeTabs.CREATIVE_TAB_ARRAY) {
                if (s.equals(tab.getTabLabel())) {
                    return tab;
                }
            }
            return null;
        });
        registerBracketHandler("textformat", s -> {
            TextFormatting textformat = TextFormatting.getValueByName(s);
            if (textformat == null) {
                try {
                    textformat = TextFormatting.fromColorIndex(Integer.parseInt(s));
                } catch (NumberFormatException ignored) { }
            }
            return textformat;
        });
    }

    public static FluidStack parseFluidStack(String s) {
        return FluidRegistry.getFluidStack(s, 1);
    }

    public static Object handleBracket(String name, String mainArg, Object... args) {
        BracketHandler<?> bracketHandler = BracketHandlerManager.getBracketHandler(name);
        if (bracketHandler != null) {
            return bracketHandler.invoke(mainArg, args);
        }
        return null;
    }

    private static Object handleBracket(String name, Object... args) {
        GroovyLog.get().error("First argument of a bracket handler must be a string!");
        return null;
    }

    public static class BracketHandler<T> {

        private final String name;
        private final String mod;
        private final IBracketHandler<T> handler;
        private final Supplier<T> defaultValue;

        private BracketHandler(String name, String mod, IBracketHandler<T> handler, Supplier<T> defaultValue) {
            this.name = name;
            this.mod = mod;
            this.handler = handler;
            this.defaultValue = defaultValue;
        }

        private T invoke(String s, Object... args) {
            T t = handler.parse(s, args);
            if (t == null) {
                if (this.mod == null) {
                    GroovyLog.get().error("Can't find {} for name {}!", name, s);
                } else {
                    GroovyLog.get().error("Can't find {} {} for name {}!", mod, name, s);
                }
                return this.defaultValue.get();
            }
            return t;
        }
    }
}
