package com.cleanroommc.groovyscript.core;

import com.cleanroommc.groovyscript.sandbox.MixinSandbox;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Internal
public class LateMixinPlugin extends AbstractMixinPlugin {

    @Override
    public List<String> getMixins() {
        return MixinSandbox.getLateMixinClasses();
    }
}
