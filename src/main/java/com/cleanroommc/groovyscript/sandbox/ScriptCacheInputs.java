package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.packmode.Packmode;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

final class ScriptCacheInputs {

    private ScriptCacheInputs() {}

    static String capture(URL[] scriptRoots, CompilerConfiguration config, Map<String, Path> additionalInputs) throws IOException {
        Map<String, Path> roots = new LinkedHashMap<>();
        for (int i = 0; i < scriptRoots.length; i++) roots.put("scripts/" + i, CacheFingerprint.classpathFile(scriptRoots[i]));
        roots.put("configuration", new File(SandboxData.getMinecraftHome(), "config/groovyscript.cfg").toPath());
        additionalInputs.forEach((name, path) -> roots.put("integration/" + name, path));
        Map<String, String> environment = new TreeMap<>();
        URL[] classpath = Launch.classLoader.getURLs();
        for (int i = 0; i < classpath.length; i++) {
            // Forge's generated transformer wrappers are derived from the loader and coremod JARs below.
            if ("asmgen".equals(classpath[i].getProtocol())) {
                environment.put("classpath/" + i, classpath[i].toExternalForm());
            } else {
                roots.put("classpath/" + i, CacheFingerprint.classpathFile(classpath[i]));
            }
        }
        // Include container sources as well: a loader may not expose every nested mod in its URLs.
        for (ModContainer mod : Loader.instance().getModList()) {
            environment.put("mod/" + mod.getModId(), mod.getVersion());
            if (mod.getSource() != null) roots.put("mod/" + mod.getModId(), CacheFingerprint.classpathFile(mod.getSource().toURI().toURL()));
        }
        for (String property : new String[]{
                "java.runtime.version", "java.vm.version", "java.vendor", "file.encoding", "user.language", "user.country", "user.timezone"
        }) {
            environment.put(property, System.getProperty(property, ""));
        }
        environment.put("side", FMLLaunchHandler.side().name());
        environment.put("deobfuscated", Boolean.toString(FMLLaunchHandler.isDeobfuscatedEnvironment()));
        environment.put("packmode", String.valueOf(Packmode.getPackmode()));
        environment.put("encoding", config.getSourceEncoding());
        environment.put("target", config.getTargetBytecode());
        environment.put("scriptBaseClass", String.valueOf(config.getScriptBaseClass()));
        environment.put("extensions", new TreeSet<>(config.getScriptExtensions()).toString());
        environment.put("optimizations", new TreeMap<>(config.getOptimizationOptions()).toString());
        environment.put("disabledTransforms", String.valueOf(config.getDisabledGlobalASTTransformations() == null ? null : new TreeSet<>(config.getDisabledGlobalASTTransformations())));
        System.getProperties()
                .stringPropertyNames()
                .stream()
                .filter(name -> name.startsWith("groovy.") || name.startsWith("groovyscript."))
                .forEach(name -> environment.put("property/" + name, System.getProperty(name)));
        return CacheFingerprint.capture(roots, environment);
    }
}
