package com.cleanroommc.groovyscript.registry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.mixin.JeiProxyAccessor;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.alloysmelter.AlloyRecipeManager;
import crazypants.enderio.base.recipe.sagmill.SagMillRecipeManager;
import mekanism.common.recipe.RecipeHandler;
import mezz.jei.JustEnoughItems;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.ApiStatus;

@GroovyBlacklist
public class ReloadableRegistryManager {

    private static boolean shouldRegisterAsReloadable = false;

    public static boolean isShouldRegisterAsReloadable() {
        return shouldRegisterAsReloadable;
    }

    public static void setShouldRegisterAsReloadable(boolean shouldRegisterAsReloadable) {
        ReloadableRegistryManager.shouldRegisterAsReloadable = shouldRegisterAsReloadable;
    }

    @ApiStatus.Internal
    public static void onReload() {
        reloadRegistry(ForgeRegistries.RECIPES);
        if (Loader.isModLoaded("mekanism")) {
            RecipeHandler.Recipe.values().forEach(recipe -> ((IReloadableRegistry<?>) recipe).onReload());
        }
        if (Loader.isModLoaded("enderio")) {
            ((IReloadableRegistry<?>) AlloyRecipeManager.getInstance()).onReload();
            ((IReloadableRegistry<?>) (Object) SagMillRecipeManager.getInstance()).onReload();
            ((IReloadableRegistry<?>) MachineRecipeRegistry.instance.getRecipeHolderssForMachine(MachineRecipeRegistry.SOULBINDER)).onReload();
            ((IReloadableRegistry<?>) MachineRecipeRegistry.instance.getRecipeHolderssForMachine(MachineRecipeRegistry.ENCHANTER)).onReload();
            ((IReloadableRegistry<?>) MachineRecipeRegistry.instance.getRecipeHolderssForMachine(MachineRecipeRegistry.TANK_EMPTYING)).onReload();
            ((IReloadableRegistry<?>) MachineRecipeRegistry.instance.getRecipeHolderssForMachine(MachineRecipeRegistry.TANK_FILLING)).onReload();
        }
    }

    @ApiStatus.Internal
    public static void afterScriptRun() {
        if (Loader.isModLoaded("enderio")) {
            ((IReloadableRegistry<?>) AlloyRecipeManager.getInstance()).afterScript();
        }
    }

    public static void reloadRegistry(IForgeRegistry<?> registry) {
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
        boolean old = isShouldRegisterAsReloadable();
        setShouldRegisterAsReloadable(true);
        registry.register(value);
        setShouldRegisterAsReloadable(old);
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
        if (Loader.isModLoaded("jei")) {
            JeiProxyAccessor jeiProxy = (JeiProxyAccessor) JustEnoughItems.getProxy();
            jeiProxy.getStarter().start(jeiProxy.getPlugins(), jeiProxy.getTextures());
        }
    }
}
