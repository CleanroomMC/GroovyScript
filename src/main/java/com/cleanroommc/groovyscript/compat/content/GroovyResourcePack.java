package com.cleanroommc.groovyscript.compat.content;


import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.sandbox.FileUtil;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class GroovyResourcePack extends FolderResourcePack {

    public GroovyResourcePack() {
        super(GroovyScript.getScriptFile());
        if (GroovyScript.getRunConfig().isValidPackId()) {
            String root = FileUtil.makePath(GroovyScript.getResourcesFile().getPath(), GroovyScript.getRunConfig().getPackId());
            FileUtil.mkdirsAndFile(FileUtil.makeFile(root, "lang", "en_us.lang"));
            FileUtil.mkdirsAndFile(FileUtil.makeFile(root, "models", "item"));
            FileUtil.mkdirsAndFile(FileUtil.makeFile(root, "models", "block"));
            FileUtil.mkdirsAndFile(FileUtil.makeFile(root, "textures", "items"));
            FileUtil.mkdirsAndFile(FileUtil.makeFile(root, "textures", "blocks"));
            FileUtil.mkdirsAndFile(FileUtil.makeFile(root, "blockstates"));
        }
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