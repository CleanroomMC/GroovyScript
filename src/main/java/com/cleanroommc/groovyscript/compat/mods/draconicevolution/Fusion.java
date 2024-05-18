package com.cleanroommc.groovyscript.compat.mods.draconicevolution;

import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionRecipe;
import com.brandon3055.draconicevolution.lib.RecipeManager;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.draconicevolution.FusionRegistryAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;

@RegistryDescription
public class Fusion extends VirtualizedRegistry<IFusionRecipe> {

    @Override
    public void onReload() {
        removeScripted().forEach(RecipeManager.FUSION_REGISTRY::remove);
        restoreFromBackup().forEach(RecipeManager.FUSION_REGISTRY::add);
    }

    @RecipeBuilderDescription(example = {
            @Example(".catalyst(item('minecraft:diamond')).input(ore('ingotIron'), ore('ingotIron'), item('minecraft:dirt'), item('minecraft:grass'), item('minecraft:grass'), item('minecraft:dirt'), ore('ingotGold'), ore('ingotGold')).output(item('minecraft:nether_star')).energy(10).tier(1)"),
            @Example(".catalyst(item('minecraft:diamond')).input(item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay')).output(item('minecraft:nether_star')).energy(100000).tierChaotic()")
    })
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

    @MethodDescription(example = @Example("item('draconicevolution:chaos_shard')"))
    public void removeByCatalyst(ItemStack item) {
        for (IFusionRecipe recipe : RecipeManager.FUSION_REGISTRY.getRecipes().stream().filter(x -> x.getRecipeCatalyst().isItemEqual(item)).collect(Collectors.toList())) {
            remove(recipe);
        }
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ((FusionRegistryAccessor) RecipeManager.FUSION_REGISTRY).getREGISTRY().forEach(this::addBackup);
        ((FusionRegistryAccessor) RecipeManager.FUSION_REGISTRY).getREGISTRY().clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<IFusionRecipe> streamRecipes() {
        return new SimpleObjectStream<>(((FusionRegistryAccessor) RecipeManager.FUSION_REGISTRY).getREGISTRY())
                .setRemover(this::remove);
    }

    @Property(property = "input", valid = {@Comp(type = Comp.Type.GTE, value = "1"), @Comp(type = Comp.Type.LTE, value = "54")})
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<IFusionRecipe> {

        @Property(valid = @Comp(type = Comp.Type.NOT, value = "null"))
        private ItemStack catalyst;
        @Property(defaultValue = "1000000", valid = @Comp(type = Comp.Type.GT, value = "0"))
        private long energy = 1000000;
        @Property(valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "3")})
        private int tier;

        @RecipeBuilderMethodDescription
        public RecipeBuilder energy(long energy) {
            this.energy = energy;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder catalyst(ItemStack catalyst) {
            this.catalyst = catalyst;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder tier(int tier) {
            this.tier = tier;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "tier")
        public RecipeBuilder tierNormal() {
            return tier(0);
        }

        @RecipeBuilderMethodDescription(field = "tier")
        public RecipeBuilder tierBasic() {
            return tier(0);
        }

        @RecipeBuilderMethodDescription(field = "tier")
        public RecipeBuilder tierWyvern() {
            return tier(1);
        }

        @RecipeBuilderMethodDescription(field = "tier")
        public RecipeBuilder tierDraconic() {
            return tier(2);
        }

        @RecipeBuilderMethodDescription(field = "tier")
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
        @RecipeBuilderRegistrationMethod
        public @Nullable IFusionRecipe register() {
            if (!validate()) return null;
            GroovyFusionRecipe recipe = new GroovyFusionRecipe(output.get(0), catalyst, input, energy, tier);
            ModSupport.DRACONIC_EVOLUTION.get().fusion.add(recipe);
            return recipe;
        }
    }
}
