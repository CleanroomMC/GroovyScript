package com.cleanroommc.groovyscript.gameobjects;

import com.cleanroommc.groovyscript.mapper.ObjectMapper;
import com.cleanroommc.groovyscript.mapper.ObjectMapperManager;
import com.cleanroommc.groovyscript.server.Completions;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
public class GameObjectHandlerManager {

    public static final String EMPTY = "empty", WILDCARD = "*", SPLITTER = ":";

    public static void init() {
    }

    @Nullable
    public static Object getGameObject(String name, String mainArg, Object... args) {
        return ObjectMapperManager.getGameObject(false, name, mainArg, args);
    }

    public static boolean hasGameObjectHandler(String key) {
        return ObjectMapperManager.hasObjectMapper(key);
    }

    public static ObjectMapper<?> getGameObjectHandler(String key) {
        return ObjectMapperManager.getObjectMapper(key);
    }

    public static List<ObjectMapper<?>> getConflicts(String key) {
        return ObjectMapperManager.getConflicts(key);
    }

    public static ObjectMapper<?> getGameObjectHandler(Class<?> containerClass, String key) {
        return ObjectMapperManager.getObjectMapper(containerClass, key);
    }

    public static Collection<ObjectMapper<?>> getGameObjectHandlers() {
        return ObjectMapperManager.getObjectMappers();
    }

    public static Class<?> getReturnTypeOf(String name) {
        return ObjectMapperManager.getReturnTypeOf(name);
    }

    public static void provideCompletion(String name, int index, Completions items) {
        ObjectMapperManager.provideCompletion(name, index, items);
    }
}
