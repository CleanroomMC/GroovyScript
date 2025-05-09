package com.cleanroommc.groovyscript.compat.mods.bewitchment;

import com.bewitchment.api.registry.SigilRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RegistryDescription(override = @MethodOverride(method = @MethodDescription(method = "remove(Lnet/minecraft/util/ResourceLocation;)V", example = @Example("resource('bewitchment:mending')"))))
public class Sigil extends ForgeRegistryWrapper<SigilRecipe> {

    public Sigil() {
        super(GameRegistry.findRegistry(SigilRecipe.class));
    }

    @RecipeBuilderDescription
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(example = @Example("item('bewitchment:bottle_of_blood')"))
    public void removeByInput(IIngredient input) {
        getRegistry().forEach(recipe -> {
            if (recipe.input.stream().map(Ingredient::getMatchingStacks).flatMap(Arrays::stream).anyMatch(input)) {
                remove(recipe);
            }
        });
    }

    @MethodDescription(example = @Example("item('bewitchment:sigil_disorientation')"))
    public void removeByOutput(IIngredient output) {
        getRegistry().forEach(recipe -> {
            if (output.test(recipe.output)) {
                remove(recipe);
            }
        });
    }

    @Property(property = "name")
    @Property(property = "input", comp = @Comp(eq = 25))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<SigilRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Bewitchment Sigil Recipe";
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_sigil_recipe_";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 25, 25, 1, 1);
            validateFluids(msg);
            validateName();
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable SigilRecipe register() {
            // TODO make grid-based recipe?
            List<Ingredient> inputs = input.stream().map(IIngredient::toMcIngredient).collect(Collectors.toList());
            SigilRecipe recipe = new SigilRecipe(super.name, inputs, output.get(0));
            ModSupport.BEWITCHMENT.get().sigil.add(recipe);
            return recipe;
        }
    }
}
