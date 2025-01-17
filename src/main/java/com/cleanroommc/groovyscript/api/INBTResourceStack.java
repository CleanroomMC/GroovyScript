package com.cleanroommc.groovyscript.api;

import com.cleanroommc.groovyscript.helper.ingredient.NbtHelper;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * An object that holds an amount and a NBT tag
 */
public interface INBTResourceStack extends IResourceStack {

    default boolean hasNbt() {
        return getNbt() != null;
    }

    @Nullable
    NBTTagCompound getNbt();

    default @NotNull NBTTagCompound getOrCreateNbt() {
        NBTTagCompound nbt = getNbt();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            setNbt(nbt);
        }
        return nbt;
    }

    default @Nullable NBTBase getSubTag(String key) {
        NBTTagCompound nbt = getNbt();
        return nbt == null ? null : nbt.getTag(key);
    }

    void setNbt(NBTTagCompound nbt);

    default INBTResourceStack withNbt(NBTTagCompound nbt) {
        INBTResourceStack resourceStack = (INBTResourceStack) exactCopy();
        resourceStack.setNbt(nbt);
        return resourceStack;
    }

    default INBTResourceStack withNbt(Map<String, Object> map) {
        return withNbt(NbtHelper.ofMap(map));
    }

    default INBTResourceStack withEmptyNbt() {
        return withNbt(new NBTTagCompound());
    }
}
