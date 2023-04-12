package com.cleanroommc.groovyscript.compat.mods.astralsorcery;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.core.mixin.astralsorcery.OreTypesAccessor;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import hellfirepvp.astralsorcery.common.base.OreTypes;
import hellfirepvp.astralsorcery.common.base.sets.OreEntry;
import org.jetbrains.annotations.ApiStatus;

public class OreChance extends VirtualizedRegistry<OreEntry> {

    private final OreTypesAccessor REGISTRY;

    public OreChance(OreTypes registry) {
        super();
        this.REGISTRY = (OreTypesAccessor) registry;
    }

    public static OreChance mineralisRitualRegistry() {
        return new OreChance(OreTypes.RITUAL_MINERALIS);
    }

    public static OreChance aevitasPerkRegistry() {
        return new OreChance(OreTypes.AEVITAS_ORE_PERK);
    }

    public static OreChance trashPerkRegistry() {
        return new OreChance(OreTypes.PERK_VOID_TRASH_REPLACEMENT);
    }

    public static OreChance treasureShrineRegistry() {
        return new OreChance(OreTypes.TREASURE_SHRINE_GEN);
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(this::remove);
        restoreFromBackup().forEach(this::add);
    }

    public void add(OreEntry entry) {
        this.REGISTRY.add(entry);
    }

    public void add(OreDictIngredient ore, int weight) {
        if (ore == null || ore.getOreDict() == null || ore.getOreDict().equals("")) {
            GroovyLog.msg("Error adding Astral Sorcery OreChance. Ore name cannot be null.").error().post();
            return;
        }
        this.add(ore.getOreDict(), weight);
    }

    public void add(String ore, int weight) {
        if (weight <= 0) {
            GroovyLog.msg("Error adding Astral Sorcery OreChance. Weight must be a positive integer.").error().post();
            return;
        }
        if (ore == null || ore.equals("")) {
            GroovyLog.msg("Error adding Astral Sorcery OreChance. Ore name cannot be null.").error().post();
            return;
        }
        OreEntry newOE = new OreEntry(ore, weight);
        this.addScripted(newOE);
        this.add(newOE);
    }

    public void remove(OreEntry entry) {
        this.REGISTRY.getEntries().forEach(registeredEntry -> {
            if (registeredEntry.oreName.equals(entry.oreName))
                this.REGISTRY.setTotalWeight(this.REGISTRY.getTotalWeight() - registeredEntry.weight);
        });
        this.REGISTRY.getEntries().removeIf(registeredEntry -> registeredEntry.oreName.equals(entry.oreName));
    }

    public void remove(OreDictIngredient entry) {
        this.remove(entry.getOreDict());
    }

    public void remove(String ore) {
        this.REGISTRY.getEntries().forEach(registeredEntry -> {
            if (registeredEntry.oreName.equals(ore)) {
                this.addBackup(registeredEntry);
                this.REGISTRY.setTotalWeight(this.REGISTRY.getTotalWeight() - registeredEntry.weight);
            }
        });
        this.REGISTRY.getEntries().removeIf(registeredEntry -> registeredEntry.oreName.equals(ore));
    }

}
