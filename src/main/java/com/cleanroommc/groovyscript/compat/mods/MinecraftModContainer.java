package com.cleanroommc.groovyscript.compat.mods;

import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public final class MinecraftModContainer extends GroovyContainer<VanillaModule> {

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
}
