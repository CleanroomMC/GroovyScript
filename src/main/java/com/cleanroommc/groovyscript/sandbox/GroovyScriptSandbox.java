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
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.runtime.InvokerInvocationException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GroovyScriptSandbox extends GroovySandbox {

    private final File cacheRoot;
    private final File scriptRoot;
    private final ImportCustomizer importCustomizer = new ImportCustomizer();
    private final Map<List<StackTraceElement>, AtomicInteger> storedExceptions;
    /**
     * Version of the cache. Used to auto delete current cache if changes to the cache system were made.
     * 1: Default
     */
    private int cacheVersion = 1;
    private final Map<String, CompiledScript> index = new Object2ObjectOpenHashMap<>();

    public static final boolean WRITE_CACHE = true;

    private LoadStage currentLoadStage;

    public GroovyScriptSandbox(File scriptRoot, File cacheRoot) throws MalformedURLException {
        super(new URL[]{scriptRoot.toURI().toURL()});
        this.scriptRoot = scriptRoot;
        this.cacheRoot = cacheRoot;
        registerBinding("Mods", ModSupport.INSTANCE);
        registerBinding("Log", GroovyLog.get());
        registerBinding("EventManager", GroovyEventManager.INSTANCE);

        this.importCustomizer.addStaticStars(GroovyHelper.class.getName(), MathHelper.class.getName());
        registerStaticImports(GroovyHelper.class, MathHelper.class);
        this.importCustomizer.addImports("net.minecraft.world.World",
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
        this.cacheVersion = json.get("version").getAsInt();
        if (this.cacheVersion != 1) {
            // only version 1 allowed currently
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
        if (!WRITE_CACHE) return;
        JsonObject json = new JsonObject();
        json.addProperty("!DANGER!", "DO NOT EDIT THIS FILE!!!");
        json.addProperty("version", this.cacheVersion);
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
        startRunning();
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
            stopRunning();
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
        if (shortPath.equals(truePath) &&
            su.getAST().getMainClassName() != null &&
            !su.getAST().getMainClassName().equals(clazz.getName())) {
            inner = true;
        }

        boolean finalInner = inner;
        CompiledScript comp = this.index.computeIfAbsent(truePath, k -> new CompiledScript(k, finalInner ? -1 : 0));
        CompiledClass innerClass = comp;
        if (inner) innerClass = comp.findInnerClass(clazz.getName());
        innerClass.onCompile(code, clazz, this.cacheRoot.getPath());
    }

    /**
     * Called via mixin when a script class needs to be recompiled. This happens when a script was loaded because
     * another script depends on it. Groovy will then try to compile the script again. If we already compiled the class
     * we just stop the compilation process.
     */
    @ApiStatus.Internal
    public Class<?> onRecompileClass(GroovyClassLoader classLoader, URL source, String className) {
        String path = source.toExternalForm();
        CompiledScript cs = this.index.get(FileUtil.relativize(this.scriptRoot.getPath(), path));
        Class<?> c = null;
        if (cs != null) {
            if (cs.clazz == null && cs.readData(this.cacheRoot.getPath())) {
                cs.ensureLoaded(classLoader, this.cacheRoot.getPath());
            }
            c = cs.clazz;
        }
        return c;
    }

    @Override
    protected Class<?> loadScriptClass(GroovyScriptEngine engine, File file) {
        File relativeFile = this.scriptRoot.toPath().relativize(file.toPath()).toFile();
        long lastModified = file.lastModified();
        CompiledScript comp = this.index.get(relativeFile.toString());

        if (comp != null && lastModified <= comp.lastEdited && comp.clazz == null && comp.readData(this.cacheRoot.getPath())) {
            // class is not loaded, but the cached class bytes are still valid
            if (!comp.checkPreprocessors(this.scriptRoot)) {
                return GroovyLog.class; // failed preprocessor check
            }
            comp.ensureLoaded(engine.getGroovyClassLoader(), this.cacheRoot.getPath());

        } else if (comp == null || comp.clazz == null || lastModified > comp.lastEdited) {
            // class is not loaded and class bytes don't exist yet or script has been edited
            if (comp == null) {
                comp = new CompiledScript(relativeFile.toString(), 0);
                this.index.put(relativeFile.toString(), comp);
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
                GroovyLog.get().errorMC("Class for {} was loaded, but didn't receive class created callback! Index: {}", relativeFile, this.index);
                comp.clazz = clazz;
            }
        } else {
            // class is loaded and script wasn't edited
            if (!comp.checkPreprocessors(this.scriptRoot)) {
                return GroovyLog.class; // failed preprocessor check
            }
            comp.ensureLoaded(engine.getGroovyClassLoader(), this.cacheRoot.getPath());
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
        config.addCompilationCustomizers(GroovyScriptCompiler.transformer());
        config.addCompilationCustomizers(this.importCustomizer);
    }

    @Override
    protected void preRun() {
        GroovyLog.get().infoMC("Running scripts in loader '{}'", this.currentLoadStage);
        MinecraftForge.EVENT_BUS.post(new ScriptRunEvent.Pre());
        if (this.currentLoadStage.isReloadable() && !ReloadableRegistryManager.isFirstLoad()) {
            ReloadableRegistryManager.onReload();
            MinecraftForge.EVENT_BUS.post(new GroovyReloadEvent());
        }
        GroovyEventManager.INSTANCE.reset();
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
        MinecraftForge.EVENT_BUS.post(new ScriptRunEvent.Post());
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

    public ImportCustomizer getImportCustomizer() {
        return importCustomizer;
    }

    public File getScriptRoot() {
        return scriptRoot;
    }

    @ApiStatus.Internal
    public boolean deleteScriptCache() {
        this.index.clear();
        try {
            FileUtils.cleanDirectory(this.cacheRoot);
            return true;
        } catch (IOException e) {
            GroovyScript.LOGGER.throwing(e);
            return false;
        }
    }
}
