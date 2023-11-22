package com.cleanroommc.groovyscript.compat.mods.appliedenergistics2;

import appeng.api.AEApi;
import com.cleanroommc.groovyscript.core.mixin.appliedenergistics2.MatterCannonAmmoRegistryAccessor;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

public class CannonAmmo extends VirtualizedRegistry<Pair<ItemStack, Double>> {

    public CannonAmmo() {
        super(Alias.generateOf("Cannon"));
    }

    @Override
    public void onReload() {
        removeScripted().forEach(pair -> ((MatterCannonAmmoRegistryAccessor) AEApi.instance().registries().matterCannon()).getDamageModifiers().entrySet().removeIf(x -> ItemStack.areItemStacksEqual(x.getKey(), pair.getKey())));
        restoreFromBackup().forEach(pair -> ((MatterCannonAmmoRegistryAccessor) AEApi.instance().registries().matterCannon()).getDamageModifiers().put(pair.getKey(), pair.getValue()));
    }

    public void add(ItemStack item, double value) {
        addScripted(Pair.of(item, value));
        ((MatterCannonAmmoRegistryAccessor) AEApi.instance().registries().matterCannon()).getDamageModifiers().put(item, value);
    }

    public void remove(ItemStack item) {
        addBackup(Pair.of(item, ((MatterCannonAmmoRegistryAccessor) AEApi.instance().registries().matterCannon()).getDamageModifiers().get(item)));
        ((MatterCannonAmmoRegistryAccessor) AEApi.instance().registries().matterCannon()).getDamageModifiers().entrySet().removeIf(x -> ItemStack.areItemStacksEqual(x.getKey(), item));
    }

    public void removeAll() {
        ((MatterCannonAmmoRegistryAccessor) AEApi.instance().registries().matterCannon()).getDamageModifiers().forEach((item, value) -> addBackup(Pair.of(item, value)));
        ((MatterCannonAmmoRegistryAccessor) AEApi.instance().registries().matterCannon()).getDamageModifiers().clear();
    }

}
