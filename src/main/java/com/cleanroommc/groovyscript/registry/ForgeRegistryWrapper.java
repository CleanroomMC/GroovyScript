package com.cleanroommc.groovyscript.registry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IReloadableForgeRegistry;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Collection;
import java.util.Objects;

/**
 * Class for controlling the storage of reloadable compat stored in Forge Registries.
 * <p>
 * To document this class, the annotation {@link com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription @RegistryDescription}
 * exists. This annotation <i>must</i> exist for the registry to be documented via GroovyScript.
 * <p>
 * Anything that isn't a Forge Registry should be handled via {@link VirtualizedRegistry} or an inheriting class instead.
 *
 * @param <T> the forge registry entry being stored and reloaded
 * @see com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription @RegistryDescription
 * @see VirtualizedRegistry
 */
public class ForgeRegistryWrapper<T extends IForgeRegistryEntry<T>> extends NamedRegistry implements IScriptReloadable {

    private final IForgeRegistry<T> registry;

    public ForgeRegistryWrapper(IForgeRegistry<T> registry) {
        this(registry, null);
    }

    public ForgeRegistryWrapper(IForgeRegistry<T> registry, Collection<String> aliases) {
        super(aliases);
        this.registry = Objects.requireNonNull(registry);
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

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.forgewrapper.add")
    public void add(T entry) {
        if (entry != null) {
            ReloadableRegistryManager.addRegistryEntry(this.registry, entry);
        }
    }

    @MethodDescription(description = "groovyscript.wiki.forgewrapper.removeResource")
    public void remove(ResourceLocation loc) {
        Objects.requireNonNull(loc);
        ReloadableRegistryManager.removeRegistryEntry(this.registry, loc);
    }

    @MethodDescription(description = "groovyscript.wiki.forgewrapper.removeString")
    public void remove(String loc) {
        Objects.requireNonNull(loc);
        ReloadableRegistryManager.removeRegistryEntry(this.registry, loc);
    }

    @MethodDescription(description = "groovyscript.wiki.forgewrapper.remove")
    public boolean remove(T recipe) {
        if (recipe == null) return false;
        remove(recipe.getRegistryName());
        return true;
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        for (T recipe : this.registry) {
            ReloadableRegistryManager.removeRegistryEntry(this.registry, recipe.getRegistryName());
        }
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<T> streamRecipes() {
        return new SimpleObjectStream<>(this.registry.getValuesCollection()).setRemover(recipe -> {
            ResourceLocation key = this.registry.getKey(recipe);
            if (key != null) ReloadableRegistryManager.removeRegistryEntry(this.registry, key);
            return key != null;
        });
    }
}
