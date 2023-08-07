package com.cleanroommc.groovyscript.compat.mods.appliedenergistics2;

import appeng.api.AEApi;
import appeng.api.config.TunnelType;
import com.cleanroommc.groovyscript.core.mixin.appliedenergistics2.P2PTunnelRegistryAccessor;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.stream.Collectors;

public class Attunement extends VirtualizedRegistry<Pair<Object, TunnelType>> {

    public Attunement() {
        super();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(pair -> {
            if (pair.getKey() instanceof ItemStack) {
                ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getTunnels().entrySet().removeIf(x -> x.getKey().isItemEqual((ItemStack) pair.getKey()) && x.getValue().equals(pair.getValue()));
            } else if (pair.getKey() instanceof String) {
                ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getModIdTunnels().entrySet().removeIf(x -> x.getKey().equals(pair.getKey()) && x.getValue().equals(pair.getValue()));
            } else if (pair.getKey() instanceof Capability<?>) {
                ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getCapTunnels().entrySet().removeIf(x -> x.getKey() == pair.getKey() && x.getValue().equals(pair.getValue()));
            }
        });
        restoreFromBackup().forEach(pair -> {
            if (pair.getKey() instanceof ItemStack) {
                AEApi.instance().registries().p2pTunnel().addNewAttunement((ItemStack) pair.getKey(), pair.getValue());
            } else if (pair.getKey() instanceof String) {
                AEApi.instance().registries().p2pTunnel().addNewAttunement((String) pair.getKey(), pair.getValue());
            } else if (pair.getKey() instanceof Capability<?>) {
                AEApi.instance().registries().p2pTunnel().addNewAttunement((Capability<?>) pair.getKey(), pair.getValue());
            }
        });
    }

    public void add(ItemStack item, TunnelType tunnel) {
        addScripted(Pair.of(item, tunnel));
        AEApi.instance().registries().p2pTunnel().addNewAttunement(item, tunnel);
    }

    public void add(String modid, TunnelType tunnel) {
        addScripted(Pair.of(modid, tunnel));
        AEApi.instance().registries().p2pTunnel().addNewAttunement(modid, tunnel);
    }

    public void add(Capability<?> capability, TunnelType tunnel) {
        addScripted(Pair.of(capability, tunnel));
        AEApi.instance().registries().p2pTunnel().addNewAttunement(capability, tunnel);
    }

    public void remove(ItemStack item, TunnelType tunnel) {
        addBackup(Pair.of(item, tunnel));
        ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getTunnels().entrySet().removeIf(x -> x.getKey().isItemEqual(item) && x.getValue().equals(tunnel));
    }

    public void remove(String modid, TunnelType tunnel) {
        addBackup(Pair.of(modid, tunnel));
        ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getModIdTunnels().entrySet().removeIf(x -> x.getKey().equals(modid) && x.getValue().equals(tunnel));
    }

    public void remove(Capability<?> capability, TunnelType tunnel) {
        addBackup(Pair.of(capability, tunnel));
        ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getCapTunnels().entrySet().removeIf(x -> x.getKey() == capability && x.getValue().equals(tunnel));
    }

    public void removeByItem(ItemStack item) {
        for (Map.Entry<ItemStack, TunnelType> pair : ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getTunnels().entrySet()
                .stream()
                .filter(x -> x.getKey().isItemEqual(item))
                .collect(Collectors.toList())) {
            addBackup(Pair.of(pair.getKey(), pair.getValue()));
            ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getTunnels().entrySet().removeIf(x -> x.getKey().isItemEqual(pair.getKey()));
        }
    }

    public void removeByMod(String modid) {
        for (Map.Entry<String, TunnelType> pair : ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getModIdTunnels().entrySet()
                .stream()
                .filter(x -> x.getKey().equals(modid))
                .collect(Collectors.toList())) {
            addBackup(Pair.of(pair.getKey(), pair.getValue()));
            ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getModIdTunnels().entrySet().removeIf(x -> x.getKey().equals(pair.getKey()));
        }
    }

    public void removeByMod(Capability<?> capability) {
        for (Map.Entry<Capability<?>, TunnelType> pair : ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getCapTunnels().entrySet()
                .stream()
                .filter(x -> x.getKey() == capability)
                .collect(Collectors.toList())) {
            addBackup(Pair.of(pair.getKey(), pair.getValue()));
            ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getCapTunnels().entrySet().removeIf(x -> x.getKey() == pair.getKey());
        }
    }

    public void removeByTunnel(TunnelType tunnel) {
        for (Map.Entry<ItemStack, TunnelType> pair : ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getTunnels().entrySet()
                .stream()
                .filter(x -> x.getValue().equals(tunnel))
                .collect(Collectors.toList())) {
            addBackup(Pair.of(pair.getKey(), pair.getValue()));
            ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getTunnels().entrySet().removeIf(x -> x.getKey().isItemEqual(pair.getKey()));
        }
        for (Map.Entry<String, TunnelType> pair : ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getModIdTunnels().entrySet()
                .stream()
                .filter(x -> x.getValue().equals(tunnel))
                .collect(Collectors.toList())) {
            addBackup(Pair.of(pair.getKey(), pair.getValue()));
            ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getModIdTunnels().entrySet().removeIf(x -> x.getKey().equals(pair.getKey()));
        }
        for (Map.Entry<Capability<?>, TunnelType> pair : ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getCapTunnels().entrySet()
                .stream()
                .filter(x -> x.getValue().equals(tunnel))
                .collect(Collectors.toList())) {
            addBackup(Pair.of(pair.getKey(), pair.getValue()));
            ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getCapTunnels().entrySet().removeIf(x -> x.getKey() == pair.getKey());
        }
    }

    public void removeAll() {
        ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getTunnels().forEach((item, value) -> addBackup(Pair.of(item, value)));
        ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getTunnels().clear();
        ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getModIdTunnels().forEach((item, value) -> addBackup(Pair.of(item, value)));
        ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getModIdTunnels().clear();
        ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getCapTunnels().forEach((item, value) -> addBackup(Pair.of(item, value)));
        ((P2PTunnelRegistryAccessor) AEApi.instance().registries().p2pTunnel()).getCapTunnels().clear();
    }

}
