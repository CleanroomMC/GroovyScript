package com.cleanroommc.groovyscript.mapper;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.IObjectParser;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.cleanroommc.groovyscript.core.mixin.CreativeTabsAccessor;
import com.cleanroommc.groovyscript.core.mixin.OreDictionaryAccessor;
import com.cleanroommc.groovyscript.core.mixin.VillagerProfessionAccessor;
import com.cleanroommc.groovyscript.sandbox.expand.ExpansionHelper;
import com.cleanroommc.groovyscript.server.CompletionParams;
import com.cleanroommc.groovyscript.server.Completions;
import groovy.lang.ExpandoMetaClass;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ObjectMapperManager {

    private static final Map<String, AbstractObjectMapper<?>> handlers = new Object2ObjectOpenHashMap<>();
    private static final Map<String, List<AbstractObjectMapper<?>>> handlerConflicts = new Object2ObjectOpenHashMap<>();
    private static final Map<Class<? extends GroovyPropertyContainer>, Map<String, AbstractObjectMapper<?>>> modHandlers = new Object2ObjectOpenHashMap<>();
    public static final String EMPTY = "empty";
    public static final String WILDCARD = "*";
    public static final String SPLITTER = ":";

    public static void registerObjectMapper(AbstractObjectMapper<?> mapper) {
        String key = mapper.getName();
        if (mapper.getMod() != null) {
            GroovyContainer<?> mod = mapper.getMod();
            Class<?> clazz = mapper.getMod().get().getClass();
            for (Class<?>[] paramTypes : mapper.getParamTypes()) {
                ExpandoMetaClass emc = ExpansionHelper.getExpandoClass(clazz);
                emc.registerInstanceMethod(new ObjectMapperMetaMethod(mapper, paramTypes, clazz));
            }
            GroovyPropertyContainer propertyContainer = mod.get();
            var map = modHandlers.computeIfAbsent(propertyContainer.getClass(), k -> new Object2ObjectOpenHashMap<>());
            if (map.containsKey(key)) {
                throw new IllegalStateException("There already is a ObjectMapper with name '" + key + "' in mod " + mod.getContainerName());
            }
            map.put(key, mapper);
        }
        if (handlerConflicts.containsKey(key)) {
            handlerConflicts.get(key).add(mapper);
        } else if (handlers.containsKey(key)) {
            List<AbstractObjectMapper<?>> conflicts = handlerConflicts.computeIfAbsent(key, k -> new ArrayList<>());
            conflicts.add(handlers.remove(key));
            conflicts.add(mapper);
        } else {
            handlers.put(key, mapper);
        }
    }

    public static void init() {
        registerObjectMapper(ItemStackMapper.INSTANCE);
        registerObjectMapper(BlockStateMapper.INSTANCE);
        ObjectMapper.builder("resource", ResourceLocation.class)
                .parser(ObjectMappers::parseResourceLocation)
                .addSignature(String.class)
                .addSignature(String.class, String.class)
                .docOfType("resource location")
                .register();
        ObjectMapper.builder("ore", IIngredient.class)
                .parser(ObjectMappers::parseOreDict)
                .completerOfNames(OreDictionaryAccessor::getIdToName)
                .docOfType("ore dict entry")
                .textureBinder(TextureBinder.ofArray(IIngredient::getMatchingStacks, TextureBinder.ofItem()))
                .tooltipOfArray(IIngredient::getMatchingStacks, i -> String.format("![](${item('%s')}) %s", i.getItem().getRegistryName(), i.getDisplayName()))
                .register();
        /*ObjectMapper.builder("item", ItemStack.class)
                .parser(ObjectMappers::parseItemStack)
                .addSignature(String.class)
                .addSignature(String.class, int.class)
                .defaultValue(() -> ItemStack.EMPTY)
                .completer(ForgeRegistries.ITEMS)
                .docOfType("item stack")
                .textureBinder(TextureBinder.ofItem())
                .register();*/
        ObjectMapper.builder("liquid", FluidStack.class)
                .parser(ObjectMappers::parseFluidStack)
                .completerOfNames(FluidRegistry.getRegisteredFluids()::keySet)
                .docOfType("fluid stack")
                .textureBinder(TextureBinder.ofFluid())
                .register();
        ObjectMapper.builder("fluid", FluidStack.class)
                .parser(ObjectMappers::parseFluidStack)
                .completerOfNames(FluidRegistry.getRegisteredFluids()::keySet)
                .textureBinder(TextureBinder.ofFluid())
                .tooltip(f -> Collections.singletonList(f.getLocalizedName()))
                .register();
        ObjectMapper.builder("block", Block.class)
                .parser(IObjectParser.wrapForgeRegistry(ForgeRegistries.BLOCKS))
                .completer(ForgeRegistries.BLOCKS)
                .defaultValue(() -> Blocks.AIR)
                .docOfType("block")
                .textureBinder(TextureBinder.of(ItemStack::new, TextureBinder.ofItem()))
                .register();
        /*ObjectMapper.builder("blockstate", IBlockState.class)
                .parser(ObjectMappers::parseBlockState)
                .addSignature(String.class)
                .addSignature(String.class, int.class)
                .addSignature(String.class, String[].class)
                .completer(ForgeRegistries.BLOCKS)
                .defaultValue(() -> Blocks.AIR.getBlockState().getBaseState())
                .docOfType("block state")
                .register();*/
        ObjectMapper.builder("enchantment", Enchantment.class)
                .parser(IObjectParser.wrapForgeRegistry(ForgeRegistries.ENCHANTMENTS))
                .completer(ForgeRegistries.ENCHANTMENTS)
                .docOfType("enchantment")
                .register();
        ObjectMapper.builder("potion", Potion.class)
                .parser(IObjectParser.wrapForgeRegistry(ForgeRegistries.POTIONS))
                .completer(ForgeRegistries.POTIONS)
                .docOfType("potion")
                .textureBinder(TextureBinder.of(potion -> PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionType.REGISTRY.getObject(potion.getRegistryName())), TextureBinder.ofItem()))
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
        ObjectMapper.builder("dimension", DimensionType.class)
                .parser(IObjectParser.wrapStringGetter(DimensionType::byName))
                .completerOfNamed(() -> Arrays.asList(DimensionType.values()), DimensionType::getName)
                .docOfType("dimension")
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

        final List<String> careerList = new ArrayList<>();
        for (var profession : ForgeRegistries.VILLAGER_PROFESSIONS) {
            if (profession != null) {
                for (var career : ((VillagerProfessionAccessor) profession).getCareers()) {
                    if (career != null) {
                        careerList.add(career.getName());
                    }
                }
            }
        }
        ObjectMapper.builder("career", VillagerRegistry.VillagerCareer.class)
                .parser(ObjectMappers::parseVillagerCareer)
                .completerOfNames(() -> careerList)
                .docOfType("villager career")
                .register();
        ObjectMapper.builder("creativeTab", CreativeTabs.class)
                .parser(ObjectMappers::parseCreativeTab)
                .completerOfNamed(() -> Arrays.asList(CreativeTabs.CREATIVE_TAB_ARRAY), v -> ((CreativeTabsAccessor) v).getTabLabel2())
                .defaultValue(() -> CreativeTabs.SEARCH)
                .docOfType("creative tab")
                .register();
        ObjectMapper.builder("textformat", TextFormatting.class)
                .parser(ObjectMappers::parseTextFormatting)
                .completerOfNamed(() -> Arrays.asList(TextFormatting.values()), format -> format.name().toLowerCase(Locale.ROOT).replaceAll("[^a-z]", ""))
                .defaultValue(() -> TextFormatting.RESET)
                .docOfType("text format")
                .register();
        ObjectMapper.builder("nbt", NBTTagCompound.class)
                .parser(ObjectMappers::parseNBT)
                .defaultValue(NBTTagCompound::new)
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
    public static @Nullable Object getGameObject(String name, String mainArg, Object... args) {
        return getGameObject(false, name, mainArg, args);
    }

    /**
     * Finds the game object handle and invokes it. Called by injected calls via the groovy script transformer.
     *
     * @param name    game object handler name (method name)
     * @param mainArg main argument
     * @param args    extra arguments
     * @param silent if error messages should be logged
     * @return game object or null
     */

    public static @Nullable Object getGameObject(boolean silent, String name, String mainArg, Object... args) {
        AbstractObjectMapper<?> objectMapper = handlers.get(name);
        if (objectMapper != null) {
            return objectMapper.invokeWithDefault(silent, mainArg, args);
        }
        return null;
    }

    public static boolean hasObjectMapper(String key) {
        return handlers.containsKey(key);
    }

    public static AbstractObjectMapper<?> getObjectMapper(String key) {
        return handlers.get(key);
    }

    public static List<AbstractObjectMapper<?>> getConflicts(String key) {
        return handlerConflicts.get(key);
    }

    public static AbstractObjectMapper<?> getObjectMapper(Class<?> containerClass, String key) {
        if (!GroovyPropertyContainer.class.isAssignableFrom(containerClass)) return null;
        var map = modHandlers.get(containerClass);
        return map != null ? map.get(key) : null;
    }

    public static Collection<AbstractObjectMapper<?>> getObjectMappers() {
        return handlers.values();
    }

    @Deprecated
    public static Class<?> getReturnTypeOf(String name) {
        AbstractObjectMapper<?> goh = handlers.get(name);
        return goh == null ? null : goh.getReturnType();
    }

    @Deprecated
    public static void provideCompletion(String name, int index, Completions items) {
        handlers.get(name).provideCompletion(index, CompletionParams.EMPTY, items);
    }
}
