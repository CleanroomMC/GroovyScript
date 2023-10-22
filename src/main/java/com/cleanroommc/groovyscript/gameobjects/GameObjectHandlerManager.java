package com.cleanroommc.groovyscript.gameobjects;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IGameObjectHandler;
import com.cleanroommc.groovyscript.api.Result;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictWildcardIngredient;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class GameObjectHandlerManager {

    private static final Map<String, GameObjectHandler<?>> bracketHandlers = new Object2ObjectOpenHashMap<>();
    public static final String EMPTY = "empty", WILDCARD = "*", SPLITTER = ":";

    public static <T> void registerGameObjectHandler(@Nullable String mod, String key, IGameObjectHandler<T> handler, Supplier<T> defaultValue) {
        if (StringUtils.isEmpty(key) || handler == null || defaultValue == null) throw new NullPointerException();
        if (bracketHandlers.containsKey(key)) {
            throw new IllegalArgumentException("Bracket handler already exists for key " + key);
        }
        bracketHandlers.put(key, new GameObjectHandler<>(key, mod, handler, defaultValue));
    }

    public static <T> void registerGameObjectHandler(@Nullable String mod, String key, IGameObjectHandler<T> handler, T defaultValue) {
        registerGameObjectHandler(mod, key, handler, (Supplier<T>) () -> defaultValue);
    }

    public static <T> void registerGameObjectHandler(@Nullable String mod, String key, IGameObjectHandler<T> handler) {
        registerGameObjectHandler(mod, key, handler, (Supplier<T>) () -> null);
    }

    public static <T> void registerGameObjectHandler(String key, IGameObjectHandler<T> handler, Supplier<T> defaultValue) {
        registerGameObjectHandler(null, key, handler, defaultValue);
    }

    public static <T> void registerGameObjectHandler(String key, IGameObjectHandler<T> handler) {
        registerGameObjectHandler(null, key, handler);
    }

    public static boolean hasGameObjectHandler(String key) {
        return bracketHandlers.containsKey(key);
    }

    public static void init() {
        registerGameObjectHandler("resource", GameObjectHandlers::parseResourceLocation);
        registerGameObjectHandler("ore", (s, args) -> s.contains("*") ? Result.some(OreDictWildcardIngredient.of(s)) : Result.some(new OreDictIngredient(s)));
        registerGameObjectHandler("item", GameObjectHandlers::parseItemStack, () -> ItemStack.EMPTY);
        registerGameObjectHandler("liquid", GameObjectHandlers::parseFluidStack);
        registerGameObjectHandler("fluid", GameObjectHandlers::parseFluidStack);
        registerGameObjectHandler("block", IGameObjectHandler.wrapForgeRegistry(ForgeRegistries.BLOCKS));
        registerGameObjectHandler("blockstate", GameObjectHandlers::parseBlockState);
        registerGameObjectHandler("enchantment", IGameObjectHandler.wrapForgeRegistry(ForgeRegistries.ENCHANTMENTS));
        registerGameObjectHandler("potion", IGameObjectHandler.wrapForgeRegistry(ForgeRegistries.POTIONS));
        registerGameObjectHandler("potionType", IGameObjectHandler.wrapForgeRegistry(ForgeRegistries.POTION_TYPES));
        registerGameObjectHandler("sound", IGameObjectHandler.wrapForgeRegistry(ForgeRegistries.SOUND_EVENTS));
        registerGameObjectHandler("entity", IGameObjectHandler.wrapForgeRegistry(ForgeRegistries.ENTITIES));
        registerGameObjectHandler("biome", IGameObjectHandler.wrapForgeRegistry(ForgeRegistries.BIOMES));
        registerGameObjectHandler("profession", IGameObjectHandler.wrapForgeRegistry(ForgeRegistries.VILLAGER_PROFESSIONS));
        registerGameObjectHandler("creativeTab", GameObjectHandlers::parseCreativeTab);
        registerGameObjectHandler("textformat", GameObjectHandlers::parseTextFormatting);
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

        private GameObjectHandler(String name, String mod, IGameObjectHandler<T> handler, Supplier<T> defaultValue) {
            this.name = name;
            this.mod = mod;
            this.handler = handler;
            this.defaultValue = defaultValue;
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
    }
}
