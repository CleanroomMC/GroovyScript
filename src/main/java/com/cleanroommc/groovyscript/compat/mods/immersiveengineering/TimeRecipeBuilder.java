package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import org.jetbrains.annotations.ApiStatus;

@Deprecated
@ApiStatus.ScheduledForRemoval
public abstract class TimeRecipeBuilder<T> extends AbstractRecipeBuilder<T> {

    protected int time;

    public TimeRecipeBuilder<T> time(int time) {
        this.time = time;
        return this;
    }
}
