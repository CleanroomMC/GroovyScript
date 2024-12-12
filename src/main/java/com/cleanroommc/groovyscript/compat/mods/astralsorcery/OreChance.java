package com.cleanroommc.groovyscript.compat.mods.astralsorcery;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.astralsorcery.OreTypesAccessor;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import hellfirepvp.astralsorcery.common.base.OreTypes;
import hellfirepvp.astralsorcery.common.base.sets.OreEntry;
import org.jetbrains.annotations.ApiStatus;

@RegistryDescription(
        category = RegistryDescription.Category.ENTRIES,
        priority = 2000
)
public class OreChance extends VirtualizedRegistry<OreEntry> {

    private final OreTypesAccessor REGISTRY;

    public OreChance(String name, OreTypes registry) {
        super(Alias.generateOf(name));
        REGISTRY = (OreTypesAccessor) registry;
    }

    public static OreChance mineralisRitualRegistry() {
        return new OreChance("MineralisRitualRegistry", OreTypes.RITUAL_MINERALIS);
    }

    public static OreChance aevitasPerkRegistry() {
        return new OreChance("AevitasPerkRegistry", OreTypes.AEVITAS_ORE_PERK);
    }

    public static OreChance trashPerkRegistry() {
        return new OreChance("TrashPerkRegistry", OreTypes.PERK_VOID_TRASH_REPLACEMENT);
    }

    public static OreChance treasureShrineRegistry() {
        return new OreChance("TreasureShrineRegistry", OreTypes.TREASURE_SHRINE_GEN);
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(r -> REGISTRY.getEntries().removeIf(entry -> {
            if (entry.oreName.equals(r.oreName)) {
                REGISTRY.setTotalWeight(REGISTRY.getTotalWeight() - entry.weight);
                return true;
            }
            return false;
        }));
        restoreFromBackup().forEach(REGISTRY::add);
    }

    public void add(OreEntry entry) {
        addScripted(entry);
        REGISTRY.add(entry);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("ore('blockDiamond'), 10000"))
    public void add(String ore, int weight) {
        if (weight <= 0) {
            GroovyLog.msg("Error adding Astral Sorcery OreChance. Weight must be a positive integer.").error().post();
            return;
        }
        if (ore == null || ore.isEmpty()) {
            GroovyLog.msg("Error adding Astral Sorcery OreChance. Ore name cannot be null.").error().post();
            return;
        }
        add(new OreEntry(ore, weight));
    }

    public void add(OreDictIngredient ore, int weight) {
        if (ore == null || ore.getOreDict() == null || ore.getOreDict().isEmpty()) {
            GroovyLog.msg("Error adding Astral Sorcery OreChance. Ore name cannot be null.").error().post();
            return;
        }
        add(ore.getOreDict(), weight);
    }

    public boolean remove(OreEntry entry) {
        return remove(entry.oreName);
    }

    @MethodDescription(example = @Example("ore('oreDiamond')"))
    public boolean remove(OreDictIngredient entry) {
        return remove(entry.getOreDict());
    }

    @MethodDescription
    public boolean remove(String ore) {
        return REGISTRY.getEntries().removeIf(entry -> {
            if (entry.oreName.equals(ore)) {
                REGISTRY.setTotalWeight(REGISTRY.getTotalWeight() - entry.weight);
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<OreEntry> streamRecipes() {
        return new SimpleObjectStream<>(REGISTRY.getEntries())
                .setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        REGISTRY.getEntries().forEach(this::addBackup);
        REGISTRY.getEntries().clear();
    }
}
