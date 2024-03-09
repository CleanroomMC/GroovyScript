package com.cleanroommc.groovyscript.compat.mods.bloodmagic;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.api.impl.recipe.RecipeAlchemyTable;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.bloodmagic.BloodMagicRecipeRegistrarAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

@RegistryDescription(
        admonition = @Admonition(type = Admonition.Type.DANGER,
                                 format = Admonition.Format.STANDARD,
                                 hasTitle = true,
                                 value = "groovyscript.wiki.bloodmagic.alchemy_table.note0")
)
public class AlchemyTable extends VirtualizedRegistry<RecipeAlchemyTable> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond'), item('minecraft:diamond')).output(item('minecraft:clay')).ticks(100).minimumTier(2).syphon(500)"),
            @Example(".input(item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('bloodmagic:slate'), item('bloodmagic:slate')).output(item('minecraft:clay')).time(2000).tier(5).drain(25000)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAlchemyRecipes()::remove);
        restoreFromBackup().forEach(((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAlchemyRecipes()::add);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public RecipeAlchemyTable add(NonNullList<Ingredient> input, ItemStack output, int syphon, int ticks, int minimumTier) {
        RecipeAlchemyTable recipe = new RecipeAlchemyTable(input, output, syphon, ticks, minimumTier);
        add(recipe);
        return recipe;
    }

    public void add(RecipeAlchemyTable recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        ((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAlchemyRecipes().add(recipe);
    }

    public boolean remove(RecipeAlchemyTable recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        ((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAlchemyRecipes().remove(recipe);
        return true;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput")
    public boolean removeByInput(IIngredient... input) {
        NonNullList<IIngredient> inputs = NonNullList.create();
        Collections.addAll(inputs, input);
        return removeByInput(inputs);
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput",
                       example = @Example("item('minecraft:nether_wart'), item('minecraft:gunpowder')"))
    public boolean removeByInput(NonNullList<IIngredient> input) {
        // Filters down to only recipes which have inputs that match all the input IIngredients (NOTE: a recipe with ABCD would match an input of AB)
        if (((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAlchemyRecipes().removeIf(recipe -> {
            boolean removeRecipe = false;
            for (IIngredient match : input) {
                boolean foundInputMatch = false;
                for (Ingredient target : recipe.getInput()) {
                    if (target.test(IngredientHelper.toItemStack(match))) foundInputMatch = true;
                }
                removeRecipe = foundInputMatch;
            }
            if (removeRecipe) {
                addBackup(recipe);
            }
            return removeRecipe;
        })) {
            return true;
        }

        GroovyLog.msg("Error removing Blood Magic Alchemy Table recipe")
                .add("could not find recipe with inputs including all of {}", input)
                .error()
                .post();
        return false;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example("item('minecraft:sand')"))
    public boolean removeByOutput(ItemStack output) {
        if (((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAlchemyRecipes().removeIf(recipe -> {
            boolean matches = ItemStack.areItemStacksEqual(recipe.getOutput(), output);
            if (matches) {
                addBackup(recipe);
            }
            return matches;
        })) {
            return true;
        }
        GroovyLog.msg("Error removing Blood Magic Alchemy Table recipe")
                .add("could not find recipe with output {}", output)
                .error()
                .post();
        return false;
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAlchemyRecipes().forEach(this::addBackup);
        ((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAlchemyRecipes().clear();
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<RecipeAlchemyTable> streamRecipes() {
        return new SimpleObjectStream<>(((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAlchemyRecipes())
                .setRemover(this::remove);
    }


    @Property(property = "input", valid = {@Comp(type = Comp.Type.GTE, value = "1"), @Comp(type = Comp.Type.LTE, value = "6")})
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<RecipeAlchemyTable> {

        @Property(valid = @Comp(type = Comp.Type.GTE, value = "0"))
        private int syphon;
        @Property(valid = @Comp(type = Comp.Type.GT, value = "0"))
        private int ticks;
        @Property(valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LT, value = "AltarTier.MAXTIERS")})
        private int minimumTier;

        @RecipeBuilderMethodDescription
        public RecipeBuilder syphon(int syphon) {
            this.syphon = syphon;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "syphon")
        public RecipeBuilder drain(int drain) {
            return syphon(drain);
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder ticks(int ticks) {
            this.ticks = ticks;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "ticks")
        public RecipeBuilder time(int time) {
            return ticks(time);
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder minimumTier(int minimumTier) {
            this.minimumTier = minimumTier;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "minimumTier")
        public RecipeBuilder tier(int tier) {
            return minimumTier(tier);
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Blood Magic Alchemy Table recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 6, 1, 1);
            msg.add(syphon < 0, "syphon must be a nonnegative integer, yet it was {}", syphon);
            msg.add(ticks <= 0, "ticks must be a positive integer greater than 0, yet it was {}", ticks);
            msg.add(minimumTier < 0, "minimumTier must be a nonnegative integer, yet it was {}", minimumTier);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable RecipeAlchemyTable register() {
            if (!validate()) return null;
            RecipeAlchemyTable recipe = ModSupport.BLOOD_MAGIC.get().alchemyTable.add(IngredientHelper.toIngredientNonNullList(input), output.get(0), syphon, ticks, minimumTier);
            return recipe;
        }
    }
}
