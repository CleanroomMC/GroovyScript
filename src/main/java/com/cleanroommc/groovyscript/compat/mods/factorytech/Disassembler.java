package com.cleanroommc.groovyscript.compat.mods.factorytech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import dalapo.factech.auxiliary.MachineRecipes;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.EntityEntry;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@RegistryDescription
public class Disassembler extends VirtualizedRegistry<Map.Entry<Class<? extends EntityLiving>, List<ItemStack>>> {

    public Map<Class<? extends EntityLiving>, List<ItemStack>> getRecipes() {
        return MachineRecipes.DISASSEMBLER;
    }

    @RecipeBuilderDescription(example = {
            @Example(".entity(entity('minecraft:chicken')).output(item('minecraft:obsidian'), item('minecraft:gold_ingot') * 2, item('minecraft:clay'), item('minecraft:diamond'))"),
            @Example(".entity(entity('minecraft:rabbit')).output(item('minecraft:clay'), item('minecraft:diamond') * 2)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        var recipes = getRecipes();
        removeScripted().forEach(pair -> recipes.remove(pair.getKey()));
        restoreFromBackup().forEach(pair -> recipes.put(pair.getKey(), pair.getValue()));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, priority = 500)
    public boolean add(Map.Entry<Class<? extends EntityLiving>, List<ItemStack>> recipe) {
        if (recipe != null) {
            getRecipes().put(recipe.getKey(), recipe.getValue());
            return doAddScripted(recipe);
        }
        return false;
    }

    @MethodDescription(priority = 500)
    public boolean remove(Map.Entry<Class<? extends EntityLiving>, List<ItemStack>> recipe) {
        return recipe != null && getRecipes().entrySet().removeIf(r -> r == recipe) && doAddBackup(recipe);
    }

    @MethodDescription
    public void removeByEntity(Class<? extends EntityLiving> entity) {
        getRecipes().entrySet().removeIf(r -> r.getKey().equals(entity) && doAddBackup(r));
    }

    @SuppressWarnings("unchecked")
    @MethodDescription(example = @Example("entity('minecraft:creeper')"))
    public void removeByEntity(EntityEntry entity) {
        removeByEntity((Class<? extends EntityLiving>) entity.getEntityClass());
    }

    @MethodDescription(example = @Example("item('minecraft:rotten_flesh')"))
    public void removeByOutput(IIngredient output) {
        getRecipes().entrySet().removeIf(r -> r.getValue().stream().anyMatch(output) && doAddBackup(r));
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        var recipes = getRecipes();
        recipes.entrySet().forEach(this::addBackup);
        recipes.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Map.Entry<Class<? extends EntityLiving>, List<ItemStack>>> streamRecipes() {
        return new SimpleObjectStream<>(getRecipes().entrySet()).setRemover(this::remove);
    }

    @Property(property = "output", comp = @Comp(gte = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<Map.Entry<Class<? extends EntityLiving>, List<ItemStack>>> {

        @Property(comp = @Comp(not = "null"))
        private Class<? extends EntityLiving> entity;

        @RecipeBuilderMethodDescription
        public RecipeBuilder entity(Class<? extends EntityLiving> entity) {
            this.entity = entity;
            return this;
        }

        @SuppressWarnings("unchecked")
        @RecipeBuilderMethodDescription
        public RecipeBuilder entity(EntityEntry entity) {
            return entity((Class<? extends EntityLiving>) entity.getEntityClass());
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Factory Tech Disassembler recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 1, Integer.MAX_VALUE);
            validateFluids(msg);
            msg.add(entity == null, "entity must be defined and extended EntityLiving, instead it was {}", entity);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable Map.Entry<Class<? extends EntityLiving>, List<ItemStack>> register() {
            if (!validate()) return null;
            Map.Entry<Class<? extends EntityLiving>, List<ItemStack>> recipe = Pair.of(entity, output);
            ModSupport.FACTORY_TECH.get().disassembler.add(recipe);
            return recipe;
        }
    }
}
