package com.cleanroommc.groovyscript.compat;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyPlugin;
import com.cleanroommc.groovyscript.api.IGroovyCompatRegistryContainer;
import com.cleanroommc.groovyscript.api.IGroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import org.jetbrains.annotations.NotNull;

@GroovyPlugin
public class TestModGroovyPlugin implements IGroovyContainer {

    @Override
    public @NotNull String getModId() {
        return GroovyScript.ID;
    }

    @Override
    public @NotNull String getModName() {
        return GroovyScript.NAME;
    }

    @Override
    public void onCompatLoaded(GroovyContainer<?> container, IGroovyCompatRegistryContainer registry) {
        GroovyScript.LOGGER.info("TestMod container loaded");
        registry.addRegistry(VanillaModule.furnace);
    }
}
