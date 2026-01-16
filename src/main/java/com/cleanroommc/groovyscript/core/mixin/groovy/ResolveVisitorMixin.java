package com.cleanroommc.groovyscript.core.mixin.groovy;

import com.cleanroommc.groovyscript.sandbox.transformer.AsmDecompileHelper;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.classgen.VariableScopeVisitor;
import org.codehaus.groovy.control.ResolveVisitor;
import org.codehaus.groovy.control.SourceUnit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Mixin(value = ResolveVisitor.class, remap = false)
public abstract class ResolveVisitorMixin extends ClassCodeExpressionTransformer {

    @Shadow
    private ClassNode currentClass;

    @Shadow
    private ImportNode currentImport;

    @Shadow
    protected abstract boolean resolve(ClassNode type, boolean testModuleImports, boolean testDefaultImports, boolean testStaticInnerClasses);

    @Shadow
    private Map<GenericsType.GenericsTypeName, GenericsType> genericParameterNames;

    @Shadow
    protected abstract void resolveGenericsHeader(GenericsType[] types);

    @Shadow
    int phase;

    @Shadow
    protected abstract void resolveOrFail(ClassNode type, ASTNode node);

    @Shadow
    protected abstract void resolveOrFail(ClassNode type, String msg, ASTNode node, boolean preferImports);

    @Shadow
    protected abstract void checkCyclicInheritance(ClassNode node, ClassNode type);

    @Shadow
    protected abstract void checkGenericsCyclicInheritance(GenericsType[] genericsTypes);

    @Shadow
    private SourceUnit source;

    /**
     * @author brachy
     * @reason groovy tries to find client only classes from imports even on server
     */
    @Overwrite
    public void visitClass(ClassNode node) {
        ClassNode oldNode = currentClass;
        currentClass = node;

        ModuleNode module = node.getModule();
        if (!module.hasImportsResolved()) {
            for (ImportNode importNode : module.getImports()) {
                currentImport = importNode;
                ClassNode type = importNode.getType();
                if (resolve(type, false, false, true)) {
                    currentImport = null;
                    continue;
                }
                currentImport = null;
                // Mixin: added check
                if (!AsmDecompileHelper.removedSidedClasses.contains(type.getName())) {
                    addError("unable to resolve class " + type.getName(), type);
                }
            }
            for (ImportNode importNode : module.getStarImports()) {
                if (importNode.getLineNumber() > 0) {
                    currentImport = importNode;
                    String importName = importNode.getPackageName();
                    importName = importName.substring(0, importName.length() - 1);
                    ClassNode type = ClassHelper.makeWithoutCaching(importName);
                    if (resolve(type, false, false, true)) {
                        importNode.setType(type);
                    }
                    currentImport = null;
                }
            }
            for (ImportNode importNode : module.getStaticImports().values()) {
                ClassNode type = importNode.getType();
                if (!resolve(type, true, true, true)) {
                    // Mixin: added check
                    if (!AsmDecompileHelper.removedSidedClasses.contains(type.getName())) {
                        addError("unable to resolve class " + type.getName(), type);
                    }
                }
            }
            for (ImportNode importNode : module.getStaticStarImports().values()) {
                ClassNode type = importNode.getType();
                if (!resolve(type, true, true, true)) {
                    // Mixin: added check
                    if (!AsmDecompileHelper.removedSidedClasses.contains(type.getName())) {
                        addError("unable to resolve class " + type.getName(), type);
                    }
                }
            }

            module.setImportsResolved(true);
        }

        //

        if (!(node instanceof InnerClassNode) || Modifier.isStatic(node.getModifiers())) {
            genericParameterNames = new HashMap<>();
        }
        resolveGenericsHeader(node.getGenericsTypes());
        switch (phase) { // GROOVY-9866, GROOVY-10466
            case 0:
            case 1:
                ClassNode sn = node.getUnresolvedSuperClass();
                if (sn != null) {
                    resolveOrFail(sn, "", node, true);
                }
                for (ClassNode in : node.getInterfaces()) {
                    resolveOrFail(in, "", node, true);
                }

                if (sn != null) checkCyclicInheritance(node, sn);
                for (ClassNode in : node.getInterfaces()) {
                    checkCyclicInheritance(node, in);
                }
                checkGenericsCyclicInheritance(node.getGenericsTypes());
            case 2:
                // VariableScopeVisitor visits anon. inner class body inline, so resolve now
                for (Iterator<InnerClassNode> it = node.getInnerClasses(); it.hasNext();) {
                    InnerClassNode cn = it.next();
                    if (cn.isAnonymous()) {
                        MethodNode enclosingMethod = cn.getEnclosingMethod();
                        if (enclosingMethod != null) {
                            resolveGenericsHeader(enclosingMethod.getGenericsTypes()); // GROOVY-6977
                        }
                        resolveOrFail(cn.getUnresolvedSuperClass(false), cn); // GROOVY-9642
                    }
                }
                if (phase == 1) break; // resolve other class headers before members, et al.

                // initialize scopes/variables now that imports and super types are resolved
                new VariableScopeVisitor(source).visitClass(node);

                visitPackage(node.getPackage());
                visitImports(node.getModule());

                node.visitContents(this);
                visitObjectInitializerStatements(node);
                visitAnnotations(node); // GROOVY-10750, GROOVY-11206
        }
        currentClass = oldNode;
    }

    @WrapOperation(method = "transformVariableExpression", at = @At(value = "INVOKE", target = "Lorg/codehaus/groovy/control/ResolveVisitor;resolve(Lorg/codehaus/groovy/ast/ClassNode;)Z"))
    public boolean transformVariableExpression(ResolveVisitor instance, ClassNode type, Operation<Boolean> original) {
        if (currentClass.getNameWithoutPackage().equals(type.getName())) {
            // if the variable name is just the simple class name of the class its inside, assume it's not referring to itself
            return false;
        }
        return original.call(instance, type);
    }
}
