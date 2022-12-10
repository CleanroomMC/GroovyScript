package com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect;

import com.cleanroommc.groovyscript.api.IResourceStack;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.consumables.ItemPhial;

public class AspectStack implements IResourceStack {

    private int amount;
    private Aspect aspect;

    public AspectStack() {
        this.amount = 0;
        this.aspect = null;
    }

    public AspectStack(String aspect) {
        this(aspect, 1);
    }

    public AspectStack(Aspect aspect) {
        this(aspect, 1);
    }

    public AspectStack(String aspect, int amount) {
        this.aspect = Aspect.getAspect(aspect);
        this.amount = amount;
    }

    public AspectStack(Aspect aspect, int amount) {
        this.aspect = aspect;
        this.amount = amount;
    }

    public Aspect getAspect() {
        return this.aspect;
    }

    public void setAspect(Aspect aspect) {
        this.aspect = aspect;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

    public ItemStack getCrystal() {
        return ThaumcraftApiHelper.makeCrystal(this.aspect, this.amount);
    }

    public ItemStack getPhial() {
        return ItemPhial.makeFilledPhial(this.aspect);
    }
}
