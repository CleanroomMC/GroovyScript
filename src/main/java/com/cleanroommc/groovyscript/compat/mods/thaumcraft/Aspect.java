package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.AspectList;

public class Aspect {

    private thaumcraft.api.aspects.Aspect thaumAspect;

    public Aspect() {
        //do nothing
    }

    public thaumcraft.api.aspects.Aspect getNativeAspect() {
        return thaumAspect;
    }

    public Aspect(String tag, int color, thaumcraft.api.aspects.Aspect[] components, ResourceLocation image, int blend) {
        try {
            thaumAspect = new thaumcraft.api.aspects.Aspect(tag, color, components, image, blend);
        } catch (IllegalArgumentException e) {
            GroovyLog.msg("Error adding Thaumcraft Aspect: ")
                    .add(e.getMessage())
                    .error()
                    .post();
        }
    }

    public AspectBuilder aspectBuilder() { return new AspectBuilder(); }

    public class AspectBuilder {

        private String tag;
        private int chatColor;
        private AspectList components = new AspectList();
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

        public AspectBuilder component(thaumcraft.api.aspects.Aspect componentIn) {
            this.components.add(componentIn, 1);
            return this;
        }

        public AspectBuilder component(thaumcraft.api.aspects.Aspect componentIn, int amount) {
            this.components.add(componentIn, amount);
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
