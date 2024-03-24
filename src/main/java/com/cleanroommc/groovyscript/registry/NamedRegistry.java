package com.cleanroommc.groovyscript.registry;

import com.cleanroommc.groovyscript.api.INamed;
import com.cleanroommc.groovyscript.helper.Alias;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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

    public String getName() {
        return name;
    }

    public List<String> getAliases() {
        return aliases;
    }

}
