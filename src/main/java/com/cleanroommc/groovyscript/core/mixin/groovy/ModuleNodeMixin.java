package com.cleanroommc.groovyscript.core.mixin.groovy;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.sandbox.FileUtil;
import com.cleanroommc.groovyscript.sandbox.RunConfig;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.PackageNode;
import org.codehaus.groovy.control.SourceUnit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModuleNode.class, remap = false)
public abstract class ModuleNodeMixin {

    @Shadow private PackageNode packageNode;

    @Shadow private transient SourceUnit context;

    @Inject(method = "<init>(Lorg/codehaus/groovy/control/SourceUnit;)V", at = @At("TAIL"))
    public void init(SourceUnit context, CallbackInfo ci) {
        // auto set package name
        String script = context.getName();
        String rel;
        if (!RunConfig.isGroovyFile(script) || (rel = FileUtil.relativizeNullable(GroovyScript.getScriptPath(), script)) == null) {
            // probably not a script file
            // can happen with traits
            return;
        }
        int i = rel.lastIndexOf('/');
        if (i >= 0) {
            // inject correct package declaration into script
            String packageName = rel.substring(0, i).replace('/', '.') + '.';
            this.packageNode = new PackageNode(packageName);
        }
    }

    @Inject(method = "setPackage", at = @At("HEAD"), cancellable = true)
    public void setPackage(PackageNode packageNode, CallbackInfo ci) {
        if (this.packageNode == null || this.context == null) return;
        String rel;
        if (!RunConfig.isGroovyFile(this.context.getName()) || (rel = FileUtil.relativizeNullable(GroovyScript.getScriptPath(), this.context.getName())) == null) {
            // probably not a script file
            // can happen with traits
            return;
        }
        // package name was already set -> only copy data of new node
        String cur = this.packageNode.getName();
        String newName = packageNode.getName();
        if (!cur.equals(newName)) {
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
