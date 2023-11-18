package com.cleanroommc.groovyscript.compat.mods.draconicevolution;

import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionRecipe;
import com.brandon3055.draconicevolution.lib.RecipeManager;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.draconicevolution.FusionRegistryAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;

public class Fusion extends VirtualizedRegistry<IFusionRecipe> {

    public Fusion() {
        super();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(RecipeManager.FUSION_REGISTRY::remove);
        restoreFromBackup().forEach(RecipeManager.FUSION_REGISTRY::add);
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void add(IFusionRecipe recipe) {
        addScripted(recipe);
        RecipeManager.FUSION_REGISTRY.add(recipe);
    }

    public boolean remove(IFusionRecipe recipe) {
        if (RecipeManager.FUSION_REGISTRY.getRecipes().contains(recipe)) {
            addBackup(recipe);
            RecipeManager.FUSION_REGISTRY.remove(recipe);
            return true;
        }
        return false;
    }

    public void removeByCatalyst(ItemStack item) {
        for (IFusionRecipe recipe : RecipeManager.FUSION_REGISTRY.getRecipes().stream().filter(x -> x.getRecipeCatalyst().isItemEqual(item)).collect(Collectors.toList())) {
            remove(recipe);
        }
    }

    public void removeAll() {
        ((FusionRegistryAccessor) RecipeManager.FUSION_REGISTRY).getREGISTRY().forEach(this::addBackup);
        ((FusionRegistryAccessor) RecipeManager.FUSION_REGISTRY).getREGISTRY().clear();
    }

    public SimpleObjectStream<IFusionRecipe> streamRecipes() {
        return new SimpleObjectStream<>(((FusionRegistryAccessor) RecipeManager.FUSION_REGISTRY).getREGISTRY())
                .setRemover(this::remove);
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<IFusionRecipe> {

        private ItemStack catalyst;
        private long energy = 1000000;
        private int tier;

        public RecipeBuilder energy(long energy) {
            this.energy = energy;
            return this;
        }

        public RecipeBuilder catalyst(ItemStack catalyst) {
            this.catalyst = catalyst;
            return this;
        }

        public RecipeBuilder tier(int tier) {
            this.tier = tier;
            return this;
        }

        public RecipeBuilder tierNormal() {
            return tier(0);
        }

        public RecipeBuilder tierBasic() {
            return tier(0);
        }

        public RecipeBuilder tierWyvern() {
            return tier(1);
        }

        public RecipeBuilder tierDraconic() {
            return tier(2);
        }

        public RecipeBuilder tierChaotic() {
            return tier(3);
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Draconic Evolution Fusion recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 54, 1, 1);
            validateFluids(msg);
            msg.add(IngredientHelper.isEmpty(catalyst), "catalyst must not be empty");
            msg.add(tier < 0 || tier > 3, "tier must be between 0 (basic) and 3 (chaotic), yet it was {}", tier);
            msg.add(energy <= 0, "energy must be greater than 0, yet it was {}", energy);
        }

        @Override
        public @Nullable IFusionRecipe register() {
            if (!validate()) return null;
            GroovyFusionRecipe recipe = new GroovyFusionRecipe(output.get(0), catalyst, input, energy, tier);
            ModSupport.DRACONIC_EVOLUTION.get().fusion.add(recipe);
            return recipe;
        }
    }
}
