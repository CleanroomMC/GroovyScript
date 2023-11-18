package com.cleanroommc.groovyscript.compat;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyPlugin;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import org.jetbrains.annotations.NotNull;

public class TestModGroovyPlugin implements GroovyPlugin {

    @Instance
    private static TestModGroovyPlugin instance;
    @Instance
    private static GroovyContainer<?> container;

    public static TestModGroovyPlugin getInstance() {
        return instance;
    }

    public final TestReg test = new TestReg();

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
        GroovyScript.LOGGER.info("The field is '{}'", instance);
        GroovyScript.LOGGER.info("The container is '{}'", container);
        container.getVirtualizedRegistrar().addRegistry(VanillaModule.furnace);
        container.getVirtualizedRegistrar().addFieldsOf(this);
    }
}
