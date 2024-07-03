package com.cleanroommc.groovyscript.mapper;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.IObjectParser;
import com.cleanroommc.groovyscript.api.Result;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.cleanroommc.groovyscript.core.mixin.CreativeTabsAccessor;
import com.cleanroommc.groovyscript.core.mixin.OreDictionaryAccessor;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictWildcardIngredient;
import com.cleanroommc.groovyscript.sandbox.expand.ExpansionHelper;
import com.cleanroommc.groovyscript.server.Completions;
import groovy.lang.ExpandoMetaClass;
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

public class ObjectMapperManager {

    private static final Map<String, ObjectMapper<?>> handlers = new Object2ObjectOpenHashMap<>();
    private static final Map<String, List<ObjectMapper<?>>> handlerConflicts = new Object2ObjectOpenHashMap<>();
    private static final Map<Class<? extends GroovyPropertyContainer>, Map<String, ObjectMapper<?>>> modHandlers = new Object2ObjectOpenHashMap<>();
    public static final String EMPTY = "empty";
    public static final String WILDCARD = "*";
    public static final String SPLITTER = ":";

    static void registerObjectMapper(GroovyContainer<?> container, ObjectMapper<?> goh) {
        String key = goh.getName();
        if (goh.getMod() != null) {
            Class<?> clazz = goh.getMod().get().getClass();
            for (Class<?>[] paramTypes : goh.getParamTypes()) {
                ExpandoMetaClass emc = ExpansionHelper.getExpandoClass(clazz);
                emc.registerInstanceMethod(new ObjectMapperMetaMethod(goh, paramTypes, clazz));
            }
        }
        if (handlerConflicts.containsKey(key)) {
            handlerConflicts.get(key).add(goh);
        } else if (handlers.containsKey(key)) {
            List<ObjectMapper<?>> conflicts = handlerConflicts.computeIfAbsent(key, k -> new ArrayList<>());
            conflicts.add(handlers.remove(key));
            conflicts.add(goh);
        } else {
            handlers.put(key, goh);
        }
        if (container != null) {
            GroovyPropertyContainer propertyContainer = container.get();
            var map = modHandlers.computeIfAbsent(propertyContainer.getClass(), k -> new Object2ObjectOpenHashMap<>());
            if (map.containsKey(key)) {
                throw new IllegalStateException("There already is a ObjectMapper with name '" + key + "' in mod " + container.getContainerName());
            }
            map.put(key, goh);
        }
    }

