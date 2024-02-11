package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.core.GroovyScriptCore;
import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.relauncher.FMLInjectionData;

import java.io.File;

public class ScriptModContainer extends DummyModContainer {

    public ScriptModContainer() {
        super(RunConfig.modMetadata);
        GroovyScript.initializeRunConfig((File) FMLInjectionData.data()[6]);
    }

    @Override
    public File getSource() {
        return GroovyScriptCore.source;
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        return true;
    }
}
