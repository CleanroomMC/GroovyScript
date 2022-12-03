package com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect;

import thaumcraft.api.aspects.Aspect;

public class AspectStack {

    private int quantity;
    private Aspect aspect;

    public AspectStack() {
        this.quantity = 0;
        this.aspect = null;
    }

    public AspectStack(String aspect) { this(aspect, 1); }

    public AspectStack(Aspect aspect) { this(aspect, 1); }

    public AspectStack(String aspect, int quantity) {
        this.aspect = Aspect.getAspect(aspect);
        this.quantity = quantity;
    }

    public AspectStack(Aspect aspect, int quantity) {
        this.aspect = aspect;
        this.quantity = quantity;
    }

    public Aspect getAspect() {
        return this.aspect;
    }

    public void setAspect(Aspect aspect) {
        this.aspect = aspect;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
