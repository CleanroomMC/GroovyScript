package com.cleanroommc.groovyscript.gameobjects;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IGameObjectHandler;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.Result;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictWildcardIngredient;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class GameObjectHandlerManager {

    private static final Map<String, GameObjectHandler<?>> bracketHandlers = new Object2ObjectOpenHashMap<>();
    public static final String EMPTY = "empty", WILDCARD = "*", SPLITTER = ":";

    public static <T> GameObjectHandler<T> registerGameObjectHandler(@Nullable String mod, String key, Class<T> returnType, IGameObjectHandler<T> handler, Supplier<T> defaultValue) {
        if (StringUtils.isEmpty(key) || handler == null || defaultValue == null) throw new NullPointerException();
        if (bracketHandlers.containsKey(key)) {
            throw new IllegalArgumentException("Bracket handler already exists for key " + key);
        }
        GameObjectHandler<T> goh = new GameObjectHandler<>(key, mod, handler, defaultValue, returnType);
        bracketHandlers.put(key, goh);
        return goh;
    }

    public static <T> GameObjectHandler<T> registerGameObjectHandler(@Nullable String mod, String key, Class<T> returnType, IGameObjectHandler<T> handler, T defaultValue) {
        return registerGameObjectHandler(mod, key, returnType, handler, (Supplier<T>) () -> defaultValue);
    }

    public static <T> GameObjectHandler<T> registerGameObjectHandler(@Nullable String mod, String key, Class<T> returnType, IGameObjectHandler<T> handler) {
        return registerGameObjectHandler(mod, key, returnType, handler, (Supplier<T>) () -> null);
    }

    public static <T> GameObjectHandler<T> registerGameObjectHandler(String key, Class<T> returnType, IGameObjectHandler<T> handler, Supplier<T> defaultValue) {
        return registerGameObjectHandler(null, key, returnType, handler, defaultValue);
    }

    public static <T> GameObjectHandler<T> registerGameObjectHandler(String key, Class<T> returnType, IGameObjectHandler<T> handler) {
        return registerGameObjectHandler(null, key, returnType, handler);
    }

    public static boolean hasGameObjectHandler(String key) {
        return bracketHandlers.containsKey(key);
    }

    public static Collection<GameObjectHandler<?>> getGameObjectHandlers() {
        return bracketHandlers.values();
    }

    public static Class<?> getReturnTypeOf(String name) {
        GameObjectHandler<?> goh = bracketHandlers.get(name);
        return goh == null ? null : goh.getReturnType();
    }

    public static void init() {
        registerGameObjectHandler("resource", ResourceLocation.class, GameObjectHandlers::parseResourceLocation)
                .withDesc(String.class, String.class);
        registerGameObjectHandler("ore", IIngredient.class, (s, args) -> s.contains("*") ? Result.some(OreDictWildcardIngredient.of(s)) : Result.some(new OreDictIngredient(s)));
        registerGameObjectHandler("item", ItemStack.class, GameObjectHandlers::parseItemStack, () -> ItemStack.EMPTY)
                .withDesc(String.class, int.class);
        registerGameObjectHandler("liquid", FluidStack.class, GameObjectHandlers::parseFluidStack);
        registerGameObjectHandler("fluid", FluidStack.class, GameObjectHandlers::parseFluidStack);
        registerGameObjectHandler("block", Block.class, IGameObjectHandler.wrapForgeRegistry(ForgeRegistries.BLOCKS));
        registerGameObjectHandler("blockstate", IBlockState.class, GameObjectHandlers::parseBlockState)
                .withDesc(String.class, int.class)
                .withDesc(String.class, String[].class);
        registerGameObjectHandler("enchantment", Enchantment.class, IGameObjectHandler.wrapForgeRegistry(ForgeRegistries.ENCHANTMENTS));
        registerGameObjectHandler("potion", Potion.class, IGameObjectHandler.wrapForgeRegistry(ForgeRegistries.POTIONS));
        registerGameObjectHandler("potionType", PotionType.class, IGameObjectHandler.wrapForgeRegistry(ForgeRegistries.POTION_TYPES));
        registerGameObjectHandler("sound", SoundEvent.class, IGameObjectHandler.wrapForgeRegistry(ForgeRegistries.SOUND_EVENTS));
        registerGameObjectHandler("entity", EntityEntry.class, IGameObjectHandler.wrapForgeRegistry(ForgeRegistries.ENTITIES));
        registerGameObjectHandler("biome", Biome.class, IGameObjectHandler.wrapForgeRegistry(ForgeRegistries.BIOMES));
        registerGameObjectHandler("profession", VillagerRegistry.VillagerProfession.class, IGameObjectHandler.wrapForgeRegistry(ForgeRegistries.VILLAGER_PROFESSIONS));
        registerGameObjectHandler("creativeTab", CreativeTabs.class, GameObjectHandlers::parseCreativeTab);
        registerGameObjectHandler("textformat", TextFormatting.class, GameObjectHandlers::parseTextFormatting)
                .withDesc(int.class);
        registerGameObjectHandler("nbt", NBTTagCompound.class, GameObjectHandlers::parseNBT);
    }

    /**
     * Finds the game object handle and invokes it. Called by injected calls via the groovy script transformer.
     *
     * @param name    game object handler name (method name)
     * @param mainArg main argument
     * @param args    extra arguments
     * @return game object or null
     */
    @Nullable
    public static Object getGameObject(String name, String mainArg, Object... args) {
        GameObjectHandler<?> gameObjectHandler = bracketHandlers.get(name);
        if (gameObjectHandler != null) {
            return gameObjectHandler.invoke(mainArg, args);
        }
        return null;
    }

    public static class GameObjectHandler<T> {

        private final String name;
        private final String mod;
        private final IGameObjectHandler<T> handler;
        private final Supplier<T> defaultValue;
        private final Class<T> returnType;
        private final List<Class<?>[]> paramTypes = new ArrayList<>();

        private GameObjectHandler(String name, String mod, IGameObjectHandler<T> handler, Supplier<T> defaultValue, Class<T> returnType) {
            this.name = name;
            this.mod = mod;
            this.handler = handler;
            this.defaultValue = defaultValue;
            this.returnType = returnType;
            withDesc(String.class);
        }

        private T invoke(String s, Object... args) {
            Result<T> t = Objects.requireNonNull(handler.parse(s, args), "Bracket handlers must return a non null result!");
            if (t.hasError()) {
                if (this.mod == null) {
                    GroovyLog.get().error("Can't find {} for name {}!", name, s);
                } else {
                    GroovyLog.get().error("Can't find {} {} for name {}!", mod, name, s);
                }
                if (t.getError() != null && !t.getError().isEmpty()) {
                    GroovyLog.get().error(" - reason: {}", t.getError());
                }
                return this.defaultValue.get();
            }
            return Objects.requireNonNull(t.getValue(), "Bracket handler result must contain a non-null value!");
        }

        public String getMod() {
            return mod;
        }

        public String getName() {
            return name;
        }

        public GameObjectHandler<T> withDesc(Class<?>... paramTypes) {
            this.paramTypes.add(paramTypes);
            return this;
        }

        public List<Class<?>[]> getParamTypes() {
            return this.paramTypes;
        }

        public Class<T> getReturnType() {
            return returnType;
        }
    }
}
