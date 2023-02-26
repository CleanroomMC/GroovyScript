package com.cleanroommc.groovyscript.compat.content;


import com.cleanroommc.groovyscript.GroovyScript;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class GroovyResourcePack extends FolderResourcePack {

    private static void makePath(File root, String... pieces) {
        File file = GroovyScript.makeFile(root, pieces);
        if (!file.isDirectory()) {
            file.mkdirs();
        }
    }

    private static void makeFile(File root, String... pieces) {
        File file = GroovyScript.makeFile(root, pieces);
        if (!file.isFile()) {
            try {
                file.getParentFile().mkdirs();
                Files.createFile(file.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public GroovyResourcePack() {
        super(GroovyScript.getScriptFile());
        File root = GroovyScript.makeFile(GroovyScript.getResourcesFile(), GroovyScript.getRunConfig().getPackId());
        makeFile(root, "lang", "en_us.lang");
        makePath(root, "models", "item");
        makePath(root, "models", "block");
        makePath(root, "textures", "items");
        makePath(root, "textures", "blocks");
        makePath(root, "blockstates");
    }

    @Override
    public @NotNull BufferedImage getPackImage() throws IOException {
        return null;
    }

    @Override
    public <T extends IMetadataSection> T getPackMetadata(@NotNull MetadataSerializer metadataSerializer, @NotNull String metadataSectionName) throws IOException {
        return null;
    }

    @Override
    public @NotNull String getPackName() {
        return "GroovyScriptResources";
    }
}