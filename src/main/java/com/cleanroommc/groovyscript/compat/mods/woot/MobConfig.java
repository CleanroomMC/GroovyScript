package com.cleanroommc.groovyscript.compat.mods.woot;

import com.cleanroommc.groovyscript.core.mixin.woot.WootConfigurationManagerAccessor;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import ipsis.Woot;
import ipsis.woot.configuration.EnumConfigKey;
import ipsis.woot.util.WootMobName;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class MobConfig extends VirtualizedRegistry<Pair<String, Integer>> {

    public MobConfig() {
        super();
    }

    @Override
    public void onReload() {
        restoreFromBackup().forEach(pair -> ((WootConfigurationManagerAccessor) Woot.wootConfiguration).getIntegerMobMap().put(pair.getKey(), pair.getValue()));
        removeScripted().forEach(pair -> {
            if (pair.getKey().contains(":")) ((WootConfigurationManagerAccessor) Woot.wootConfiguration).getIntegerMobMap().remove(pair.getKey());
            else ((WootConfigurationManagerAccessor) Woot.wootConfiguration).getIntegerMap().put(EnumConfigKey.get(pair.getKey()), pair.getValue());
        });
    }

    public void add(WootMobName name, EnumConfigKey key, int value) {
        String target = ((WootConfigurationManagerAccessor) Woot.wootConfiguration).callMakeKey(name, key);
        addScripted(Pair.of(target, ((WootConfigurationManagerAccessor) Woot.wootConfiguration).getIntegerMobMap().get(target)));
        ((WootConfigurationManagerAccessor) Woot.wootConfiguration).getIntegerMobMap().put(target, value);
    }

    public void add(String name, EnumConfigKey key, int value) {
        add(new WootMobName(name), key, value);
    }

    public void add(WootMobName name, String key, int value) {
        add(name, EnumConfigKey.get(key.toUpperCase(Locale.ROOT)), value);
    }

    public void add(String name, String key, int value) {
        add(new WootMobName(name), EnumConfigKey.get(key.toUpperCase(Locale.ROOT)), value);
    }

    // Note: there is no remove method for the IntegerMap since all values must be defined
    public void add(EnumConfigKey key, int value) {
        addScripted(Pair.of(key.name(), ((WootConfigurationManagerAccessor) Woot.wootConfiguration).getIntegerMap().get(key)));
        ((WootConfigurationManagerAccessor) Woot.wootConfiguration).getIntegerMap().put(key, value);
    }

    public void add(String key, int value) {
        add(EnumConfigKey.get(key.toUpperCase(Locale.ROOT)), value);
    }

    public void remove(WootMobName name, EnumConfigKey key) {
        String target = ((WootConfigurationManagerAccessor) Woot.wootConfiguration).callMakeKey(name, key);
        addBackup(Pair.of(target, ((WootConfigurationManagerAccessor) Woot.wootConfiguration).getIntegerMobMap().get(target)));
        ((WootConfigurationManagerAccessor) Woot.wootConfiguration).getIntegerMobMap().remove(target);
    }

    public void remove(String name, EnumConfigKey key) {
        remove(new WootMobName(name), key);
    }

    public void remove(WootMobName name, String key) {
        remove(name, EnumConfigKey.get(key.toUpperCase(Locale.ROOT)));
    }

    public void remove(String name, String key) {
        remove(new WootMobName(name), EnumConfigKey.get(key.toUpperCase(Locale.ROOT)));
    }

    public void remove(WootMobName name) {
        for (Map.Entry<String, Integer> entry : ((WootConfigurationManagerAccessor) Woot.wootConfiguration).getIntegerMobMap().entrySet().stream()
                .filter(x -> x.getKey().startsWith(name.toString()))
                .collect(Collectors.toList())) {
            addBackup(Pair.of(entry.getKey(), entry.getValue()));
            ((WootConfigurationManagerAccessor) Woot.wootConfiguration).getIntegerMobMap().remove(entry.getKey());
        }
    }


    public void remove(String name) {
        remove(new WootMobName(name));
    }

    public void removeAll() {
        ((WootConfigurationManagerAccessor) Woot.wootConfiguration).getIntegerMobMap().forEach((key, value) -> addBackup(Pair.of(key, value)));
        ((WootConfigurationManagerAccessor) Woot.wootConfiguration).getIntegerMobMap().clear();
    }
}
