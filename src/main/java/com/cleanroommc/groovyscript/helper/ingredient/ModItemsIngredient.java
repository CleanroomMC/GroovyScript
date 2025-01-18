package com.cleanroommc.groovyscript.helper.ingredient;

import com.cleanroommc.groovyscript.api.IIngredient;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModItemsIngredient extends ItemsIngredient {

    private static final Map<String, List<ItemStack>> CACHE = new Object2ObjectOpenHashMap<>();

    private final String modName;

    public ModItemsIngredient(String modName, List<ItemStack> itemStacks) {
        super(itemStacks);
        this.modName = modName;
    }

    public static ModItemsIngredient of(ModContainer mod) {
        return of(mod.getModId());
    }

    public static ModItemsIngredient of(String modId) {
        return new ModItemsIngredient(modId, CACHE.computeIfAbsent(modId, ModItemsIngredient::itemStacksFromLocation));
    }

    private static List<ItemStack> itemStacksFromLocation(String namespace) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        ForgeRegistries.ITEMS.getEntries()
                .stream()
                .filter(entry -> entry.getValue() != null)
                .filter(entry -> namespace.equals(entry.getKey().getNamespace()))
                .forEach(entry -> entry.getValue().getSubItems(CreativeTabs.SEARCH, stacks));
        return new ArrayList<>(stacks);
    }

    public String getModName() {
        return modName;
    }

    @Override
    public IIngredient exactCopy() {
        var mii = new ModItemsIngredient(this.modName, getItemStacks());
        mii.setAmount(getAmount());
        mii.transformer = transformer;
        mii.matchCondition = matchCondition;
        return mii;
    }
}
