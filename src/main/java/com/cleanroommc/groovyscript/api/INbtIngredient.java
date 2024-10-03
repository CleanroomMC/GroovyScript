package com.cleanroommc.groovyscript.api;

import com.cleanroommc.groovyscript.helper.ingredient.NbtHelper;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Map;

public interface INbtIngredient extends INBTResourceStack {

    INbtIngredient withNbtExact(NBTTagCompound nbt);

    default INbtIngredient withNbtExact(Map<String, Object> map) {
        return withNbtExact(NbtHelper.ofMap(map));
    }

}
