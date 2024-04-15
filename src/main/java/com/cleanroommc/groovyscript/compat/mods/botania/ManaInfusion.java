package com.cleanroommc.groovyscript.compat.mods.botania;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipeManaInfusion;

@RegistryDescription(
        admonition = @Admonition(value = "groovyscript.wiki.botania.mana_infusion.note", type = Admonition.Type.WARNING)
)
public class ManaInfusion extends VirtualizedRegistry<RecipeManaInfusion> {

    @RecipeBuilderDescription(example = @Example(".input(ore('ingotGold')).output(item('botania:manaresource', 1)).mana(500).catalyst(blockstate('minecraft:stone'))"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(BotaniaAPI.manaInfusionRecipes::remove);
        BotaniaAPI.manaInfusionRecipes.addAll(restoreFromBackup());
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public RecipeManaInfusion add(ItemStack output, IIngredient input, int mana) {
        RecipeManaInfusion recipe = new RecipeManaInfusion(output, input instanceof OreDictIngredient ? ((OreDictIngredient) input).getOreDict()
                                                                                                      : input.getMatchingStacks()[0], mana);
        add(recipe);
        return recipe;
    }

    public void add(RecipeManaInfusion recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        BotaniaAPI.manaInfusionRecipes.add(recipe);
    }

    public boolean remove(RecipeManaInfusion recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        return BotaniaAPI.manaInfusionRecipes.remove(recipe);
    }

    @MethodDescription(example = @Example("item('botania:managlass')"))
    public boolean removeByOutput(ItemStack output) {
        if (BotaniaAPI.manaInfusionRecipes.removeIf(recipe -> {
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
        if (BotaniaAPI.manaInfusionRecipes.removeIf(recipe -> {
            boolean found = recipe.getInput() instanceof ItemStack ? input.test((ItemStack) recipe.getInput())
                                                                   : (input instanceof OreDictIngredient && ((OreDictIngredient) input).getOreDict().equals(recipe.getInput()));
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
        if (BotaniaAPI.manaInfusionRecipes.removeIf(recipe -> {
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

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        BotaniaAPI.manaInfusionRecipes.forEach(this::addBackup);
        BotaniaAPI.manaInfusionRecipes.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<RecipeManaInfusion> streamRecipes() {
        return new SimpleObjectStream<>(BotaniaAPI.manaInfusionRecipes).setRemover(this::remove);
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public class RecipeBuilder extends AbstractRecipeBuilder<RecipeManaInfusion> {

        @Property(defaultValue = "100", valid = @Comp(value = "1", type = Comp.Type.GTE))
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
        public String getErrorMsg() {
            return "Error adding Botania Mana Infusion recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateFluids(msg, 0, 0, 0, 0);
            validateItems(msg, 1, 1, 1, 1);
            msg.add(mana < 1, "Mana amount must be at least 1, got " + mana);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable RecipeManaInfusion register() {
            if (!validate()) return null;
            RecipeManaInfusion recipe = new RecipeManaInfusion(output.get(0),
                                                               input.get(0) instanceof OreDictIngredient ? ((OreDictIngredient) input.get(0)).getOreDict()
                                                                                                         : input.get(0).getMatchingStacks()[0], mana);
            if (catalyst != null) recipe.setCatalyst(catalyst);
            add(recipe);
            return recipe;
        }
    }
}
