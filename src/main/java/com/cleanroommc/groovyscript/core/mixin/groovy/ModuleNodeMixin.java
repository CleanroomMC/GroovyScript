package com.cleanroommc.groovyscript.core.mixin.groovy;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.sandbox.FileUtil;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.PackageNode;
import org.codehaus.groovy.control.SourceUnit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(value = ModuleNode.class, remap = false)
public abstract class ModuleNodeMixin {

    @Shadow private PackageNode packageNode;

    @Shadow private transient SourceUnit context;

    @Inject(method = "<init>(Lorg/codehaus/groovy/control/SourceUnit;)V", at = @At("TAIL"))
    public void init(SourceUnit context, CallbackInfo ci) {
        // auto set package name
        String script = context.getName();
        String rel = FileUtil.relativize(GroovyScript.getScriptPath(), script);
        int i = rel.lastIndexOf(File.separatorChar);
        if (i >= 0) {
            // inject correct package declaration into script
            String packageName = rel.substring(0, i).replace(File.separatorChar, '.') + '.';
            this.packageNode = new PackageNode(packageName);
        }
    }

    @Inject(method = "setPackage", at = @At("HEAD"), cancellable = true)
    public void setPackage(PackageNode packageNode, CallbackInfo ci) {
        if (this.packageNode == null || this.context == null) return;
        // package name was already set -> only copy data of new node
        String cur = this.packageNode.getName();
        String newName = packageNode.getName();
        if (!cur.equals(newName)) {
            String rel = FileUtil.relativize(GroovyScript.getScriptPath(), this.context.getName());
            GroovyLog.get().error("Expected package {} but got {} in script {}", cur, newName, rel);
        }
        if (this.packageNode.getAnnotations() != null) {
            this.packageNode.getAnnotations().clear();
        }
        if (packageNode.getAnnotations() != null) {
            this.packageNode.addAnnotations(packageNode.getAnnotations());
        }
        this.packageNode.setMetaDataMap(null);
        this.packageNode.copyNodeMetaData(packageNode);
        this.packageNode.setDeclaringClass(packageNode.getDeclaringClass());
        this.packageNode.setSynthetic(packageNode.isSynthetic());
        this.packageNode.setSourcePosition(packageNode);
        ci.cancel();
    }
}
