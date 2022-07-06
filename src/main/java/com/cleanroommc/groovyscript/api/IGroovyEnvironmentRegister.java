package com.cleanroommc.groovyscript.api;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public interface IGroovyEnvironmentRegister {

    default Map<Class<?>, Collection<String>> getBannedMethods() {
        return Collections.emptyMap();
    }

    default Collection<String> getBannedPackages() {
        return Collections.emptyList();
    }

    default Collection<Class<?>> getBannedClasses() {
        return Collections.emptyList();
    }
}
