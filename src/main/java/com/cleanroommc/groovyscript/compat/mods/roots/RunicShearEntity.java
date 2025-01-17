package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import epicsquid.roots.init.ModRecipes;
import epicsquid.roots.recipe.RunicShearConditionalEntityRecipe;
import epicsquid.roots.recipe.RunicShearEntityRecipe;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;

@RegistryDescription
public class RunicShearEntity extends VirtualizedRegistry<RunicShearEntityRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".name('clay_from_wither_skeletons').entity(entity('minecraft:wither_skeleton')).output(item('minecraft:clay')).cooldown(1000)"),
            @Example(".name('creeper_at_the_last_moment').entity(entity('minecraft:creeper')).output(item('minecraft:diamond'), item('minecraft:nether_star')).functionMap({ entityLivingBase -> entityLivingBase.hasIgnited() ? item('minecraft:nether_star') : item('minecraft:dirt') })"),
            @Example(".entity(entity('minecraft:witch')).output(item('minecraft:clay'))")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        // FIXME Not currently reloadable due to being controlled by the Capability RunicShearsCapabilityProvider
        removeScripted().forEach(recipe -> ModRecipes.getRunicShearEntityRecipes().remove(recipe.getRegistryName()));
        restoreFromBackup().forEach(recipe -> ModRecipes.getRunicShearEntityRecipes().put(recipe.getRegistryName(), recipe));
    }

    public void add(RunicShearEntityRecipe recipe) {
        add(recipe.getRegistryName(), recipe);
    }

    public void add(ResourceLocation name, RunicShearEntityRecipe recipe) {
        ModRecipes.getRunicShearEntityRecipes().put(name, recipe);
        addScripted(recipe);
    }

    public ResourceLocation findRecipe(RunicShearEntityRecipe recipe) {
        for (Map.Entry<ResourceLocation, RunicShearEntityRecipe> entry : ModRecipes.getRunicShearEntityRecipes().entrySet()) {
            if (entry.getValue().equals(recipe)) return entry.getKey();
        }
        return null;
    }

    @MethodDescription(example = @Example("resource('roots:slime_strange_ooze')"))
    public boolean removeByName(ResourceLocation name) {
        RunicShearEntityRecipe recipe = ModRecipes.getRunicShearEntityRecipes().get(name);
        if (recipe == null) return false;
        ModRecipes.getRunicShearEntityRecipes().remove(name);
        addBackup(recipe);
        return true;
    }

    @MethodDescription(example = @Example("item('roots:fey_leather')"))
    public boolean removeByOutput(ItemStack output) {
        return ModRecipes.getRunicShearEntityRecipes().entrySet().removeIf(x -> {
            if (ItemStack.areItemsEqual(x.getValue().getDrop(), output)) {
                addBackup(x.getValue());
                return true;
            }
            return false;
        });
    }

    @SuppressWarnings("unchecked")
    @MethodDescription(example = @Example("entity('minecraft:chicken')"))
    public boolean removeByEntity(EntityEntry entity) {
        return removeByEntity((Class<? extends EntityLivingBase>) entity.getEntityClass());
    }

    @MethodDescription
    public boolean removeByEntity(Class<? extends EntityLivingBase> clazz) {
        for (Map.Entry<ResourceLocation, RunicShearEntityRecipe> x : ModRecipes.getRunicShearEntityRecipes().entrySet()) {
            if (x.getValue().getClazz() == clazz) {
                ModRecipes.getRunicShearEntityRecipes().remove(x.getKey());
                addBackup(x.getValue());
                return true;
            }
        }
        return false;
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ModRecipes.getRunicShearEntityRecipes().values().forEach(this::addBackup);
        ModRecipes.getRunicShearEntityRecipes().clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Map.Entry<ResourceLocation, RunicShearEntityRecipe>> streamRecipes() {
        return new SimpleObjectStream<>(ModRecipes.getRunicShearEntityRecipes().entrySet())
                .setRemover(r -> this.removeByName(r.getKey()));
    }

    @Property(property = "name")
    @Property(property = "output", comp = @Comp(gte = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<RunicShearEntityRecipe> {

        @Property(comp = @Comp(not = "null"))
        private Class<? extends EntityLivingBase> entity;
        @Property(comp = @Comp(not = "null"))
        private int cooldown;
        @Property
        private Function<EntityLivingBase, ItemStack> functionMap;

        @SuppressWarnings("unchecked")
        @RecipeBuilderMethodDescription
        public RecipeBuilder entity(EntityEntry entity) {
            this.entity = (Class<? extends EntityLivingBase>) entity.getEntityClass();
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder cooldown(int cooldown) {
            this.cooldown = cooldown;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder functionMap(Function<EntityLivingBase, ItemStack> functionMap) {
            this.functionMap = functionMap;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Roots Runic Shear Entity recipe";
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_runic_shear_entity_";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateName();
            msg.add(!input.isEmpty(), () -> "No item inputs allowed, but found " + input.size());
            validateFluids(msg);
            msg.add(output.size() > 1 && functionMap == null, "if output is greater than 1, functionMap must be defined");
            msg.add(entity == null, "entity must be defined and extended EntityLivingBase, instead it was {}", entity);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable RunicShearEntityRecipe register() {
            if (!validate()) return null;
            RunicShearEntityRecipe recipe;
            if (functionMap == null) recipe = new RunicShearEntityRecipe(super.name, output.get(0), entity, cooldown);
            else recipe = new RunicShearConditionalEntityRecipe(super.name, functionMap, new HashSet<>(output), entity, cooldown);
            ModSupport.ROOTS.get().runicShearEntity.add(super.name, recipe);
            return recipe;
        }
    }
}
