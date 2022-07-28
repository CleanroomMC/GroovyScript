package com.cleanroommc.groovyscript.api;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface INBTResourceStack extends IResourceStack {

    default boolean hasNbt() {
        return getNbt() != null;
    }

    @Nullable
    NBTTagCompound getNbt();

    @NotNull
    default NBTTagCompound getOrCreateNbt() {
        NBTTagCompound nbt = getNbt();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            setNbt(nbt);
        }
        return nbt;
    }

    @Nullable
    default NBTBase getSubTag(String key) {
        NBTTagCompound nbt = getNbt();
        return nbt == null ? null : nbt.getTag(key);
    }

    void setNbt(NBTTagCompound nbt);
}
