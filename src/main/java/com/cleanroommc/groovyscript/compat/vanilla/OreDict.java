package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.core.mixin.OreDictionaryAccessor;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

public class OreDict extends VirtualizedRegistry<OreDictEntry> {

    public OreDict() {
        super();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(entry -> remove(entry.name, entry.stack, false));
        restoreFromBackup().forEach(entry -> OreDictionary.registerOre(entry.name, entry.stack));
    }

    public void add(String name, Item item) {
        add(name, new ItemStack(item));
    }

    public void add(String name, Block block) {
        add(name, new ItemStack(block));
    }

    public void add(String name, ItemStack stack) {
        if (GroovyLog.msg("Error adding ore dictionary entry")
                .add(StringUtils.isEmpty(name), () -> "Name must not be empty")
                .add(IngredientHelper.isEmpty(stack), () -> "Item must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        add(new OreDictEntry(name, stack));
    }

    @GroovyBlacklist
    public void add(OreDictEntry entry) {
        addScripted(entry);
        OreDictionary.registerOre(entry.name, entry.stack);
    }

    public List<ItemStack> getItems(String name) {
        return new ArrayList<>(OreDictionary.getOres(name, false));
    }

    // groovy [] operator
    public List<ItemStack> getAt(String name) {
        return getItems(name);
    }

    public boolean exists(String name) {
        return OreDictionary.doesOreNameExist(name);
    }

    public boolean remove(OreDictEntry entry, ItemStack stack) {
        return entry != null && remove(entry.name, stack);
    }

    public boolean remove(String name, ItemStack stack) {
        return remove(name, stack, true);
    }

    @GroovyBlacklist
    private boolean remove(String oreDict, ItemStack ore, boolean scripted) {
        Integer id = OreDictionaryAccessor.getNameToId().get(oreDict);
        if (id != null) {
            GroovyLog.get().infoMC("Removing {} from {}", ore, oreDict);
            int i = id;

            List<ItemStack> items = OreDictionaryAccessor.getIdToStack().get(i);
            items.removeIf(itemStack -> itemStack.isItemEqual(ore));
            //int hash = getItemHash(itemStack);
            int hash = Item.REGISTRY.getIDForObject(ore.getItem().delegate.get());
            List<Integer> oreDicts = OreDictionaryAccessor.getStackToId().get(hash);
            if (oreDicts != null) {
                oreDicts.remove(id);
            }
            if (ore.getItemDamage() != OreDictionary.WILDCARD_VALUE) {
                hash |= ((ore.getItemDamage() + 1) << 16); // +1 so 0 is significant
            }
            oreDicts = OreDictionaryAccessor.getStackToId().get(hash);
            if (oreDicts != null) {
                oreDicts.remove(id);
            }


            if (scripted) {
                addBackup(new OreDictEntry(oreDict, ore));
            }
        }
        return true;
    }

    private static int getItemHash(ItemStack itemStack) {
        int hash = Item.REGISTRY.getIDForObject(itemStack.getItem().delegate.get());
        if (itemStack.getItemDamage() != OreDictionary.WILDCARD_VALUE) {
            hash |= ((itemStack.getItemDamage() + 1) << 16); // +1 so 0 is significant
        }
        return hash;
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
