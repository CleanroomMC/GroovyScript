package com.cleanroommc.groovyscript.api;

import com.cleanroommc.groovyscript.helper.ingredient.NbtHelper;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Map;
import java.util.function.Predicate;

public interface INbtIngredient extends INBTResourceStack {

    INbtIngredient withNbtExact(NBTTagCompound nbt);

    INbtIngredient withNbtFilter(Predicate<NBTTagCompound> nbtFilter);

    default INbtIngredient withNbtExact(Map<String, Object> map) {
        return withNbtExact(NbtHelper.ofMap(map));
    }
}
