package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import net.minecraft.util.ResourceLocation;

public class Aspect {

    public Aspect() {
        //do nothing
    }

    public void add(String tag, int color, thaumcraft.api.aspects.Aspect[] components, ResourceLocation image, int blend) {
        try {
            new thaumcraft.api.aspects.Aspect(tag, color, components, image, blend);
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
        private thaumcraft.api.aspects.Aspect[] components;
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

        public AspectBuilder components(thaumcraft.api.aspects.Aspect[] components) {
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
            try {
                new thaumcraft.api.aspects.Aspect(tag, chatColor, components, image, blend);
            } catch (IllegalArgumentException e) {
                GroovyLog.msg("Error adding Thaumcraft Aspect: ")
                        .add(e.getMessage())
                        .error()
                        .post();
            }
        }
    }
}
