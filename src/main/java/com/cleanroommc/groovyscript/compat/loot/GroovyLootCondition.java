package com.cleanroommc.groovyscript.compat.loot;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import groovy.lang.Closure;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Random;

public class GroovyLootCondition implements LootCondition {

    public final Closure<Object> condition;

    public GroovyLootCondition(Closure<Object> condition) {
        this.condition = condition;
        if (!Arrays.equals(condition.getParameterTypes(), new Class[]{Random.class, LootContext.class})) {
            GroovyLog.msg("Warning: LootCondition closures must take the following parameters (java.util.Random, net.minecraft.world.storage.loot.LootContext)")
                    .debug()
                    .post();
        }
    }

    @Override
    public boolean testCondition(@NotNull Random rand, @NotNull LootContext context) {
        return ClosureHelper.call(true, condition, rand, context);
    }

}
