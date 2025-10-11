package com.cleanroommc.groovyscript.core;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.sandbox.security.GroovySecurityManager;
import com.cleanroommc.groovyscript.sandbox.security.SandboxSecurityException;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public abstract class AbstractMixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return false;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}


    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        if (!GroovySecurityManager.INSTANCE.isValid(targetClass, targetClassName)) {
            GroovyLog.get()
                    .exception(
                            "An exception while applying a mixin occurred.",
                            new SandboxSecurityException("Can't mixin into class '" + targetClassName + "', since it is blacklisted for groovy!"),
                            true);
        }
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
