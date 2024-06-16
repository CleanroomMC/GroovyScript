package com.cleanroommc.groovyscript.compat.mods.essentialcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.IRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import essentialcraft.api.OreSmeltingRecipe;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription(reloadability = RegistryDescription.Reloadability.DISABLED, admonition={
    @Admonition(value = "groovyscript.wiki.essentialcraft.magmatic_smeltery.note0", type = Admonition.Type.DANGER),
    @Admonition(value = "groovyscript.wiki.essentialcraft.magmatic_smeltery.note1", type = Admonition.Type.WARNING),
})
public class MagmaticSmeltery extends VirtualizedRegistry<OreSmeltingRecipe> {
    static boolean didReload = false;

    @RecipeBuilderDescription(example = @Example(".input('blockIron').output('ingotGold').factor(3).color(0x0000ff)"))
    public MagmaticSmeltery.RecipeBuilder recipeBuilder() {
        return new MagmaticSmeltery.RecipeBuilder();
    }

    @Override
    public void onReload() {
        Collection<OreSmeltingRecipe> scripted = removeScripted();
        Collection<OreSmeltingRecipe> backup = restoreFromBackup();
        if (!scripted.isEmpty() || backup.isEmpty()) {
            GroovyLog.msg("Magmatic Smeltery recipes cannot be currently reloaded!")
                    .warn()
                    .post();
        }
        didReload = true;
    }

    @MethodDescription(example = @Example("'oreDiamond'"))
    public boolean removeByInput(String x) {
        if (didReload) return false;
        if (OreSmeltingRecipe.RECIPE_MAP.containsKey(x)) {
            OreSmeltingRecipe recipe = OreSmeltingRecipe.RECIPE_MAP.get(x);
            addBackup(recipe);
            OreSmeltingRecipe.removeRecipe(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(example = @Example(priority = 2000, commented = true))
    public void removeAll() {
        if (didReload) return;
        OreSmeltingRecipe.RECIPES.forEach(this::addBackup);
        OreSmeltingRecipe.RECIPES.clear();
        OreSmeltingRecipe.RECIPE_MAP.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<OreSmeltingRecipe> streamRecipes() {
        return new SimpleObjectStream<>(OreSmeltingRecipe.RECIPES).setRemover(r -> removeByInput(r.oreName));
    }

    public class RecipeBuilder implements IRecipeBuilder<OreSmeltingRecipe> {
        @Property(valid = @Comp("1"))
        private String input;

        @Property(valid = @Comp("1"))
        private String output;

        @Property(valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "0xffffff")})
        private int color;

        @Property(valid = @Comp(type = Comp.Type.GTE, value = "1"), defaultValue = "1")
        private int factor = 1;

        @RecipeBuilderMethodDescription
        public MagmaticSmeltery.RecipeBuilder input(String input) {
            this.input = input;
            return this;
        }

        @RecipeBuilderMethodDescription
        public MagmaticSmeltery.RecipeBuilder output(String output) {
            this.output = output;
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
            msg.add(!OreDictionary.doesOreNameExist(input), "Unknown input OreDict: {}", input);
            msg.add(!OreDictionary.doesOreNameExist(output), "Unknown output OreDict: {}", output);
            msg.add(OreSmeltingRecipe.RECIPE_MAP.containsKey(input), "This OreDict can already be processed in Magmatic Smeltery: {}", input);
            msg.add(color < 0 || color >= (1 << 24), "color must be between 0 and 0xffffff, got {}", color);
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
            if (didReload || !validate()) return null;
            OreSmeltingRecipe recipe = new OreSmeltingRecipe(input, output, color, factor);
            recipe.register();
            addScripted(recipe);
            return recipe;
        }
    }
}
