package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.OreDictionaryAccessor;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictMatcherIngredient;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES)
public class OreDict extends VirtualizedRegistry<OreDictEntry> {

    public OreDict() {
        super(Alias.generateOfClass(OreDict.class).andGenerate("OreDictionary"));
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(entry -> remove(entry.name, entry.stack, false));
        restoreFromBackup().forEach(entry -> OreDictionary.registerOre(entry.name, entry.stack));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(String name, Item item) {
        add(name, new ItemStack(item));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(String name, Block block) {
        add(name, new ItemStack(block));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = {
            @Example("'ingotGold', item('minecraft:nether_star')"),
            @Example("'netherStar', item('minecraft:gold_ingot')")
    })
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

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public List<ItemStack> getItems(String name) {
        return new ArrayList<>(OreDictionary.getOres(name, false));
    }

    // groovy [] operator
    public List<ItemStack> getAt(String name) {
        return getItems(name);
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public boolean exists(String name) {
        return OreDictionary.doesOreNameExist(name);
    }

    // groovy in operator
    public boolean isCase(String name) {
        return OreDictionary.doesOreNameExist(name);
    }

    @MethodDescription(example = @Example("'netherStar', item('minecraft:nether_star')"))
    public boolean remove(String name, ItemStack stack) {
        if (GroovyLog.msg("Error removing ore dictionary entry")
                .add(StringUtils.isEmpty(name), () -> "Name must not be empty")
                .add(IngredientHelper.isEmpty(stack), () -> "Item must not be empty")
                .error()
                .postIfNotEmpty()) {
            return false;
        }
        return remove(name, stack, true);
    }

    @GroovyBlacklist
    private boolean remove(String oreDict, ItemStack ore, boolean scripted) {
        Integer id = OreDictionaryAccessor.getNameToId().get(oreDict);
        if (id != null) {
            int i = id;
            List<ItemStack> items = OreDictionaryAccessor.getIdToStack().get(i);
            items.removeIf(itemStack -> itemStack.isItemEqual(ore));
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
            return true;
        }
        return false;
    }

    @MethodDescription(example = @Example(value = "'plankWood'", commented = true))
    public boolean clear(String name) {
        return removeAll(name);
    }

    @MethodDescription(example = @Example(value = "'ingotIron'", commented = true), description = "groovyscript.wiki.minecraft.ore_dict.clear")
    public boolean removeAll(String name) {
        List<ItemStack> list = getItems(name);
        if (GroovyLog.msg("Error removing from OreDictionary entry")
                .add(list.isEmpty(), "OreDictionary Entry was empty")
                .error()
                .postIfNotEmpty()) {
            return false;
        }
        list.forEach(stack -> remove(name, stack));
        return true;
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        for (String name : OreDictionary.getOreNames()) {
            for (ItemStack stack : getItems(name)) {
                remove(name, stack);
            }
        }
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<String> streamOreNames() {
        return new SimpleObjectStream<>(OreDictionaryAccessor.getIdToName()).setRemover(this::removeAll);
    }

    @MethodDescription(type = MethodDescription.Type.QUERY, example = @Example("'ingot*'"))
    public IIngredient getOres(String pattern) {
        return new OreDictMatcherIngredient(pattern);
    }

    @MethodDescription(type = MethodDescription.Type.QUERY, example = {
            @Example("~/.*Gold/"),
            @Example("~/.*or.*/"),
            @Example("~/.*/"),
    })
    public IIngredient getOres(Pattern pattern) {
        return new OreDictMatcherIngredient(pattern);
    }
}
