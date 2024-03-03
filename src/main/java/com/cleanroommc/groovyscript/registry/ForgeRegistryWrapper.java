package com.cleanroommc.groovyscript.registry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IReloadableForgeRegistry;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.AnvilRecipe;
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

    public boolean remove(T recipe) {
        if (recipe == null) return false;
        remove(recipe.getRegistryName());
        return true;
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        for (T recipe : this.registry) {
            ReloadableRegistryManager.removeRegistryEntry(this.registry, recipe.getRegistryName());
        }
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<T> streamRecipes() {
        return new SimpleObjectStream<>(this.registry.getValuesCollection()).setRemover(recipe -> {
            ResourceLocation key = this.registry.getKey(recipe);
            if (key != null) ReloadableRegistryManager.removeRegistryEntry(this.registry, key);
            return key != null;
        });
    }
}
