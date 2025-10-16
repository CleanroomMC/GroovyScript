package com.cleanroommc.groovyscript.documentation.helper;

import com.cleanroommc.groovyscript.api.IGroovyContainer;
import com.cleanroommc.groovyscript.api.INamed;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.github.bsideup.jabel.Desugar;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Holds data for the container being documented.
 * Has two helper initialization methods.
 *
 * @param id         the container id
 * @param name       the name of the container
 * @param access     the default method to access the container
 * @param aliases    all aliases the container has
 * @param registries all registries the container has
 */
@Desugar
public record ContainerHolder(String id, String name, String access, Collection<String> aliases, Collection<INamed> registries) {

    public static final String BASE_ACCESS_COMPAT = "mods";

    public static ContainerHolder of(GroovyContainer<? extends GroovyPropertyContainer> mod) {
        return of(mod, mod.get().getRegistries());
    }

    public static ContainerHolder of(IGroovyContainer container, Collection<INamed> registries) {
        return new ContainerHolder(container.getModId(), container.getContainerName(), BASE_ACCESS_COMPAT + "." + container.getModId(), ContainerHolder.expandAliases(container.getAliases()), new ObjectOpenHashSet<>(registries));
    }

    /**
     * @param aliases a list of base aliases to expand
     * @return prepends the basic method to access compat to each element
     */
    public static Collection<String> expandAliases(Collection<String> aliases) {
        List<String> list = new ArrayList<>();
        for (String alias : aliases) {
            list.add(BASE_ACCESS_COMPAT + "." + alias);
        }
        return list;
    }
}
