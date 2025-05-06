package com.cleanroommc.groovyscript.sandbox.transformer;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.codehaus.groovy.ast.decompiled.ClassStub;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AsmDecompileHelper {

    public static final String SIDE = FMLLaunchHandler.side().name();
    private static final boolean DEBUG = false;
    private static final List<String> transformerExceptions = new ArrayList<>();
    private static Class<?> decompilerClass;
    private static Constructor<?> decompilerConstructor;
    private static Field resultField;

    private static final Map<String, SoftReference<ClassStub>> stubCache = new Object2ObjectOpenHashMap<>();

    static {
        transformerExceptions.add("javax.");
        transformerExceptions.add("argo.");
        transformerExceptions.add("org.objectweb.asm.");
        transformerExceptions.add("com.google.common.");
        transformerExceptions.add("org.bouncycastle.");
        transformerExceptions.add("net.minecraft.launchwrapper.injector.");
    }

    public static groovyjarjarasm.asm.ClassVisitor makeGroovyDecompiler() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (decompilerClass == null) {
            decompilerClass = Class.forName("org.codehaus.groovy.ast.decompiled.AsmDecompiler$DecompilingVisitor");
            decompilerConstructor = decompilerClass.getDeclaredConstructors()[0];
            decompilerConstructor.setAccessible(true);
        }
        return (groovyjarjarasm.asm.ClassVisitor) decompilerConstructor.newInstance();
    }

    public static ClassStub getDecompiledClass(groovyjarjarasm.asm.ClassVisitor classVisitor) throws NoSuchFieldException, IllegalAccessException {
        if (resultField == null) {
            resultField = decompilerClass.getDeclaredField("result");
            resultField.setAccessible(true);
        }
        return (ClassStub) resultField.get(classVisitor);
    }

    /**
     * Finds decompiled class bytes via forge class loader.
     * This magically fixes the "class version 68" error. Which was caused by loading java classes on java > 23.
     * This will return null for java classes.
     */
    public static @Nullable ClassStub findDecompiledClass(String className) {
        SoftReference<ClassStub> ref = stubCache.get(className);
        ClassStub stub = ref == null ? null : ref.get();
        if (stub != null) return stub;
        try {
            // TODO consider transformer exclusions
            byte[] bytes = Launch.classLoader.getClassBytes(className);
            if (bytes == null) return null;
            bytes = transform(className, bytes);
            groovyjarjarasm.asm.ClassReader classReader = new groovyjarjarasm.asm.ClassReader(bytes);
            groovyjarjarasm.asm.ClassVisitor decompiler = makeGroovyDecompiler();
            classReader.accept(decompiler, ClassReader.SKIP_FRAMES);
            stub = AsmDecompileHelper.getDecompiledClass(decompiler);
            stubCache.put(className, new SoftReference<>(stub));
        } catch (IOException e) {
            return null;
        } catch (NoSuchFieldException | ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return stub;
    }

    public static byte[] transform(String className, byte[] bytes) {
        for (String s : transformerExceptions) {
            if (className.startsWith(s)) {
                return bytes;
            }
        }
        String untransformed = FMLDeobfuscatingRemapper.INSTANCE.unmap(className.replace('.', '/')).replace('/', '.');
        String transformed = FMLDeobfuscatingRemapper.INSTANCE.map(className.replace('.', '/')).replace('/', '.');
        for (IClassTransformer transformer : Launch.classLoader.getTransformers()) {
            bytes = transformer.transform(untransformed, transformed, bytes);
        }
        return bytes;
    }

    public static boolean remove(List<AnnotationNode> anns, String side) {
        if (anns == null) {
            return false;
        }
        for (AnnotationNode ann : anns) {
            if (ann.desc.equals(Type.getDescriptor(SideOnly.class))) {
                if (ann.values != null) {
                    for (int x = 0; x < ann.values.size() - 1; x += 2) {
                        Object key = ann.values.get(x);
                        Object value = ann.values.get(x + 1);
                        if (key instanceof String && key.equals("value")) {
                            if (value instanceof String[]strings) {
                                if (!strings[1].equals(side)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static short readClassVersion(byte[] classBytes) {
        int offset = 6;
        return (short) ((classBytes[offset] & 255) << 8 | classBytes[offset + 1] & 255);
    }

    public static void writeClassVersion(byte[] classBytes, short version) {
        int offset = 6;
        classBytes[offset] = (byte) ((version >> 8) & 255);
        classBytes[offset + 1] = (byte) (version & 255);
    }
}
