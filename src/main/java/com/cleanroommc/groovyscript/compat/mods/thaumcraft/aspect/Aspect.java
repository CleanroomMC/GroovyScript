package com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.Thaumcraft;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.AspectList;

import java.util.Map;

@RegistryDescription
public class Aspect extends VirtualizedRegistry<thaumcraft.api.aspects.Aspect> {

    @RecipeBuilderDescription(example = @Example(".tag('humor').chatColor(14013676).component(aspect('cognitio')).component('perditio').image(resource('thaumcraft:textures/aspects/humor.png'))"))
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

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Map.Entry<String, thaumcraft.api.aspects.Aspect>> streamRecipes() {
        return new SimpleObjectStream<>(thaumcraft.api.aspects.Aspect.aspects.entrySet())
                .setRemover(x -> remove(x.getValue()));
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        thaumcraft.api.aspects.Aspect.aspects.forEach((k, v) -> addBackup(v));
        thaumcraft.api.aspects.Aspect.aspects.clear();
    }

    public static class AspectBuilder {

        @Property(valid = @Comp(value = "null", type = Comp.Type.NOT), requirement = "groovyscript.wiki.thaumcraft.aspect.tag.required")
        private String tag;
        @Property
        private int chatColor;
        @Property(valid = {@Comp(value = "0", type = Comp.Type.GTE), @Comp(value = "2", type = Comp.Type.LTE)})
        private final AspectList components = new AspectList();
        @Property
        private ResourceLocation image;
        @Property(defaultValue = "1")
        private int blend = 1;

        @RecipeBuilderMethodDescription
        public AspectBuilder tag(String tag) {
            this.tag = tag;
            return this;
        }

        @RecipeBuilderMethodDescription
        public AspectBuilder chatColor(int color) {
            this.chatColor = color;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "components")
        public AspectBuilder component(AspectStack component) {
            this.components.add(component.getAspect(), component.getAmount());
            return this;
        }

        @RecipeBuilderMethodDescription(field = "components")
        public AspectBuilder component(String tag, int amount) {
            thaumcraft.api.aspects.Aspect a = Thaumcraft.validateAspect(tag);
            if (a != null) this.components.add(a, amount);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "components")
        public AspectBuilder component(String tag) {
            return this.component(tag, 1);
        }

        @RecipeBuilderMethodDescription
        public AspectBuilder image(ResourceLocation image) {
            this.image = image;
            return this;
        }

        @RecipeBuilderMethodDescription
        public AspectBuilder image(String image) {
            this.image = new ResourceLocation(image);
            return this;
        }

        @RecipeBuilderMethodDescription
        public AspectBuilder image(String mod, String image) {
            this.image = new ResourceLocation(mod, image);
            return this;
        }

        @RecipeBuilderMethodDescription
        public AspectBuilder blend(int blend) {
            this.blend = blend;
            return this;
        }

        @RecipeBuilderRegistrationMethod
        public thaumcraft.api.aspects.Aspect register() {
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
