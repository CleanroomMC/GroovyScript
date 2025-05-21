package com.cleanroommc.groovyscript.compat.mods.bewitchment;

import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodOverride;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES, override = @MethodOverride(method = @MethodDescription(method = "remove(Lnet/minecraft/util/ResourceLocation;)V", example = @Example("resource('bewitchment:cornucopia')"))))
public class Fortune extends ForgeRegistryWrapper<com.bewitchment.api.registry.Fortune> {

    public Fortune() {
        super(GameRegistry.findRegistry(com.bewitchment.api.registry.Fortune.class));
    }
}
