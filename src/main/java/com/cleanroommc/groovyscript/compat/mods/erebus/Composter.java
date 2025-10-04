package com.cleanroommc.groovyscript.compat.mods.erebus;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.cleanroommc.groovyscript.api.documentation.annotations.Admonition;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.erebus.ComposterRegistryAccessor;
import com.cleanroommc.groovyscript.registry.AbstractReloadableStorage;
import com.cleanroommc.groovyscript.registry.NamedRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;

import java.util.List;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES, admonition = {
        @Admonition(value = "groovyscript.wiki.erebus.composter.note0", type = Admonition.Type.BUG), @Admonition("groovyscript.wiki.erebus.composter.note1")
})
public class Composter extends NamedRegistry implements IScriptReloadable {

    private final AbstractReloadableStorage<Material> materialStorage = new AbstractReloadableStorage<>();
    private final AbstractReloadableStorage<ItemStack> registryStorage = new AbstractReloadableStorage<>();
    private final AbstractReloadableStorage<ItemStack> blacklistStorage = new AbstractReloadableStorage<>();

    private static List<Material> getMaterial() {
        return ComposterRegistryAccessor.getMaterial();
    }

    private static List<ItemStack> getRegistry() {
        return ComposterRegistryAccessor.getRegistry();
    }

    private static List<ItemStack> getBlacklist() {
        return ComposterRegistryAccessor.getBlacklist();
    }

    @Override
    public void onReload() {
        var material = getMaterial();
        var registry = getRegistry();
        var blacklist = getBlacklist();
        material.removeAll(materialStorage.removeScripted());
        material.addAll(materialStorage.restoreFromBackup());
        registry.removeAll(registryStorage.removeScripted());
        registry.addAll(registryStorage.restoreFromBackup());
        blacklist.removeAll(blacklistStorage.removeScripted());
        blacklist.addAll(blacklistStorage.restoreFromBackup());
    }

    @Override
    public void afterScriptLoad() {}

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("material('tnt')"))
    public boolean addMaterial(Material material) {
        return getMaterial().add(material) && materialStorage.addScripted(material);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:gold_ingot')"))
    public boolean addRegistry(ItemStack stack) {
        return getRegistry().add(stack) && registryStorage.addScripted(stack);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = {
            @Example("item('erebus:wall_plants', 7)"),
            @Example("item('erebus:wall_plants_cultivated', 7)")
    })
    public boolean addBlacklist(ItemStack stack) {
        return getBlacklist().add(stack) && blacklistStorage.addScripted(stack);
    }

    @MethodDescription(example = @Example("material('sponge')"))
    public boolean removeFromMaterial(Material material) {
        return getMaterial().removeIf(r -> material == r && materialStorage.addBackup(r));
    }

    @MethodDescription(example = @Example("item('minecraft:stick')"))
    public boolean removeFromRegistry(IIngredient ingredient) {
        return getRegistry().removeIf(r -> ingredient.test(r) && registryStorage.addBackup(r));
    }

    @MethodDescription(example = @Example(value = "item('erebus:wall_plants', 1)", commented = true))
    public boolean removeFromBlacklist(IIngredient ingredient) {
        return getBlacklist().removeIf(r -> ingredient.test(r) && blacklistStorage.addBackup(r));
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAllFromMaterial() {
        var entries = getMaterial();
        entries.forEach(materialStorage::addBackup);
        entries.clear();
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAllFromRegistry() {
        var entries = getRegistry();
        entries.forEach(registryStorage::addBackup);
        entries.clear();
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAllFromBlacklist() {
        var entries = getBlacklist();
        entries.forEach(blacklistStorage::addBackup);
        entries.clear();
    }
}
