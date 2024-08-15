package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.core.GroovyScriptCore;
import com.google.common.eventbus.EventBus;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;

import java.io.File;

public class ScriptModContainer extends DummyModContainer {

    public ScriptModContainer() {
        super(RunConfig.modMetadata);
        if (FMLLaunchHandler.isDeobfuscatedEnvironment()) {
            // fixes error when forge tries to find a mod jar inside classgraph
            Launch.classLoader.getSources().removeIf(url -> url.toString().contains("io.github.classgraph"));
        }
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
