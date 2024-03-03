package com.cleanroommc.groovyscript.compat.mods.bloodmagic;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.bloodmagic.BloodMagicValueManagerAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@RegistryDescription
public class Sacrificial extends VirtualizedRegistry<Pair<ResourceLocation, Integer>> {

    @RecipeBuilderDescription(example = @Example(".entity('minecraft:enderman').value(1000)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(pair -> ((BloodMagicValueManagerAccessor) BloodMagicAPI.INSTANCE.getValueManager()).getSacrificial().put(pair.getKey(), pair.getValue()));
        restoreFromBackup().forEach(pair -> ((BloodMagicValueManagerAccessor) BloodMagicAPI.INSTANCE.getValueManager()).getSacrificial().remove(pair.getKey()));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public boolean add(ResourceLocation entity, int value) {
        if (EntityList.getClass(entity) != null) {
            ((BloodMagicValueManagerAccessor) BloodMagicAPI.INSTANCE.getValueManager()).getSacrificial().put(entity, value);
            addScripted(Pair.of(entity, value));
            return true;
        }
        return false;
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public boolean add(String entity, int value) {
        return add(new ResourceLocation(entity), value);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public boolean add(Entity entity, int value) {
        return add(entity.getName(), value);
    }

    @MethodDescription(description = "groovyscript.wiki.bloodmagic.sacrificial.remove0", example = @Example("resource('minecraft:villager')"))
    public boolean remove(ResourceLocation entity) {
        if (EntityList.getClass(entity) != null) {
            ((BloodMagicValueManagerAccessor) BloodMagicAPI.INSTANCE.getValueManager()).getSacrificial().remove(entity);
            return true;
        }
        return false;
    }

    @MethodDescription(description = "groovyscript.wiki.bloodmagic.sacrificial.remove1", example = @Example("'minecraft:villager'"))
    public boolean remove(String entity) {
        return remove(new ResourceLocation(entity));
    }

    @MethodDescription(description = "groovyscript.wiki.bloodmagic.sacrificial.remove2")
    public boolean remove(Entity entity) {
        return remove(entity.getName());
    }

    @MethodDescription(description = "groovyscript.wiki.bloodmagic.sacrificial.remove3", example = @Example("entity('minecraft:villager')"))
    public boolean remove(EntityEntry entity) {
        return remove(entity.getName());
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ((BloodMagicValueManagerAccessor) BloodMagicAPI.INSTANCE.getValueManager()).getSacrificial().forEach((l, r) -> this.addBackup(Pair.of(l, r)));
        ((BloodMagicValueManagerAccessor) BloodMagicAPI.INSTANCE.getValueManager()).getSacrificial().clear();
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Map.Entry<ResourceLocation, Integer>> streamRecipes() {
        return new SimpleObjectStream<>(((BloodMagicValueManagerAccessor) BloodMagicAPI.INSTANCE.getValueManager()).getSacrificial().entrySet())
                .setRemover(r -> this.remove(r.getKey()));
    }


    public static class RecipeBuilder {

        @Property(valid = @Comp(type = Comp.Type.NOT, value = "null"))
        private ResourceLocation entity;
        @Property(valid = @Comp(type = Comp.Type.GTE, value = "0"))
        private int value;

        @RecipeBuilderMethodDescription
        public RecipeBuilder entity(String entity) {
            ResourceLocation location = new ResourceLocation(entity);
            if (EntityList.getClass(location) != null) {
                this.entity = location;
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder entity(ResourceLocation entity) {
            if (EntityList.getClass(entity) != null) {
                this.entity = entity;
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder entity(Entity entity) {
            this.entity = new ResourceLocation(entity.getName());
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder value(int value) {
            this.value = value;
            return this;
        }

        public String getErrorMsg() {
            return "Error adding Blood Magic Tranquility key recipe";
        }

        public boolean validate() {
            GroovyLog.Msg msg = GroovyLog.msg(getErrorMsg()).error();
            msg.add(entity == null, "entity must be non null");
            msg.add(value < 0, "value must be a nonnegative integer, yet it was {}", value);
            return !msg.postIfNotEmpty();
        }

        @RecipeBuilderRegistrationMethod
        public @Nullable Object register() {
            if (!validate()) return null;
            ModSupport.BLOOD_MAGIC.get().sacrificial.add(entity, value);
            return null;
        }
    }
}
