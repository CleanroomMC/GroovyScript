package com.cleanroommc.groovyscript.compat.mods.essentialcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.IRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import essentialcraft.api.OreSmeltingRecipe;
import org.jetbrains.annotations.Nullable;

@RegistryDescription(reloadability = RegistryDescription.Reloadability.DISABLED, admonition = {
        @Admonition(value = "groovyscript.wiki.essentialcraft.magmatic_smeltery.note0", type = Admonition.Type.WARNING),
})
public class MagmaticSmeltery extends VirtualizedRegistry<OreSmeltingRecipe> {

    @RecipeBuilderDescription(example = @Example(".input('blockIron').output('ingotGold').factor(3).color(0x0000ff)"))
    public MagmaticSmeltery.RecipeBuilder recipeBuilder() {
        return new MagmaticSmeltery.RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(OreSmeltingRecipe::removeRecipe);
        restoreFromBackup().forEach(OreSmeltingRecipe::register);
    }

    @MethodDescription(example = @Example("ore('oreIron')"))
    public boolean removeByInput(OreDictIngredient x) {
        return removeByInput(x.getOreDict());
    }

    @MethodDescription(example = @Example("'oreDiamond'"))
    public boolean removeByInput(String x) {
        if (OreSmeltingRecipe.RECIPE_MAP.containsKey(x)) {
            OreSmeltingRecipe recipe = OreSmeltingRecipe.RECIPE_MAP.get(x);
            addBackup(recipe);
            OreSmeltingRecipe.removeRecipe(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        OreSmeltingRecipe.RECIPES.forEach(this::addBackup);
        OreSmeltingRecipe.RECIPES.clear();
        OreSmeltingRecipe.RECIPE_MAP.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<OreSmeltingRecipe> streamRecipes() {
        return new SimpleObjectStream<>(OreSmeltingRecipe.RECIPES).setRemover(r -> removeByInput(r.oreName));
    }

    public static class RecipeBuilder implements IRecipeBuilder<OreSmeltingRecipe> {

        @Property(comp = @Comp(types = Comp.Type.EQ, eq = 1))
        private String input;

        @Property(comp = @Comp(types = Comp.Type.EQ, eq = 1))
        private String output;

        @Property(comp = @Comp(types = {Comp.Type.GTE, Comp.Type.LTE}, lte = 0xffffff))
        private int color;

        @Property(comp = @Comp(types = Comp.Type.GTE, gte = 1), defaultValue = "1")
        private int factor = 1;

        @RecipeBuilderMethodDescription
        public MagmaticSmeltery.RecipeBuilder input(String input) {
            this.input = input;
            return this;
        }

        @RecipeBuilderMethodDescription
        public MagmaticSmeltery.RecipeBuilder input(OreDictIngredient input) {
            this.input = input.getOreDict();
            return this;
        }

        @RecipeBuilderMethodDescription
        public MagmaticSmeltery.RecipeBuilder output(String output) {
            this.output = output;
            return this;
        }

        @RecipeBuilderMethodDescription
        public MagmaticSmeltery.RecipeBuilder output(OreDictIngredient output) {
            this.output = output.getOreDict();
            return this;
        }

        @RecipeBuilderMethodDescription
        public MagmaticSmeltery.RecipeBuilder color(int color) {
            this.color = color;
            return this;
        }

        @RecipeBuilderMethodDescription
        public MagmaticSmeltery.RecipeBuilder factor(int factor) {
            this.factor = factor;
            return this;
        }

        public String getErrorMsg() {
            return "Error adding Magmatic Smeltery Recipe";
        }

        public void validate(GroovyLog.Msg msg) {
            msg.add(OreSmeltingRecipe.RECIPE_MAP.containsKey(input), "This OreDict can already be processed in Magmatic Smeltery: {}", input);
            msg.add(color < 0 || color >= (1 << 24), "color must be between 0 and 0xffffff, got {}", Integer.toHexString(color));
        }

        @Override
        public boolean validate() {
            GroovyLog.Msg msg = GroovyLog.msg(getErrorMsg()).error();
            validate(msg);
            return !msg.postIfNotEmpty();
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable OreSmeltingRecipe register() {
            if (!validate()) return null;
            OreSmeltingRecipe recipe = new OreSmeltingRecipe(input, output, color, factor);
            recipe.register();
            ModSupport.ESSENTIALCRAFT.get().magmaticSmeltery.addScripted(recipe);
            return recipe;
        }
    }
}
