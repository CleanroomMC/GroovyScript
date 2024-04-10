package com.cleanroommc.groovyscript.compat.mods.woot;

import com.cleanroommc.groovyscript.api.documentation.annotations.Admonition;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.woot.PolicyRepositoryAccessor;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import ipsis.Woot;
import ipsis.woot.util.WootMobName;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

@RegistryDescription(
        admonition = @Admonition(value = "groovyscript.wiki.woot.policy.note", type = Admonition.Type.WARNING)
)
public class Policy extends VirtualizedRegistry<Pair<Policy.PolicyType, Object>> {

    @Override
    public void onReload() {
        restoreFromBackup().forEach(pair -> {
            switch (pair.getKey()) {
                case ENTITY_MOD_BLACKLIST:
                    ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalModBlacklist().add((String) pair.getValue());
                    break;
                case ENTITY_BLACKLIST:
                    ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalEntityBlacklist().add((WootMobName) pair.getValue());
                    break;
                case ITEM_MOD_BLACKLIST:
                    ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalItemModBlacklist().add((String) pair.getValue());
                    break;
                case ITEM_BLACKLIST:
                    ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalItemBlacklist().add((ItemStack) pair.getValue());
                    break;
                case ENTITY_WHITELIST:
                    ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalEntityWhitelist().add((WootMobName) pair.getValue());
                    break;
                case GENERATE_ONLY_LIST:
                    ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalGenerateOnlyList().add((WootMobName) pair.getValue());
                    break;
            }
        });
        removeScripted().forEach(pair -> {
            switch (pair.getKey()) {
                case ENTITY_MOD_BLACKLIST:
                    ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalModBlacklist().remove((String) pair.getValue());
                    break;
                case ENTITY_BLACKLIST:
                    ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalEntityBlacklist().remove((WootMobName) pair.getValue());
                    break;
                case ITEM_MOD_BLACKLIST:
                    ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalItemModBlacklist().remove((String) pair.getValue());
                    break;
                case ITEM_BLACKLIST:
                    ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalItemBlacklist().remove((ItemStack) pair.getValue());
                    break;
                case ENTITY_WHITELIST:
                    ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalEntityWhitelist().remove((WootMobName) pair.getValue());
                    break;
                case GENERATE_ONLY_LIST:
                    ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalGenerateOnlyList().remove((WootMobName) pair.getValue());
                    break;
            }
        });
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example(value = "'minecraft'", commented = true))
    public void addToEntityModBlacklist(String name) {
        ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalModBlacklist().add(name);
        addScripted(Pair.of(PolicyType.ENTITY_MOD_BLACKLIST, name));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void addToEntityBlacklist(WootMobName name) {
        ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalEntityBlacklist().add(name);
        addScripted(Pair.of(PolicyType.ENTITY_BLACKLIST, name));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'minecraft:witch'"))
    public void addToEntityBlacklist(String name) {
        addToEntityBlacklist(new WootMobName(name));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'woot'"))
    public void addToItemModBlacklist(String name) {
        ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalItemModBlacklist().add(name);
        addScripted(Pair.of(PolicyType.ITEM_MOD_BLACKLIST, name));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:gunpowder')"))
    public void addToItemBlacklist(ItemStack item) {
        ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalItemBlacklist().add(item);
        addScripted(Pair.of(PolicyType.ITEM_BLACKLIST, item));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void addToEntityWhitelist(WootMobName name) {
        ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalEntityWhitelist().add(name);
        addScripted(Pair.of(PolicyType.ENTITY_WHITELIST, name));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example(value = "'minecraft:zombie'", commented = true))
    public void addToEntityWhitelist(String name) {
        addToEntityWhitelist(new WootMobName(name));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void addToGenerateOnlyList(WootMobName name) {
        ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalGenerateOnlyList().add(name);
        addScripted(Pair.of(PolicyType.GENERATE_ONLY_LIST, name));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'minecraft:skeleton'"))
    public void addToGenerateOnlyList(String name) {
        addToGenerateOnlyList(new WootMobName(name));
    }

    @MethodDescription(example = @Example("'botania'"))
    public void removeFromEntityModBlacklist(String name) {
        ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalModBlacklist().remove(name);
        addBackup(Pair.of(PolicyType.ENTITY_MOD_BLACKLIST, name));
    }

    @MethodDescription
    public void removeFromEntityBlacklist(WootMobName name) {
        ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalEntityBlacklist().remove(name);
        addBackup(Pair.of(PolicyType.ENTITY_BLACKLIST, name));
    }

    @MethodDescription(example = @Example("'twilightforest:naga'"))
    public void removeFromEntityBlacklist(String name) {
        removeFromEntityBlacklist(new WootMobName(name));
    }

    @MethodDescription(example = @Example("'minecraft'"))
    public void removeFromItemModBlacklist(String name) {
        ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalItemModBlacklist().remove(name);
        addBackup(Pair.of(PolicyType.ITEM_MOD_BLACKLIST, name));
    }

    @MethodDescription(example = @Example(value = "item('minecraft:sugar')", commented = true))
    public void removeFromItemBlacklist(ItemStack item) {
        ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalItemBlacklist().remove(item);
        addBackup(Pair.of(PolicyType.ITEM_BLACKLIST, item));
    }

    @MethodDescription
    public void removeFromEntityWhitelist(WootMobName name) {
        ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalEntityWhitelist().remove(name);
        addBackup(Pair.of(PolicyType.ENTITY_WHITELIST, name));
    }

    @MethodDescription(example = @Example(value = "'minecraft:wither_skeleton'", commented = true))
    public void removeFromEntityWhitelist(String name) {
        removeFromEntityWhitelist(new WootMobName(name));
    }

    @MethodDescription
    public void removeFromGenerateOnlyList(WootMobName name) {
        ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalGenerateOnlyList().remove(name);
        addBackup(Pair.of(PolicyType.GENERATE_ONLY_LIST, name));
    }

    @MethodDescription(example = @Example(value = "'minecraft:wither_skeleton'", commented = true))
    public void removeFromGenerateOnlyList(String name) {
        removeFromGenerateOnlyList(new WootMobName(name));
    }

    @MethodDescription(priority = 1500, example = @Example(commented = true))
    public void removeAllFromEntityModBlacklist() {
        ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalModBlacklist().forEach(x -> addBackup(Pair.of(PolicyType.ENTITY_MOD_BLACKLIST, x)));
        ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalModBlacklist().clear();
    }

    @MethodDescription(priority = 1500, example = @Example(commented = true))
    public void removeAllFromEntityBlacklist() {
        ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalEntityBlacklist().forEach(x -> addBackup(Pair.of(PolicyType.ENTITY_BLACKLIST, x)));
        ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalEntityBlacklist().clear();
    }

    @MethodDescription(priority = 1500, example = @Example(commented = true))
    public void removeAllFromItemModBlacklist() {
        ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalItemModBlacklist().forEach(x -> addBackup(Pair.of(PolicyType.ITEM_MOD_BLACKLIST, x)));
        ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalItemModBlacklist().clear();
    }

    @MethodDescription(priority = 1500, example = @Example(commented = true))
    public void removeAllFromItemBlacklist() {
        ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalItemBlacklist().forEach(x -> addBackup(Pair.of(PolicyType.ITEM_BLACKLIST, x)));
        ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalItemBlacklist().clear();
    }

    @MethodDescription(priority = 1500, example = @Example(commented = true))
    public void removeAllFromEntityWhitelist() {
        ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalEntityWhitelist().forEach(x -> addBackup(Pair.of(PolicyType.ENTITY_WHITELIST, x)));
        ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalEntityWhitelist().clear();
    }

    @MethodDescription(priority = 1500, example = @Example(commented = true))
    public void removeAllFromGenerateOnlyList() {
        ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalGenerateOnlyList().forEach(x -> addBackup(Pair.of(PolicyType.GENERATE_ONLY_LIST, x)));
        ((PolicyRepositoryAccessor) Woot.policyRepository).getExternalGenerateOnlyList().clear();
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        removeAllFromEntityModBlacklist();
        removeAllFromEntityBlacklist();
        removeAllFromItemModBlacklist();
        removeAllFromItemBlacklist();
        removeAllFromEntityWhitelist();
        removeAllFromGenerateOnlyList();
    }

    public enum PolicyType {
        ENTITY_MOD_BLACKLIST,//getExternalModBlacklist
        ENTITY_BLACKLIST,//getExternalEntityBlacklist
        ITEM_MOD_BLACKLIST,//getExternalItemModBlacklist
        ITEM_BLACKLIST,//getExternalItemBlacklist
        ENTITY_WHITELIST,//getExternalEntityWhitelist
        GENERATE_ONLY_LIST//getExternalGenerateOnlyList
    }

}
