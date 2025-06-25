package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.JsonHelper;
import com.google.common.collect.AbstractIterator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import groovy.lang.GroovyCodeSource;
import groovy.util.ResourceConnector;
import groovy.util.ResourceException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.apache.commons.io.FileUtils;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.*;
import org.codehaus.groovy.runtime.IOGroovyMethods;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.tools.gse.DependencyTracker;
import org.codehaus.groovy.tools.gse.StringSetMap;
import org.codehaus.groovy.vmplugin.VMPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.util.*;

public class CustomGroovyScriptEngine implements ResourceConnector {

    /**
     * Changing this number will force the cache to be deleted and every script has to be recompiled.
     * Useful when changes to the compilation process were made.
     */
    public static final int CACHE_VERSION = 4;
    /**
     * Setting this to false will cause compiled classes to never be cached.
     * As a side effect some compilation behaviour might change. Can be useful for debugging.
     */
    public static final boolean ENABLE_CACHE = true;
    /**
     * Setting this to true will cause the cache to be deleted before each script run.
     * Useful for debugging.
     */
    public static final boolean DELETE_CACHE_ON_RUN = Boolean.parseBoolean(System.getProperty("groovyscript.disable_cache"));

    private static WeakReference<ThreadLocal<LocalData>> localData = new WeakReference<>(null);

    private static synchronized ThreadLocal<LocalData> getLocalData() {
        ThreadLocal<LocalData> local = localData.get();
        if (local != null) return local;
        local = new ThreadLocal<>();
        localData = new WeakReference<>(local);
        return local;
    }

    private final URL[] scriptEnvironment;
    private final File cacheRoot;
    private final File scriptRoot;
    private final CompilerConfiguration config;
    private final ScriptClassLoader classLoader;
    private final Map<String, CompiledScript> index = new Object2ObjectOpenHashMap<>();
    private final Map<String, CompiledClass> loadedClasses = new Object2ObjectOpenHashMap<>();

    public CustomGroovyScriptEngine(URL[] scriptEnvironment, File cacheRoot, File scriptRoot, CompilerConfiguration config) {
        this.scriptEnvironment = scriptEnvironment;
        this.cacheRoot = cacheRoot;
        this.scriptRoot = scriptRoot;
        this.config = config;
        this.classLoader = new ScriptClassLoader(CustomGroovyScriptEngine.class.getClassLoader(), config, Collections.unmodifiableMap(this.loadedClasses));
        readIndex();
    }

    public File getScriptRoot() {
        return scriptRoot;
    }

    public File getCacheRoot() {
        return cacheRoot;
    }

    public CompilerConfiguration getConfig() {
        return config;
    }

    public GroovyScriptClassLoader getClassLoader() {
        return classLoader;
    }

    public Iterable<Class<?>> getAllLoadedScriptClasses() {
        return () -> new AbstractIterator<>() {

            private final Iterator<CompiledClass> it = loadedClasses.values().iterator();
            private Iterator<CompiledClass> innerClassesIt;

            @Override
            protected Class<?> computeNext() {
                if (innerClassesIt != null && innerClassesIt.hasNext()) {
                    return innerClassesIt.next().clazz;
                }
                innerClassesIt = null;
                CompiledClass cc;
                while (it.hasNext()) {
                    cc = it.next();
                    if (cc instanceof CompiledScript cs && !cs.preprocessorCheckFailed() && cs.clazz != null) {
                        if (!cs.innerClasses.isEmpty()) {
                            innerClassesIt = cs.innerClasses.iterator();
                        }
                        return cs.clazz;
                    }
                }
                return endOfData();
            }
        };
    }

