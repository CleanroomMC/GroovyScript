package com.cleanroommc.groovyscript.core;

import com.cleanroommc.groovyscript.core.visitors.*;
import com.cleanroommc.groovyscript.sandbox.security.GroovySecurityManager;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class GroovyScriptTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (bytes == null) return null;
        switch (name) {
            case InvokerHelperVisitor.CLASS_NAME: {
                ClassWriter classWriter = new ClassWriter(0);
                new ClassReader(bytes).accept(new InvokerHelperVisitor(classWriter), 0);
                return classWriter.toByteArray();
            }
            case CachedClassMethodsVisitor.CLASS_NAME: {
                ClassWriter classWriter = new ClassWriter(0);
                new ClassReader(bytes).accept(new CachedClassMethodsVisitor(classWriter), 0);
                return classWriter.toByteArray();
            }
            case CachedClassFieldsVisitor.CLASS_NAME: {
                ClassWriter classWriter = new ClassWriter(0);
                new ClassReader(bytes).accept(new CachedClassFieldsVisitor(classWriter), 0);
                return classWriter.toByteArray();
            }
            case CachedClassConstructorsVisitor.CLASS_NAME: {
                ClassWriter classWriter = new ClassWriter(0);
                new ClassReader(bytes).accept(new CachedClassConstructorsVisitor(classWriter), 0);
                return classWriter.toByteArray();
            }
            case StaticVerifierVisitor.CLASS_NAME: {
                ClassWriter classWriter = new ClassWriter(0);
                new ClassReader(bytes).accept(new StaticVerifierVisitor(classWriter), 0);
                return classWriter.toByteArray();
            }
        }
        return transformSideOnly(transformedName, bytes);
    }

    private byte[] transformSideOnly(String className, byte[] bytes) {
        SideOnlyConfig.MethodSet bannedProperties = SideOnlyConfig.getRemovedProperties(FMLLaunchHandler.side(), className);
        if (bannedProperties == null) return bytes;

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        // prevent banning of classes which are blacklisted for groovy
        if (!GroovySecurityManager.INSTANCE.isValid(classNode)) {
            GroovyScriptCore.LOG.warn("Tried to remove class '{}', but class is blacklisted for groovy. Skipping this class...", className);
            return bytes;
        }

        if (bannedProperties.bansClass) {
            throw new RuntimeException(
                    String.format("Attempted to load class %s for invalid side %s", className, FMLLaunchHandler.side().name()));
        }

        classNode.fields.removeIf(field -> bannedProperties.contains(field.name));

        LambdaGatherer lambdaGatherer = new LambdaGatherer();
        Iterator<MethodNode> methods = classNode.methods.iterator();
        while (methods.hasNext()) {
            MethodNode method = methods.next();
            if (bannedProperties.contains(method.name + "()") && GroovySecurityManager.INSTANCE.isValidMethod(classNode, method.name)) {
                methods.remove();
                lambdaGatherer.accept(method);
            }
        }

        // remove dynamic synthetic lambda methods that are inside of removed methods
        for (List<Handle> dynamicLambdaHandles = lambdaGatherer.getDynamicLambdaHandles(); !dynamicLambdaHandles.isEmpty(); dynamicLambdaHandles = lambdaGatherer.getDynamicLambdaHandles()) {
            lambdaGatherer = new LambdaGatherer();
            methods = classNode.methods.iterator();
            while (methods.hasNext()) {
                MethodNode method = methods.next();
                if ((method.access & Opcodes.ACC_SYNTHETIC) == 0) continue;
                for (Handle dynamicLambdaHandle : dynamicLambdaHandles) {
                    if (method.name.equals(dynamicLambdaHandle.getName()) && method.desc.equals(dynamicLambdaHandle.getDesc())) {
                        methods.remove();
                        lambdaGatherer.accept(method);
                    }
                }
            }
        }
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private static class LambdaGatherer extends MethodVisitor {

        private static final Handle META_FACTORY = new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory",
                                                              "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;",
                                                              false);
        private final List<Handle> dynamicLambdaHandles = new ArrayList<Handle>();

        public LambdaGatherer() {
            super(Opcodes.ASM5);
        }

        public void accept(MethodNode method) {
            ListIterator<AbstractInsnNode> insnNodeIterator = method.instructions.iterator();
            while (insnNodeIterator.hasNext()) {
                AbstractInsnNode insnNode = insnNodeIterator.next();
                if (insnNode.getType() == AbstractInsnNode.INVOKE_DYNAMIC_INSN) {
                    insnNode.accept(this);
                }
            }
        }

        @Override
        public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
            if (META_FACTORY.equals(bsm)) {
                Handle dynamicLambdaHandle = (Handle) bsmArgs[1];
                dynamicLambdaHandles.add(dynamicLambdaHandle);
            }
        }

        public List<Handle> getDynamicLambdaHandles() {
            return dynamicLambdaHandles;
        }
    }
}
