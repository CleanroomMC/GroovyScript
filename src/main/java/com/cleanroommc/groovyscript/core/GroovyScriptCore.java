package com.cleanroommc.groovyscript.core;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;
import java.util.Map;

@IFMLLoadingPlugin.Name("GroovyScript-Core")
@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
public class GroovyScriptCore implements IFMLLoadingPlugin, IEarlyMixinLoader {

    public static File source;

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return "com.cleanroommc.groovyscript.sandbox.ScriptModContainer";
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        source = (File) data.getOrDefault("coremodLocation", null);
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
