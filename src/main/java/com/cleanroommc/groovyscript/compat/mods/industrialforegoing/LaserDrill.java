package com.cleanroommc.groovyscript.compat.mods.industrialforegoing;

import com.buuz135.industrial.api.recipe.LaserDrillEntry;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RegistryDescription
public class LaserDrill extends VirtualizedRegistry<Pair<LaserDrillEntry.LaserDrillEntryExtended, LaserDrillEntry.OreRarity>> {

    @RecipeBuilderDescription(example = @Example(".output(item('minecraft:clay')).lensMeta(5).weight(100)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(pair -> LaserDrillEntry.findForOre(pair.getKey().getStack(), pair.getKey()).getRarities().remove(pair.getValue()));
        restoreFromBackup().forEach(pair -> LaserDrillEntry.findForOre(pair.getKey().getStack(), pair.getKey()).getRarities().add(pair.getValue()));
    }

    @Override
    @GroovyBlacklist
    public void afterScriptLoad() {
        // Clear any empty values to prevent JEI integration from causing an IndexOutOfBoundsException
        LaserDrillEntry.LASER_DRILL_UNIQUE_VALUES.removeIf(x -> x.getRarities().isEmpty());

        // Clear and update the LASER_DRILL_ENTRIES based on current LASER_DRILL_UNIQUE_VALUES
        for (int y = 0; y < LaserDrillEntry.LASER_DRILL_ENTRIES.length; y++) {
            LaserDrillEntry.LASER_DRILL_ENTRIES[y].clear();
        }

        for (LaserDrillEntry.LaserDrillEntryExtended entryExtended : LaserDrillEntry.LASER_DRILL_UNIQUE_VALUES) {
            for (LaserDrillEntry.OreRarity oreRarity : entryExtended.getRarities()) {
                for (int y = oreRarity.getMinY(); y <= oreRarity.getMaxY(); y++) {
                    LaserDrillEntry.LASER_DRILL_ENTRIES[y].add(new LaserDrillEntry(entryExtended.getLaserMeta(), entryExtended.getStack(), oreRarity.getWeight(), oreRarity.getWhitelist(), oreRarity.getBlacklist()));
                }
            }
        }
    }

    public void add(LaserDrillEntry.LaserDrillEntryExtended recipe, LaserDrillEntry.OreRarity entry) {
        if (recipe == null) return;
        addScripted(Pair.of(recipe, entry));
        LaserDrillEntry.findForOre(recipe.getStack(), recipe).getRarities().add(entry);
    }

    public boolean remove(LaserDrillEntry.LaserDrillEntryExtended recipe) {
        if (recipe == null) return false;
        for (LaserDrillEntry.OreRarity rarity : recipe.getRarities()) {
            addBackup(Pair.of(recipe, rarity));
        }
        recipe.getRarities().clear();
        return true;
    }

    @MethodDescription(example = @Example("5"))
    public boolean removeByLens(int lensMeta) {
        boolean removed = false;
        for (LaserDrillEntry.LaserDrillEntryExtended laserDrillUniqueValue : LaserDrillEntry.LASER_DRILL_UNIQUE_VALUES) {
            removed = laserDrillUniqueValue.getRarities().removeIf(recipe -> {
                if (lensMeta == laserDrillUniqueValue.getLaserMeta()) {
                    addBackup(Pair.of(laserDrillUniqueValue, recipe));
                    return true;
                }
                return false;
            }) || removed;
        }
        return removed;
    }

    @MethodDescription(example = @Example(value = "item('industrialforegoing:laser_lens:5')", commented = true))
    public boolean removeByLens(ItemStack lens) {
        return removeByLens(lens.getItemDamage());
    }

    @MethodDescription(example = @Example("item('minecraft:coal_ore')"))
    public boolean removeByOutput(IIngredient output) {
        boolean removed = false;
        for (LaserDrillEntry.LaserDrillEntryExtended laserDrillUniqueValue : LaserDrillEntry.LASER_DRILL_UNIQUE_VALUES) {
            removed = laserDrillUniqueValue.getRarities().removeIf(recipe -> {
                if (output.test(laserDrillUniqueValue.getStack())) {
                    addBackup(Pair.of(laserDrillUniqueValue, recipe));
                    return true;
                }
                return false;
            }) || removed;
        }
        return removed;
    }

    @MethodDescription(example = @Example("biome('minecraft:hell')"))
    public boolean removeByWhitelist(Biome biome) {
        boolean removed = false;
        for (LaserDrillEntry.LaserDrillEntryExtended laserDrillUniqueValue : LaserDrillEntry.LASER_DRILL_UNIQUE_VALUES) {
            removed = laserDrillUniqueValue.getRarities().removeIf(recipe -> {
                if (recipe.getWhitelist().contains(biome)) {
                    addBackup(Pair.of(laserDrillUniqueValue, recipe));
                    return true;
                }
                return false;
            }) || removed;
        }
        return removed;
    }

    @MethodDescription(example = @Example("biome('minecraft:sky')"))
    public boolean removeByBlacklist(Biome biome) {
        boolean removed = false;
        for (LaserDrillEntry.LaserDrillEntryExtended laserDrillUniqueValue : LaserDrillEntry.LASER_DRILL_UNIQUE_VALUES) {
            removed = laserDrillUniqueValue.getRarities().removeIf(recipe -> {
                if (recipe.getBlacklist().contains(biome)) {
                    addBackup(Pair.of(laserDrillUniqueValue, recipe));
                    return true;
                }
                return false;
            }) || removed;
        }
        return removed;
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        LaserDrillEntry.LASER_DRILL_UNIQUE_VALUES.forEach(recipe -> {
            recipe.getRarities().forEach(entry -> addBackup(Pair.of(recipe, entry)));
            recipe.getRarities().clear();
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<LaserDrillEntry.LaserDrillEntryExtended> streamRecipes() {
        return new SimpleObjectStream<>(LaserDrillEntry.LASER_DRILL_UNIQUE_VALUES)
                .setRemover(this::remove);
    }

    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<LaserDrillEntry.LaserDrillEntryExtended> {

        @Property
        private final List<Biome> whitelist = new ArrayList<>();
        @Property
        private final List<Biome> blacklist = new ArrayList<>();
        @Property
        private int lensMeta;
        @Property(comp = @Comp(gt = 0))
        private int weight;
        @Property(comp = @Comp(lte = 255))
        private int minY;
        @Property(defaultValue = "255", comp = @Comp(lte = 255))
        private int maxY = 255;

        @RecipeBuilderMethodDescription
        public RecipeBuilder whitelist(Biome whitelist) {
            this.whitelist.add(whitelist);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder whitelist(Biome... biomes) {
            for (Biome biome : biomes) {
                whitelist(biome);
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder whitelist(Collection<Biome> biomes) {
            for (Biome biome : biomes) {
                whitelist(biome);
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder blacklist(Biome blacklist) {
            this.blacklist.add(blacklist);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder blacklist(Biome... biomes) {
            for (Biome biome : biomes) {
                blacklist(biome);
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder blacklist(Collection<Biome> biomes) {
            for (Biome biome : biomes) {
                blacklist(biome);
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder lensMeta(int lensMeta) {
            this.lensMeta = lensMeta;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder weight(int weight) {
            this.weight = weight;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder minY(int minY) {
            this.minY = minY;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder maxY(int maxY) {
            this.maxY = maxY;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Industrial Foregoing Laser Drill Entry";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 1, 1);
            validateFluids(msg);
            msg.add(weight <= 0, "weight must be greater than or equal to 1, yet it was {}", weight);
            msg.add(minY < 0, "minY must be greater than or equal to 0, yet it was {}", minY);
            msg.add(maxY > 255, "maxY must be less than or equal to 255, yet it was {}", maxY);
            msg.add(minY > maxY, "minY must be less than or equal to maxY, yet minY was {} and maxY was {}", minY, maxY);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable LaserDrillEntry.LaserDrillEntryExtended register() {
            if (!validate()) return null;
            LaserDrillEntry.LaserDrillEntryExtended recipe = LaserDrillEntry.findForOre(output.get(0), new LaserDrillEntry.LaserDrillEntryExtended(lensMeta, output.get(0)));
            LaserDrillEntry.OreRarity entry = new LaserDrillEntry.OreRarity(weight, whitelist, blacklist, maxY, minY);
            ModSupport.INDUSTRIAL_FOREGOING.get().laserDrill.add(recipe, entry);
            return recipe;
        }
    }

}
