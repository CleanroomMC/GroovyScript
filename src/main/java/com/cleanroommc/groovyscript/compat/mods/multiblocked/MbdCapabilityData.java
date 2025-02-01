package com.cleanroommc.groovyscript.compat.mods.multiblocked;

import com.cleanroommc.multiblocked.api.capability.MultiblockCapability;
import com.cleanroommc.multiblocked.api.recipe.Content;

public class MbdCapabilityData<T> {

    protected T value;
    private final MultiblockCapability<T> capability;
    private boolean perTick;
    private float chance = 1.0f;
    private String slotName = null;

    public MbdCapabilityData(T value, MultiblockCapability<T> capability) {
        this.value = value;
        this.capability = capability;
    }

    public MbdCapabilityData<T> perTick() {
        perTick = true;
        return this;
    }

    public MbdCapabilityData<T> chance(float c) {
        chance = c;
        return this;
    }

    public MbdCapabilityData<T> slotName(String n) {
        slotName = n;
        return this;
    }

    public boolean isPerTick() {
        return perTick;
    }

    public MultiblockCapability<T> getCapability() {
        return capability;
    }

    public Content getContent() {
        return new Content(value, chance, slotName);
    }

}
