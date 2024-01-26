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

import java.util.regex.Pattern;

@Mixin(value = GroovyClassLoader.ClassCollector.class, remap = false)
public class GroovyClassLoaderMixin {

    @Shadow
    @Final
    private SourceUnit su;

    @Inject(method = "createClass", at = @At("RETURN"))
    public void onCreateClass(byte[] code, ClassNode classNode, CallbackInfoReturnable<Class<?>> cir) {
        boolean closure = Pattern.compile(".*_closure[0-9]+").matcher(classNode.getName()).matches();
        GroovyScript.getSandbox().onCompileScript(closure ? classNode.getName() : su.getName(), code, closure);
    }
}
