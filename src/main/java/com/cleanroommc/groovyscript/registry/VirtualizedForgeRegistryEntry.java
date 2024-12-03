package com.cleanroommc.groovyscript.registry;

import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Objects;

@SuppressWarnings("ClassCanBeRecord")
public class VirtualizedForgeRegistryEntry<T extends IForgeRegistryEntry<T>> {

    private final T value;
    private final int id;
    private final Object override;

    public VirtualizedForgeRegistryEntry(T value, int id, Object override) {
        this.value = value;
        this.id = id;
        this.override = override;
    }

    public T getValue() {
        return value;
    }

    public int getId() {
        return id;
    }

    public Object getOverride() {
        return override;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VirtualizedForgeRegistryEntry<?> that = (VirtualizedForgeRegistryEntry<?>) o;
        return id == that.id && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, id);
    }

    @Override
    public String toString() {
        return value.getRegistryName().toString();
    }
}
