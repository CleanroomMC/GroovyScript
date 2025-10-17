package com.cleanroommc.groovyscript.compat.mods;

import com.cleanroommc.groovyscript.api.documentation.IContainerDocumentation;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.documentation.Documentation;
import com.cleanroommc.groovyscript.documentation.Exporter;
import com.cleanroommc.groovyscript.documentation.helper.ContainerHolder;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.sandbox.LoadStage;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.NotNull;

import java.io.File;
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
            t.inWorldCrafting.addPropertyFieldsOf(t.inWorldCrafting, false);
            return t;
        });
        Set<String> aliasSet = new ObjectOpenHashSet<>(Alias.generateOf(containerName).andGenerate("Vanilla").and("mc").and("MC"));
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

    private ContainerHolder getContainer() {
        var aliases = ContainerHolder.expandAliases(getAliases());
        aliases.addAll(getAliases());
        return new ContainerHolder(getModId(), "Vanilla Registries", getModId(), importBlock -> importBlock + "\nlog 'running Vanilla Minecraft example'", aliases, new ObjectOpenHashSet<>(get().getRegistries()));
    }

    @Override
    public boolean generateExamples(File suggestedFile, LoadStage stage) {
        Exporter.generateExamples(suggestedFile, stage, getContainer());
        return false;
    }

    @Override
    public boolean generateWiki(File suggestedFolder) {
        var minecraftCompatFolder = new File(Documentation.WIKI_MINECRAFT, "helpers");
        Exporter.generateWiki(minecraftCompatFolder, getContainer());
        return false;
    }
}
