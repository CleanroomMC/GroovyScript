package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import mekanism.api.infuse.InfuseObject;
import mekanism.api.infuse.InfuseRegistry;
import mekanism.api.infuse.InfuseType;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class Infusion extends VirtualizedRegistry<Pair<String, InfuseType>> {

    private List<Pair<ItemStack, InfuseObject>> objectBackup;
    private List<Pair<ItemStack, InfuseObject>> objectScripted;

    public Infusion() {
        super();
        this.objectBackup = new ArrayList<>();
        this.objectScripted = new ArrayList<>();
    }

    public static InfusionItems infusion(InfuseType type) {
        return new InfusionItems(type);
    }


    public static InfusionItems infusion(String type, ResourceLocation resource) {
        return new InfusionItems(type, resource);
    }

    public static InfusionItems infusion(String type, String resource) {
        return new InfusionItems(type, resource);
    }

    public static InfusionItems infusion(String type) {
        return new InfusionItems(type);
    }

    @Override
    public void onReload() {
        removeScripted().forEach(pair -> InfuseRegistry.getInfuseMap().put(pair.getKey(), pair.getValue()));
        restoreFromBackup().forEach(pair -> InfuseRegistry.getInfuseMap().remove(pair.getKey()));

        this.objectBackup.forEach(pair -> InfuseRegistry.getObjectMap().put(pair.getKey(), pair.getValue()));
        this.objectScripted.forEach(pair -> InfuseRegistry.getObjectMap().remove(pair.getKey()));

        this.objectBackup = new ArrayList<>();
        this.objectScripted = new ArrayList<>();
    }

    public void addType(String name, ResourceLocation resource) {
        InfuseType infuse = new InfuseType(name.toUpperCase(Locale.ROOT), resource);
        infuse.unlocalizedName = name.toLowerCase(Locale.ROOT);
        infuse.setIcon(Minecraft.getMinecraft().getTextureMapBlocks().registerSprite(infuse.iconResource));
        addScripted(Pair.of(name.toUpperCase(Locale.ROOT), infuse));
        InfuseRegistry.registerInfuseType(infuse);
    }

    public void addType(String name, String resource) {
        addType(name, new ResourceLocation(resource));
    }

    public boolean removeType(String name) {
        addBackup(Pair.of(name.toUpperCase(Locale.ROOT), InfuseRegistry.get(name)));
        InfuseRegistry.getInfuseMap().remove(name.toUpperCase(Locale.ROOT));
        return true;
    }

    public void add(InfuseType type, int amount, ItemStack item) {
        InfuseObject object = new InfuseObject(type, amount);
        this.objectScripted.add(Pair.of(item, object));
        InfuseRegistry.registerInfuseObject(item, object);
    }

    public void add(InfuseType type, int amount, IIngredient... ingredients) {
        for (ItemStack item : Arrays.stream(ingredients).flatMap(g -> Arrays.stream(g.getMatchingStacks())).collect(Collectors.toList())) {
            add(type, amount, item);
        }
    }

    public void add(String type, int amount, IIngredient... ingredients) {
        add(InfuseRegistry.get(type.toUpperCase(Locale.ROOT)), amount, ingredients);
    }

    public void add(InfuseType type, int amount, Collection<IIngredient> ingredients) {
        for (ItemStack item : ingredients.stream().flatMap(g -> Arrays.stream(g.getMatchingStacks())).collect(Collectors.toList())) {
            add(type, amount, item);
        }
    }

    public void add(String type, int amount, Collection<IIngredient> ingredients) {
        add(InfuseRegistry.get(type.toUpperCase(Locale.ROOT)), amount, ingredients);
    }

    public void remove(IIngredient item) {
        for (Map.Entry<ItemStack, InfuseObject> entry : InfuseRegistry.getObjectMap().entrySet().stream().filter(x -> item.test(x.getKey())).collect(Collectors.toList())) {
            objectBackup.add(Pair.of(entry.getKey(), entry.getValue()));
            InfuseRegistry.getObjectMap().remove(entry.getKey());
        }
    }

    public void remove(IIngredient... ingredients) {
        for (IIngredient item : ingredients) {
            remove(item);
        }
    }

    public void remove(Collection<IIngredient> ingredients) {
        for (IIngredient item : ingredients) {
            remove(item);
        }
    }

    public void removeByType(InfuseType type) {
        for (Map.Entry<ItemStack, InfuseObject> entry : InfuseRegistry.getObjectMap().entrySet().stream().filter(x -> x.getValue().type == type).collect(Collectors.toList())) {
            objectBackup.add(Pair.of(entry.getKey(), entry.getValue()));
            InfuseRegistry.getObjectMap().remove(entry.getKey());
        }
    }

    public void removeByType(String type) {
        removeByType(InfuseRegistry.get(type.toUpperCase(Locale.ROOT)));
    }

    public void removeAll() {
        InfuseRegistry.getInfuseMap().forEach((l, r) -> addBackup(Pair.of(l, r)));
        InfuseRegistry.getInfuseMap().clear();
        InfuseRegistry.getObjectMap().forEach((l, r) -> objectBackup.add(Pair.of(l, r)));
        InfuseRegistry.getObjectMap().clear();
    }

    public static class InfusionItems {

        private InfuseType type;

        public InfusionItems(InfuseType type) {
            this.type = type;
        }

        public InfusionItems(String type, ResourceLocation resource) {
            if (!InfuseRegistry.contains(type.toUpperCase(Locale.ROOT))) ModSupport.MEKANISM.get().infusion.addType(type, resource);
            this.type = InfuseRegistry.get(type.toUpperCase(Locale.ROOT));
        }

        public InfusionItems(String type, String resource) {
            this(type, new ResourceLocation(resource));
        }

        public InfusionItems(String type) {
            if (InfuseRegistry.contains(type.toUpperCase(Locale.ROOT))) this.type = InfuseRegistry.get(type.toUpperCase(Locale.ROOT));
            else GroovyLog.msg("Error creating Mekansim Infusion type")
                    .add("No ResourceLocation was defined for requested infusion type {}", type)
                    .error()
                    .post();
        }

        public InfusionItems add(int amount, IIngredient item) {
            ModSupport.MEKANISM.get().infusion.add(type, amount, item);
            return this;
        }

        public InfusionItems add(int amount, IIngredient... item) {
            ModSupport.MEKANISM.get().infusion.add(type, amount, item);
            return this;
        }

        public InfusionItems add(int amount, Collection<IIngredient> item) {
            ModSupport.MEKANISM.get().infusion.add(type, amount, item);
            return this;
        }

        public InfusionItems remove(IIngredient item) {
            ModSupport.MEKANISM.get().infusion.remove(item);
            return this;
        }

        public InfusionItems remove(IIngredient... item) {
            ModSupport.MEKANISM.get().infusion.remove(item);
            return this;
        }

        public InfusionItems remove(Collection<IIngredient> item) {
            ModSupport.MEKANISM.get().infusion.remove(item);
            return this;
        }

        public InfusionItems removeAll() {
            ModSupport.MEKANISM.get().infusion.removeByType(type);
            return this;
        }

    }

}
