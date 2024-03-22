package com.cleanroommc.groovyscript.registry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.INamed;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.cleanroommc.groovyscript.helper.Alias;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;
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
        List<String> aliases1 = local.stream().distinct().collect(Collectors.toList());
        this.name = aliases1.get(0).toLowerCase(Locale.ROOT);
        this.aliases = Collections.unmodifiableList(aliases1);
    }

    public String getName() {
        return name;
    }

    public List<String> getAliases() {
        return aliases;
    }

}
