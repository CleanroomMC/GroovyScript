package com.cleanroommc.groovyscript.compat.mods.appliedenergistics2;

import appeng.api.AEApi;
import appeng.api.config.TunnelType;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.appliedenergistics2.P2PTunnelRegistryAccessor;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.stream.Collectors;

@RegistryDescription
public class Attunement extends VirtualizedRegistry<Pair<Object, TunnelType>> {

    @Override
    public void onReload() {
        removeScripted().forEach(pair -> {
            if (pair.getKey() instanceof ItemStack) {
                ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getTunnels().entrySet().removeIf(x -> x.getKey().isItemEqual((ItemStack) pair.getKey()) && x.getValue() == pair.getValue());
            } else if (pair.getKey() instanceof String) {
                ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getModIdTunnels().entrySet().removeIf(x -> x.getKey().equals(pair.getKey()) && x.getValue() == pair.getValue());
            } else if (pair.getKey() instanceof Capability<?>) {
                ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getCapTunnels().entrySet().removeIf(x -> x.getKey() == pair.getKey() && x.getValue() == pair.getValue());
            }
        });
        restoreFromBackup().forEach(pair -> {
            if (pair.getKey() instanceof ItemStack itemStack) {
                AEApi.instance().registries().p2pTunnel().addNewAttunement(itemStack, pair.getValue());
            } else if (pair.getKey() instanceof String s) {
                AEApi.instance().registries().p2pTunnel().addNewAttunement(s, pair.getValue());
            } else if (pair.getKey() instanceof Capability<?>capability) {
                AEApi.instance().registries().p2pTunnel().addNewAttunement(capability, pair.getValue());
            }
        });
    }

    @MethodDescription(description = "groovyscript.wiki.appliedenergistics2.attunement.add0", type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:clay'), tunnel('item')"))
    public void add(ItemStack item, TunnelType tunnel) {
        addScripted(Pair.of(item, tunnel));
        AEApi.instance().registries().p2pTunnel().addNewAttunement(item, tunnel);
    }

    @MethodDescription(description = "groovyscript.wiki.appliedenergistics2.attunement.add1", type = MethodDescription.Type.ADDITION, example = @Example("'thermaldynamics', tunnel('redstone')"))
    public void add(String modid, TunnelType tunnel) {
        addScripted(Pair.of(modid, tunnel));
        AEApi.instance().registries().p2pTunnel().addNewAttunement(modid, tunnel);
    }

    @MethodDescription(description = "groovyscript.wiki.appliedenergistics2.attunement.add2", type = MethodDescription.Type.ADDITION, example = @Example(value = "Capabilities.FORGE_ENERGY, tunnel('item')", imports = "appeng.capabilities.Capabilities"))
    public void add(Capability<?> capability, TunnelType tunnel) {
        addScripted(Pair.of(capability, tunnel));
        AEApi.instance().registries().p2pTunnel().addNewAttunement(capability, tunnel);
    }

    @MethodDescription(description = "groovyscript.wiki.appliedenergistics2.attunement.remove0", example = @Example("item('minecraft:lever'), tunnel('redstone')"))
    public void remove(ItemStack item, TunnelType tunnel) {
        addBackup(Pair.of(item, tunnel));
        ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getTunnels().entrySet().removeIf(x -> x.getKey().isItemEqual(item) && x.getValue() == tunnel);
    }

    @MethodDescription(description = "groovyscript.wiki.appliedenergistics2.attunement.remove1", example = @Example("'thermaldynamics', tunnel('fe_power')"))
    public void remove(String modid, TunnelType tunnel) {
        addBackup(Pair.of(modid, tunnel));
        ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getModIdTunnels().entrySet().removeIf(x -> x.getKey().equals(modid) && x.getValue() == tunnel);
    }

    @MethodDescription(description = "groovyscript.wiki.appliedenergistics2.attunement.remove2", example = @Example(value = "Capabilities.FORGE_ENERGY, tunnel('fe_power')", imports = "appeng.capabilities.Capabilities"))
    public void remove(Capability<?> capability, TunnelType tunnel) {
        addBackup(Pair.of(capability, tunnel));
        ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getCapTunnels().entrySet().removeIf(x -> x.getKey() == capability && x.getValue() == tunnel);
    }

    @MethodDescription
    public void removeByItem(ItemStack item) {
        for (Map.Entry<ItemStack, TunnelType> pair : ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getTunnels()
                .entrySet()
                .stream()
                .filter(x -> x.getKey().isItemEqual(item))
                .collect(Collectors.toList())) {
            addBackup(Pair.of(pair.getKey(), pair.getValue()));
            ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getTunnels().entrySet().removeIf(x -> x.getKey().isItemEqual(pair.getKey()));
        }
    }

    @MethodDescription
    public void removeByMod(String modid) {
        for (Map.Entry<String, TunnelType> pair : ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getModIdTunnels()
                .entrySet()
                .stream()
                .filter(x -> x.getKey().equals(modid))
                .collect(Collectors.toList())) {
            addBackup(Pair.of(pair.getKey(), pair.getValue()));
            ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getModIdTunnels().entrySet().removeIf(x -> x.getKey().equals(pair.getKey()));
        }
    }

    @MethodDescription
    public void removeByCapability(Capability<?> capability) {
        for (Map.Entry<Capability<?>, TunnelType> pair : ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getCapTunnels()
                .entrySet()
                .stream()
                .filter(x -> x.getKey() == capability)
                .collect(Collectors.toList())) {
            addBackup(Pair.of(pair.getKey(), pair.getValue()));
            ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getCapTunnels().entrySet().removeIf(x -> x.getKey() == pair.getKey());
        }
    }

    @MethodDescription(example = @Example("tunnel('item')"))
    public void removeByTunnel(TunnelType tunnel) {
        for (Map.Entry<ItemStack, TunnelType> pair : ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getTunnels()
                .entrySet()
                .stream()
                .filter(x -> x.getValue() == tunnel)
                .collect(Collectors.toList())) {
            addBackup(Pair.of(pair.getKey(), pair.getValue()));
            ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getTunnels().entrySet().removeIf(x -> x.getKey().isItemEqual(pair.getKey()));
        }
        for (Map.Entry<String, TunnelType> pair : ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getModIdTunnels()
                .entrySet()
                .stream()
                .filter(x -> x.getValue() == tunnel)
                .collect(Collectors.toList())) {
            addBackup(Pair.of(pair.getKey(), pair.getValue()));
            ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getModIdTunnels().entrySet().removeIf(x -> x.getKey().equals(pair.getKey()));
        }
        for (Map.Entry<Capability<?>, TunnelType> pair : ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getCapTunnels()
                .entrySet()
                .stream()
                .filter(x -> x.getValue() == tunnel)
                .collect(Collectors.toList())) {
            addBackup(Pair.of(pair.getKey(), pair.getValue()));
            ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getCapTunnels().entrySet().removeIf(x -> x.getKey() == pair.getKey());
        }
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getTunnels().forEach((item, value) -> addBackup(Pair.of(item, value)));
        ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getTunnels().clear();
        ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getModIdTunnels().forEach((item, value) -> addBackup(Pair.of(item, value)));
        ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getModIdTunnels().clear();
        ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getCapTunnels().forEach((item, value) -> addBackup(Pair.of(item, value)));
        ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getCapTunnels().clear();
    }
}
