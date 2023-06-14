package com.cleanroommc.groovyscript.compat.mods.chisel;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import org.apache.commons.lang3.tuple.Pair;
import team.chisel.api.carving.CarvingUtils;
import team.chisel.api.carving.ICarvingGroup;

import java.util.ArrayList;
import java.util.Collection;

public class Carving extends VirtualizedRegistry<Pair<String, ItemStack>> {

    private ArrayList<Pair<String, SoundEvent>> sounds;
    private ArrayList<String> groupBackup;
    private ArrayList<String> groupScripted;

    public Carving() {
        super();
        this.sounds = new ArrayList<>();
        this.groupBackup = new ArrayList<>();
        this.groupScripted = new ArrayList<>();
    }

    public static CarvingGroup carvingGroup(String group) {
        return new CarvingGroup(group);
    }

    @Override
    public void onReload() {

        team.chisel.common.carving.Carving.chisel.removeGroup("s");

        removeScripted().forEach(pair -> CarvingUtils.getChiselRegistry().removeVariation(pair.getValue(), pair.getKey()));
        restoreFromBackup().forEach(pair -> CarvingUtils.getChiselRegistry().addVariation(pair.getKey(), CarvingUtils.variationFor(pair.getValue(), 0)));

        this.sounds.forEach(pair -> CarvingUtils.getChiselRegistry().setVariationSound(pair.getKey(), pair.getValue()));
        this.groupBackup.forEach(group -> CarvingUtils.getChiselRegistry().addGroup(CarvingUtils.getDefaultGroupFor(group)));
        this.groupScripted.forEach(group -> CarvingUtils.getChiselRegistry().removeGroup(group));

        this.sounds = new ArrayList<>();
        this.groupBackup = new ArrayList<>();
        this.groupScripted = new ArrayList<>();
    }

    public void addVariation(String groupName, ItemStack item) {
        try {
            CarvingUtils.getChiselRegistry().addVariation(groupName, CarvingUtils.variationFor(item, 0));
            addScripted(Pair.of(groupName, item));
        } catch (UnsupportedOperationException e) {
            GroovyLog.msg("Error adding a Chisel Carving variation")
                    .add("you cannot add variations to Oredict chisel groups {}", groupName)
                    .add("instead, edit the oredict via `oredict.add('{}', {})`", groupName, IngredientHelper.asGroovyCode(item, false))
                    .error()
                    .post();
        }
    }

    public void removeVariation(String groupName, ItemStack item) {
        try {
            CarvingUtils.getChiselRegistry().removeVariation(item, groupName);
            addBackup(Pair.of(groupName, item));
        } catch (UnsupportedOperationException e) {
            GroovyLog.msg("Error removing a Chisel Carving variation")
                    .add("you cannot remove variations to Oredict chisel groups {}", groupName)
                    .add("instead, edit the oredict via `oredict.remove('{}', {})`", groupName, IngredientHelper.asGroovyCode(item, false))
                    .error()
                    .post();
        }
    }

    public void setSound(String group, SoundEvent sound) {
        setSound(CarvingUtils.getChiselRegistry().getGroup(group), sound);
    }

    public void setSound(ICarvingGroup group, SoundEvent sound) {
        CarvingUtils.getChiselRegistry().setVariationSound(group.getName(), sound);
        this.sounds.add(Pair.of(group.getName(), group.getSound()));
    }

    public void addGroup(String groupName) {
        if (CarvingUtils.getChiselRegistry().getSortedGroupNames().contains(groupName)) {
            GroovyLog.msg("Error adding Chisel Carving group")
                    .add("found a duplicate Chisel Carving group with name {}", groupName)
                    .error()
                    .post();
            return;
        }
        CarvingUtils.getChiselRegistry().addGroup(CarvingUtils.getDefaultGroupFor(groupName));
        this.groupScripted.add(groupName);
    }

    public void removeGroup(String groupName) {
        if (!CarvingUtils.getChiselRegistry().getSortedGroupNames().contains(groupName)) {
            GroovyLog.msg("Error removing Chisel Carving group")
                    .add("could not find Chisel Carving group with name {}", groupName)
                    .error()
                    .post();
            return;
        }
        CarvingUtils.getChiselRegistry().removeGroup(groupName);
        this.groupBackup.add(groupName);
    }

    public void removeAll() {
        CarvingUtils.getChiselRegistry().getSortedGroupNames().forEach(name -> {
            CarvingUtils.getChiselRegistry().removeGroup(name);
            this.groupBackup.add(name);
        });
    }


    public static class CarvingGroup {

        ICarvingGroup group;

        public CarvingGroup(String group) {
            if (!CarvingUtils.getChiselRegistry().getSortedGroupNames().contains(group)) ModSupport.CHISEL.get().carving.addGroup(group);
            this.group = CarvingUtils.getChiselRegistry().getGroup(group);
        }

        public CarvingGroup sound(SoundEvent sound) {
            ModSupport.CHISEL.get().carving.setSound(group, sound);
            return this;
        }

        public CarvingGroup add(ItemStack item) {
            ModSupport.CHISEL.get().carving.addVariation(this.group.getName(), item);
            return this;
        }

        public CarvingGroup add(ItemStack... items) {
            for (ItemStack item : items) {
                add(item);
            }
            return this;
        }

        public CarvingGroup add(Collection<ItemStack> items) {
            for (ItemStack item : items) {
                add(item);
            }
            return this;
        }

        public CarvingGroup remove(ItemStack item) {
            ModSupport.CHISEL.get().carving.removeVariation(this.group.getName(), item);
            return this;
        }

        public CarvingGroup remove(ItemStack... items) {
            for (ItemStack item : items) {
                remove(item);
            }
            return this;
        }

        public CarvingGroup remove(Collection<ItemStack> items) {
            for (ItemStack item : items) {
                remove(item);
            }
            return this;
        }
    }

}
