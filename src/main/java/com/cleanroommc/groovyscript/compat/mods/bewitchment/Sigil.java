package com.cleanroommc.groovyscript.compat.mods.bewitchment;

import com.bewitchment.api.registry.SigilRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.helper.recipe.RecipeName;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class Sigil extends ForgeRegistryWrapper<SigilRecipe> {
    public Sigil() {
        super(GameRegistry.findRegistry(SigilRecipe.class));
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void removeByOutput(ItemStack output) {
        this.getRegistry().getValuesCollection().forEach(recipe -> {
            if (recipe.output.isItemEqual(output)) {
                remove(recipe);
            }
        });
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<SigilRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Bewitchment Sigil Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 25, 25, 1, 1);
            validateFluids(msg);
            if (name == null || name.getNamespace().isEmpty() || name.getPath().isEmpty()) name = RecipeName.generateRl("sigil_recipe");
        }

        @Override
        public @Nullable SigilRecipe register() {
            List<Ingredient> inputs = input.stream().map(IIngredient::toMcIngredient).collect(Collectors.toList());
            SigilRecipe recipe = new SigilRecipe(name, inputs, output.get(0));
            Bewitchment.sigil.add(recipe);
            return recipe;
        }
    }
}
