package com.cleanroommc.groovyscript.core;

import com.cleanroommc.groovyscript.core.visitors.*;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class GroovyScriptTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] classBytes) {
        switch (name) {
            case InvokerHelperVisitor.CLASS_NAME: {
                ClassWriter classWriter = new ClassWriter(0);
                new ClassReader(classBytes).accept(new InvokerHelperVisitor(classWriter), 0);
                return classWriter.toByteArray();
            }
            case CachedClassMethodsVisitor.CLASS_NAME: {
                ClassWriter classWriter = new ClassWriter(0);
                new ClassReader(classBytes).accept(new CachedClassMethodsVisitor(classWriter), 0);
                return classWriter.toByteArray();
            }
            case CachedClassFieldsVisitor.CLASS_NAME: {
                ClassWriter classWriter = new ClassWriter(0);
                new ClassReader(classBytes).accept(new CachedClassFieldsVisitor(classWriter), 0);
                return classWriter.toByteArray();
            }
            case CachedClassConstructorsVisitor.CLASS_NAME: {
                ClassWriter classWriter = new ClassWriter(0);
                new ClassReader(classBytes).accept(new CachedClassConstructorsVisitor(classWriter), 0);
                return classWriter.toByteArray();
            }
            case StaticVerifierVisitor.CLASS_NAME: {
                ClassWriter classWriter = new ClassWriter(0);
                new ClassReader(classBytes).accept(new StaticVerifierVisitor(classWriter), 0);
                return classWriter.toByteArray();
            }
        }
        return classBytes;
    }
}
