package com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.EntityEntry;
import thaumcraft.api.aspects.AspectList;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class AspectListHelper {

    public ItemStack item;
    public EntityEntry entity;
    public ArrayList<AspectStack> aspects;

    public AspectListHelper() {
        this.item = null;
        this.entity = null;
        this.aspects = new ArrayList<>();
    }

    public AspectListHelper(ItemStack item) {
        this.item = item;
        this.entity = null;
        this.aspects = new ArrayList<>();
    }

    public AspectListHelper(ItemStack item, ArrayList<AspectStack> aspects) {
        this.item = item;
        this.entity = null;
        this.aspects = aspects;
    }

    public AspectListHelper(EntityEntry entity) {
        this.item = null;
        this.entity = entity;
        this.aspects = new ArrayList<>();
    }

    public AspectListHelper(EntityEntry entity, ArrayList<AspectStack> aspects) {
        this.item = null;
        this.entity = entity;
        this.aspects = aspects;
    }

    public void addAspect(AspectStack aspect) {
        AtomicBoolean found = new AtomicBoolean(false);
        aspects.forEach(as -> {
            if (aspect.getAspect().equals(as.getAspect())) {
                as.setAmount(aspect.getAmount());
                found.set(true);
            }
        });

        if (!found.get()) {
            aspects.add(aspect);
        }
    }

    public AspectList getAspectList() {
        AspectList result = new AspectList();

        aspects.forEach(as -> result.add(as.getAspect(), as.getAmount()));

        return result;
    }

}