    void readIndex() {
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
                    this.loadedClasses.put(cs.name, cs);
                    for (CompiledClass cc : cs.innerClasses) {
                        this.loadedClasses.put(cc.name, cc);
                    }
                }
            }
        }
    }

    void writeIndex() {
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

    @ApiStatus.Internal
    public boolean deleteScriptCache() {
        this.index.values().forEach(script -> script.deleteCache(this.cacheRoot.getPath()));
        this.index.clear();
        this.loadedClasses.clear();
        getClassLoader().clearCache();
        try {
            FileUtils.cleanDirectory(this.cacheRoot);
            return true;
        } catch (IOException e) {
            GroovyScript.LOGGER.throwing(e);
            return false;
        }
    }

    List<CompiledScript> findScripts(Collection<File> files) {
        List<CompiledScript> scripts = new ArrayList<>(files.size());
        for (File file : files) {
            CompiledScript cs = checkScriptLoadability(file);
            if (cs != null && !cs.preprocessorCheckFailed()) scripts.add(cs);
        }
        return scripts;
    }

    void loadScript(CompiledScript script) {
        if (script != null && script.requiresReload() && !script.preprocessorCheckFailed()) {
            Class<?> clazz = loadScriptClassInternal(new File(script.path), true);
            script.setRequiresReload(false);
            if (script.clazz == null) {
                // should not happen
                GroovyLog.get().errorMC("Class for {} was loaded, but didn't receive class created callback!", script.path);
                if (ENABLE_CACHE) script.clazz = clazz;
            }
        }
    }

    CompiledScript loadScriptClass(File file) {
        CompiledScript compiledScript = checkScriptLoadability(file);
        loadScript(compiledScript);
        return compiledScript;
    }

    CompiledScript checkScriptLoadability(File file) {
        String relativeFileName = FileUtil.relativize(this.scriptRoot.getPath(), file.getPath());
        if (!FileUtil.validateScriptPath(relativeFileName)) {
            // skip
            return null;
        }
        // File relativeFile = new File(relativeFileName);
        long lastModified = file.lastModified();
        CompiledScript comp = this.index.get(relativeFileName);

        if (ENABLE_CACHE && comp != null && lastModified <= comp.lastEdited && comp.clazz == null && comp.readData(this.cacheRoot.getPath())) {
            // class is not loaded, but the cached class bytes are still valid
            comp.setRequiresReload(false);
            if (comp.checkPreprocessorsFailed(this.scriptRoot)) {
                return comp;
            }
            comp.ensureLoaded(getClassLoader(), this.loadedClasses, this.cacheRoot.getPath());
        } else if (!ENABLE_CACHE || (comp == null || comp.clazz == null || lastModified > comp.lastEdited)) {
            // class is not loaded and class bytes don't exist yet or script has been edited
            if (comp == null) {
                comp = new CompiledScript(relativeFileName, 0);
                this.index.put(relativeFileName, comp);
            }
            if (comp.clazz != null) {
                InvokerHelper.removeClass(comp.clazz);
                comp.clazz = null;
            }
            comp.setRequiresReload(true);
            if (lastModified > comp.lastEdited || comp.preprocessors == null) {
                // recompile preprocessors if there is no data or script was edited
                comp.preprocessors = Preprocessor.parsePreprocessors(file);
            }
            comp.lastEdited = lastModified;
            if (comp.checkPreprocessorsFailed(this.scriptRoot)) {
                // delete class bytes to make sure it's recompiled once the preprocessors returns true
                comp.deleteCache(this.cacheRoot.getPath());
                return comp;
            }
        } else {
            // class is loaded and script wasn't edited
            comp.setRequiresReload(false);
            if (comp.checkPreprocessorsFailed(this.scriptRoot)) {
                return comp;
            }
            comp.ensureLoaded(getClassLoader(), this.loadedClasses, this.cacheRoot.getPath());
        }
        comp.setPreprocessorCheckFailed(false);
        return comp;
    }

    protected Class<?> loadScriptClassInternal(File file, boolean isFileRelative) {
        Class<?> scriptClass = null;
        try {
            scriptClass = parseDynamicScript(file, isFileRelative);
        } catch (Exception e) {
            GroovyLog.get().exception("An error occurred while trying to load script class " + file.toString(), e);
        }
        return scriptClass;
    }

    @Nullable
    private File findScriptFileOfClass(String className) {
        for (String ending : this.config.getScriptExtensions()) {
            File file = findScriptFile(className + "." + ending);
            if (file != null) return file;
        }
        return null;
    }

    @Nullable
    private File findScriptFile(String scriptName) {
        File file;
        for (URL root : this.scriptEnvironment) {
            try {
                File rootFile = new File(root.toURI());
                // try to combine the root with the file ending
                file = new File(rootFile, scriptName);
                if (file.exists()) {
                    // found a valid file
                    return file;
                }
            } catch (URISyntaxException e) {
                GroovyScript.LOGGER.throwing(e);
            }
        }
        return null;
    }

    private @Nullable Class<?> parseDynamicScript(File file, boolean isFileRelative) {
        if (isFileRelative) {
            File absoutefile = findScriptFile(file.getPath());
            if (absoutefile == null) {
                throw new IllegalArgumentException("Now file was found for '" + file.getPath() + "'");
            }
            file = absoutefile;
        }
        Class<?> clazz = null;
        try {
            String encoding = config.getSourceEncoding();
            String content = IOGroovyMethods.getText(new FileInputStream(file), encoding);
            clazz = this.classLoader.parseClassRaw(content, file.toPath().toUri().toURL().toExternalForm());
            // manually load the file as a groovy script
            //clazz = this.classLoader.parseClassRaw(path.toFile());
        } catch (IOException e) {
            GroovyScript.LOGGER.throwing(e);
        }
        return clazz;
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
        this.loadedClasses.put(innerClass.name, innerClass);
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
                cs.ensureLoaded(getClassLoader(), this.loadedClasses, this.cacheRoot.getPath());
            }
            c = cs.clazz;
        }
        return c;
    }

    private static String mainClassName(String name) {
        return name.contains("$") ? name.split("\\$", 2)[0] : name;
    }


    @Override
    public URLConnection getResourceConnection(String resourceName) throws ResourceException {
        // Get the URLConnection
        URLConnection groovyScriptConn = null;

        ResourceException se = null;
        for (URL root : this.scriptEnvironment) {
            URL scriptURL = null;
            try {
                scriptURL = new URL(root, resourceName);
                groovyScriptConn = openConnection(scriptURL);

                break; // Now this is a bit unusual
            } catch (MalformedURLException e) {
                String message = "Malformed URL: with context=" + root + " and spec=" + resourceName + " because " + e.getMessage();
                if (se == null) {
                    se = new ResourceException(message);
                } else {
                    se = new ResourceException(message, se);
                }
            } catch (IOException e1) {
                String message = "Cannot open URL: " + scriptURL;
                groovyScriptConn = null;
                if (se == null) {
                    se = new ResourceException(message);
                } else {
                    se = new ResourceException(message, se);
                }
            }
        }

        if (se == null) se = new ResourceException("No resource for " + resourceName + " was found");

        // If we didn't find anything, report on all the exceptions that occurred.
        if (groovyScriptConn == null) throw se;
        return groovyScriptConn;
    }

    private static URLConnection openConnection(URL scriptURL) throws IOException {
        URLConnection urlConnection = scriptURL.openConnection();
        verifyInputStream(urlConnection);

        return scriptURL.openConnection();
    }

    private static void forceClose(URLConnection urlConnection) {
        if (urlConnection != null) {
            // We need to get the input stream and close it to force the open
            // file descriptor to be released. Otherwise, we will reach the limit
            // for number of files open at one time.

            try {
                verifyInputStream(urlConnection);
            } catch (Exception e) {
                // Do nothing: We were not going to use it anyway.
            }
        }
    }

    private static void verifyInputStream(URLConnection urlConnection) throws IOException {
        try (InputStream in = urlConnection.getInputStream()) {
        }
    }

    private static class LocalData {

        CompilationUnit cu;
        final StringSetMap dependencyCache = new StringSetMap();
        final Map<String, String> precompiledEntries = new HashMap<>();
    }

    private class ScriptClassLoader extends GroovyScriptClassLoader {

        public ScriptClassLoader(ClassLoader loader, CompilerConfiguration config, Map<String, CompiledClass> cache) {
            super(loader, config, cache);
            init();
        }

        @Override
        public URL loadResource(String name) throws MalformedURLException {
            File file = CustomGroovyScriptEngine.this.findScriptFileOfClass(name);
            if (file != null) {
                return file.toURI().toURL();
            }
            return null;
        }

        @Override
        protected ClassCollector createCustomCollector(CompilationUnit unit, SourceUnit su) {
            return super.createCustomCollector(unit, su).creatClassCallback((code, clz) -> {
                onCompileClass(su, su.getName(), clz, code, clz.getName().contains("$"));
            });
        }

        @Override
        protected CompilationUnit createCompilationUnit(CompilerConfiguration configuration, CodeSource source) {
            CompilationUnit cu = super.createCompilationUnit(configuration, source);
            LocalData local = getLocalData().get();
            local.cu = cu;
            final StringSetMap cache = local.dependencyCache;
            final Map<String, String> precompiledEntries = local.precompiledEntries;

            // "." is used to transfer compilation dependencies, which will be
            // recollected later during compilation
            for (String depSourcePath : cache.get(".")) {
                try {
                    cache.get(depSourcePath);
                    cu.addSource(getResourceConnection(depSourcePath).getURL()); // todo remove usage of resource connection
                } catch (ResourceException e) {
                    /* ignore */
                }
            }

            // remove all old entries including the "." entry
            cache.clear();

            cu.addPhaseOperation((final SourceUnit sourceUnit, final GeneratorContext context, final ClassNode classNode) -> {
                // GROOVY-4013: If it is an inner class, tracking its dependencies doesn't really
                // serve any purpose and also interferes with the caching done to track dependencies
                if (classNode.getOuterClass() != null) return;
                DependencyTracker dt = new DependencyTracker(sourceUnit, cache, precompiledEntries);
                dt.visitClass(classNode);
            }, Phases.CLASS_GENERATION);

            cu.setClassNodeResolver(new ClassNodeResolver() {

                @Override
                public LookupResult findClassNode(String origName, CompilationUnit compilationUnit) {
                    String name = origName.replace('.', '/');
                    File scriptFile = CustomGroovyScriptEngine.this.findScriptFileOfClass(name);
                    if (scriptFile != null) {
                        CompiledScript result = checkScriptLoadability(scriptFile);
                        if (result != null) {
                            if (result.requiresReload() || result.clazz == null) {
                                try {
                                    return new LookupResult(compilationUnit.addSource(scriptFile.toURI().toURL()), null);
                                } catch (MalformedURLException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                return new LookupResult(null, ClassHelper.make(result.clazz));
                            }
                        }
                    }
                    return super.findClassNode(origName, compilationUnit);
                }
            });

            return cu;
        }

        @Override
        protected Class<?> recompile(URL source, String className) throws CompilationFailedException, IOException {
            if (source != null) {
                Class<?> c = CustomGroovyScriptEngine.this.onRecompileClass(source, className);
                if (c != null) {
                    return c;
                }
            }
            return super.recompile(source, className);
        }

        @Override
        public Class<?> parseClass(GroovyCodeSource codeSource, boolean shouldCacheSource) throws CompilationFailedException {
            synchronized (sourceCache) {
                File file = codeSource.getFile();
                if (file == null) {
                    file = new File(codeSource.getName());
                }
                CompiledScript compiledScript = loadScriptClass(file);
                if (compiledScript.preprocessorCheckFailed()) {
                    throw new IllegalStateException("Figure this out");
                }
                if (compiledScript.requiresReload() || compiledScript.clazz == null) {
                    compiledScript.clazz = CustomGroovyScriptEngine.this.parseDynamicScript(file, false);
                }
                return compiledScript.clazz;
            }
        }

        public Class<?> parseClassRaw(GroovyCodeSource source) {
            synchronized (sourceCache) {
                return doParseClass(source);
            }
        }

        public Class<?> parseClassRaw(File file) throws IOException {
            return parseClassRaw(new GroovyCodeSource(file, CustomGroovyScriptEngine.this.config.getSourceEncoding()));
        }

        public Class<?> parseClassRaw(final String text, final String fileName) throws CompilationFailedException {
            GroovyCodeSource gcs = new GroovyCodeSource(text, fileName, "/groovy/script");
            gcs.setCachable(false);
            return parseClassRaw(gcs);
        }

        private Class<?> doParseClass(GroovyCodeSource codeSource) {
            // local is kept as hard reference to avoid garbage collection
            ThreadLocal<LocalData> localTh = getLocalData();
            LocalData localData = new LocalData();
            localTh.set(localData);
            StringSetMap cache = localData.dependencyCache;
            Class<?> answer = null;
            try {
                answer = super.parseClass(codeSource, false);
            } finally {
                cache.clear();
                localTh.remove();
            }
            return answer;
        }
    }
}
