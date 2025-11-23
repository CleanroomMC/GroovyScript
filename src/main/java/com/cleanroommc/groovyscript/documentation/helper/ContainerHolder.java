package com.cleanroommc.groovyscript.documentation.helper;

import com.cleanroommc.groovyscript.api.IGroovyContainer;
import com.cleanroommc.groovyscript.api.INamed;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.github.bsideup.jabel.Desugar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * Holds data for the container being documented.
 * Has three helper initialization methods.
 *
 * @param id         the container id
 * @param name       the name of the container
 * @param access     the default method to access the container, often {@code mod.{id}}
 * @param header     a function that takes the import block to allow adding text before and/or after it
 * @param aliases    all aliases the container has
 * @param registries all registries the container has
 */
@Desugar
public record ContainerHolder(String id, String name, String access, Function<String, String> header,
                              Collection<String> aliases, Collection<INamed> registries) {

    public static final String BASE_ACCESS_COMPAT = "mods";

    private static final String HEADER = """
            // MODS_LOADED: %1$s
            %2$s
            log 'mod \\'%1$s\\' detected, running script'""";

    /**
     * @param id         the container id
     * @param name       the name of the container
     * @param access     the default method to access the container
     * @param log        a message to be logged to {@code groovy.log} when running the script, typically "running this file"
     * @param aliases    all aliases the container had
     * @param registries all registries the container has
     */
    public static ContainerHolder of(String id,
                                     String name,
                                     String access,
                                     String log,
                                     Collection<String> aliases,
                                     Collection<INamed> registries) {
        return new ContainerHolder(id, name, access, importBlock -> importBlock + "%nlog '" + log + "'", aliases, registries);
    }

    /**
     * @param mod the groovy container, which represents a mod.
     */
    public static ContainerHolder of(GroovyContainer<? extends GroovyPropertyContainer> mod) {
        return of(mod, mod.get().getRegistries());
    }

    /**
     * @param container  the container information
     * @param registries all registries for that container
     */
    public static ContainerHolder of(IGroovyContainer container, Collection<INamed> registries) {
        return new ContainerHolder(
                container.getModId(),
                container.getContainerName(),
                BASE_ACCESS_COMPAT + "." + container.getModId(),
                importBlock -> String.format(HEADER, container.getModId(), importBlock),
                ContainerHolder.expandAliases(container.getAliases()),
                registries);
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
