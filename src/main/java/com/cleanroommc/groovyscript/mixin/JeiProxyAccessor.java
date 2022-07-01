package com.cleanroommc.groovyscript.mixin;

import mezz.jei.api.IModPlugin;
import mezz.jei.gui.textures.Textures;
import mezz.jei.startup.JeiStarter;
import mezz.jei.startup.ProxyCommonClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ProxyCommonClient.class)
public interface JeiProxyAccessor {

    @Accessor
    List<IModPlugin> getPlugins();

    @Accessor
    JeiStarter getStarter();

    @Accessor
    Textures getTextures();
}
