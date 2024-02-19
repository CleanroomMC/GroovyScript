package com.cleanroommc.groovyscript.compat.loot;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import groovy.lang.Closure;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Random;

public class GroovyLootFunction extends LootFunction {

    private final Closure<Object> function;

    public GroovyLootFunction(Closure<Object> function) {
        this(new LootCondition[0], function);
    }

    public GroovyLootFunction(LootCondition[] conditions, Closure<Object> function) {
        super(conditions);
        this.function = function;
        if (!Arrays.equals(function.getParameterTypes(), new Class[]{ItemStack.class, Random.class, LootContext.class})) {
            GroovyLog.msg("Warning: LootFunction closures must take the following parameters (net.minecraft.item.ItemStack, java.util.Random, net.minecraft.world.storage.loot.LootContext)")
                    .debug()
                    .post();
        }
    }

    @Override
    public @NotNull ItemStack apply(@NotNull ItemStack stack, @NotNull Random rand, @NotNull LootContext context) {
        return ClosureHelper.call(stack, function, stack, rand, context);
    }

}
