package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.GroovyScript;
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
import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.io.FileUtils;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GroovyScriptSandbox extends GroovySandbox {

    private final File cachePath;
    private final File basePath;
    private final ImportCustomizer importCustomizer = new ImportCustomizer();
    private final Map<List<StackTraceElement>, AtomicInteger> storedExceptions;

    private final Map<String, CompiledScript> index = new Object2ObjectOpenHashMap<>();

    public static final boolean WRITE_CACHE = true;

    private LoadStage currentLoadStage;

    public GroovyScriptSandbox(File basePath, File cachePath) throws MalformedURLException {
        super(new URL[]{basePath.toURI().toURL()});
        this.basePath = basePath;
        this.cachePath = cachePath;
        registerBinding("Mods", ModSupport.INSTANCE);
        registerBinding("Log", GroovyLog.get());
        registerBinding("EventManager", GroovyEventManager.INSTANCE);
        this.importCustomizer.addStaticStars(GroovyHelper.class.getName(), MathHelper.class.getName());
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
                                         "com.cleanroommc.groovyscript.event.EventBusType");
        this.storedExceptions = new Object2ObjectOpenHashMap<>();
        readIndex();
    }

    private void readIndex() {
        JsonElement jsonElement = JsonHelper.loadJson(new File(this.cachePath, "_index.json"));
        if (jsonElement == null || !jsonElement.isJsonObject()) return;
        JsonObject json = jsonElement.getAsJsonObject();
        JsonArray index = json.getAsJsonArray("index");
        for (JsonElement element : index) {
            if (element.isJsonObject()) {
                JsonObject jsonEntry = element.getAsJsonObject();
                CompiledScript compiledScript = new CompiledScript(jsonEntry.get("path").getAsString(), jsonEntry.get("name").getAsString(), jsonEntry.get("lm").getAsLong());
                if (new File(this.basePath, compiledScript.path).exists()) {
                    if (jsonEntry.has("inner")) {
                        for (JsonElement element1 : jsonEntry.getAsJsonArray("inner")) {
                            compiledScript.innerClasses.add(new CompiledClass(element1.getAsString()));
                        }
                    }
                    this.index.put(compiledScript.path, compiledScript);
                    //entry.readData(this.cachePath);
                }
            }
        }
    }

    private void writeIndex() {
        if (!WRITE_CACHE) return;
        JsonObject json = new JsonObject();
        json.addProperty("!DANGER!", "DO NOT EDIT THIS FILE!!!");
        JsonArray index = new JsonArray();
        json.add("index", index);
        for (Map.Entry<String, CompiledScript> entry : this.index.entrySet()) {
            JsonObject jsonEntry = compiledClassToJson(entry.getValue());
            index.add(jsonEntry);
        }
        JsonHelper.saveJson(new File(this.cachePath, "_index.json"), json);
    }

    @NotNull
    private static JsonObject compiledClassToJson(CompiledScript compiledScript) {
        JsonObject jsonEntry = new JsonObject();
        jsonEntry.addProperty("name", compiledScript.getName());
        jsonEntry.addProperty("path", compiledScript.path);
        jsonEntry.addProperty("lm", compiledScript.lastEdited);
        if (!compiledScript.innerClasses.isEmpty()) {
            JsonArray inner = new JsonArray();
            for (CompiledClass comp : compiledScript.innerClasses) {
                inner.add(comp.name);
            }
            jsonEntry.add("inner", inner);
        }
        return jsonEntry;
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

    @ApiStatus.Internal
    @Override
    public void load() throws Exception {
        throw new UnsupportedOperationException("Use run(Loader loader) instead!");
    }

    @Override
    public <T> T runClosure(Closure<T> closure, Object... args) {
        startRunning();
        T result = null;
        try {
            result = closure.call(args);
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

    private static String mainClassName(String name) {
        return name.contains("$") ? name.split("\\$", 2)[0] : name;
    }

    private String getShortPath(String path) {
        if (File.separatorChar != '/') {
            path = path.replace('/', File.separatorChar);
        }
        String base = this.basePath.toString();
        int index = path.indexOf(base);
        if (index < 0) throw new IllegalArgumentException();
        return path.substring(index + base.length() + 1);
    }

    @ApiStatus.Internal
    public void onCompileClass(SourceUnit su, String path, Class<?> clazz, byte[] code, boolean inner) {
        String shortPath = getShortPath(path);
        // if the script was compiled because another script depends on it, the source unit is wrong
        // we need to find the source unit of the compiled class
        // TODO stop groovy from force compiling script dependencies
        SourceUnit trueSource = su.getAST().getUnit().getScriptSourceLocation(mainClassName(clazz.getName()));
        String truePath = trueSource == null ? shortPath : getShortPath(trueSource.getName());
        GroovyLog.get().debugMC("Compiled class {}, path {}. Inner: {}", clazz.getName(), shortPath, inner);

        CompiledScript comp = this.index.computeIfAbsent(truePath, k -> new CompiledScript(k, inner ? -1 : 0));
        CompiledClass innerClass = comp;
        if (inner) innerClass = comp.findInnerClass(clazz.getName());
        innerClass.onCompile(code, clazz, this.cachePath);
    }

    @Override
    protected Class<?> loadScriptClass(GroovyScriptEngine engine, File file) {
        GroovyLog.get().debugMC("Loading script {}", file);
        long lastModified = new File(this.basePath, file.toString()).lastModified();
        CompiledScript comp = this.index.get(file.toString());

        if (comp != null && lastModified <= comp.lastEdited && comp.clazz == null && comp.readData(this.cachePath)) {

            GroovyLog.get().debugMC(" script {} is already compiled", file);
            comp.ensureLoaded(engine.getGroovyClassLoader(), this.cachePath);

        } else if (comp == null || comp.clazz == null || lastModified > comp.lastEdited) {
            if (comp == null) {
                comp = new CompiledScript(file.toString(), 0);
                this.index.put(file.toString(), comp);
            }
            GroovyLog.get().debugMC(" compiling script {}", file);
            Class<?> clazz = super.loadScriptClass(engine, file);
            if (comp.clazz == null) {
                GroovyLog.get().debugMC("Class for {} was loaded, but didnt receive class created callback! Index: {}", file, this.index);
                comp.clazz = clazz;
            }
            comp.lastEdited = lastModified;

        } else {
            GroovyLog.get().debugMC(" script {} is already compiled and loaded", file);
            comp.ensureLoaded(engine.getGroovyClassLoader(), this.cachePath);
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
        GroovyLog.get().info(" - executing {}", file.toString());
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
        return GroovyScript.getRunConfig().getClassFiles(this.currentLoadStage.getName());
    }

    @Override
    public Collection<File> getScriptFiles() {
        return GroovyScript.getRunConfig().getSortedFiles(this.currentLoadStage.getName());
    }

    @Nullable
    public LoadStage getCurrentLoader() {
        return currentLoadStage;
    }

    public ImportCustomizer getImportCustomizer() {
        return importCustomizer;
    }

    @ApiStatus.Internal
    public void deleteClassCache() {
        try {
            FileUtils.cleanDirectory(new File(this.basePath, "compiled"));
        } catch (IOException e) {
            GroovyScript.LOGGER.throwing(e);
        }
    }
}
