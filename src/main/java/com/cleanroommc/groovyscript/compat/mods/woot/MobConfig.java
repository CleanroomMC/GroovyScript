package com.cleanroommc.groovyscript.compat.mods.woot;

import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.woot.WootConfigurationManagerAccessor;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import ipsis.Woot;
import ipsis.woot.configuration.EnumConfigKey;
import ipsis.woot.util.WootMobName;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@RegistryDescription
public class MobConfig extends VirtualizedRegistry<Pair<String, Integer>> {

    @Override
    public void onReload() {
        restoreFromBackup().forEach(pair -> ((WootConfigurationManagerAccessor) Woot.wootConfiguration).getIntegerMobMap().put(pair.getKey(), pair.getValue()));
        removeScripted().forEach(pair -> {
            if (pair.getKey().contains(":")) ((WootConfigurationManagerAccessor) Woot.wootConfiguration).getIntegerMobMap().remove(pair.getKey());
            else ((WootConfigurationManagerAccessor) Woot.wootConfiguration).getIntegerMap().put(EnumConfigKey.get(pair.getKey()), pair.getValue());
        });
    }

    @MethodDescription(description = "groovyscript.wiki.woot.mob_config.add_normal", type = MethodDescription.Type.ADDITION)
    public void add(WootMobName name, EnumConfigKey key, int value) {
        String target = ((WootConfigurationManagerAccessor) Woot.wootConfiguration).callMakeKey(name, key);
        addScripted(Pair.of(target, ((WootConfigurationManagerAccessor) Woot.wootConfiguration).getIntegerMobMap().get(target)));
        ((WootConfigurationManagerAccessor) Woot.wootConfiguration).getIntegerMobMap().put(target, value);
    }

    @MethodDescription(description = "groovyscript.wiki.woot.mob_config.add_normal", type = MethodDescription.Type.ADDITION)
    public void add(String name, EnumConfigKey key, int value) {
        add(new WootMobName(name), key, value);
    }

    @MethodDescription(description = "groovyscript.wiki.woot.mob_config.add_normal", type = MethodDescription.Type.ADDITION)
    public void add(WootMobName name, String key, int value) {
        add(name, EnumConfigKey.get(key.toUpperCase(Locale.ROOT)), value);
    }

    @MethodDescription(description = "groovyscript.wiki.woot.mob_config.add_normal", type = MethodDescription.Type.ADDITION, example = @Example("'minecraft:zombie', 'spawn_ticks', 1"))
    public void add(String name, String key, int value) {
        add(new WootMobName(name), EnumConfigKey.get(key.toUpperCase(Locale.ROOT)), value);
    }

    // Note: there is no remove method for the IntegerMap since all values must be defined
    @MethodDescription(description = "groovyscript.wiki.woot.mob_config.add_global", type = MethodDescription.Type.ADDITION)
    public void add(EnumConfigKey key, int value) {
        addScripted(Pair.of(key.name(), ((WootConfigurationManagerAccessor) Woot.wootConfiguration).getIntegerMap().get(key)));
        ((WootConfigurationManagerAccessor) Woot.wootConfiguration).getIntegerMap().put(key, value);
    }

    @MethodDescription(description = "groovyscript.wiki.woot.mob_config.add_global", type = MethodDescription.Type.ADDITION, example = @Example("'spawn_ticks', 100"))
    public void add(String key, int value) {
        add(EnumConfigKey.get(key.toUpperCase(Locale.ROOT)), value);
    }

    @MethodDescription(description = "groovyscript.wiki.woot.mob_config.remove")
    public void remove(WootMobName name, EnumConfigKey key) {
        String target = ((WootConfigurationManagerAccessor) Woot.wootConfiguration).callMakeKey(name, key);
        addBackup(Pair.of(target, ((WootConfigurationManagerAccessor) Woot.wootConfiguration).getIntegerMobMap().get(target)));
        ((WootConfigurationManagerAccessor) Woot.wootConfiguration).getIntegerMobMap().remove(target);
    }

    @MethodDescription(description = "groovyscript.wiki.woot.mob_config.remove")
    public void remove(String name, EnumConfigKey key) {
        remove(new WootMobName(name), key);
    }

    @MethodDescription(description = "groovyscript.wiki.woot.mob_config.remove")
    public void remove(WootMobName name, String key) {
        remove(name, EnumConfigKey.get(key.toUpperCase(Locale.ROOT)));
    }

    @MethodDescription(description = "groovyscript.wiki.woot.mob_config.remove", example = @Example("'minecraft:wither_skeleton', 'spawn_units'"))
    public void remove(String name, String key) {
        remove(new WootMobName(name), EnumConfigKey.get(key.toUpperCase(Locale.ROOT)));
    }

    @MethodDescription(description = "groovyscript.wiki.woot.mob_config.removeByEntity", example = @Example("'minecraft:wither'"))
    public void remove(WootMobName name) {
        for (Map.Entry<String, Integer> entry : ((WootConfigurationManagerAccessor) Woot.wootConfiguration).getIntegerMobMap().entrySet().stream()
                .filter(x -> x.getKey().startsWith(name.toString()))
                .collect(Collectors.toList())) {
            addBackup(Pair.of(entry.getKey(), entry.getValue()));
            ((WootConfigurationManagerAccessor) Woot.wootConfiguration).getIntegerMobMap().remove(entry.getKey());
        }
    }


    @MethodDescription(description = "groovyscript.wiki.woot.mob_config.removeByEntity")
    public void remove(String name) {
        remove(new WootMobName(name));
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ((WootConfigurationManagerAccessor) Woot.wootConfiguration).getIntegerMobMap().forEach((key, value) -> addBackup(Pair.of(key, value)));
        ((WootConfigurationManagerAccessor) Woot.wootConfiguration).getIntegerMobMap().clear();
    }
}
