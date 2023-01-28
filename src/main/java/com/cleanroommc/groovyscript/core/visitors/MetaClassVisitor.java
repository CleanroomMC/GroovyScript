package com.cleanroommc.groovyscript.core.visitors;

import com.cleanroommc.groovyscript.GroovyScript;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MetaClassVisitor extends ClassVisitor implements Opcodes {

    public static final String CLASS_NAME = "groovy.lang.MetaClassImpl";

    public static final String METHOD = "chooseMethodInternal";

    private static final String PARAM_TYPES_CLASS = "org/codehaus/groovy/reflection/ParameterTypes";
    private static final String INTERCEPTOR_CLASS = "com/cleanroommc/groovyscript/sandbox/interception/InterceptionManager";

    public MetaClassVisitor(ClassVisitor cv) {
        super(ASM5, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor visitor = super.visitMethod(access, name, desc, signature, exceptions);
        if (METHOD.equals(name)) {
            return new Method(visitor);
        }
        return visitor;
    }

    private static class Method extends MethodVisitor {

        private int[] vars = {2, 6, 11};
        private int i = 0;
        private boolean flag = false;

        public Method(MethodVisitor mv) {
            super(ASM5, mv);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
            if (opcode == INVOKEVIRTUAL && PARAM_TYPES_CLASS.equals(owner) && "isValidMethod".equals(name)) {
                flag = true;
            }
        }

        @Override
        public void visitJumpInsn(int opcode, Label label) {
            super.visitJumpInsn(opcode, label);
            if (opcode == IFEQ && flag) {
                mv.visitFieldInsn(GETSTATIC, INTERCEPTOR_CLASS, "INSTANCE", "L" + INTERCEPTOR_CLASS + ";");
                mv.visitVarInsn(ALOAD, vars[i++]);
                mv.visitMethodInsn(INVOKEVIRTUAL, INTERCEPTOR_CLASS, "isValid", "(Ljava/lang/Object;)Z", false);
                mv.visitJumpInsn(IFEQ, label);
                flag = false;
                GroovyScript.LOGGER.info("Applied method validation core mod");
            }
        }
    }
}
