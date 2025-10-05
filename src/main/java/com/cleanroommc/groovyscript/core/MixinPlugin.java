package com.cleanroommc.groovyscript.core;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.sandbox.MixinSandbox;
import com.cleanroommc.groovyscript.sandbox.security.GroovySecurityManager;
import com.cleanroommc.groovyscript.sandbox.security.SandboxSecurityException;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

@ApiStatus.Internal
public class MixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String s) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String s, String s1) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {}

    @Override
    public List<String> getMixins() {
        return MixinSandbox.getMixinClasses();
    }

    @Override
    public void preApply(String className, org.objectweb.asm.tree.ClassNode classNode, String mixinName, IMixinInfo info) {
        if (!GroovySecurityManager.INSTANCE.isValid(classNode, className)) {
            GroovyLog.get()
                    .exception(
                            "An exception while applying a mixin occurred.",
                            new SandboxSecurityException("Can't mixin into class '" + className + "', since it is blacklisted for groovy!"),
                            true);
        }
    }

    @Override
    public void postApply(String s, org.objectweb.asm.tree.ClassNode classNode, String s1, IMixinInfo iMixinInfo) {}
}
