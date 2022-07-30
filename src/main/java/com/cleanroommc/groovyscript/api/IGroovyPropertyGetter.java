package com.cleanroommc.groovyscript.api;

import org.jetbrains.annotations.Nullable;

public interface IGroovyPropertyGetter {

    @Nullable
    Object getProperty(String name);
}
