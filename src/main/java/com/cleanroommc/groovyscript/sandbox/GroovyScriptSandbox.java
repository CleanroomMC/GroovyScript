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
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GroovyScriptSandbox extends GroovySandbox {

    private final File basePath;
    private final ImportCustomizer importCustomizer = new ImportCustomizer();
    private final Map<List<StackTraceElement>, AtomicInteger> storedExceptions;

    private final Map<String, Entry> index = new Object2ObjectOpenHashMap<>();
    private final List<Entry> cachedClosures = new ArrayList<>();

    public static class Entry {

        private final String path;
        private long lastEdited;
        private byte[] data;
        private Class<?> clazz;

        public Entry(String path, long lastEdited) {
            this.path = path;
            this.lastEdited = lastEdited;
        }

        private void onCompile(byte[] data, Class<?> clazz, File basePath) {
            this.data = data;
            onCompile(clazz, basePath);
        }

        private void onCompile(Class<?> clazz, File basePath) {
            this.clazz = clazz;
            if (this.data == null) throw new IllegalStateException("The class doesnt seem to be compiled yet. (" + this.path + ")");
            try {
                File file = getDataFile(basePath);
                file.getParentFile().mkdirs();
                try (FileOutputStream stream = new FileOutputStream(file)) {
                    stream.write(this.data);
                    stream.flush();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void readData(File basePath) {
            File file = getDataFile(basePath);
            if (!file.exists()) return;
            try {
                this.data = Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private File getDataFile(File basePath) {
            return new File(basePath, "compiled/" + this.path.replace(File.separatorChar, '.') + ".clz");
        }

        public boolean isClosure() {
            return lastEdited < 0;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null) return false;
            Entry entry = (Entry) o;
            return Objects.equals(path, entry.path);
        }

        @Override
        public int hashCode() {
            return Objects.hash(path);
        }
    }

    private LoadStage currentLoadStage;

    public GroovyScriptSandbox(File basePath) throws MalformedURLException {
        super(new URL[]{basePath.toURI().toURL()});
        this.basePath = basePath;
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
        JsonElement jsonElement = JsonHelper.loadJson(new File(this.basePath, "compiled/index.json"));
        if (jsonElement == null || !jsonElement.isJsonObject()) return;
        JsonObject json = jsonElement.getAsJsonObject();
        JsonArray index = json.getAsJsonArray("index");
        for (JsonElement element : index) {
            if (element.isJsonObject()) {
                JsonObject jsonEntry = element.getAsJsonObject();
                Entry entry = new Entry(jsonEntry.get("path").getAsString(), jsonEntry.get("lm").getAsLong());
                if (new File(this.basePath, entry.path).exists()) {
                    this.index.put(entry.path, entry);
                    entry.readData(this.basePath);
                }
            }
        }
        JsonArray closures = json.getAsJsonArray("closures");
        for (JsonElement element : closures) {
            if (element.isJsonPrimitive()) {
                Entry entry = new Entry(element.getAsString(), -1);
                this.index.put(entry.path, entry);
                entry.readData(this.basePath);
                this.cachedClosures.add(entry);
            }
        }
    }

    private void writeIndex() {
        JsonObject json = new JsonObject();
        json.addProperty("!DANGER!", "DO NOT EDIT THIS FILE!!!");
        JsonArray index = new JsonArray();
        json.add("index", index);
        JsonArray closures = new JsonArray();
        json.add("closures", closures);
        for (Map.Entry<String, Entry> entry : this.index.entrySet()) {
            if (entry.getValue().isClosure()) {
                closures.add(entry.getValue().path);
            } else {
                JsonObject jsonEntry = new JsonObject();
                jsonEntry.addProperty("path", entry.getValue().path);
                jsonEntry.addProperty("lm", entry.getValue().lastEdited);
                index.add(jsonEntry);
            }
        }
        JsonHelper.saveJson(new File(this.basePath, "compiled/index.json"), json);
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

    @ApiStatus.Internal
    public void onCompileScript(String name, Class<?> clazz, byte[] code, boolean closure) {
        if (closure) {
            this.index.computeIfAbsent(name, n -> new Entry(n, -1)).onCompile(code, clazz, this.basePath);
            return;
        }
        if (File.separatorChar != '/') {
            name = name.replace('/', File.separatorChar);
        }
        String base = this.basePath.toString();
        int index = name.indexOf(base);
        if (index < 0) throw new IllegalArgumentException();
        String shortName = name.substring(index + base.length() + 1);
        this.index.computeIfAbsent(shortName, n -> new Entry(n, 0)).onCompile(code, clazz, this.basePath);
    }

    @Override
    protected Class<?> loadScriptClass(GroovyScriptEngine engine, File file) {
        long lastModified = new File(this.basePath, file.toString()).lastModified();
        Entry entry = this.index.get(file.toString());
        if (entry == null) {
            entry = new Entry(file.toString(), lastModified);
            this.index.put(file.toString(), entry);
        } else if (entry.isClosure()) {
            throw new IllegalStateException("Closures are not loaded like this. What's going on?");
        }
        if (lastModified <= entry.lastEdited && entry.clazz == null && entry.data != null) {
            String name = entry.path;
            int i = name.lastIndexOf(File.separatorChar);
            if (i >= 0) name = entry.path.substring(i + 1);
            i = name.lastIndexOf('.');
            if (i >= 0) name = name.substring(0, i);
            entry.clazz = engine.getGroovyClassLoader().defineClass(name, entry.data);
            GroovyLog.get().debug(" script {} is already compiled", file);
        } else if (entry.clazz == null || lastModified > entry.lastEdited) {
            GroovyLog.get().debug(" compiling script {}", file);
            entry.onCompile(super.loadScriptClass(engine, file), this.basePath);
            entry.lastEdited = lastModified;
        } else {
            GroovyLog.get().debug(" script {} is already compiled and loaded", file);
        }
        return entry.clazz;
    }

    @Override
    protected void postInitBindings(Binding binding) {
        binding.setProperty("out", GroovyLog.get().getWriter());
        binding.setVariable("globals", getBindings());
    }

    @Override
    protected void initEngine(GroovyScriptEngine engine, CompilerConfiguration config) {
        if (!this.cachedClosures.isEmpty()) {
            // this needs to be done in every loader for some reason
            this.cachedClosures.forEach(entry -> entry.clazz = engine.getGroovyClassLoader().defineClass(entry.path, entry.data));
        }
        config.addCompilationCustomizers(GroovyScriptCompiler.transformer());
        config.addCompilationCustomizers(this.importCustomizer);
    }

    @Override
    protected void preRun() {
        GroovyLog.get().info("Running scripts in loader '{}'", this.currentLoadStage);
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
