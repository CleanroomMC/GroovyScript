package com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.Thaumcraft;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.AspectList;

public class Aspect {

    public AspectBuilder aspectBuilder() {
        return new AspectBuilder();
    }

    public static class AspectBuilder {

        private String tag;
        private int chatColor;
        private final AspectList components = new AspectList();
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

        public AspectBuilder component(AspectStack component) {
            this.components.add(component.getAspect(), component.getAmount());
            return this;
        }

        public AspectBuilder component(String tag, int amount) {
            thaumcraft.api.aspects.Aspect a = Thaumcraft.validateAspect(tag);
            if (a != null) this.components.add(a, amount);
            return this;
        }

        public AspectBuilder image(String image) {
            this.image = new ResourceLocation(image);
            return this;
        }

        public AspectBuilder image(String mod, String image) {
            this.image = new ResourceLocation(mod, image);
            return this;
        }

        public AspectBuilder blend(int blend) {
            this.blend = blend;
            return this;
        }

        public AspectBuilder register() {
            try {
                new thaumcraft.api.aspects.Aspect(tag, chatColor, components.getAspects(), image, blend);
            } catch (IllegalArgumentException e) {
                GroovyLog.msg("Error adding Thaumcraft Aspect: ")
                        .add(e.getMessage())
                        .error()
                        .post();
            }
            return this;
        }
    }
}
