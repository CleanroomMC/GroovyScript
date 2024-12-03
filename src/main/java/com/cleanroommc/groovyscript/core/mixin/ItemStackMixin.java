package com.cleanroommc.groovyscript.core.mixin;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.compat.vanilla.ItemStackMixinExpansion;
import com.cleanroommc.groovyscript.compat.vanilla.ItemStackTransformer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Predicate;

@Mixin(value = ItemStack.class)
public abstract class ItemStackMixin implements ItemStackMixinExpansion {

    @Unique
    protected Predicate<ItemStack> groovyScript$matchCondition;
    @Unique
    protected ItemStackTransformer groovyScript$transformer;
    @Unique
    protected Predicate<NBTTagCompound> groovyScript$nbtMatcher;
    @Unique
    protected String groovyScript$mark;

    @GroovyBlacklist
    @Override
    public ItemStack grs$getItemStack() {
        return (ItemStack) (Object) this;
    }

    @GroovyBlacklist
    @Override
    public ItemStackTransformer grs$getTransformer() {
        return groovyScript$transformer;
    }

    @GroovyBlacklist
    @Override
    public Predicate<ItemStack> grs$getMatcher() {
        return groovyScript$matchCondition;
    }

    @GroovyBlacklist
    @Override
    public Predicate<NBTTagCompound> grs$getNbtMatcher() {
        return groovyScript$nbtMatcher;
    }

    @GroovyBlacklist
    @Override
    public void grs$setTransformer(ItemStackTransformer transformer) {
        if (grs$getItemStack() != ItemStack.EMPTY) {
            this.groovyScript$transformer = transformer;
        }
    }

    @GroovyBlacklist
    @Override
    public void grs$setMatcher(Predicate<ItemStack> matcher) {
        if (grs$getItemStack() != ItemStack.EMPTY) {
            this.groovyScript$matchCondition = matcher;
        }
    }

    @GroovyBlacklist
    @Override
    public void grs$setNbtMatcher(Predicate<NBTTagCompound> nbtMatcher) {
        if (grs$getItemStack() != ItemStack.EMPTY) {
            this.groovyScript$nbtMatcher = nbtMatcher;
        }
    }

    @GroovyBlacklist
    @Override
    public @Nullable String grs$getMark() {
        return groovyScript$mark;
    }

    @GroovyBlacklist
    @Override
    public void grs$setMark(String mark) {
        this.groovyScript$mark = mark;
    }
}
