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
import team.chisel.api.carving.ICarvingRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Carving extends VirtualizedRegistry<Pair<String, ItemStack>> {

    private static ICarvingRegistry getRegistry() {
        if (CarvingUtils.getChiselRegistry() == null) {
            throw new IllegalStateException("Chisel carving getRegistry() is not yet initialized!");
        }
        return CarvingUtils.getChiselRegistry();
    }

    private List<Pair<String, SoundEvent>> sounds;
    private List<String> groupBackup;
    private List<String> groupScripted;

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
        removeScripted().forEach(pair -> getRegistry().removeVariation(pair.getValue(), pair.getKey()));
        restoreFromBackup().forEach(pair -> getRegistry().addVariation(pair.getKey(), CarvingUtils.variationFor(pair.getValue(), 0)));

        this.sounds.forEach(pair -> getRegistry().setVariationSound(pair.getKey(), pair.getValue()));
        this.groupBackup.forEach(group -> getRegistry().addGroup(CarvingUtils.getDefaultGroupFor(group)));
        this.groupScripted.forEach(getRegistry()::removeGroup);

        this.sounds = new ArrayList<>();
        this.groupBackup = new ArrayList<>();
        this.groupScripted = new ArrayList<>();
    }

    public void addVariation(String groupName, ItemStack item) {
        try {
            getRegistry().addVariation(groupName, CarvingUtils.variationFor(item, 0));
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
            getRegistry().removeVariation(item, groupName);
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
        ICarvingGroup carvingGroup = getRegistry().getGroup(group);
        if (carvingGroup == null) {
            GroovyLog.msg("Error setting the sound for a Chisel Carving group")
                    .add("could not find a Carving Group with the name {}", group)
                    .error()
                    .post();
            return;
        }
        setSound(carvingGroup, sound);
    }

    public void setSound(ICarvingGroup group, SoundEvent sound) {
        getRegistry().setVariationSound(group.getName(), sound);
        this.sounds.add(Pair.of(group.getName(), group.getSound()));
    }

    public void addGroup(String groupName) {
        if (getRegistry().getSortedGroupNames().contains(groupName)) {
            GroovyLog.msg("Error adding Chisel Carving group")
                    .add("found a duplicate Chisel Carving group with name {}", groupName)
                    .error()
                    .post();
            return;
        }
        getRegistry().addGroup(CarvingUtils.getDefaultGroupFor(groupName));
        this.groupScripted.add(groupName);
    }

    public void removeGroup(String groupName) {
        if (!getRegistry().getSortedGroupNames().contains(groupName)) {
            GroovyLog.msg("Error removing Chisel Carving group")
                    .add("could not find Chisel Carving group with name {}", groupName)
                    .error()
                    .post();
            return;
        }
        getRegistry().removeGroup(groupName);
        this.groupBackup.add(groupName);
    }

    public void removeAll() {
        getRegistry().getSortedGroupNames().forEach(name -> {
            getRegistry().removeGroup(name);
            this.groupBackup.add(name);
        });
    }


    public static class CarvingGroup {

        private final ICarvingGroup group;

        public CarvingGroup(String group) {
            if (!getRegistry().getSortedGroupNames().contains(group)) ModSupport.CHISEL.get().carving.addGroup(group);
            this.group = getRegistry().getGroup(group);
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
