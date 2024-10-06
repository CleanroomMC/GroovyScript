package com.cleanroommc.groovyscript.sandbox.transformer;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.codehaus.groovy.ast.decompiled.ClassStub;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class AsmDecompileHelper {

    public static final String SIDE = FMLLaunchHandler.side().name();
    private static final boolean DEBUG = false;
    private static final List<String> transformerExceptions = new ArrayList<>();
    private static Class<?> decompilerClass;
    private static Constructor<?> decompilerConstructor;
    private static Field resultField;

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
                            if (value instanceof String[]) {
                                if (!((String[]) value)[1].equals(side)) {
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

    public static class DecompileVisitor extends ClassVisitor {

        private ClassStub result;

        public DecompileVisitor() {
            super(Opcodes.ASM5);
        }
    }
}
