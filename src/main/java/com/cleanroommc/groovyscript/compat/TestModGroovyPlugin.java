package com.cleanroommc.groovyscript.compat;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyPlugin;
import com.cleanroommc.groovyscript.api.IGroovyCompat;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import org.jetbrains.annotations.NotNull;

@GroovyPlugin
public class TestModGroovyPlugin implements IGroovyCompat {

    @Override
    public @NotNull String getModId() {
        return GroovyScript.ID;
    }

    @Override
    public @NotNull String getModName() {
        return GroovyScript.NAME;
    }

    @Override
    public void onCompatLoaded(GroovyContainer<?> container) {
        GroovyScript.LOGGER.info("TestMod container loaded");
        container.getVirtualizedRegistrar().addRegistry(VanillaModule.furnace);
    }
}
