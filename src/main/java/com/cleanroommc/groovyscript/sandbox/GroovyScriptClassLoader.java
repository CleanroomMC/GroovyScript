package com.cleanroommc.groovyscript.sandbox;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.util.CharsetToolkit;
import groovyjarjarasm.asm.ClassVisitor;
import groovyjarjarasm.asm.ClassWriter;
import net.minecraft.launchwrapper.Launch;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.control.*;
import org.codehaus.groovy.util.URLStreams;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class GroovyScriptClassLoader extends GroovyClassLoader {

    private final CompilerConfiguration config;
    private final String sourceEncoding;

    private final Map<String, CompiledClass> cache;

    GroovyScriptClassLoader(ClassLoader parent, CompilerConfiguration config, Map<String, CompiledClass> cache) {
        super(parent, config, false);
        this.config = config;
        this.sourceEncoding = initSourceEncoding(config);
        this.cache = cache;
        setResourceLoader(this::loadResource);
        setShouldRecompile(false);
    }

    private String initSourceEncoding(CompilerConfiguration config) {
        String sourceEncoding = config.getSourceEncoding();
        if (null == sourceEncoding) {
            // Keep the same default source encoding with the one used by #parseClass(InputStream, String)
            // TODO should we use org.codehaus.groovy.control.CompilerConfiguration.DEFAULT_SOURCE_ENCODING instead?
            return CharsetToolkit.getDefaultSystemCharset().name();
        }
        return sourceEncoding;
    }

    public abstract @Nullable URL loadResource(String name) throws MalformedURLException;

    @Override
    protected void setClassCacheEntry(Class cls) {}

    @Override
    protected Class<?> getClassCacheEntry(String name) {
        CompiledClass cc = this.cache.get(name);
        return cc != null ? cc.clazz : null;
    }

    @Override
    public Class<?> defineClass(ClassNode classNode, String file, String newCodeBase) {
        CodeSource codeSource = null;
        try {
            codeSource = new CodeSource(new URL("file", "", newCodeBase), (java.security.cert.Certificate[]) null);
        } catch (MalformedURLException e) {
            //swallow
        }

        CompilationUnit unit = createCompilationUnit(config, codeSource);
        ClassCollector collector = createCustomCollector(unit, classNode.getModule().getContext());
        try {
            unit.addClassNode(classNode);
            unit.setClassgenCallback(collector);
            unit.compile(Phases.CLASS_GENERATION);
            definePackageInternal(collector.generatedClass.getName());
            return collector.generatedClass;
        } catch (CompilationFailedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Parses the given code source into a Java class. If there is a class file
     * for the given code source, then no parsing is done, instead the cached class is returned.
     *
     * @param shouldCacheSource if true then the generated class will be stored in the source cache
     * @return the main class defined in the given script
     */
    @Override
    public Class<?> parseClass(final GroovyCodeSource codeSource, boolean shouldCacheSource) throws CompilationFailedException {
        return doParseClass(codeSource);
    }

    private Class<?> doParseClass(GroovyCodeSource codeSource) {
        validate(codeSource);
        Class<?> answer;  // Was neither already loaded nor compiling, so compile and add to cache.
        CompilationUnit unit = createCompilationUnit(config, codeSource.getCodeSource());
        /*if (recompile!=null && recompile || recompile==null && config.getRecompileGroovySource()) {
            unit.addFirstPhaseOperation(GroovyClassLoader.TimestampAdder.INSTANCE, CompilePhase.CLASS_GENERATION.getPhaseNumber());
        }*/
        SourceUnit su = null;
        File file = codeSource.getFile();
        if (file != null) {
            su = unit.addSource(file);
        } else {
            URL url = codeSource.getURL();
            if (url != null) {
                su = unit.addSource(url);
            } else {
                su = unit.addSource(codeSource.getName(), codeSource.getScriptText());
            }
        }

        ClassCollector collector = createCustomCollector(unit, su);
        unit.setClassgenCallback(collector);
        int goalPhase = Phases.CLASS_GENERATION;
        if (config != null && config.getTargetDirectory() != null) goalPhase = Phases.OUTPUT;
        unit.compile(goalPhase);

        answer = collector.generatedClass;
        String mainClass = su.getAST().getMainClassName();
        for (Class<?> clazz : collector.getLoadedClasses()) {
            String clazzName = clazz.getName();
            definePackageInternal(clazzName);
            setClassCacheEntry(clazz);
            if (clazzName.equals(mainClass)) answer = clazz;
        }
        return answer;
    }

    /**
     * loads a class from a file or a parent classloader.
     *
     * @param name                  of the class to be loaded
     * @param lookupScriptFiles     if false no lookup at files is done at all
     * @param preferClassOverScript if true the file lookup is only done if there is no class
     * @param resolve               see {@link java.lang.ClassLoader#loadClass(java.lang.String, boolean)}
     * @return the class found or the class created from a file lookup
     * @throws ClassNotFoundException     if the class could not be found
     * @throws CompilationFailedException if the source file could not be compiled
     */
    @Override
    public Class<?> loadClass(final String name, boolean lookupScriptFiles, boolean preferClassOverScript, boolean resolve) throws ClassNotFoundException, CompilationFailedException {
        // look into cache
        Class<?> cls = getClassCacheEntry(name);

        // enable recompilation?
        boolean recompile = isRecompilable(cls);
        if (!recompile) return cls;

        // try parent loader
        ClassNotFoundException last = null;
        try {
            Class<?> parentClassLoaderClass = Launch.classLoader.findClass(name);//super.loadClass(name, resolve);
            // always return if the parent loader was successful
            if (parentClassLoaderClass != null) return parentClassLoaderClass;
        } catch (ClassNotFoundException cnfe) {
            last = cnfe;
        } catch (NoClassDefFoundError ncdfe) {
            if (ncdfe.getMessage().indexOf("wrong name") > 0) {
                last = new ClassNotFoundException(name);
            } else {
                throw ncdfe;
            }
        }

        // at this point the loading from a parent loader failed,
        // and we want to recompile if needed.
        if (lookupScriptFiles) {
            // try groovy file
            try {
                // check if recompilation already happened.
                final Class<?> classCacheEntry = null;
                URL source = loadResource(name);
                // if recompilation fails, we want cls==null
                cls = recompile(source, name);
            } catch (IOException ioe) {
                last = new ClassNotFoundException("IOException while opening groovy source: " + name, ioe);
            } finally {
                if (cls == null) {
                    removeClassCacheEntry(name);
                } else {
                    setClassCacheEntry(cls);
                }
            }
        }

        if (cls == null) {
            // no class found, there should have been an exception before now
            if (last == null) throw new AssertionError(true);
            throw last;
        }
        return cls;
    }

    @Override
    protected Class<?> recompile(URL source, String className, Class oldClass) throws CompilationFailedException, IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * (Re)Compiles the given source.
     * This method starts the compilation of a given source, if
     * the source has changed since the class was created. For
     * this isSourceNewer is called.
     *
     * @param source    the source pointer for the compilation
     * @param className the name of the class to be generated
     * @return the old class if the source wasn't new enough, the new class else
     * @throws CompilationFailedException if the compilation failed
     * @throws IOException                if the source is not readable
     * @see #isSourceNewer(URL, Class)
     */
    protected Class<?> recompile(URL source, String className) throws CompilationFailedException, IOException {
        if (source != null) {
            String name = source.toExternalForm();
            if (isFile(source)) {
                try {
                    return parseClass(new GroovyCodeSource(new File(source.toURI()), sourceEncoding));
                } catch (URISyntaxException e) {
                    // do nothing and fall back to the other version
                }
            }
            return parseClass(new InputStreamReader(URLStreams.openUncachedStream(source), sourceEncoding), name);
        }
        return null;
    }

    /**
     * gets the time stamp of a given class. For groovy
     * generated classes this usually means to return the value
     * of the static field __timeStamp. If the parameter doesn't
     * have such a field, then Long.MAX_VALUE is returned
     *
     * @param cls the class
     * @return the time stamp
     */
    @Override
    protected long getTimeStamp(Class cls) {
        return Long.MAX_VALUE;
    }

    /**
     * This method will take a file name and try to "decode" any URL encoded characters.  For example
     * if the file name contains any spaces this method call will take the resulting %20 encoded values
     * and convert them to spaces.
     * <p>
     * This method was added specifically to fix defect:  Groovy-1787.  The defect involved a situation
     * where two scripts were sitting in a directory with spaces in its name.  The code would fail
     * when the class loader tried to resolve the file name and would choke on the URLEncoded space values.
     */
    private static String decodeFileName(String fileName) {
        String decodedFile = fileName;
        try {
            decodedFile = URLDecoder.decode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.err.println("Encountered an invalid encoding scheme when trying to use URLDecoder.decode() inside of the GroovyClassLoader.decodeFileName() method.  Returning the unencoded URL.");
            System.err.println("Please note that if you encounter this error and you have spaces in your directory you will run into issues.  Refer to GROOVY-1787 for description of this bug.");
        }

        return decodedFile;
    }

    private static boolean isFile(URL ret) {
        return ret != null && ret.getProtocol().equals("file");
    }

    private static void validate(GroovyCodeSource codeSource) {
        if (codeSource.getFile() == null && codeSource.getScriptText() == null) {
            throw new IllegalArgumentException("Script text to compile cannot be null!");
        }
    }

    @SuppressWarnings("deprecation") // TODO replace getPackage with getDefinedPackage once min JDK version >= 9
    private void definePackageInternal(String className) {
        int i = className.lastIndexOf('.');
        if (i != -1) {
            String pkgName = className.substring(0, i);
            java.lang.Package pkg = getPackage(pkgName);
            if (pkg == null) {
                definePackage(pkgName, null, null, null, null, null, null, null);
            }
        }
    }

    /**
     * creates a ClassCollector for a new compilation.
     *
     * @param unit the compilationUnit
     * @param su   the SourceUnit
     * @return the ClassCollector
     */
    protected ClassCollector createCustomCollector(CompilationUnit unit, SourceUnit su) {
        return new ClassCollector(this, unit, su);
    }

    @Override
    protected GroovyClassLoader.ClassCollector createCollector(CompilationUnit unit, SourceUnit su) {
        throw new UnsupportedOperationException();
    }

    public static class ClassCollector implements CompilationUnit.ClassgenCallback {

        private Class<?> generatedClass;
        private final GroovyScriptClassLoader cl;
        private final SourceUnit su;
        private final CompilationUnit unit;
        private final Collection<Class<?>> loadedClasses;
        private BiConsumer<byte[], Class<?>> creatClassCallback;

        protected ClassCollector(GroovyScriptClassLoader cl, CompilationUnit unit, SourceUnit su) {
            this.cl = cl;
            this.unit = unit;
            this.loadedClasses = new ArrayList<>();
            this.su = su;
        }

        public GroovyScriptClassLoader getDefiningClassLoader() {
            return cl;
        }

        protected Class<?> createClass(byte[] code, ClassNode classNode) {
            BytecodeProcessor bytecodePostprocessor = unit.getConfiguration().getBytecodePostprocessor();
            byte[] fcode = code;
            if (bytecodePostprocessor != null) {
                fcode = bytecodePostprocessor.processBytecode(classNode.getName(), fcode);
            }
            GroovyScriptClassLoader cl = getDefiningClassLoader();
            Class<?> theClass = cl.defineClass(classNode.getName(), fcode, 0, fcode.length, unit.getAST().getCodeSource());
            this.loadedClasses.add(theClass);

            if (generatedClass == null) {
                ModuleNode mn = classNode.getModule();
                SourceUnit msu = null;
                if (mn != null) msu = mn.getContext();
                ClassNode main = null;
                if (mn != null) main = mn.getClasses().get(0);
                if (msu == su && main == classNode) generatedClass = theClass;
            }

            if (this.creatClassCallback != null) {
                this.creatClassCallback.accept(code, theClass);
            }
            return theClass;
        }

        protected Class<?> onClassNode(ClassWriter classWriter, ClassNode classNode) {
            byte[] code = classWriter.toByteArray();
            return createClass(code, classNode);
        }

        @Override
        public void call(ClassVisitor classWriter, ClassNode classNode) {
            onClassNode((ClassWriter) classWriter, classNode);
        }

        public Collection<Class<?>> getLoadedClasses() {
            return this.loadedClasses;
        }

        public ClassCollector creatClassCallback(BiConsumer<byte[], Class<?>> creatClassCallback) {
            this.creatClassCallback = creatClassCallback;
            return this;
        }
    }
}
