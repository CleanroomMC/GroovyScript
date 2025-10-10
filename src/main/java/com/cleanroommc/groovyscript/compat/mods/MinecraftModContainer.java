package com.cleanroommc.groovyscript.compat.mods;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.documentation.IContainerDocumentation;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.documentation.Documentation;
import com.cleanroommc.groovyscript.documentation.Exporter;
import com.cleanroommc.groovyscript.sandbox.LoadStage;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public final class MinecraftModContainer extends GroovyContainer<VanillaModule> implements IContainerDocumentation {

    private static final String modId = "minecraft";
    private static final String containerName = "Minecraft";
    private final Supplier<VanillaModule> modProperty;
    private final Collection<String> aliases;

    MinecraftModContainer() {
        this.modProperty = Suppliers.memoize(() -> {
            VanillaModule t = VanillaModule.INSTANCE;
            t.addPropertyFieldsOf(t, false);
            return t;
        });
        Set<String> aliasSet = new ObjectOpenHashSet<>();
        aliasSet.add("mc");
        aliasSet.add("vanilla");
        aliasSet.add(modId);
        this.aliases = Collections.unmodifiableSet(aliasSet);
        ModSupport.INSTANCE.registerContainer(this);
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public @NotNull Collection<String> getAliases() {
        return aliases;
    }

    @Override
    public VanillaModule get() {
        return modProperty.get();
    }

    @Override
    public @NotNull String getModId() {
        return modId;
    }

    @Override
    public @NotNull String getContainerName() {
        return containerName;
    }

    @Override
    public void onCompatLoaded(GroovyContainer<?> container) {}

    @Override
    public void generateExamples(File suggestedFile, LoadStage stage) {
        Exporter.generateExamples(suggestedFile, stage, this);
    }

    @Override
    public void generateWiki(File suggestedFolder) {
        var minecraftCompatFolder = new File(new File(Documentation.WIKI, "minecraft"), "helpers");
        try {
            Files.createDirectories(minecraftCompatFolder.toPath());
            Exporter.generateWiki(minecraftCompatFolder, this);
            Exporter.exportFile(new File(minecraftCompatFolder, "index.md"), "wiki/vanilla/index.md");
        } catch (IOException e) {
            GroovyScript.LOGGER.throwing(e);
        }
    }
}
