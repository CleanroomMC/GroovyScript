package com.cleanroommc.groovyscript.gameobjects;

import com.cleanroommc.groovyscript.mapper.ObjectMapper;
import org.jetbrains.annotations.ApiStatus;

@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
public class GameObjectHandler<T> {

    @ApiStatus.Internal
    public static <T> ObjectMapper.Builder<T> builder(String name, Class<T> returnTpe) {
        return ObjectMapper.builder(name, returnTpe);
    }
}
