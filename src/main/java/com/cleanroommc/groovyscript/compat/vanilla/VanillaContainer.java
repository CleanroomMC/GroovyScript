package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import org.jetbrains.annotations.NotNull;

public class VanillaContainer extends GroovyContainer<VanillaModule> {

    public static final VanillaContainer INSTANCE = new VanillaContainer();

    private VanillaContainer() {
        super();
    }

    @Override
    public @NotNull String getModId() {
        return "minecraft";
    }

    @Override
    public @NotNull String getContainerName() {
        return "Vanilla";
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public void onCompatLoaded(GroovyContainer<?> container) {}

    @Override
    public VanillaModule get() {
        return VanillaModule.INSTANCE;
    }
}
