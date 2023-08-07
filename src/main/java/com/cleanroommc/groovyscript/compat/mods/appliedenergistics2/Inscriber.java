package com.cleanroommc.groovyscript.compat.mods.appliedenergistics2;

import appeng.api.AEApi;
import appeng.api.features.IInscriberRecipe;
import appeng.api.features.IInscriberRecipeBuilder;
import appeng.api.features.InscriberProcessType;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Inscriber extends VirtualizedRegistry<IInscriberRecipe> {

    public Inscriber() {
        super();
    }

    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(AEApi.instance().registries().inscriber()::removeRecipe);
        restoreFromBackup().forEach(AEApi.instance().registries().inscriber()::addRecipe);
    }

    public void add(IInscriberRecipe recipe) {
        AEApi.instance().registries().inscriber().addRecipe(recipe);
        addScripted(recipe);
    }

    public void remove(IInscriberRecipe recipe) {
        AEApi.instance().registries().inscriber().removeRecipe(recipe);
        addBackup(recipe);
    }

    public void removeByOutput(ItemStack output) {
        List<IInscriberRecipe> recipes = AEApi.instance().registries().inscriber().getRecipes().stream().filter(x -> ItemStack.areItemStacksEqual(x.getOutput(), output)).collect(Collectors.toList());
        for (IInscriberRecipe recipe : recipes) {
            AEApi.instance().registries().inscriber().removeRecipe(recipe);
            addBackup(recipe);
        }
    }

    public void removeAll() {
        for (IInscriberRecipe recipe : AEApi.instance().registries().inscriber().getRecipes()) {
            AEApi.instance().registries().inscriber().removeRecipe(recipe);
            addBackup(recipe);
        }
    }


    public static class RecipeBuilder extends AbstractRecipeBuilder<IInscriberRecipe> {

        private InscriberProcessType type = InscriberProcessType.PRESS;
        private ItemStack top = ItemStack.EMPTY;
        private ItemStack bottom = ItemStack.EMPTY;


        public RecipeBuilder type(InscriberProcessType type) {
            this.type = type;
            return this;
        }

        public RecipeBuilder type(String type) {
            this.type = InscriberProcessType.valueOf(type.toUpperCase(Locale.ROOT));
            return this;
        }

        public RecipeBuilder press() {
            this.type = InscriberProcessType.PRESS;
            return this;
        }

        public RecipeBuilder inscribe() {
            this.type = InscriberProcessType.INSCRIBE;
            return this;
        }

        public RecipeBuilder top(ItemStack top) {
            this.top = top;
            return this;
        }

        public RecipeBuilder bottom(ItemStack bottom) {
            this.bottom = bottom;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Applied Energistics 2 Inscriber recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(type == null, "type must be defined");
            msg.add(IngredientHelper.isEmpty(top) && IngredientHelper.isEmpty(bottom), "either top or bottom must be defined, yet neither were");
        }

        @Override
        public @Nullable IInscriberRecipe register() {
            if (!validate()) return null;
            IInscriberRecipeBuilder builder = AEApi.instance().registries().inscriber().builder()
                    .withInputs(input.stream().flatMap(x -> Arrays.stream(x.toMcIngredient().getMatchingStacks())).collect(Collectors.toList()))
                    .withOutput(output.get(0))
                    .withProcessType(type);
            if (!IngredientHelper.isEmpty(top)) builder.withTopOptional(top);
            if (!IngredientHelper.isEmpty(bottom)) builder.withBottomOptional(bottom);
            IInscriberRecipe recipe = builder.build();
            ModSupport.APPLIED_ENERGISTICS_2.get().inscriber.add(recipe);
            return recipe;
        }
    }

}
