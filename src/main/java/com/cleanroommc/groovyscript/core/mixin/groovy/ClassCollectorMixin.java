package com.cleanroommc.groovyscript.core.mixin.groovy;

import com.cleanroommc.groovyscript.GroovyScript;
import groovy.lang.GroovyClassLoader;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.control.SourceUnit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GroovyClassLoader.ClassCollector.class, remap = false)
public class ClassCollectorMixin {

    @Shadow
    @Final
    private SourceUnit su;

    @Inject(method = "createClass", at = @At("RETURN"))
    public void onCreateClass(byte[] code, ClassNode classNode, CallbackInfoReturnable<Class<?>> cir) {
        GroovyScript.getSandbox().onCompileClass(su, su.getName(), cir.getReturnValue(), code, classNode.getName().contains("$"));
    }
}