    public static void init() {
        ObjectMapper.builder("resource", ResourceLocation.class)
                .parser(ObjectMappers::parseResourceLocation)
                .addSignature(String.class)
                .addSignature(String.class, String.class)
                .docOfType("resource location")
                .register();
        ObjectMapper.builder("ore", IIngredient.class)
                .parser((s, args) -> s.contains(WILDCARD) ? Result.some(OreDictWildcardIngredient.of(s)) : Result.some(new OreDictIngredient(s)))
                .completerOfNames(OreDictionaryAccessor::getIdToName)
                .docOfType("ore dict entry")
                .register();
        ObjectMapper.builder("item", ItemStack.class)
                .parser(ObjectMappers::parseItemStack)
                .addSignature(String.class)
                .addSignature(String.class, int.class)
                .defaultValue(() -> ItemStack.EMPTY)
                .completer(ForgeRegistries.ITEMS)
                .docOfType("item stack")
                .register();
        ObjectMapper.builder("liquid", FluidStack.class)
                .parser(ObjectMappers::parseFluidStack)
                .completerOfNames(FluidRegistry.getRegisteredFluids()::keySet)
                .docOfType("fluid stack")
                .register();
        ObjectMapper.builder("fluid", FluidStack.class)
                .parser(ObjectMappers::parseFluidStack)
                .completerOfNames(FluidRegistry.getRegisteredFluids()::keySet)
                .register();
        ObjectMapper.builder("block", Block.class)
                .parser(IObjectParser.wrapForgeRegistry(ForgeRegistries.BLOCKS))
                .completer(ForgeRegistries.BLOCKS)
                .docOfType("block")
                .register();
        ObjectMapper.builder("blockstate", IBlockState.class)
                .parser(ObjectMappers::parseBlockState)
                .addSignature(String.class)
                .addSignature(String.class, int.class)
                .addSignature(String.class, String[].class)
                .completer(ForgeRegistries.BLOCKS)
                .docOfType("block state")
                .register();
        ObjectMapper.builder("enchantment", Enchantment.class)
                .parser(IObjectParser.wrapForgeRegistry(ForgeRegistries.ENCHANTMENTS))
                .completer(ForgeRegistries.ENCHANTMENTS)
                .docOfType("enchantment")
                .register();
        ObjectMapper.builder("potion", Potion.class)
                .parser(IObjectParser.wrapForgeRegistry(ForgeRegistries.POTIONS))
                .completer(ForgeRegistries.POTIONS)
                .docOfType("potion")
                .register();
        ObjectMapper.builder("potionType", PotionType.class)
                .parser(IObjectParser.wrapForgeRegistry(ForgeRegistries.POTION_TYPES))
                .completer(ForgeRegistries.POTION_TYPES)
                .docOfType("potion type")
                .register();
        ObjectMapper.builder("sound", SoundEvent.class)
                .parser(IObjectParser.wrapForgeRegistry(ForgeRegistries.SOUND_EVENTS))
                .completer(ForgeRegistries.SOUND_EVENTS)
                .docOfType("sound")
                .register();
        ObjectMapper.builder("entity", EntityEntry.class)
                .parser(IObjectParser.wrapForgeRegistry(ForgeRegistries.ENTITIES))
                .completer(ForgeRegistries.ENTITIES)
                .docOfType("entity entry")
                .register();
        ObjectMapper.builder("biome", Biome.class)
                .parser(IObjectParser.wrapForgeRegistry(ForgeRegistries.BIOMES))
                .completer(ForgeRegistries.BIOMES)
                .docOfType("biome")
                .register();
        ObjectMapper.builder("profession", VillagerRegistry.VillagerProfession.class)
                .parser(IObjectParser.wrapForgeRegistry(ForgeRegistries.VILLAGER_PROFESSIONS))
                .completer(ForgeRegistries.VILLAGER_PROFESSIONS)
                .docOfType("villager profession")
                .register();
        ObjectMapper.builder("creativeTab", CreativeTabs.class)
                .parser(ObjectMappers::parseCreativeTab)
                .completerOfNamed(() -> Arrays.asList(CreativeTabs.CREATIVE_TAB_ARRAY), v -> ((CreativeTabsAccessor) v).getTabLabel2())
                .docOfType("creative tab")
                .register();
        ObjectMapper.builder("textformat", TextFormatting.class)
                .parser(ObjectMappers::parseTextFormatting)
                .completerOfNamed(() -> Arrays.asList(TextFormatting.values()), format -> format.name().toLowerCase(Locale.ROOT).replaceAll("[^a-z]", ""))
                .docOfType("text format")
                .register();
        ObjectMapper.builder("nbt", NBTTagCompound.class)
                .parser(ObjectMappers::parseNBT)
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
        ObjectMapper<?> objectMapper = handlers.get(name);
        if (objectMapper != null) {
            return objectMapper.invoke(mainArg, args);
        }
        return null;
    }

    public static boolean hasObjectMapper(String key) {
        return handlers.containsKey(key);
    }

    public static ObjectMapper<?> getObjectMapper(String key) {
        return handlers.get(key);
    }

    public static List<ObjectMapper<?>> getConflicts(String key) {
        return handlerConflicts.get(key);
    }

    public static ObjectMapper<?> getObjectMapper(Class<?> containerClass, String key) {
        if (!GroovyPropertyContainer.class.isAssignableFrom(containerClass)) return null;
        var map = modHandlers.get(containerClass);
        return map != null ? map.get(key) : null;
    }

    public static Collection<ObjectMapper<?>> getObjectMappers() {
        return handlers.values();
    }

    public static Class<?> getReturnTypeOf(String name) {
        ObjectMapper<?> goh = handlers.get(name);
        return goh == null ? null : goh.getReturnType();
    }

    public static void provideCompletion(String name, int index, Completions items) {
        Completer completer = handlers.get(name).getCompleter();
        if (completer == null) return;
        completer.complete(index, items);
    }
}
