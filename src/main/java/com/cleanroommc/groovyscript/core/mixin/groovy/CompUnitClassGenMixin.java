package com.cleanroommc.groovyscript.core.mixin.groovy;

import com.cleanroommc.groovyscript.sandbox.transformer.GroovyCodeFactory;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.GroovyClassVisitor;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.SourceUnit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Pseudo
@Mixin(targets = "org/codehaus/groovy/control/CompilationUnit$3", remap = false)
public class CompUnitClassGenMixin {

    @Inject(method = "call",
            at = @At(value = "INVOKE",
                     target = "Lorg/codehaus/groovy/ast/GroovyClassVisitor;visitClass(Lorg/codehaus/groovy/ast/ClassNode;)V",
                     ordinal = 4),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void call(SourceUnit source, GeneratorContext context, ClassNode classNode, CallbackInfo ci, GroovyClassVisitor visitor) {
        GroovyCodeFactory.remapOverrides(classNode);
    }
}
