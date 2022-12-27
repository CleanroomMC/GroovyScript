package com.cleanroommc.groovyscript.compat.mods.bloodmagic;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.bloodmagic.BloodMagicValueManagerAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class Sacrificial extends VirtualizedRegistry<Pair<ResourceLocation, Integer>> {

    public Sacrificial() {
        super();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(pair -> ((BloodMagicValueManagerAccessor) BloodMagicAPI.INSTANCE.getValueManager()).getSacrificial().put(pair.getKey(), pair.getValue()));
        restoreFromBackup().forEach(pair -> ((BloodMagicValueManagerAccessor) BloodMagicAPI.INSTANCE.getValueManager()).getSacrificial().remove(pair.getKey()));
    }

    public boolean add(ResourceLocation entity, int value) {
        if (EntityList.getClass(entity) != null) {
            ((BloodMagicValueManagerAccessor) BloodMagicAPI.INSTANCE.getValueManager()).getSacrificial().put(entity, value);
            addScripted(Pair.of(entity, value));
            return true;
        }
        return false;
    }

    public boolean add(String entity, int value) {
        return add(new ResourceLocation(entity), value);
    }

    public boolean add(Entity entity, int value) {
        return add(entity.getName(), value);
    }

    public boolean remove(ResourceLocation entity) {
        if (EntityList.getClass(entity) != null) {
            ((BloodMagicValueManagerAccessor) BloodMagicAPI.INSTANCE.getValueManager()).getSacrificial().remove(entity);
            return true;
        }
        return false;
    }

    public boolean remove(String entity) {
        return remove(new ResourceLocation(entity));
    }

    public boolean remove(Entity entity) {
        return remove(entity.getName());
    }

    public void removeAll() {
        ((BloodMagicValueManagerAccessor) BloodMagicAPI.INSTANCE.getValueManager()).getSacrificial().forEach((l, r) -> this.addBackup(Pair.of(l, r)));
        ((BloodMagicValueManagerAccessor) BloodMagicAPI.INSTANCE.getValueManager()).getSacrificial().clear();
    }

    public SimpleObjectStream<Map.Entry<ResourceLocation, Integer>> streamRecipes() {
        return new SimpleObjectStream<>(((BloodMagicValueManagerAccessor) BloodMagicAPI.INSTANCE.getValueManager()).getSacrificial().entrySet())
                .setRemover(r -> this.remove(r.getKey()));
    }


    public static class RecipeBuilder {
        private ResourceLocation entity;
        private int value;

        public RecipeBuilder entity(String entity) {
            ResourceLocation location = new ResourceLocation(entity);
            if (EntityList.getClass(location) != null) {
                this.entity = location;
            }
            return this;
        }

        public RecipeBuilder entity(ResourceLocation entity) {
            if (EntityList.getClass(entity) != null) {
                this.entity = entity;
            }
            return this;
        }

        public RecipeBuilder entity(Entity entity) {
            this.entity = new ResourceLocation(entity.getName());
            return this;
        }

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

        public @Nullable Object register() {
            if (!validate()) return null;
            ModSupport.BLOOD_MAGIC.get().sacrificial.add(entity, value);
            return null;
        }
    }
}
