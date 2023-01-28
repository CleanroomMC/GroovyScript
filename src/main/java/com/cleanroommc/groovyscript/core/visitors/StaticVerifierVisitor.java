package com.cleanroommc.groovyscript.core.visitors;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class StaticVerifierVisitor extends ClassVisitor implements Opcodes {

    public static final String CLASS_NAME = "org.codehaus.groovy.control.StaticVerifier";
    private static final String METHOD = "visitVariableExpression";

    public StaticVerifierVisitor(ClassVisitor cv) {
        super(ASM5, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals(METHOD)) {
            return new VisitVariableVisitor(mv);
        }
        return mv;
    }

    /**
     * Overwrites visit variable method to check if there is a binding for the variables name before erroring
     */
    private static class VisitVariableVisitor extends MethodVisitor {

        private final MethodVisitor mv;

        public VisitVariableVisitor(MethodVisitor mv) {
            super(ASM5, null);
            this.mv = mv;
        }

        @Override
        public void visitCode() {
            super.visitCode();
            Label label0 = new Label();
            mv.visitLabel(label0);
            mv.visitLineNumber(30, label0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/codehaus/groovy/ast/expr/VariableExpression", "getAccessedVariable", "()Lorg/codehaus/groovy/ast/Variable;", false);
            mv.visitTypeInsn(INSTANCEOF, "org/codehaus/groovy/ast/DynamicVariable");
            Label label1 = new Label();
            mv.visitJumpInsn(IFEQ, label1);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/codehaus/groovy/ast/expr/VariableExpression", "isInStaticContext", "()Z", false);
            Label label2 = new Label();
            mv.visitJumpInsn(IFNE, label2);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/codehaus/groovy/control/StaticVerifier", "inSpecialConstructorCall", "Z");
            mv.visitJumpInsn(IFEQ, label1);
            mv.visitLabel(label2);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/codehaus/groovy/control/StaticVerifier", "inClosure", "Z");
            mv.visitJumpInsn(IFNE, label1);
            Label label3 = new Label();
            mv.visitLabel(label3);
            mv.visitLineNumber(32, label3);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/codehaus/groovy/control/StaticVerifier", "methodNode", "Lorg/codehaus/groovy/ast/MethodNode;");
            Label label4 = new Label();
            mv.visitJumpInsn(IFNULL, label4);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/codehaus/groovy/control/StaticVerifier", "methodNode", "Lorg/codehaus/groovy/ast/MethodNode;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/codehaus/groovy/ast/MethodNode", "isStatic", "()Z", false);
            mv.visitJumpInsn(IFEQ, label4);
            Label label5 = new Label();
            mv.visitLabel(label5);
            mv.visitLineNumber(33, label5);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/codehaus/groovy/control/StaticVerifier", "methodNode", "Lorg/codehaus/groovy/ast/MethodNode;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/codehaus/groovy/ast/MethodNode", "getDeclaringClass", "()Lorg/codehaus/groovy/ast/ClassNode;", false);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/codehaus/groovy/ast/expr/VariableExpression", "getName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKESTATIC, "org/codehaus/groovy/control/StaticVerifier", "getDeclaredOrInheritedField", "(Lorg/codehaus/groovy/ast/ClassNode;Ljava/lang/String;)Lorg/codehaus/groovy/ast/FieldNode;", false);
            mv.visitVarInsn(ASTORE, 2);
            Label label6 = new Label();
            mv.visitLabel(label6);
            mv.visitLineNumber(34, label6);
            mv.visitVarInsn(ALOAD, 2);
            Label label7 = new Label();
            mv.visitJumpInsn(IFNULL, label7);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/codehaus/groovy/ast/FieldNode", "isStatic", "()Z", false);
            Label label8 = new Label();
            mv.visitJumpInsn(IFNE, label8);
            mv.visitLabel(label7);
            mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{"org/codehaus/groovy/ast/FieldNode"}, 0, null);
            mv.visitMethodInsn(INVOKESTATIC, "com/cleanroommc/groovyscript/GroovyScript", "getSandbox", "()Lcom/cleanroommc/groovyscript/sandbox/GroovyScriptSandbox;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/cleanroommc/groovyscript/sandbox/GroovyScriptSandbox", "getBindings", "()Ljava/util/Map;", false);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/codehaus/groovy/ast/expr/VariableExpression", "getName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "containsKey", "(Ljava/lang/Object;)Z", true);
            mv.visitJumpInsn(IFEQ, label4);
            mv.visitLabel(label8);
            mv.visitLineNumber(35, label8);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitInsn(RETURN);
            mv.visitLabel(label4);
            mv.visitLineNumber(38, label4);
            mv.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
            mv.visitMethodInsn(INVOKESTATIC, "com/cleanroommc/groovyscript/GroovyScript", "getSandbox", "()Lcom/cleanroommc/groovyscript/sandbox/GroovyScriptSandbox;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/cleanroommc/groovyscript/sandbox/GroovyScriptSandbox", "getBindings", "()Ljava/util/Map;", false);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/codehaus/groovy/ast/expr/VariableExpression", "getName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "containsKey", "(Ljava/lang/Object;)Z", true);
            Label label9 = new Label();
            mv.visitJumpInsn(IFEQ, label9);
            Label label10 = new Label();
            mv.visitLabel(label10);
            mv.visitLineNumber(39, label10);
            mv.visitInsn(RETURN);
            mv.visitLabel(label9);
            mv.visitLineNumber(41, label9);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mv.visitLdcInsn("Apparent variable '");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/codehaus/groovy/ast/expr/VariableExpression", "getName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitLdcInsn("' was found in a static scope but doesn't refer to a local variable, static field or class. Possible causes:\nYou attempted to reference a variable in the binding or an instance variable from a static context.\nYou misspelled a classname or statically imported field. Please check the spelling.\nYou attempted to use a method '");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitVarInsn(ALOAD, 1);
            Label label11 = new Label();
            mv.visitLabel(label11);
            mv.visitLineNumber(44, label11);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/codehaus/groovy/ast/expr/VariableExpression", "getName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitLdcInsn("' but left out brackets in a place not allowed by the grammar.");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitVarInsn(ALOAD, 1);
            Label label12 = new Label();
            mv.visitLabel(label12);
            mv.visitLineNumber(41, label12);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/codehaus/groovy/control/StaticVerifier", "addError", "(Ljava/lang/String;Lorg/codehaus/groovy/ast/ASTNode;)V", false);
            mv.visitLabel(label1);
            mv.visitLineNumber(46, label1);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitInsn(RETURN);
            Label label13 = new Label();
            mv.visitLabel(label13);
            mv.visitLocalVariable("fieldNode", "Lorg/codehaus/groovy/ast/FieldNode;", null, label6, label4, 2);
            mv.visitLocalVariable("this", "Lcom/cleanroommc/groovyscript/core/visitors/Test;", null, label0, label13, 0);
            mv.visitLocalVariable("ve", "Lorg/codehaus/groovy/ast/expr/VariableExpression;", null, label0, label13, 1);
            mv.visitMaxs(3, 3);
            mv.visitEnd();
        }

        public void asm() {
            //mv = classWriter.visitMethod(ACC_PUBLIC, "visitVariableExpression", "(Lorg/codehaus/groovy/ast/expr/VariableExpression;)V", null, null);
            mv.visitCode();
            Label label0 = new Label();
            mv.visitLabel(label0);
            mv.visitLineNumber(20, label0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/codehaus/groovy/ast/expr/VariableExpression", "getAccessedVariable", "()Lorg/codehaus/groovy/ast/Variable;", false);
            mv.visitTypeInsn(INSTANCEOF, "org/codehaus/groovy/ast/DynamicVariable");
            Label label1 = new Label();
            mv.visitJumpInsn(IFEQ, label1);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/codehaus/groovy/ast/expr/VariableExpression", "isInStaticContext", "()Z", false);
            Label label2 = new Label();
            mv.visitJumpInsn(IFNE, label2);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "com/cleanroommc/groovyscript/core/visitors/Test", "inSpecialConstructorCall", "Z");
            mv.visitJumpInsn(IFEQ, label1);
            mv.visitLabel(label2);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "com/cleanroommc/groovyscript/core/visitors/Test", "inClosure", "Z");
            mv.visitJumpInsn(IFNE, label1);
            Label label3 = new Label();
            mv.visitLabel(label3);
            mv.visitLineNumber(22, label3);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "com/cleanroommc/groovyscript/core/visitors/Test", "methodNode", "Lorg/codehaus/groovy/ast/MethodNode;");
            Label label4 = new Label();
            mv.visitJumpInsn(IFNULL, label4);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "com/cleanroommc/groovyscript/core/visitors/Test", "methodNode", "Lorg/codehaus/groovy/ast/MethodNode;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/codehaus/groovy/ast/MethodNode", "isStatic", "()Z", false);
            mv.visitJumpInsn(IFEQ, label4);
            Label label5 = new Label();
            mv.visitLabel(label5);
            mv.visitLineNumber(23, label5);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "com/cleanroommc/groovyscript/core/visitors/Test", "methodNode", "Lorg/codehaus/groovy/ast/MethodNode;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/codehaus/groovy/ast/MethodNode", "getDeclaringClass", "()Lorg/codehaus/groovy/ast/ClassNode;", false);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/codehaus/groovy/ast/expr/VariableExpression", "getName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKESTATIC, "com/cleanroommc/groovyscript/core/visitors/Test", "getDeclaredOrInheritedField", "(Lorg/codehaus/groovy/ast/ClassNode;Ljava/lang/String;)Lorg/codehaus/groovy/ast/FieldNode;", false);
            mv.visitVarInsn(ASTORE, 2);
            Label label6 = new Label();
            mv.visitLabel(label6);
            mv.visitLineNumber(24, label6);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitJumpInsn(IFNULL, label4);
            mv.visitVarInsn(ALOAD, 2);
            //
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/codehaus/groovy/ast/FieldNode", "isStatic", "()Z", false);
            mv.visitJumpInsn(IFEQ, label4);
            Label label7 = new Label();
            mv.visitLabel(label7);
            mv.visitLineNumber(25, label7);
            mv.visitInsn(RETURN);
            mv.visitLabel(label4);
            mv.visitLineNumber(28, label4);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mv.visitLdcInsn("Apparent variable '");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/codehaus/groovy/ast/expr/VariableExpression", "getName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitLdcInsn("' was found in a static scope but doesn't refer to a local variable, static field or class. Possible causes:\nYou attempted to reference a variable in the binding or an instance variable from a static context.\nYou misspelled a classname or statically imported field. Please check the spelling.\nYou attempted to use a method '");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitVarInsn(ALOAD, 1);
            Label label8 = new Label();
            mv.visitLabel(label8);
            mv.visitLineNumber(31, label8);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/codehaus/groovy/ast/expr/VariableExpression", "getName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitLdcInsn("' but left out brackets in a place not allowed by the grammar.");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitVarInsn(ALOAD, 1);
            Label label9 = new Label();
            mv.visitLabel(label9);
            mv.visitLineNumber(28, label9);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/cleanroommc/groovyscript/core/visitors/Test", "addError", "(Ljava/lang/String;Lorg/codehaus/groovy/ast/ASTNode;)V", false);
            mv.visitLabel(label1);
            mv.visitLineNumber(33, label1);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitInsn(RETURN);
            Label label10 = new Label();
            mv.visitLabel(label10);
            mv.visitLocalVariable("fieldNode", "Lorg/codehaus/groovy/ast/FieldNode;", null, label6, label4, 2);
            mv.visitLocalVariable("this", "Lcom/cleanroommc/groovyscript/core/visitors/Test;", null, label0, label10, 0);
            mv.visitLocalVariable("ve", "Lorg/codehaus/groovy/ast/expr/VariableExpression;", null, label0, label10, 1);
            mv.visitMaxs(3, 3);
            mv.visitEnd();
        }
    }
}
