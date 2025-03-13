package com.cleanroommc.groovyscript.compat.mods.bloodarsenal;

import arcaratus.bloodarsenal.recipe.RecipeFilter;
import arcaratus.bloodarsenal.recipe.RecipeSanguineInfusion;
import arcaratus.bloodarsenal.recipe.SanguineInfusionRecipeRegistry;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.AbstractReloadableStorage;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.crafting.Ingredient;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RegistryDescription(admonition = @Admonition("groovyscript.wiki.bloodarsenal.sanguine_infusion.note0"))
public class SanguineInfusion extends StandardListRegistry<RecipeSanguineInfusion> {

    @SuppressWarnings("rawtypes")
    private final AbstractReloadableStorage<Class> blacklistStorage = new AbstractReloadableStorage<>();

    public SanguineInfusion() {
        super(Alias.generateOfClassAnd(SanguineInfusion.class, "Infusion"));
    }

    private static boolean containsInput(IIngredient input, List<Pair<Ingredient, Integer>> list) {
        for (var pair : list) {
            for (var stack : pair.getKey().getMatchingStacks()) {
                if (input.test(stack)) return true;
            }
        }
        return false;
    }

    @Override
    public Collection<RecipeSanguineInfusion> getRecipes() {
        return SanguineInfusionRecipeRegistry.getInfusionRecipes();
    }

    @SuppressWarnings("rawtypes")
    public Collection<Class> getBlacklistedClasses() {
        return SanguineInfusionRecipeRegistry.getBlacklistedClasses();
    }

    @Override
    public void onReload() {
        super.onReload();
        var list = getBlacklistedClasses();
        list.removeAll(blacklistStorage.removeScripted());
        list.addAll(blacklistStorage.restoreFromBackup());
    }

    @RecipeBuilderDescription(example = {
            @Example(".infuse(item('minecraft:gold_ingot')).input(item('minecraft:clay')).output(item('minecraft:diamond')).cost(1000)"),
            @Example(".infuse(item('minecraft:emerald')).input(item('minecraft:clay') * 64, item('minecraft:clay') * 64, item('minecraft:clay') * 64, item('minecraft:clay') * 64, item('minecraft:clay') * 64, item('minecraft:clay') * 64, item('minecraft:clay') * 64, item('minecraft:clay') * 64).output(item('minecraft:diamond') * 64).cost(5000)"),
            @Example(".infuse(item('minecraft:gold_ingot')).input(item('minecraft:clay'), item('minecraft:diamond')).output(item('minecraft:diamond'))"),
            @Example(".input(item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2).modifier('xperienced').levelMultiplier(3).cost(3000)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(example = {
            @Example("item('minecraft:feather')"), @Example("item('bloodmagic:bound_axe')")
    })
    public void removeByInput(IIngredient input) {
        getRecipes().removeIf(r -> (input.test(r.getInfuse()) || containsInput(input, r.getInputs())) && doAddBackup(r));
    }

    @MethodDescription(example = @Example("item('bloodarsenal:stasis_pickaxe')"))
    public void removeByOutput(IIngredient output) {
        getRecipes().removeIf(r -> output.test(r.getOutput()) && doAddBackup(r));
    }

    @MethodDescription(example = @Example(value = "'beneficial_potion'", commented = true))
    public void removeByModifierKey(String key) {
        getRecipes().removeIf(r -> key.equals(r.getModifierKey()) && doAddBackup(r));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example(value = "WayofTime.bloodmagic.iface.ISigil.class", commented = true))
    public boolean addBlacklist(@SuppressWarnings("rawtypes") Class clazz) {
        return clazz != null && getBlacklistedClasses().add(clazz) && blacklistStorage.addScripted(clazz);
    }

    @MethodDescription(example = @Example(value = "WayofTime.bloodmagic.iface.ISigil.class", commented = true))
    public boolean removeBlacklist(@SuppressWarnings("rawtypes") Class clazz) {
        return clazz != null && getBlacklistedClasses().removeIf(x -> x == clazz) && blacklistStorage.addBackup(clazz);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAllBlacklist() {
        var list = getBlacklistedClasses();
        list.forEach(blacklistStorage::addBackup);
        list.clear();
    }

    @Property(property = "input", comp = @Comp(gte = 1, lte = 8))
    @Property(property = "output", comp = @Comp(gte = 0, lte = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<RecipeSanguineInfusion> {

        @Property(comp = @Comp(gte = 0))
        private int levelMultiplier;
        @Property(comp = @Comp(gte = 0))
        private int cost;
        @Property
        private String modifier;
        @Property
        private IIngredient infuse;
        @Property
        private IIngredient filter;

        @RecipeBuilderMethodDescription
        public RecipeBuilder levelMultiplier(int levelMultiplier) {
            this.levelMultiplier = levelMultiplier;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder cost(int cost) {
            this.cost = cost;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder modifier(String modifier) {
            this.modifier = modifier;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder infuse(IIngredient infuse) {
            this.infuse = infuse;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder filter(IIngredient filter) {
            this.filter = filter;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Blood Arsenal Sanguine Infusion recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateFluids(msg);

            if (modifier == null) {
                validateItems(msg, 1, 8, 1, 1);
                msg.add(infuse == null, "infuse was null and modifier was null, yet one must be defined");
                msg.add(levelMultiplier != 0, "levelMultiplier was set to the non-default value of {} while modifier was null, yet it has no effect without modifier being set", levelMultiplier);
            } else {
                validateItems(msg, 1, 8, 0, 0);
                msg.add(infuse != null, "infuse was not null and modifier was not null, yet only one must be defined");
                msg.add(filter != null, "filter was not null and modifier was not null, yet filter has no effect when modifier is not null");
                msg.add(levelMultiplier < 0, "levelMultiplier must be a nonnegative integer, yet it was {}", levelMultiplier);
            }

            msg.add(cost < 0, "cost must be a nonnegative integer, yet it was {}", cost);
        }

        @SuppressWarnings("unchecked")
        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable RecipeSanguineInfusion register() {
            if (!validate()) return null;
            List<Pair<Object, Integer>> inputs = new ArrayList<>();
            for (var ingredient : input) {
                if (ingredient instanceof OreDictIngredient ore) {
                    inputs.add(Pair.of(ore.getOreDict(), ingredient.getAmount()));
                } else {
                    inputs.add(Pair.of(ingredient.getMatchingStacks()[0], ingredient.getAmount()));
                }
            }
            if (filter != null) inputs.add(Pair.of(new RecipeFilter(filter), 0));

            RecipeSanguineInfusion recipe = null;
            if (modifier == null) {
                for (var stack : infuse.getMatchingStacks()) {
                    recipe = new RecipeSanguineInfusion(output.get(0), cost, stack, inputs.toArray(new Pair[0]));
                    ModSupport.BLOOD_ARSENAL.get().sanguineInfusion.add(recipe);
                }
            } else {
                recipe = new RecipeSanguineInfusion(cost, modifier, inputs.toArray(new Pair[0]));
                recipe.setLevelMultiplier(levelMultiplier);
                ModSupport.BLOOD_ARSENAL.get().sanguineInfusion.add(recipe);
            }
            return recipe;
        }
    }
}
