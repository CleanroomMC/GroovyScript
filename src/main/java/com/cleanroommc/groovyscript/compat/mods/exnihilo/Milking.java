package com.cleanroommc.groovyscript.compat.mods.exnihilo;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import exnihilocreatio.registries.manager.ExNihiloRegistryManager;
import exnihilocreatio.registries.types.Milkable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class Milking extends VirtualizedRegistry<Milkable> {

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> ExNihiloRegistryManager.MILK_ENTITY_REGISTRY.getRegistry().removeIf(milkable -> recipe.getEntityOnTop().equals(milkable.getEntityOnTop())));
        restoreFromBackup().forEach(recipe -> ExNihiloRegistryManager.MILK_ENTITY_REGISTRY.getRegistry().add(recipe));
    }

    public void remove(ResourceLocation entity) {
        if (!EntityList.isRegistered(entity)) {
            GroovyLog.msg("Error removing Ex Nihilo milking recipe")
                    .add(String.format("Can't find entity %s", entity))
                    .error()
                    .post();
            return;
        }
        boolean successful = ExNihiloRegistryManager.MILK_ENTITY_REGISTRY.getRegistry().removeIf(milkable -> {
            if (milkable.getEntityOnTop().equals(entity.toString())) {
                addBackup(milkable);
                return true;
            }
            return false;
        });
        if (!successful) {
            GroovyLog.msg("Error removing Ex Nihilo milking recipe")
                    .add(String.format("can't find recipe for %s", entity))
                    .error()
                    .post();
        }

    }
    public void remove(String entity) {
        remove(new ResourceLocation(entity));
    }

    public void remove(Entity entity) {
        remove(entity.getName());
    }

    public void removeAll() {
        for (Milkable milkable : ExNihiloRegistryManager.MILK_ENTITY_REGISTRY.getRegistry()) {
            addBackup(milkable);
        }
        ExNihiloRegistryManager.MILK_ENTITY_REGISTRY.getRegistry().clear();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public class RecipeBuilder extends AbstractRecipeBuilder<Milkable> {

        private ResourceLocation entity;
        private int cooldown;
        private FluidStack fluid;


        @Override
        public String getErrorMsg() {
            return "Error removing Ex Nihilo milking recipe";
        }

        public RecipeBuilder entity(ResourceLocation entity) {
            this.entity = entity;
            return this;
        }

        public RecipeBuilder entity(String entity) {
            this.entity = new ResourceLocation(entity);
            return this;
        }

        public RecipeBuilder entity(Entity entity) {
            this.entity = new ResourceLocation(entity.getName());
            return this;
        }

        public RecipeBuilder cooldown(int cooldown) {
            this.cooldown = cooldown;
            return this;
        }

        public RecipeBuilder fluid(FluidStack fluid) {
            this.fluid = fluid;
            return this;
        }


        @Override
        public void validate(GroovyLog.Msg msg) {
            msg.add(!EntityList.isRegistered(entity), String.format("Can't find entity %s", entity));
            msg.add(IngredientHelper.isEmpty(fluid), "Fluid must not be empty");

        }

        @Override
        public @Nullable Milkable register() {
            if (!validate()) return null;
            Milkable milkable = new Milkable(entity.toString(), fluid.getFluid().getName(), fluid.amount, cooldown);
            addScripted(milkable);
            ExNihiloRegistryManager.MILK_ENTITY_REGISTRY.getRegistry().add(milkable);
            return milkable;
        }
    }
}
