package com.cleanroommc.groovyscript.core.mixin.groovy;

import com.cleanroommc.groovyscript.sandbox.transformer.AsmDecompileHelper;
import groovy.lang.GroovyRuntimeException;
import org.codehaus.groovy.ast.decompiled.AsmDecompiler;
import org.codehaus.groovy.ast.decompiled.ClassStub;
import org.codehaus.groovy.util.URLStreams;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

@Mixin(value = AsmDecompiler.class, remap = false)
public class AsmDecompilerMixin {

    @Shadow
    @Final
    private static Map<URI, SoftReference<ClassStub>> stubCache;

    @Inject(method = "parseClass", at = @At("HEAD"), cancellable = true)
    private static void parseClass(URL url, CallbackInfoReturnable<ClassStub> cir) {
        URI uri;
        try {
            uri = url.toURI();
        } catch (URISyntaxException e) {
            throw new GroovyRuntimeException(e);
        }

        SoftReference<ClassStub> ref = stubCache.get(uri);
        ClassStub stub = (ref != null ? ref.get() : null);
        if (stub == null) {
            try (InputStream stream = new BufferedInputStream(URLStreams.openUncachedStream(url))) {
                ClassReader classReader = new ClassReader(stream);
                ClassNode classNode = new ClassNode();
                classReader.accept(classNode, 0);
                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                classNode.accept(writer);
                byte[] bytes = writer.toByteArray();
                if (!AsmDecompileHelper.remove(classNode.visibleAnnotations, AsmDecompileHelper.SIDE)) {
                    bytes = AsmDecompileHelper.transform(classNode.name, bytes);
                }

                // now decompile the class normally
                groovyjarjarasm.asm.ClassReader classReader2 = new groovyjarjarasm.asm.ClassReader(bytes);
                groovyjarjarasm.asm.ClassVisitor decompiler = AsmDecompileHelper.makeGroovyDecompiler();
                classReader2.accept(decompiler, ClassReader.SKIP_FRAMES);
                stub = AsmDecompileHelper.getDecompiledClass(decompiler);
                stubCache.put(uri, new SoftReference<>(stub));
            } catch (IOException |
                     ClassNotFoundException |
                     NoSuchFieldException |
                     NoSuchMethodException |
                     IllegalAccessException |
                     InvocationTargetException |
                     InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
        cir.setReturnValue(stub);
    }
}
