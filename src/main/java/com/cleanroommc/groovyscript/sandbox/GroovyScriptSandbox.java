package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.event.GroovyEventManager;
import com.cleanroommc.groovyscript.event.GroovyReloadEvent;
import com.cleanroommc.groovyscript.event.ScriptRunEvent;
import com.cleanroommc.groovyscript.helper.GroovyHelper;
import com.cleanroommc.groovyscript.helper.JsonHelper;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.cleanroommc.groovyscript.sandbox.transformer.GroovyScriptCompiler;
import com.cleanroommc.groovyscript.sandbox.transformer.GroovyScriptEarlyCompiler;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import groovy.lang.*;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.io.FileUtils;
import org.apache.groovy.internal.util.UncheckedThrow;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.runtime.InvokerInvocationException;
import org.codehaus.groovy.vmplugin.VMPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GroovyScriptSandbox extends GroovySandbox {

    /**
     * Changing this number will force the cache to be deleted and every script has to be recompiled.
     * Useful when changes to the compilation process were made.
     */
    public static final int CACHE_VERSION = 3;
    /**
     * Setting this to false will cause compiled classes to never be cached.
     * As a side effect some compilation behaviour might change. Can be useful for debugging.
     */
    public static final boolean ENABLE_CACHE = true;
    /**
     * Setting this to true will cause the cache to be deleted before each script run.
     * Useful for debugging.
     */
    public static final boolean DELETE_CACHE_ON_RUN = false;

    private final File cacheRoot;
    private final File scriptRoot;
    private final Map<List<StackTraceElement>, AtomicInteger> storedExceptions;
    private final Map<String, CompiledScript> index = new Object2ObjectOpenHashMap<>();

    private LoadStage currentLoadStage;

    @ApiStatus.Internal
    public GroovyScriptSandbox() {
        super(SandboxData.getRootUrls());
        this.scriptRoot = SandboxData.getScriptFile();
        this.cacheRoot = SandboxData.getCachePath();
        registerBinding("Mods", ModSupport.INSTANCE);
        registerBinding("Log", GroovyLog.get());
        registerBinding("EventManager", GroovyEventManager.INSTANCE);

        getImportCustomizer().addStaticStars(GroovyHelper.class.getName(), MathHelper.class.getName());
        getImportCustomizer().addImports(
                "net.minecraft.world.World",
                "net.minecraft.block.state.IBlockState",
                "net.minecraft.block.Block",
                "net.minecraft.block.SoundType",
                "net.minecraft.enchantment.Enchantment",
                "net.minecraft.entity.Entity",
                "net.minecraft.entity.player.EntityPlayer",
                "net.minecraft.init.Biomes",
                "net.minecraft.init.Blocks",
                "net.minecraft.init.Enchantments",
                "net.minecraft.init.Items",
                "net.minecraft.init.MobEffects",
                "net.minecraft.init.PotionTypes",
                "net.minecraft.init.SoundEvents",
                "net.minecraft.item.EnumRarity",
                "net.minecraft.item.Item",
                "net.minecraft.item.ItemStack",
                "net.minecraft.nbt.NBTTagCompound",
                "net.minecraft.nbt.NBTTagList",
                "net.minecraft.tileentity.TileEntity",
                "net.minecraft.util.math.BlockPos",
                "net.minecraft.util.DamageSource",
                "net.minecraft.util.EnumHand",
                "net.minecraft.util.EnumHandSide",
                "net.minecraft.util.EnumFacing",
                "net.minecraft.util.ResourceLocation",
                "net.minecraftforge.fml.common.eventhandler.EventPriority",
                "com.cleanroommc.groovyscript.event.EventBusType",
                "net.minecraftforge.fml.relauncher.Side",
                "net.minecraftforge.fml.relauncher.SideOnly");
        this.storedExceptions = new Object2ObjectOpenHashMap<>();
        readIndex();
    }

    private void readIndex() {
        this.index.clear();
        JsonElement jsonElement = JsonHelper.loadJson(new File(this.cacheRoot, "_index.json"));
        if (jsonElement == null || !jsonElement.isJsonObject()) return;
        JsonObject json = jsonElement.getAsJsonObject();
        int cacheVersion = json.get("version").getAsInt();
        String java = json.has("java") ? json.get("java").getAsString() : "";
        if (cacheVersion != CACHE_VERSION || !java.equals(VMPlugin.getJavaVersion())) {
            // cache version changed -> force delete cache
            deleteScriptCache();
            return;
        }
        for (JsonElement element : json.getAsJsonArray("index")) {
            if (element.isJsonObject()) {
                CompiledScript cs = CompiledScript.fromJson(element.getAsJsonObject(), this.scriptRoot.getPath(), this.cacheRoot.getPath());
                if (cs != null) {
                    this.index.put(cs.path, cs);
                }
            }
        }
    }

    private void writeIndex() {
        if (!ENABLE_CACHE) return;
        JsonObject json = new JsonObject();
        json.addProperty("!DANGER!", "DO NOT EDIT THIS FILE!!!");
        json.addProperty("version", CACHE_VERSION);
        json.addProperty("java", VMPlugin.getJavaVersion());
        JsonArray index = new JsonArray();
        json.add("index", index);
        for (Map.Entry<String, CompiledScript> entry : this.index.entrySet()) {
            index.add(entry.getValue().toJson());
        }
        JsonHelper.saveJson(new File(this.cacheRoot, "_index.json"), json);
    }

    public void checkSyntax() {
        GroovyScriptEngine engine = createScriptEngine();
        Binding binding = createBindings();
        Set<File> executedClasses = new ObjectOpenHashSet<>();

        for (LoadStage loadStage : LoadStage.getLoadStages()) {
            GroovyLog.get().info("Checking syntax in loader '{}'", this.currentLoadStage);
            this.currentLoadStage = loadStage;
            load(engine, binding, executedClasses, false);
        }
    }

    public void run(LoadStage currentLoadStage) {
        this.currentLoadStage = Objects.requireNonNull(currentLoadStage);
        try {
            super.load();
        } catch (IOException | ScriptException | ResourceException e) {
            GroovyLog.get().errorMC("An exception occurred while trying to run groovy code! This is might be a internal groovy issue.");
            GroovyLog.get().exception(e);
        } catch (Throwable t) {
            GroovyLog.get().exception(t);
        } finally {
            this.currentLoadStage = null;
            if (currentLoadStage == LoadStage.POST_INIT) {
                writeIndex();
            }
        }
    }

    @Override
    protected void runScript(Script script) {
        GroovyLog.get().info(" - running {}", script.getClass().getName());
        super.runScript(script);
    }

    @ApiStatus.Internal
    @Override
    public void load() throws Exception {
        throw new UnsupportedOperationException("Use run(Loader loader) instead!");
    }

    @ApiStatus.Internal
    @Override
    public <T> T runClosure(Closure<T> closure, Object... args) {
        boolean wasRunning = isRunning();
        if (!wasRunning) startRunning();
        T result = null;
        try {
            result = runClosureInternal(closure, args);
        } catch (Throwable t) {
            this.storedExceptions.computeIfAbsent(Arrays.asList(t.getStackTrace()), k -> {
                GroovyLog.get().error("An exception occurred while running a closure!");
                GroovyLog.get().exception(t);
                return new AtomicInteger();
            }).addAndGet(1);
        } finally {
            if (!wasRunning) stopRunning();
        }
        return result;
    }

    @GroovyBlacklist
    private static <T> T runClosureInternal(Closure<T> closure, Object[] args) {
        // original Closure.call(Object... arguments) code
        try {
            //noinspection unchecked
            return (T) closure.getMetaClass().invokeMethod(closure, "doCall", args);
        } catch (InvokerInvocationException e) {
            UncheckedThrow.rethrow(e.getCause());
            return null; // unreachable statement
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw e;
            } else {
                throw new GroovyRuntimeException(e.getMessage(), e);
            }
        }
    }

    private static String mainClassName(String name) {
        return name.contains("$") ? name.split("\\$", 2)[0] : name;
    }

    /**
     * Called via mixin when groovy compiled a class from scripts.
     */
    @ApiStatus.Internal
    public void onCompileClass(SourceUnit su, String path, Class<?> clazz, byte[] code, boolean inner) {
        String shortPath = FileUtil.relativize(this.scriptRoot.getPath(), path);
        // if the script was compiled because another script depends on it, the source unit is wrong
        // we need to find the source unit of the compiled class
        SourceUnit trueSource = su.getAST().getUnit().getScriptSourceLocation(mainClassName(clazz.getName()));
        String truePath = trueSource == null ? shortPath : FileUtil.relativize(this.scriptRoot.getPath(), trueSource.getName());
        if (shortPath.equals(truePath) && su.getAST().getMainClassName() != null && !su.getAST().getMainClassName().equals(clazz.getName())) {
            inner = true;
        }

        boolean finalInner = inner;
        CompiledScript comp = this.index.computeIfAbsent(truePath, k -> new CompiledScript(k, finalInner ? -1 : 0));
        CompiledClass innerClass = comp;
        if (inner) innerClass = comp.findInnerClass(clazz.getName());
        innerClass.onCompile(code, clazz, this.cacheRoot.getPath());
    }

    /**
     * Called via mixin when a script class needs to be recompiled. This happens when a script was loaded because another script depends on
     * it. Groovy will then try to compile the script again. If we already compiled the class we just stop the compilation process.
     */
    @ApiStatus.Internal
    public Class<?> onRecompileClass(URL source, String className) {
        String path = source.toExternalForm();
        String rel = FileUtil.relativize(this.scriptRoot.getPath(), path);
        CompiledScript cs = this.index.get(rel);
        Class<?> c = null;
        if (cs != null) {
            if (cs.clazz == null && cs.readData(this.cacheRoot.getPath())) {
                cs.ensureLoaded(getClassLoader(), this.cacheRoot.getPath());
            }
            c = cs.clazz;
        }
        return c;
    }

    @Override
    protected Class<?> loadScriptClass(GroovyScriptEngine engine, File file) {
        String relativeFileName = FileUtil.relativize(this.scriptRoot.getPath(), file.getPath());
        File relativeFile = new File(relativeFileName);
        long lastModified = file.lastModified();
        CompiledScript comp = this.index.get(relativeFileName);

        if (ENABLE_CACHE && comp != null && lastModified <= comp.lastEdited && comp.clazz == null && comp.readData(this.cacheRoot.getPath())) {
            // class is not loaded, but the cached class bytes are still valid
            if (!comp.checkPreprocessors(this.scriptRoot)) {
                return GroovyLog.class; // failed preprocessor check
            }
            comp.ensureLoaded(getClassLoader(), this.cacheRoot.getPath());

        } else if (!ENABLE_CACHE || (comp == null || comp.clazz == null || lastModified > comp.lastEdited)) {
            // class is not loaded and class bytes don't exist yet or script has been edited
            if (comp == null) {
                comp = new CompiledScript(relativeFileName, 0);
                this.index.put(relativeFileName, comp);
            }
            if (lastModified > comp.lastEdited || comp.preprocessors == null) {
                // recompile preprocessors if there is no data or script was edited
                comp.preprocessors = Preprocessor.parsePreprocessors(file);
            }
            comp.lastEdited = lastModified;
            if (!comp.checkPreprocessors(this.scriptRoot)) {
                // delete class bytes to make sure it's recompiled once the preprocessors returns true
                comp.deleteCache(this.cacheRoot.getPath());
                comp.clazz = null;
                comp.data = null;
                return GroovyLog.class; // failed preprocessor check
            }
            Class<?> clazz = super.loadScriptClass(engine, relativeFile);
            if (comp.clazz == null) {
                // should not happen
                GroovyLog.get().errorMC("Class for {} was loaded, but didn't receive class created callback!", relativeFileName);
                if (ENABLE_CACHE) comp.clazz = clazz;
            }
        } else {
            // class is loaded and script wasn't edited
            if (!comp.checkPreprocessors(this.scriptRoot)) {
                return GroovyLog.class; // failed preprocessor check
            }
            comp.ensureLoaded(getClassLoader(), this.cacheRoot.getPath());
        }
        return comp.clazz;
    }

    @Override
    protected void postInitBindings(Binding binding) {
        binding.setProperty("out", GroovyLog.get().getWriter());
        binding.setVariable("globals", getBindings());
    }

    @Override
    protected void initEngine(GroovyScriptEngine engine, CompilerConfiguration config) {
        config.addCompilationCustomizers(new GroovyScriptCompiler());
        config.addCompilationCustomizers(new GroovyScriptEarlyCompiler());
    }

    @Override
    protected void preRun() {
        if (DELETE_CACHE_ON_RUN) deleteScriptCache();
        // first clear all added events
        GroovyEventManager.INSTANCE.reset();
        if (this.currentLoadStage.isReloadable() && !ReloadableRegistryManager.isFirstLoad()) {
            // if this is not the first time this load stage is executed, reload all virtual registries
            ReloadableRegistryManager.onReload();
            // invoke reload event
            MinecraftForge.EVENT_BUS.post(new GroovyReloadEvent());
        }
        GroovyLog.get().infoMC("Running scripts in loader '{}'", this.currentLoadStage);
        // and finally invoke pre script run event
        MinecraftForge.EVENT_BUS.post(new ScriptRunEvent.Pre(this.currentLoadStage));
    }

    @Override
    protected boolean shouldRunFile(File file) {
        //GroovyLog.get().info(" - executing {}", file.toString());
        return true;
    }

    @Override
    protected void postRun() {
        if (this.currentLoadStage == LoadStage.POST_INIT) {
            ReloadableRegistryManager.afterScriptRun();
        }
        MinecraftForge.EVENT_BUS.post(new ScriptRunEvent.Post(this.currentLoadStage));
        if (this.currentLoadStage == LoadStage.POST_INIT && ReloadableRegistryManager.isFirstLoad()) {
            ReloadableRegistryManager.setLoaded();
        }
    }

    @Override
    public Collection<File> getClassFiles() {
        return GroovyScript.getRunConfig().getClassFiles(this.scriptRoot, this.currentLoadStage.getName());
    }

    @Override
    public Collection<File> getScriptFiles() {
        return GroovyScript.getRunConfig().getSortedFiles(this.scriptRoot, this.currentLoadStage.getName());
    }

    @Nullable
    public LoadStage getCurrentLoader() {
        return currentLoadStage;
    }

    public File getScriptRoot() {
        return scriptRoot;
    }

    @ApiStatus.Internal
    public boolean deleteScriptCache() {
        this.index.clear();
        getClassLoader().clearCache();
        try {
            FileUtils.cleanDirectory(this.cacheRoot);
            return true;
        } catch (IOException e) {
            GroovyScript.LOGGER.throwing(e);
            return false;
        }
    }
}
