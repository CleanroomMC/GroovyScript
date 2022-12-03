package com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect;

import com.cleanroommc.groovyscript.api.GroovyLog;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectEventProxy;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.AspectRegistryEvent;

public class AspectHelper {

    private String entity;
    private ItemStack object;
    private AspectList aspects = new AspectList();

    public AspectHelper() {
        //do nothing
    }

    public AspectHelper entity(String entity) {
        this.entity = entity;
        return this;
    }

    public AspectHelper object(ItemStack object) {
        this.object = object;
        return this;
    }

    public AspectHelper aspect(AspectStack aspect) {
        this.aspects.add(aspect.getAspect(), aspect.getQuantity());
        return this;
    }

    @SuppressWarnings("deprecation")
    public AspectHelper stripAspects() {
        if (entity != null) {
            ThaumcraftApi.registerEntityTag(entity, new AspectList(), new ThaumcraftApi.EntityTagsNBT[0]);
        } else if (object != null) {
            ThaumcraftApi.registerObjectTag(object, new AspectList());
        } else {
            GroovyLog.msg("Error removing Thaumcraft Aspects from item/entity")
                    .error()
                    .post();
        }
        AspectRegistryEvent are = new AspectRegistryEvent();
        are.register = new AspectEventProxy();
        MinecraftForge.EVENT_BUS.post(are);
        return this;
    }

    @SuppressWarnings("deprecation")
    public void register() {
        if (entity != null) {
            ThaumcraftApi.registerEntityTag(entity, aspects, new ThaumcraftApi.EntityTagsNBT[0]);
        } else if (object != null) {
            ThaumcraftApi.registerObjectTag(object, aspects);
        } else {
            GroovyLog.msg("Error adding Thaumcraft Aspects to item/entity")
                    .error()
                    .post();
        }
        AspectRegistryEvent are = new AspectRegistryEvent();
        are.register = new AspectEventProxy();
        MinecraftForge.EVENT_BUS.post(are);
    }
}
