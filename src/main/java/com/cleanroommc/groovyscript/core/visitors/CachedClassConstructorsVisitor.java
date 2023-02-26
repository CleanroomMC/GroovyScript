package com.cleanroommc.groovyscript.core.visitors;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class CachedClassConstructorsVisitor extends ClassVisitor implements Opcodes {

    public static final String CLASS_NAME = "org.codehaus.groovy.reflection.CachedClass$2";
    public static final String CLASS_NAME_2 = "org/codehaus/groovy/reflection/CachedClass";
    private static final String METHOD_NAME = "initValue";

    public CachedClassConstructorsVisitor(ClassVisitor cv) {
        super(ASM5, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals(METHOD_NAME)) {
            return new InitConstructorVisitor(mv);
        }
        return mv;
    }

    public static class InitConstructorVisitor extends MethodVisitor {

        public InitConstructorVisitor(MethodVisitor mv) {
            super(ASM5, mv);
        }

        @Override
        public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
            mv.visitFieldInsn(GETFIELD, CLASS_NAME_2 + "$2", "this$0", "L" + CLASS_NAME_2 + ";");
            mv.visitMethodInsn(INVOKESTATIC, "com/cleanroommc/groovyscript/sandbox/GroovyCodeFactory", "makeConstructorsHook", "(L" + CLASS_NAME_2 + ";)Ljava/security/PrivilegedAction;", false);
        }
    }
}
