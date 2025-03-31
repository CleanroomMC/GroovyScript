package com.cleanroommc.groovyscript.compat.mods.betterwithmods;

import betterwithmods.api.tile.IHopperFilter;
import betterwithmods.common.BWRegistry;
import betterwithmods.common.registry.HopperFilter;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.betterwithmods.HopperFiltersAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;

@RegistryDescription
public class HopperFilters extends VirtualizedRegistry<IHopperFilter> {

    @RecipeBuilderDescription(example = {
            @Example(".name('too_weak_to_stop').filter(item('minecraft:string'))"),
            @Example(".name('groovyscript:clay_only').filter(item('minecraft:clay')).input(item('minecraft:clay'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> ((HopperFiltersAccessor) BWRegistry.HOPPER_FILTERS).getFILTERS().remove(recipe.getName()));
        restoreFromBackup().forEach(recipe -> ((HopperFiltersAccessor) BWRegistry.HOPPER_FILTERS).getFILTERS().put(recipe.getName(), recipe));
    }

    public IHopperFilter add(IHopperFilter recipe) {
        if (recipe != null) {
            addScripted(recipe);
            ((HopperFiltersAccessor) BWRegistry.HOPPER_FILTERS).getFILTERS().put(recipe.getName(), recipe);
        }
        return recipe;
    }

    public boolean remove(IHopperFilter recipe) {
        if (((HopperFiltersAccessor) BWRegistry.HOPPER_FILTERS).getFILTERS().remove(recipe.getName(), recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(example = @Example("'betterwithmods:ladder'"))
    public boolean removeByName(String name) {
        return ((HopperFiltersAccessor) BWRegistry.HOPPER_FILTERS).getFILTERS().remove(name) != null;
    }

    @MethodDescription(example = @Example("item('minecraft:trapdoor')"))
    public boolean removeByFilter(IIngredient input) {
        return ((HopperFiltersAccessor) BWRegistry.HOPPER_FILTERS).getFILTERS().values().removeIf(r -> {
            for (ItemStack item : r.getFilter().getMatchingStacks()) {
                if (input.test(item)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription
    public boolean removeByFiltered(IIngredient output) {
        return ((HopperFiltersAccessor) BWRegistry.HOPPER_FILTERS).getFILTERS().values().removeIf(r -> {
            if (!(r instanceof HopperFilter hopperFilter)) return false;
            for (Ingredient ingredient : hopperFilter.getFiltered()) {
                for (ItemStack item : ingredient.getMatchingStacks()) {
                    if (output.test(item)) {
                        addBackup(r);
                        return true;
                    }
                }
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<IHopperFilter> streamRecipes() {
        return new SimpleObjectStream<>(((HopperFiltersAccessor) BWRegistry.HOPPER_FILTERS).getFILTERS().values())
                .setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ((HopperFiltersAccessor) BWRegistry.HOPPER_FILTERS).getFILTERS().values().forEach(this::addBackup);
        ((HopperFiltersAccessor) BWRegistry.HOPPER_FILTERS).getFILTERS().values().clear();
    }

    @Property(property = "input", value = "groovyscript.wiki.betterwithmods.hopper_filters.filtered.value", comp = @Comp(gte = 0))
    public static class RecipeBuilder extends AbstractRecipeBuilder<IHopperFilter> {

        @Property(comp = @Comp(not = "null"))
        private IIngredient filter;

        @RecipeBuilderMethodDescription
        public RecipeBuilder filter(IIngredient filter) {
            this.filter = filter;
            return this;
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_hopper_filter_";
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Better With Mods Hopper Filter recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateName();
            validateItems(msg, 0, Integer.MAX_VALUE, 0, 0);
            validateFluids(msg);
            msg.add(IngredientHelper.isEmpty(filter), "filter must be defined");
            validateStackSize(msg, 1, "filter", filter);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable IHopperFilter register() {
            if (!validate()) return null;

            IHopperFilter recipe = new HopperFilter(super.name.toString(), filter.toMcIngredient(), input.stream().map(IIngredient::toMcIngredient).collect(Collectors.toList()));
            ModSupport.BETTER_WITH_MODS.get().hopperFilters.add(recipe);
            return recipe;
        }
    }
}
