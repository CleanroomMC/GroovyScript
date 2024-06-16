package com.cleanroommc.groovyscript.core.mixin.groovy;

import org.codehaus.groovy.ast.ImportNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = ModuleNode.class, remap = false)
public interface ModuleNodeAccessor {

    @Accessor("imports")
    List<ImportNode> getModifiableImports();
}
