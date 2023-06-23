package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.GroovyScript;
import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.relauncher.FMLInjectionData;

import java.io.File;

public class ScriptModContainer extends DummyModContainer {

    private final File source;

    public ScriptModContainer() {
        super(RunConfig.modMetadata);
        this.source = (File) FMLInjectionData.data()[6];
        GroovyScript.initializeRunConfig(this.source);
    }

    @Override
    public File getSource() {
        return source;
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        return true;
    }
}
