package com.cleanroommc.groovyscript.core.mixin.groovy;

import org.codehaus.groovy.ast.decompiled.AsmDecompiler;
import org.codehaus.groovy.ast.decompiled.ClassStub;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.URL;

@Mixin(value = AsmDecompiler.class, remap = false)
public class AsmDecompilerMixin {

    @Inject(method = "parseClass", at = @At("HEAD"))
    private static void parseClass(URL url, CallbackInfoReturnable<ClassStub> cir) {
        // redirected in ClassNodeResolverMixin
        throw new UnsupportedOperationException();
    }
}
