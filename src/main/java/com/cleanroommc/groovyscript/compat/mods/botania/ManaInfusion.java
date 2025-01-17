package com.cleanroommc.groovyscript.compat.mods.botania;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipeManaInfusion;

import java.util.Collection;

@RegistryDescription(
        admonition = @Admonition(value = "groovyscript.wiki.botania.mana_infusion.note", type = Admonition.Type.WARNING)
)
public class ManaInfusion extends StandardListRegistry<RecipeManaInfusion> {

    @RecipeBuilderDescription(example = @Example(".input(ore('ingotGold')).output(item('botania:manaresource', 1)).mana(500).catalyst(blockstate('minecraft:stone'))"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<RecipeManaInfusion> getRecipes() {
        return BotaniaAPI.manaInfusionRecipes;
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public RecipeManaInfusion add(ItemStack output, IIngredient input, int mana) {
        return recipeBuilder().mana(mana).output(output).input(input).register();
    }

    @MethodDescription(example = @Example("item('botania:managlass')"))
    public boolean removeByOutput(ItemStack output) {
        if (getRecipes().removeIf(recipe -> {
            boolean found = ItemStack.areItemStacksEqual(recipe.getOutput(), output);
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Botania Mana Infusion recipe")
                .add("could not find recipe with output {}", output)
                .error()
                .post();
        return false;
    }

    @MethodDescription(example = @Example("item('minecraft:ender_pearl')"))
    public boolean removeByInput(IIngredient input) {
        if (getRecipes().removeIf(recipe -> {
            boolean found = recipe.getInput() instanceof ItemStack itemStack
                    ? input.test(itemStack)
                    : (input instanceof OreDictIngredient oreDictIngredient && oreDictIngredient.getOreDict().equals(recipe.getInput()));
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Botania Mana Infusion recipe")
                .add("could not find recipe with input {}", input)
                .error()
                .post();
        return false;
    }

    @MethodDescription(example = @Example("blockstate('botania:alchemycatalyst')"))
    public boolean removeByCatalyst(IBlockState catalyst) {
        if (getRecipes().removeIf(recipe -> {
            if (recipe.getCatalyst() == null) return false;
            boolean found = recipe.getCatalyst().equals(catalyst);
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Botania Mana Infusion recipe")
                .add("could not find recipe with catalyst {}", catalyst)
                .error()
                .post();
        return false;
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public class RecipeBuilder extends AbstractRecipeBuilder<RecipeManaInfusion> {

        @Property(defaultValue = "100", comp = @Comp(gte = 1))
        protected int mana = 100;
        @Property
        protected IBlockState catalyst;

        @RecipeBuilderMethodDescription
        public RecipeBuilder mana(int amount) {
            this.mana = amount;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder catalyst(IBlockState block) {
            this.catalyst = block;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "catalyst")
        public RecipeBuilder useAlchemy() {
            return catalyst(RecipeManaInfusion.alchemyState);
        }

        @RecipeBuilderMethodDescription(field = "catalyst")
        public RecipeBuilder useConjuration() {
            return catalyst(RecipeManaInfusion.conjurationState);
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Botania Mana Infusion recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateFluids(msg);
            validateItems(msg, 1, 1, 1, 1);
            msg.add(mana < 1, "Mana amount must be at least 1, got " + mana);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable RecipeManaInfusion register() {
            if (!validate()) return null;
            RecipeManaInfusion recipe = new RecipeManaInfusion(
                    output.get(0),
                    input.get(0) instanceof OreDictIngredient ? ((OreDictIngredient) input.get(0)).getOreDict() : input.get(0).getMatchingStacks()[0],
                    mana);
            if (catalyst != null) recipe.setCatalyst(catalyst);
            add(recipe);
            return recipe;
        }
    }
}
