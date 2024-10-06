package com.cleanroommc.groovyscript.registry;

import com.cleanroommc.groovyscript.api.INamed;
import com.cleanroommc.groovyscript.helper.Alias;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Controls the name and aliases of registries.
 * <p>
 * If the empty constructor is used or null is passed in, the method {@link Alias#generateOfClass(Object)} will be called
 * with {@code this} as the parameter to create the list of aliases.
 * <p>
 * If the aliases generated from this are undesired or do not contain all desired aliases, a collection of strings may be passed in instead.
 * It is suggested to use {@link Alias} to generate this list.
 * <p>
 * The name will be the first parameter of the aliases, in lower case form.
 *
 * @see Alias
 */
public abstract class NamedRegistry implements INamed {

    protected final String name;
    private final List<String> aliases;

    public NamedRegistry() {
        this(null);
    }

    public NamedRegistry(@Nullable Collection<String> aliases) {
        Collection<String> local = aliases == null ? Alias.generateOfClass(this) : aliases;
        if (local.isEmpty()) {
            throw new IllegalArgumentException("NamedRegistry must have at least one name!");
        }
        this.aliases = Collections.unmodifiableList(local.stream().distinct().collect(Collectors.toList()));
        this.name = this.aliases.get(0).toLowerCase(Locale.ENGLISH);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

}
