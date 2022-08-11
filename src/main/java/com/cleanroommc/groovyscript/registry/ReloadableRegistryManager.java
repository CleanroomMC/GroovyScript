package com.cleanroommc.groovyscript.registry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.compat.ModSupport;
import com.cleanroommc.groovyscript.compat.thermalexpansion.ThermalExpansion;
import com.cleanroommc.groovyscript.mixin.JeiProxyAccessor;
import crazypants.enderio.base.fluid.FluidFuelRegister;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.alloysmelter.AlloyRecipeManager;
import crazypants.enderio.base.recipe.sagmill.SagMillRecipeManager;
import crazypants.enderio.base.recipe.slicensplice.SliceAndSpliceRecipeManager;
import crazypants.enderio.base.recipe.vat.VatRecipeManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mekanism.common.recipe.RecipeHandler;
import mezz.jei.JustEnoughItems;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@GroovyBlacklist
public class ReloadableRegistryManager {

    private static final AtomicBoolean firstLoad = new AtomicBoolean(true);
    private static final List<IReloadableVirtualizedRegistry<?>> reloadableRegistries = new ArrayList<>();
    private static final Map<Class<?>, List<Object>> recipeRecovery = new Object2ObjectOpenHashMap<>();
    private static final Map<Class<?>, List<Object>> scriptRecipes = new Object2ObjectOpenHashMap<>();

    public static boolean isFirstLoad() {
        return firstLoad.get();
    }

    public static void setLoaded() {
        firstLoad.set(false);
    }

    public static void addReloadableRegistry(IReloadableVirtualizedRegistry<?> registry) {
        if (reloadableRegistries.stream().noneMatch(r -> r.getClass() == registry.getClass())) {
            reloadableRegistries.add(registry);
        }
    }

    @ApiStatus.Internal
    public static void addRecipeForRecovery(Class<?> clazz, Object object) {
        recipeRecovery.computeIfAbsent(clazz, k -> new ArrayList<>()).add(object);
    }

    @ApiStatus.Internal
    public static void markScriptRecipe(Class<?> clazz, Object object) {
        scriptRecipes.computeIfAbsent(clazz, k -> new ArrayList<>()).add(object);
    }

    @ApiStatus.Internal
    public static List<Object> recoverRecipes(Class<?> clazz) {
        List<Object> recoveredRecipes = recipeRecovery.remove(clazz);
        return recoveredRecipes == null ? Collections.emptyList() : recoveredRecipes;
    }

    @ApiStatus.Internal
    public static List<Object> unmarkScriptRecipes(Class<?> clazz) {
        List<Object> marked = scriptRecipes.remove(clazz);
        return marked == null ? Collections.emptyList() : marked;
    }

    @ApiStatus.Internal
    public static void onReload() {
        // TODO: reloadableRegistries.forEach(IReloadableVirtualizedRegistry::onReload);
        reloadForgeRegistry(ForgeRegistries.RECIPES);
        if (ModSupport.MEKANISM.isLoaded()) {
            RecipeHandler.Recipe.values().forEach(recipe -> ((IReloadableRegistry<?>) recipe).onReload());
        }
        if (ModSupport.ENDER_IO.isLoaded()) {
            ((IReloadableRegistry<?>) AlloyRecipeManager.getInstance()).onReload();
            ((IReloadableRegistry<?>) (Object) SagMillRecipeManager.getInstance()).onReload();
            ((IReloadableRegistry<?>) SliceAndSpliceRecipeManager.getInstance()).onReload();
            ((IReloadableRegistry<?>) VatRecipeManager.getInstance()).onReload();
            ((IReloadableRegistry<?>) MachineRecipeRegistry.instance.getRecipeHolderssForMachine(MachineRecipeRegistry.SOULBINDER)).onReload();
            ((IReloadableRegistry<?>) MachineRecipeRegistry.instance.getRecipeHolderssForMachine(MachineRecipeRegistry.ENCHANTER)).onReload();
            ((IReloadableRegistry<?>) MachineRecipeRegistry.instance.getRecipeHolderssForMachine(MachineRecipeRegistry.TANK_EMPTYING)).onReload();
            ((IReloadableRegistry<?>) MachineRecipeRegistry.instance.getRecipeHolderssForMachine(MachineRecipeRegistry.TANK_FILLING)).onReload();
            ((IReloadableRegistry<?>) FluidFuelRegister.instance).onReload();
        }
        if (ModSupport.THERMAL_EXPANSION.isLoaded()) {
            ModSupport.THERMAL_EXPANSION.getProperty(ThermalExpansion.class).Pulverizer.onReload();
        }
    }

    @ApiStatus.Internal
    public static void afterScriptRun() {
        if (ModSupport.ENDER_IO.isLoaded()) {
            ((IReloadableRegistry<?>) AlloyRecipeManager.getInstance()).afterScript();
        }
    }

    @ApiStatus.Internal
    public static void reloadForgeRegistry(IForgeRegistry<?> registry) {
        if (!(registry instanceof ForgeRegistry)) throw new IllegalArgumentException();
        ((IReloadableRegistry<?>) registry).onReload();
    }

    /**
     * Registers a reloadable entry to a forge registry
     *
     * @param registry registry to register too
     * @param value    value to register
     * @param <V>      type of the registry
     */
    public static <V extends IForgeRegistryEntry<V>> void registerEntry(IForgeRegistry<V> registry, V value) {
        boolean old = firstLoad.get();
        firstLoad.set(true);
        registry.register(value);
        firstLoad.set(old);
    }

    public static <V extends IForgeRegistryEntry<V>> void removeEntry(IForgeRegistry<V> registry, ResourceLocation rl, V dummy) {
        ((IReloadableRegistry<V>) registry).removeEntry(dummy.setRegistryName(rl));
    }

    public static void removeRecipe(String name) {
        removeEntry(ForgeRegistries.RECIPES, new ResourceLocation(name), new DummyRecipe());
    }

    /**
     * Reloads JEI completely. Is called after groovy scripts are re ran.
     */
    @ApiStatus.Internal
    @SideOnly(Side.CLIENT)
    public static void reloadJei() {
        if (ModSupport.JEI.isLoaded()) {
            JeiProxyAccessor jeiProxy = (JeiProxyAccessor) JustEnoughItems.getProxy();
            jeiProxy.getStarter().start(jeiProxy.getPlugins(), jeiProxy.getTextures());
        }
    }
}
