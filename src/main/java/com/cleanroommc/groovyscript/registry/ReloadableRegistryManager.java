package com.cleanroommc.groovyscript.registry;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.INamed;
import com.cleanroommc.groovyscript.api.IReloadableForgeRegistry;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.jei.JeiProxyAccessor;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mezz.jei.Internal;
import mezz.jei.JustEnoughItems;
import mezz.jei.ingredients.IngredientFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.ApiStatus;
import sonar.core.integration.jei.JEISonarPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@GroovyBlacklist
public class ReloadableRegistryManager {

    private static final AtomicBoolean firstLoad = new AtomicBoolean(true);
    private static final Map<Class<? extends IForgeRegistryEntry<?>>, Supplier<? extends IForgeRegistryEntry<?>>> registryDummies = new Object2ObjectOpenHashMap<>();

    private static final Map<Class<?>, List<Object>> recipeRecovery = new Object2ObjectOpenHashMap<>();
    private static final Map<Class<?>, List<Object>> scriptRecipes = new Object2ObjectOpenHashMap<>();

    public static boolean isFirstLoad() {
        return firstLoad.get();
    }

    public static void setLoaded() {
        firstLoad.set(false);
    }

    public static void backup(Class<?> clazz, Object object) {
        recipeRecovery.computeIfAbsent(clazz, k -> new ArrayList<>()).add(object);
    }

    public static void markScripted(Class<?> clazz, Object object) {
        scriptRecipes.computeIfAbsent(clazz, k -> new ArrayList<>()).add(object);
    }

    public static <T> List<T> restore(Class<?> registryClass, @SuppressWarnings("unused") Class<T> recipeClass) {
        List<T> recoveredRecipes = (List<T>) recipeRecovery.remove(registryClass);
        return recoveredRecipes == null ? Collections.emptyList() : recoveredRecipes;
    }

    public static <T> List<T> unmarkScripted(Class<?> registryClass, @SuppressWarnings("unused") Class<T> recipeClass) {
        List<T> marked = (List<T>) scriptRecipes.remove(registryClass);
        return marked == null ? Collections.emptyList() : marked;
    }

    @ApiStatus.Internal
    public static void init() {
        registryDummies.put(IRecipe.class, DummyRecipe::new);
    }

    @ApiStatus.Internal
    public static void onReload() {
        GroovyScript.reloadRunConfig(false);
        ModSupport.getAllContainers()
                .stream()
                .filter(GroovyContainer::isLoaded)
                .map(GroovyContainer::get)
                .map(GroovyPropertyContainer::getRegistries)
                .flatMap(Collection::stream)
                .distinct()
                .filter(INamed::isEnabled)
                .filter(IScriptReloadable.class::isInstance)
                .map(IScriptReloadable.class::cast)
                .forEach(IScriptReloadable::onReload);
    }

    @ApiStatus.Internal
    public static void afterScriptRun() {
        ModSupport.getAllContainers()
                .stream()
                .filter(GroovyContainer::isLoaded)
                .map(GroovyContainer::get)
                .map(GroovyPropertyContainer::getRegistries)
                .flatMap(Collection::stream)
                .distinct()
                .filter(INamed::isEnabled)
                .filter(IScriptReloadable.class::isInstance)
                .map(IScriptReloadable.class::cast)
                .forEach(IScriptReloadable::afterScriptLoad);
        unfreezeForgeRegistries();
    }

    public static <V extends IForgeRegistryEntry<V>> void addRegistryEntry(IForgeRegistry<V> registry, String name, V entry) {
        addRegistryEntry(registry, new ResourceLocation(name), entry);
    }

    public static <V extends IForgeRegistryEntry<V>> void addRegistryEntry(IForgeRegistry<V> registry, ResourceLocation name, V entry) {
        ((IReloadableForgeRegistry<V>) registry).groovyScript$registerEntry(entry.setRegistryName(name));
    }

    public static <V extends IForgeRegistryEntry<V>> void addRegistryEntry(IForgeRegistry<V> registry, V entry) {
        if (entry.getRegistryName() == null) {
            throw new IllegalArgumentException("Expected the name to have a registry name. Add it or use a different method!");
        }
        ((IReloadableForgeRegistry<V>) registry).groovyScript$registerEntry(entry);
    }

    public static <V extends IForgeRegistryEntry<V>> void removeRegistryEntry(IForgeRegistry<V> registry, String name) {
        removeRegistryEntry(registry, new ResourceLocation(name));
    }

    public static <V extends IForgeRegistryEntry<V>> void removeRegistryEntry(IForgeRegistry<V> registry, ResourceLocation name) {
        ((IReloadableForgeRegistry<V>) registry).groovyScript$removeEntry(name);
    }

    public static <V extends IForgeRegistryEntry<V>> Supplier<V> getDummySupplier(Class<V> registryClass) {
        return (Supplier<V>) registryDummies.getOrDefault(registryClass, () -> null);
    }

    public static boolean hasNonDummyRecipe(ResourceLocation rl) {
        IRecipe recipe = ForgeRegistries.RECIPES.getValue(rl);
        return recipe != null && recipe.canFit(1000, 1000);
    }

    /**
     * Reloads JEI completely. Is called after groovy scripts are ran.
     */
    @ApiStatus.Internal
    @SideOnly(Side.CLIENT)
    public static void reloadJei(boolean msgPlayer) {
        if (ModSupport.JEI.isLoaded()) {
            JeiProxyAccessor jeiProxy = (JeiProxyAccessor) JustEnoughItems.getProxy();
            long time = System.currentTimeMillis();

            // Sonar Core adds its categories to JEISonarPlugin#providers every time JeiStarter#start() is called
            // So, to prevent duplicate categories, we need to clear the List before running.
            if (Loader.isModLoaded("sonarcore")) {
                jeiProxy.getPlugins().forEach(plugin -> {
                    if (plugin instanceof JEISonarPlugin jeiSonarPlugin) jeiSonarPlugin.providers.clear();
                });
            }

            jeiProxy.getStarter().start(jeiProxy.getPlugins(), jeiProxy.getTextures());
            time = System.currentTimeMillis() - time;
            if (msgPlayer) {
                Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Reloading JEI took " + time + "ms"));
            }

            // Fix: HEI Removals Disappearing on Reload
            // Reloads the Removed Ingredients (Actually removes them)
            // Must use Internal, no other way to get IngredientFilter
            // Reflection, method doesn't exist in JEI
            var filter = Internal.getIngredientFilter();
            try {
                //noinspection JavaReflectionMemberAccess
                IngredientFilter.class.getDeclaredMethod("block").invoke(filter);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {
            }
        }
    }

    protected static void reloadForgeRegistries(IForgeRegistry<?>... registries) {
        for (IForgeRegistry<?> registry : registries) {
            ((IReloadableForgeRegistry<?>) registry).groovyScript$onReload();
        }
    }

    private static void unfreezeForgeRegistries() {
        ((ForgeRegistry<IRecipe>) ForgeRegistries.RECIPES).unfreeze();
    }
}
