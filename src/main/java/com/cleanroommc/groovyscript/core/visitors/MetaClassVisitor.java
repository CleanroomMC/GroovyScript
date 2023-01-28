package com.cleanroommc.groovyscript.core.visitors;

import com.cleanroommc.groovyscript.GroovyScript;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MetaClassVisitor extends ClassVisitor implements Opcodes {

    public static final String CLASS_NAME = "groovy.lang.MetaClassImpl";

    public static final String METHOD = "chooseMethodInternal";
    public static final String METHOD_2 = "invokeStaticMissingProperty";

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
        if (METHOD_2.equals(name)) {
            return new StaticMissingProperty(visitor);
        }
        return visitor;
    }

    /**
     * Adds an extra validation to method finding
     */
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

    /**
     * Overwrites the missing static property method to check if there is a binding with that name
     */
    private static class StaticMissingProperty extends MethodVisitor {

        private final MethodVisitor mv;

        public StaticMissingProperty(MethodVisitor mv) {
            super(ASM5, null);
            this.mv = mv;
        }

        @Override
        public void visitCode() {
            super.visitCode();
            Label label0 = new Label();
            mv.visitLabel(label0);
            mv.visitLineNumber(51, label0);
            mv.visitMethodInsn(INVOKESTATIC, "com/cleanroommc/groovyscript/GroovyScript", "getSandbox", "()Lcom/cleanroommc/groovyscript/sandbox/GroovyScriptSandbox;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/cleanroommc/groovyscript/sandbox/GroovyScriptSandbox", "getBindings", "()Ljava/util/Map;", false);
            mv.visitVarInsn(ASTORE, 5);
            Label label1 = new Label();
            mv.visitLabel(label1);
            mv.visitLineNumber(52, label1);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "containsKey", "(Ljava/lang/Object;)Z", true);
            Label label2 = new Label();
            mv.visitJumpInsn(IFEQ, label2);
            Label label3 = new Label();
            mv.visitLabel(label3);
            mv.visitLineNumber(53, label3);
            mv.visitVarInsn(ILOAD, 4);
            Label label4 = new Label();
            mv.visitJumpInsn(IFEQ, label4);
            Label label5 = new Label();
            mv.visitLabel(label5);
            mv.visitLineNumber(54, label5);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
            mv.visitInsn(ARETURN);
            mv.visitLabel(label4);
            mv.visitLineNumber(56, label4);
            mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{"java/util/Map"}, 0, null);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
            mv.visitInsn(POP);
            Label label6 = new Label();
            mv.visitLabel(label6);
            mv.visitLineNumber(57, label6);
            mv.visitInsn(ACONST_NULL);
            mv.visitInsn(ARETURN);
            mv.visitLabel(label2);
            mv.visitLineNumber(61, label2);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(INSTANCEOF, "java/lang/Class");
            Label label7 = new Label();
            mv.visitJumpInsn(IFEQ, label7);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "groovy/lang/MetaClassImpl", "registry", "Lgroovy/lang/MetaClassRegistry;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, "java/lang/Class");
            mv.visitMethodInsn(INVOKEINTERFACE, "groovy/lang/MetaClassRegistry", "getMetaClass", "(Ljava/lang/Class;)Lgroovy/lang/MetaClass;", true);
            Label label8 = new Label();
            mv.visitJumpInsn(GOTO, label8);
            mv.visitLabel(label7);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitLabel(label8);
            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"groovy/lang/MetaClass"});
            mv.visitVarInsn(ASTORE, 6);
            Label label9 = new Label();
            mv.visitLabel(label9);
            mv.visitLineNumber(62, label9);
            mv.visitVarInsn(ILOAD, 4);
            Label label10 = new Label();
            mv.visitJumpInsn(IFEQ, label10);
            Label label11 = new Label();
            mv.visitLabel(label11);
            mv.visitLineNumber(63, label11);
            mv.visitVarInsn(ALOAD, 6);
            mv.visitLdcInsn("$static_propertyMissing");
            mv.visitFieldInsn(GETSTATIC, "groovy/lang/MetaClassImpl", "GETTER_MISSING_ARGS", "[Ljava/lang/Class;");
            mv.visitMethodInsn(INVOKEINTERFACE, "groovy/lang/MetaClass", "getMetaMethod", "(Ljava/lang/String;[Ljava/lang/Object;)Lgroovy/lang/MetaMethod;", true);
            mv.visitVarInsn(ASTORE, 7);
            Label label12 = new Label();
            mv.visitLabel(label12);
            mv.visitLineNumber(64, label12);
            mv.visitVarInsn(ALOAD, 7);
            Label label13 = new Label();
            mv.visitJumpInsn(IFNULL, label13);
            Label label14 = new Label();
            mv.visitLabel(label14);
            mv.visitLineNumber(65, label14);
            mv.visitVarInsn(ALOAD, 7);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_1);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKEVIRTUAL, "groovy/lang/MetaMethod", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitInsn(ARETURN);
            mv.visitLabel(label13);
            mv.visitLineNumber(67, label13);
            mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{"groovy/lang/MetaClass"}, 0, null);
            Label label15 = new Label();
            mv.visitJumpInsn(GOTO, label15);
            mv.visitLabel(label10);
            mv.visitLineNumber(68, label10);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 6);
            mv.visitLdcInsn("$static_propertyMissing");
            mv.visitFieldInsn(GETSTATIC, "groovy/lang/MetaClassImpl", "SETTER_MISSING_ARGS", "[Ljava/lang/Class;");
            mv.visitMethodInsn(INVOKEINTERFACE, "groovy/lang/MetaClass", "getMetaMethod", "(Ljava/lang/String;[Ljava/lang/Object;)Lgroovy/lang/MetaMethod;", true);
            mv.visitVarInsn(ASTORE, 7);
            Label label16 = new Label();
            mv.visitLabel(label16);
            mv.visitLineNumber(69, label16);
            mv.visitVarInsn(ALOAD, 7);
            mv.visitJumpInsn(IFNULL, label15);
            Label label17 = new Label();
            mv.visitLabel(label17);
            mv.visitLineNumber(70, label17);
            mv.visitVarInsn(ALOAD, 7);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_2);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_1);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKEVIRTUAL, "groovy/lang/MetaMethod", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitInsn(ARETURN);
            mv.visitLabel(label15);
            mv.visitLineNumber(74, label15);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(INSTANCEOF, "java/lang/Class");
            Label label18 = new Label();
            mv.visitJumpInsn(IFEQ, label18);
            Label label19 = new Label();
            mv.visitLabel(label19);
            mv.visitLineNumber(75, label19);
            mv.visitTypeInsn(NEW, "groovy/lang/MissingPropertyException");
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, "java/lang/Class");
            mv.visitMethodInsn(INVOKESPECIAL, "groovy/lang/MissingPropertyException", "<init>", "(Ljava/lang/String;Ljava/lang/Class;)V", false);
            mv.visitInsn(ATHROW);
            mv.visitLabel(label18);
            mv.visitLineNumber(77, label18);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitTypeInsn(NEW, "groovy/lang/MissingPropertyException");
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "groovy/lang/MetaClassImpl", "theClass", "Ljava/lang/Class;");
            mv.visitMethodInsn(INVOKESPECIAL, "groovy/lang/MissingPropertyException", "<init>", "(Ljava/lang/String;Ljava/lang/Class;)V", false);
            mv.visitInsn(ATHROW);
            Label label20 = new Label();
            mv.visitLabel(label20);
            mv.visitLocalVariable("propertyMissing", "Lgroovy/lang/MetaMethod;", null, label12, label13, 7);
            mv.visitLocalVariable("propertyMissing", "Lgroovy/lang/MetaMethod;", null, label16, label15, 7);
            mv.visitLocalVariable("this", "Lgroovy/lang/MetaClassImpl;", null, label0, label20, 0);
            mv.visitLocalVariable("instance", "Ljava/lang/Object;", null, label0, label20, 1);
            mv.visitLocalVariable("propertyName", "Ljava/lang/String;", null, label0, label20, 2);
            mv.visitLocalVariable("optionalValue", "Ljava/lang/Object;", null, label0, label20, 3);
            mv.visitLocalVariable("isGetter", "Z", null, label0, label20, 4);
            mv.visitLocalVariable("bindings", "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;", label1, label20, 5);
            mv.visitLocalVariable("mc", "Lgroovy/lang/MetaClass;", null, label9, label20, 6);
            mv.visitMaxs(6, 8);
            mv.visitEnd();
        }
    }
}
