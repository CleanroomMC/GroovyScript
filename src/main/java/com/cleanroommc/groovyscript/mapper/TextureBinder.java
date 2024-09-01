package com.cleanroommc.groovyscript.mapper;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public interface TextureBinder<T> extends Consumer<T> {

    static TextureBinder<ResourceLocation> ofResource() {
        return resource -> Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
    }
}
