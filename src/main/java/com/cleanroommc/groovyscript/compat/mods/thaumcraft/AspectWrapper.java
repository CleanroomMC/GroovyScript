package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectEventProxy;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.AspectRegistryEvent;
import thaumcraft.api.internal.CommonInternals;

import java.util.Iterator;

public class AspectWrapper {

    public static void add(String tag, int color, Aspect[] components, ResourceLocation image, int blend) {
        try {
            new Aspect(tag, color, components, image, blend);
        } catch (IllegalArgumentException e) {
            GroovyLog.msg("Error adding Thaumcraft Aspect: ")
                    .add(e.getMessage())
                    .error()
                    .post();
        }
    }

    public static class AspectBuilder {

        private String tag;
        private int chatColor;
        private Aspect[] components;
        private ResourceLocation image;
        private int blend = 1;

        public AspectBuilder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public AspectBuilder chatColor(int color) {
            this.chatColor = color;
            return this;
        }

        public AspectBuilder components(Aspect[] components) {
            this.components = components;
            return this;
        }

        public AspectBuilder image(ResourceLocation image) {
            this.image = image;
            return this;
        }

        public AspectBuilder blend(int blend) {
            this.blend = blend;
            return this;
        }

        public void register() {
            AspectWrapper.add(tag, chatColor, components, image, blend);
        }
    }

    public static class AspectHelper {
        private String entity;
        private ItemStack object;
        private AspectList aspects;

        public AspectHelper() {
            // break
        }

        public AspectHelper entity(String entity) {
            this.entity = entity;
            return this;
        }

        public AspectHelper object(ItemStack object) {
            this.object = object;
            return this;
        }

        public AspectHelper aspects(AspectList aspects) {
            this.aspects = aspects;
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
}
