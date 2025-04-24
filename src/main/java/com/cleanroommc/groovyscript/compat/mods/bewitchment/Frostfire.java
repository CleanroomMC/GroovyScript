package com.cleanroommc.groovyscript.compat.mods.bewitchment;

import com.bewitchment.api.registry.FrostfireRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class Frostfire extends ForgeRegistryWrapper<FrostfireRecipe> {

    public Frostfire() {
        super(GameRegistry.findRegistry(FrostfireRecipe.class));
    }

    @RecipeBuilderDescription
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void removeByOutput(IIngredient output) {
        this.getRegistry().getValuesCollection().forEach(recipe -> {
            if (output.test(recipe.output)) {
                remove(recipe);
            }
        });
    }

    @Property(property = "name")
    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<FrostfireRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Bewitchment Frostfire recipe";
        }

        @Override
        public String getRecipeNamePrefix() {
            return "frostfire_recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            validateName();
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable FrostfireRecipe register() {
            if (!validate()) return null;
            FrostfireRecipe recipe = new FrostfireRecipe(super.name, input.get(0).toMcIngredient(), output.get(0));
            ModSupport.BEWITCHMENT.get().frostfire.add(recipe);
            return recipe;
        }
    }
}
