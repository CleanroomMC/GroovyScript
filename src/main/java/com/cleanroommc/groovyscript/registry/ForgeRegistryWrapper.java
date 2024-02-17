package com.cleanroommc.groovyscript.registry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IReloadableForgeRegistry;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class ForgeRegistryWrapper<T extends IForgeRegistryEntry<T>> implements IScriptReloadable {

    private final IForgeRegistry<T> registry;
    private final Collection<String> aliases;

    public ForgeRegistryWrapper(IForgeRegistry<T> registry, Collection<String> aliases) {
        this.registry = Objects.requireNonNull(registry);
        this.aliases = Collections.unmodifiableCollection(aliases);
    }

    @GroovyBlacklist
    public IForgeRegistry<T> getRegistry() {
        return registry;
    }

    @GroovyBlacklist
    @Override
    public final void onReload() {
        ((IReloadableForgeRegistry<?>) registry).groovyScript$onReload();
    }

    @GroovyBlacklist
    @Override
    public void afterScriptLoad() {}

    @Override
    public Collection<String> getAliases() {
        return aliases;
    }

    public void add(T entry) {
        if (entry != null) {
            ReloadableRegistryManager.addRegistryEntry(this.registry, entry);
        }
    }

    public void remove(ResourceLocation loc) {
        Objects.requireNonNull(loc);
        ReloadableRegistryManager.removeRegistryEntry(this.registry, loc);
    }

    public void remove(String loc) {
        Objects.requireNonNull(loc);
        ReloadableRegistryManager.removeRegistryEntry(this.registry, loc);
    }

    public void removeAll() {
        for (T recipe : this.registry) {
            ReloadableRegistryManager.removeRegistryEntry(this.registry, recipe.getRegistryName());
        }
    }

    public SimpleObjectStream<T> streamRecipes() {
        return new SimpleObjectStream<>(this.registry.getValuesCollection()).setRemover(recipe -> {
            ResourceLocation key = this.registry.getKey(recipe);
            if (key != null) ReloadableRegistryManager.removeRegistryEntry(this.registry, key);
            return key != null;
        });
    }
}
