package com.cleanroommc.groovyscript.compat.mods.lazyae2;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import io.github.phantamanta44.libnine.util.IDisplayableMatcher;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

public class LazyAE2 extends GroovyPropertyContainer {

    public final Aggregator aggregator = new Aggregator();
    public final Centrifuge centrifuge = new Centrifuge();
    public final Energizer energizer = new Energizer();
    public final Etcher etcher = new Etcher();

    public static IDisplayableMatcher<ItemStack> matchesIIngredient(IIngredient ingredient) {
        return IDisplayableMatcher.ofMany(() -> Arrays.asList(ingredient.getMatchingStacks()), ingredient);
    }

}
