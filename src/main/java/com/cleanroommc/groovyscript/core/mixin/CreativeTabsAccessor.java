package com.cleanroommc.groovyscript.core.mixin;

import net.minecraft.creativetab.CreativeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreativeTabs.class)
public interface CreativeTabsAccessor {

    @Accessor("tabLabel")
    String getTabLabel2();
}
