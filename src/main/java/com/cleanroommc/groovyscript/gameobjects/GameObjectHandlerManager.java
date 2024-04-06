package com.cleanroommc.groovyscript.gameobjects;

import com.cleanroommc.groovyscript.api.IGameObjectParser;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.Result;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import com.cleanroommc.groovyscript.core.mixin.OreDictionaryAccessor;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictWildcardIngredient;
import com.cleanroommc.groovyscript.server.Completions;
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
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GameObjectHandlerManager {

    private static final Map<String, GameObjectHandler<?>> handlers = new Object2ObjectOpenHashMap<>();
    private static final Map<String, List<GameObjectHandler<?>>> handlerConflicts = new Object2ObjectOpenHashMap<>();
    private static final Map<Class<? extends ModPropertyContainer>, Map<String, GameObjectHandler<?>>> modHandlers = new Object2ObjectOpenHashMap<>();
    public static final String EMPTY = "empty", WILDCARD = "*", SPLITTER = ":";

    static void registerGameObjectHandler(GroovyContainer<?> container, GameObjectHandler<?> goh) {
        String key = goh.getName();
        if (handlerConflicts.containsKey(key)) {
            handlerConflicts.get(key).add(goh);
        } else if (handlers.containsKey(key)) {
            List<GameObjectHandler<?>> conflicts = handlerConflicts.computeIfAbsent(key, k -> new ArrayList<>());
            conflicts.add(handlers.remove(key));
            conflicts.add(goh);
        } else {
            handlers.put(key, goh);
        }
        if (container != null) {
            ModPropertyContainer propertyContainer = container.get();
            var map = modHandlers.computeIfAbsent(propertyContainer.getClass(), k -> new Object2ObjectOpenHashMap<>());
            if (map.containsKey(key)) {
                throw new IllegalStateException("There already is a GOH with name '" + key + "' in mod " + container.getContainerName());
            }
            map.put(key, goh);
        }
    }

    public static void init() {
        GameObjectHandler.builder("resource", ResourceLocation.class)
                .parser(GameObjectHandlers::parseResourceLocation)
                .addSignature(String.class)
                .addSignature(String.class, String.class)
                .docOfType("resource location")
                .register();
        GameObjectHandler.builder("ore", IIngredient.class)
                .parser((s, args) -> s.contains(WILDCARD) ? Result.some(OreDictWildcardIngredient.of(s)) : Result.some(new OreDictIngredient(s)))
                .completerOfNames(OreDictionaryAccessor::getIdToName)
                .docOfType("ore dict entry")
                .register();
        GameObjectHandler.builder("item", ItemStack.class)
                .parser(GameObjectHandlers::parseItemStack)
                .addSignature(String.class)
                .addSignature(String.class, int.class)
                .defaultValue(() -> ItemStack.EMPTY)
                .completer(ForgeRegistries.ITEMS)
                .docOfType("item stack")
                .register();
        GameObjectHandler.builder("liquid", FluidStack.class)
                .parser(GameObjectHandlers::parseFluidStack)
                .completerOfNames(FluidRegistry.getRegisteredFluids()::keySet)
                .docOfType("fluid stack")
                .register();
        GameObjectHandler.builder("fluid", FluidStack.class)
                .parser(GameObjectHandlers::parseFluidStack)
                .completerOfNames(FluidRegistry.getRegisteredFluids()::keySet)
                .register();
        GameObjectHandler.builder("block", Block.class)
                .parser(IGameObjectParser.wrapForgeRegistry(ForgeRegistries.BLOCKS))
                .completer(ForgeRegistries.BLOCKS)
                .docOfType("fluid stack")
                .register();
        GameObjectHandler.builder("blockstate", IBlockState.class)
                .parser(GameObjectHandlers::parseBlockState)
                .addSignature(String.class)
                .addSignature(String.class, int.class)
                .addSignature(String.class, String[].class)
                .completer(ForgeRegistries.BLOCKS)
                .docOfType("block state")
                .register();
        GameObjectHandler.builder("enchantment", Enchantment.class)
                .parser(IGameObjectParser.wrapForgeRegistry(ForgeRegistries.ENCHANTMENTS))
                .completer(ForgeRegistries.ENCHANTMENTS)
                .docOfType("enchantment")
                .register();
        GameObjectHandler.builder("potion", Potion.class)
                .parser(IGameObjectParser.wrapForgeRegistry(ForgeRegistries.POTIONS))
                .completer(ForgeRegistries.POTIONS)
                .docOfType("potion")
                .register();
        GameObjectHandler.builder("potionType", PotionType.class)
                .parser(IGameObjectParser.wrapForgeRegistry(ForgeRegistries.POTION_TYPES))
                .completer(ForgeRegistries.POTION_TYPES)
                .docOfType("potion type")
                .register();
        GameObjectHandler.builder("sound", SoundEvent.class)
                .parser(IGameObjectParser.wrapForgeRegistry(ForgeRegistries.SOUND_EVENTS))
                .completer(ForgeRegistries.SOUND_EVENTS)
                .docOfType("sound")
                .register();
        GameObjectHandler.builder("entity", EntityEntry.class)
                .parser(IGameObjectParser.wrapForgeRegistry(ForgeRegistries.ENTITIES))
                .completer(ForgeRegistries.ENTITIES)
                .docOfType("entity entry")
                .register();
        GameObjectHandler.builder("biome", Biome.class)
                .parser(IGameObjectParser.wrapForgeRegistry(ForgeRegistries.BIOMES))
                .completer(ForgeRegistries.BIOMES)
                .docOfType("biome")
                .register();
        GameObjectHandler.builder("profession", VillagerRegistry.VillagerProfession.class)
                .parser(IGameObjectParser.wrapForgeRegistry(ForgeRegistries.VILLAGER_PROFESSIONS))
                .completer(ForgeRegistries.VILLAGER_PROFESSIONS)
                .docOfType("villager profession")
                .register();
        GameObjectHandler.builder("creativeTab", CreativeTabs.class)
                .parser(GameObjectHandlers::parseCreativeTab)
                .completerOfNamed(() -> Arrays.asList(CreativeTabs.CREATIVE_TAB_ARRAY), CreativeTabs::getTabLabel)
                .docOfType("creative tab")
                .register();
        GameObjectHandler.builder("textformat", TextFormatting.class)
                .parser(GameObjectHandlers::parseTextFormatting)
                .completerOfNamed(() -> Arrays.asList(TextFormatting.values()), format -> format.name().toLowerCase(Locale.ROOT).replaceAll("[^a-z]", ""))
                .docOfType("text format")
                .register();
        GameObjectHandler.builder("nbt", NBTTagCompound.class)
                .parser(GameObjectHandlers::parseNBT)
                .docOfType("nbt tag")
                .register();
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
        GameObjectHandler<?> gameObjectHandler = handlers.get(name);
        if (gameObjectHandler != null) {
            return gameObjectHandler.invoke(mainArg, args);
        }
        return null;
    }

    public static boolean hasGameObjectHandler(String key) {
        return handlers.containsKey(key);
    }

    public static GameObjectHandler<?> getGameObjectHandler(String key) {
        return handlers.get(key);
    }

    public static List<GameObjectHandler<?>> getConflicts(String key) {
        return handlerConflicts.get(key);
    }

    public static GameObjectHandler<?> getGameObjectHandler(Class<?> containerClass, String key) {
        if (!ModPropertyContainer.class.isAssignableFrom(containerClass)) return null;
        var map = modHandlers.get(containerClass);
        return map != null ? map.get(key) : null;
    }

    public static Collection<GameObjectHandler<?>> getGameObjectHandlers() {
        return handlers.values();
    }

    public static Class<?> getReturnTypeOf(String name) {
        GameObjectHandler<?> goh = handlers.get(name);
        return goh == null ? null : goh.getReturnType();
    }

    public static void provideCompletion(String name, int index, Completions items) {
        Completer completer = handlers.get(name).getCompleter();
        if (completer == null) return;
        completer.complete(index, items);
    }
}
