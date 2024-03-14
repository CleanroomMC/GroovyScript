package com.cleanroommc.groovyscript.gameobjects;

import com.cleanroommc.groovyscript.api.IGameObjectParser;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.Result;
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

public class GameObjectHandlerManager {

    private static final Map<String, GameObjectHandler<?>> handlers = new Object2ObjectOpenHashMap<>();
    public static final String EMPTY = "empty", WILDCARD = "*", SPLITTER = ":";

    static void registerGameObjectHandler(GameObjectHandler<?> goh) {
        handlers.put(goh.getName(), goh);
    }

    public static void init() {
        GameObjectHandler.builder("resource", ResourceLocation.class)
                .parser(GameObjectHandlers::parseResourceLocation)
                .addSignature(String.class)
                .addSignature(String.class, String.class)
                .register();
        GameObjectHandler.builder("ore", IIngredient.class)
                .parser((s, args) -> s.contains(WILDCARD) ? Result.some(OreDictWildcardIngredient.of(s)) : Result.some(new OreDictIngredient(s)))
                .completerOfNames(OreDictionaryAccessor::getIdToName)
                .register();
        GameObjectHandler.builder("item", ItemStack.class)
                .parser(GameObjectHandlers::parseItemStack)
                .addSignature(String.class)
                .addSignature(String.class, int.class)
                .defaultValue(() -> ItemStack.EMPTY)
                .completer(ForgeRegistries.ITEMS)
                .register();
        GameObjectHandler.builder("liquid", FluidStack.class)
                .parser(GameObjectHandlers::parseFluidStack)
                .completerOfNames(FluidRegistry.getRegisteredFluids()::keySet)
                .register();
        GameObjectHandler.builder("fluid", FluidStack.class)
                .parser(GameObjectHandlers::parseFluidStack)
                .completerOfNames(FluidRegistry.getRegisteredFluids()::keySet)
                .register();
        GameObjectHandler.builder("block", Block.class)
                .parser(IGameObjectParser.wrapForgeRegistry(ForgeRegistries.BLOCKS))
                .completer(ForgeRegistries.BLOCKS)
                .register();
        GameObjectHandler.builder("blockstate", IBlockState.class)
                .parser(GameObjectHandlers::parseBlockState)
                .addSignature(String.class)
                .addSignature(String.class, int.class)
                .addSignature(String.class, String[].class)
                .completer(ForgeRegistries.BLOCKS)
                .register();
        GameObjectHandler.builder("enchantment", Enchantment.class)
                .parser(IGameObjectParser.wrapForgeRegistry(ForgeRegistries.ENCHANTMENTS))
                .completer(ForgeRegistries.ENCHANTMENTS)
                .register();
        GameObjectHandler.builder("potion", Potion.class)
                .parser(IGameObjectParser.wrapForgeRegistry(ForgeRegistries.POTIONS))
                .completer(ForgeRegistries.POTIONS)
                .register();
        GameObjectHandler.builder("potionType", PotionType.class)
                .parser(IGameObjectParser.wrapForgeRegistry(ForgeRegistries.POTION_TYPES))
                .completer(ForgeRegistries.POTION_TYPES)
                .register();
        GameObjectHandler.builder("sound", SoundEvent.class)
                .parser(IGameObjectParser.wrapForgeRegistry(ForgeRegistries.SOUND_EVENTS))
                .completer(ForgeRegistries.SOUND_EVENTS)
                .register();
        GameObjectHandler.builder("entity", EntityEntry.class)
                .parser(IGameObjectParser.wrapForgeRegistry(ForgeRegistries.ENTITIES))
                .completer(ForgeRegistries.ENTITIES)
                .register();
        GameObjectHandler.builder("biome", Biome.class)
                .parser(IGameObjectParser.wrapForgeRegistry(ForgeRegistries.BIOMES))
                .completer(ForgeRegistries.BIOMES)
                .register();
        GameObjectHandler.builder("profession", VillagerRegistry.VillagerProfession.class)
                .parser(IGameObjectParser.wrapForgeRegistry(ForgeRegistries.VILLAGER_PROFESSIONS))
                .completer(ForgeRegistries.VILLAGER_PROFESSIONS)
                .register();
        GameObjectHandler.builder("creativeTab", CreativeTabs.class)
                .parser(GameObjectHandlers::parseCreativeTab)
                .completerOfNamed(() -> Arrays.asList(CreativeTabs.CREATIVE_TAB_ARRAY), CreativeTabs::getTabLabel)
                .register();
        GameObjectHandler.builder("textformat", TextFormatting.class)
                .parser(GameObjectHandlers::parseTextFormatting)
                .completerOfNamed(() -> Arrays.asList(TextFormatting.values()), format -> format.name().toLowerCase(Locale.ROOT).replaceAll("[^a-z]", ""))
                .register();
        GameObjectHandler.builder("nbt", NBTTagCompound.class)
                .parser(GameObjectHandlers::parseNBT)
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
