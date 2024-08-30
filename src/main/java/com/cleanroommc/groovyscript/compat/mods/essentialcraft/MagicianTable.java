package com.cleanroommc.groovyscript.compat.mods.essentialcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import essentialcraft.api.MagicianTableRecipe;
import essentialcraft.api.MagicianTableRecipes;
import net.minecraft.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription(admonition = @Admonition(value = "groovyscript.wiki.essentialcraft.magician_table.note0", type = Admonition.Type.WARNING))
public class MagicianTable extends StandardListRegistry<MagicianTableRecipe> {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:diamond'), ore('ingotGold'), ore('ingotGold'), ore('stickWood'), ore('stickWood')).output(item('minecraft:iron_ingot')).mru(500)"))
    public MagicianTable.RecipeBuilder recipeBuilder() {
        return new MagicianTable.RecipeBuilder();
    }

    @Override
    public Collection<MagicianTableRecipe> getRecipes() {
        return MagicianTableRecipes.RECIPES;
    }

    @MethodDescription(example = @Example("item('essentialcraft:genitem')"))
    public boolean removeByOutput(IIngredient x) {
        return getRecipes().removeIf(r -> {
            if (x.test(r.getRecipeOutput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @Property(property = "input", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "5", type = Comp.Type.LTE)})
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<MagicianTableRecipe> {

        @Property(valid = @Comp(type = Comp.Type.GTE, value = "1"))
        private int mru;

        @RecipeBuilderMethodDescription
        public RecipeBuilder mru(int cost) {
            mru = cost;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Magician Table Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 5, 1, 1);
            validateFluids(msg);
            msg.add(mru < 1, "mru cost must be 1 or greater, got {}", mru);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable MagicianTableRecipe register() {
            if (!validate()) return null;
            Ingredient[] inputIngredient = input.stream().map(IIngredient::toMcIngredient).toArray(Ingredient[]::new);
            MagicianTableRecipe recipe = new MagicianTableRecipe(inputIngredient, output.get(0), mru);
            ModSupport.ESSENTIALCRAFT.get().magicianTable.addScripted(recipe);
            MagicianTableRecipes.addRecipe(recipe);
            return recipe;
        }
    }
}
