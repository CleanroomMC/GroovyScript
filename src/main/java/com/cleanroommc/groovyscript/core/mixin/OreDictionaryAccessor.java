package com.cleanroommc.groovyscript.core.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(OreDictionary.class)
public interface OreDictionaryAccessor {

    @Accessor
    static List<String> getIdToName() {
        throw new AssertionError();
    }

    @Accessor
    static Map<String, Integer> getNameToId() {
        throw new AssertionError();
    }

    @Accessor
    static List<NonNullList<ItemStack>> getIdToStack() {
        throw new AssertionError();
    }

    @Accessor
    static List<NonNullList<ItemStack>> getIdToStackUn() {
        throw new AssertionError();
    }

    @Accessor
    static Map<Integer, List<Integer>> getStackToId() {
        throw new AssertionError();
    }
}
