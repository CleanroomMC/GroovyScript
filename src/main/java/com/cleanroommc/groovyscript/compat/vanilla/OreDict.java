package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.core.mixin.OreDictionaryMixin;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.List;

public class OreDict extends VirtualizedRegistry<OreDictEntry> {

    public OreDict() {
        super("OreDict", "OreDictionary", "oredictionary", "oredict");
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(entry -> getItems(entry.name)
                .removeIf(s -> s.isItemEqual(entry.stack)));
        restoreFromBackup().forEach(entry -> OreDictionary.registerOre(entry.name, entry.stack));
    }

    public void add(String name, Item item) {
        if (name != null && item != null && item != Items.AIR) {
            add(new OreDictEntry(name, new ItemStack(item)));
        }
    }

    public void add(String name, Block block) {
        if (name != null && block != null && block != Blocks.AIR) {
            add(new OreDictEntry(name, new ItemStack(block)));
        }
    }

    public void add(String name, ItemStack stack) {
        if (name != null && stack != null) {
            add(new OreDictEntry(name, stack));
        }
    }

    public void add(OreDictEntry entry) {
        if (entry != null) {
            addScripted(entry);
            OreDictionary.registerOre(entry.name, entry.stack);
        }
    }

    public List<ItemStack> getItems(String name) {
        return OreDictionary.getOres(name, false);
    }

    public boolean exists(String name) {
        return OreDictionary.doesOreNameExist(name);
    }

    public boolean remove(OreDictEntry entry, ItemStack stack) {
        return entry != null && remove(entry.name, stack);
    }

    public boolean remove(String name, ItemStack stack) {
        List<ItemStack> list = getItems(name);
        if (GroovyLog.msg("Error removing from OreDictionary entry")
                .add(list.isEmpty(), "OreDictionary Entry '%s' was empty", name)
                .error()
                .postIfNotEmpty()) {
            return false;
        }
        Integer id = OreDictionaryMixin.getNameToId().get(name);
        if (list.removeIf(s -> s.isItemEqual(stack))) {
            if (id != null) {
                int i = id;
                OreDictionaryMixin.getNameToId().remove(name);
                OreDictionaryMixin.getIdToName().remove(i);
                OreDictionaryMixin.getIdToName().add(i, "");
                OreDictionaryMixin.getIdToStack().remove(i);
                OreDictionaryMixin.getIdToStack().add(i, NonNullList.create());
                OreDictionaryMixin.getIdToStackUn().remove(i);
                OreDictionaryMixin.getIdToStackUn().add(i, NonNullList.create());
                OreDictionaryMixin.getStackToId().remove(i | stack.getMetadata());
                OreDictionaryMixin.getStackToId().put(i | stack.getMetadata(), Collections.emptyList());
            }

            addBackup(new OreDictEntry(name, stack));
            return true;
        }
        return false;
    }

    public boolean clear(OreDictEntry entry) {
        return entry != null && clear(entry.name);
    }

    public boolean clear(String name) {
        List<ItemStack> list = getItems(name);
        if (GroovyLog.msg("Error removing from OreDictionary entry")
                    .add(list.isEmpty(), "OreDictionary Entry was empty")
                    .error()
                    .postIfNotEmpty()) {
            return false;
        }
        list.forEach(stack -> addBackup(new OreDictEntry(name, stack)));
        list.clear();
        return true;
    }

    public void removeAll() {
        for (String name : OreDictionary.getOreNames()) {
            clear(name);
        }
    }
}
