package com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.brackets.AspectBracketHandler;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.AspectList;

import java.util.Map;

public class Aspect extends VirtualizedRegistry<thaumcraft.api.aspects.Aspect> {

    public AspectBuilder aspectBuilder() {
        return new AspectBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(x -> thaumcraft.api.aspects.Aspect.aspects.remove(x.getTag()));
        restoreFromBackup().forEach(x -> thaumcraft.api.aspects.Aspect.aspects.put(x.getTag(), x));
    }

    public void add(thaumcraft.api.aspects.Aspect aspect) {
        thaumcraft.api.aspects.Aspect.aspects.put(aspect.getTag(), aspect);
        addScripted(aspect);
    }

    public boolean remove(thaumcraft.api.aspects.Aspect aspect) {
        addBackup(aspect);
        return thaumcraft.api.aspects.Aspect.aspects.remove(aspect.getTag(), aspect);
    }

    public SimpleObjectStream<Map.Entry<String, thaumcraft.api.aspects.Aspect>> streamRecipes() {
        return new SimpleObjectStream<>(thaumcraft.api.aspects.Aspect.aspects.entrySet())
                .setRemover(x -> remove(x.getValue()));
    }

    public void removeAll() {
        thaumcraft.api.aspects.Aspect.aspects.forEach((k, v) -> addBackup(v));
        thaumcraft.api.aspects.Aspect.aspects.clear();
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
            thaumcraft.api.aspects.Aspect a = AspectBracketHandler.validateAspect(tag);
            if (a != null) this.components.add(a, amount);
            return this;
        }

        public AspectBuilder component(String tag) {
            return this.component(tag, 1);
        }

        public AspectBuilder image(ResourceLocation image) {
            this.image = image;
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
                thaumcraft.api.aspects.Aspect aspect = new thaumcraft.api.aspects.Aspect(tag, chatColor, components.getAspects(), image, blend);
                ModSupport.THAUMCRAFT.get().aspect.add(aspect);
                return aspect;
            } catch (IllegalArgumentException e) {
                GroovyLog.msg("Error adding Thaumcraft Aspect: ")
                        .add(e.getMessage())
                        .error()
                        .post();
            }
            return null;
        }
    }
}
