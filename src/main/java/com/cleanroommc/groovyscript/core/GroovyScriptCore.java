package com.cleanroommc.groovyscript.core;

import com.cleanroommc.groovyscript.sandbox.SandboxData;
import com.google.common.collect.ImmutableList;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import java.io.File;
import java.util.List;
import java.util.Map;

@IFMLLoadingPlugin.Name("GroovyScript-Core")
@IFMLLoadingPlugin.SortingIndex(Integer.MIN_VALUE + 10)
@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
public class GroovyScriptCore implements IFMLLoadingPlugin, IEarlyMixinLoader {

    public static final Logger LOG = LogManager.getLogger("GroovyScript-Core");
    public static File source;

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return "com.cleanroommc.groovyscript.sandbox.ScriptModContainer";
    }

    @Override
    public @Nullable String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        source = (File) data.getOrDefault("coremodLocation", null);
        SandboxData.initialize((File) FMLInjectionData.data()[6], LOG);
        SideOnlyConfig.init();
    }

    @Override
    public String getAccessTransformerClass() {
        return "com.cleanroommc.groovyscript.core.GroovyScriptTransformer";
    }

    @Override
    public List<String> getMixinConfigs() {
        return ImmutableList.of("mixin.groovyscript.json");
    }
}
