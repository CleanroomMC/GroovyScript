package com.cleanroommc.groovyscript.core.mixin.pyrotech;

import com.codetaylor.mc.pyrotech.modules.tech.basic.init.RegistryInitializer;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = RegistryInitializer.class, remap = false)
public class RegistryInitializerMixin {

    @Redirect(method = "createRegistries", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/registries/RegistryBuilder;create()Lnet/minecraftforge/registries/IForgeRegistry;"))
    private static <T extends IForgeRegistryEntry<T>> IForgeRegistry<T> createRegistries(RegistryBuilder<T> instance) {
        return instance.disableSaving().create();
    }
}
