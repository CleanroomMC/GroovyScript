package com.cleanroommc.groovyscript.compat.mods.bloodmagic;

import WayofTime.bloodmagic.meteor.MeteorComponent;
import WayofTime.bloodmagic.meteor.MeteorRegistry;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Meteor extends VirtualizedRegistry<WayofTime.bloodmagic.meteor.Meteor> {

    public Meteor() {
        super();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(r -> MeteorRegistry.meteorMap.remove(r.getCatalystStack()));
        restoreFromBackup().forEach(r -> MeteorRegistry.registerMeteor(r.getCatalystStack(), r));
    }

    public WayofTime.bloodmagic.meteor.Meteor add(ItemStack stack, List<MeteorComponent> componentList, float explosionStrength, int radius, int cost) {
        WayofTime.bloodmagic.meteor.Meteor recipe = new WayofTime.bloodmagic.meteor.Meteor(stack, componentList, explosionStrength, radius);
        recipe.setCost(cost);
        add(recipe);
        return recipe;
    }

    public void add(WayofTime.bloodmagic.meteor.Meteor recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        MeteorRegistry.registerMeteor(recipe.getCatalystStack(), recipe);
    }

    public boolean remove(WayofTime.bloodmagic.meteor.Meteor recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        MeteorRegistry.meteorMap.remove(recipe.getCatalystStack());
        return true;
    }

    public boolean remove(ItemStack input) {
        return remove(MeteorRegistry.getMeteorForItem(input));
    }

    public boolean removeByInput(ItemStack input) {
        return remove(MeteorRegistry.getMeteorForItem(input));
    }

    public boolean removeByCatalyst(ItemStack catalyst) {
        return removeByInput(catalyst);
    }

    public void removeAll() {
        MeteorRegistry.meteorMap.forEach((i, x) -> addBackup(x));
        MeteorRegistry.meteorMap.clear();
    }

    public SimpleObjectStream<Map.Entry<ItemStack, WayofTime.bloodmagic.meteor.Meteor>> streamRecipes() {
        return new SimpleObjectStream<>(MeteorRegistry.meteorMap.entrySet())
                .setRemover(x -> this.remove(x.getKey()));
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<WayofTime.bloodmagic.meteor.Meteor> {

        private final List<MeteorComponent> components = new ArrayList<>();
        private ItemStack catalyst;
        private float explosionStrength;
        private int radius;
        private int cost = 1000000;


        public RecipeBuilder catalystStack(ItemStack catalystStack) {
            return catalyst(catalystStack);
        }

        public RecipeBuilder catalyst(ItemStack catalyst) {
            this.catalyst = catalyst;
            return this;
        }

        public RecipeBuilder component(int weight, String oreName) {
            this.components.add(new MeteorComponent(weight, oreName));
            return this;
        }

        public RecipeBuilder component(String oreName, int weight) {
            return component(weight, oreName);
        }

        public RecipeBuilder component(int weight, OreDictIngredient ore) {
            return component(weight, ore.getOreDict());
        }

        public RecipeBuilder component(OreDictIngredient ore, int weight) {
            return component(weight, ore);
        }

        public RecipeBuilder explosionStrength(float explosionStrength) {
            this.explosionStrength = explosionStrength;
            return this;
        }

        public RecipeBuilder radius(int radius) {
            this.radius = radius;
            return this;
        }

        public RecipeBuilder cost(int cost) {
            this.cost = cost;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Blood Magic Alchemy Array recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg);

            msg.add(catalyst == null, "Must have a catalyst ItemStack but didn't find any!");
            msg.add(explosionStrength < 0, "Must have a nonnegative explosion strength, but found {}!", explosionStrength);
            msg.add(radius <= 0, "Must have a positive integer radius, but found {}!", radius);
            msg.add(cost < 0, "Must have a nonnegative cost, but found {}!", cost);
        }

        @Override
        public @Nullable WayofTime.bloodmagic.meteor.Meteor register() {
            if (!validate()) return null;
            WayofTime.bloodmagic.meteor.Meteor recipe = ModSupport.BLOOD_MAGIC.get().meteor.add(catalyst, components, explosionStrength, radius, cost);
            return recipe;
        }
    }
}
