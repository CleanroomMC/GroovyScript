package com.cleanroommc.groovyscript.compat.mods.enderio;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.enderio.recipe.ManyToOneRecipe;
import com.cleanroommc.groovyscript.compat.mods.enderio.recipe.RecipeInput;
import com.cleanroommc.groovyscript.compat.mods.enderio.recipe.RecipeUtils;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import crazypants.enderio.base.recipe.*;
import crazypants.enderio.base.recipe.sagmill.SagMillRecipeManager;
import crazypants.enderio.base.recipe.slicensplice.SliceAndSpliceRecipeManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RegistryDescription
public class SliceNSplice extends VirtualizedRegistry<IManyToOneRecipe> {

    public SliceNSplice() {
        super(Alias.generateOfClassAnd(SliceNSplice.class, "SliceAndSplice"));
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:clay'), null, item('minecraft:clay')).input(null, item('minecraft:clay'), null).output(item('minecraft:gold_ingot')).energy(1000).xp(5)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(ItemStack output, List<IIngredient> input, int energy) {
        recipeBuilder()
                .energy(energy)
                .output(output)
                .input(input)
                .register();
    }

    public void add(IManyToOneRecipe recipe) {
        SliceAndSpliceRecipeManager.getInstance().addRecipe(recipe);
        addScripted(recipe);
    }

    public void add(Recipe recipe) {
        BasicManyToOneRecipe r = new BasicManyToOneRecipe(recipe);
        SliceAndSpliceRecipeManager.getInstance().addRecipe(r);
        addScripted(r);
    }

    public boolean remove(IManyToOneRecipe recipe) {
        if (recipe == null) return false;
        SagMillRecipeManager.getInstance().getRecipes().remove((Recipe) recipe);
        addBackup(recipe);
        return true;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example("item('enderio:item_material:40')"))
    public void remove(ItemStack output) {
        int count = 0;
        Iterator<IManyToOneRecipe> iter = SliceAndSpliceRecipeManager.getInstance().getRecipes().iterator();
        while (iter.hasNext()) {
            IManyToOneRecipe recipe = iter.next();
            if (OreDictionary.itemMatches(output, recipe.getOutput(), false)) {
                count++;
                iter.remove();
                addBackup(recipe);
            }
        }
        if (count == 0) {
            GroovyLog.get().error("No EnderIO Slice'n'Splice recipe found for " + output.getDisplayName());
        }
    }

    @MethodDescription(example = @Example("[item('enderio:item_alloy_ingot:7'), item('enderio:block_enderman_skull'), item('enderio:item_alloy_ingot:7'), item('minecraft:potion').withNbt(['Potion': 'minecraft:water']), item('enderio:item_basic_capacitor'), item('minecraft:potion').withNbt(['Potion': 'minecraft:water'])]"))
    public void removeByInput(List<ItemStack> input) {
        IRecipe recipe = SliceAndSpliceRecipeManager.getInstance().getRecipeForInputs(RecipeLevel.IGNORE, RecipeUtils.getMachineInputs(input));
        if (recipe instanceof IManyToOneRecipe) {
            SliceAndSpliceRecipeManager.getInstance().getRecipes().remove(recipe);
            addBackup((IManyToOneRecipe) recipe);
        } else {
            GroovyLog.get().error("No EnderIO Slice'n'Splice recipe found for " + input);
        }
    }

    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(SliceAndSpliceRecipeManager.getInstance().getRecipes()::remove);
        restoreFromBackup().forEach(SliceAndSpliceRecipeManager.getInstance().getRecipes()::add);
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<IManyToOneRecipe> streamRecipes() {
        return new SimpleObjectStream<>(SliceAndSpliceRecipeManager.getInstance().getRecipes())
                .setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        SliceAndSpliceRecipeManager.getInstance().getRecipes().forEach(this::addBackup);
        SliceAndSpliceRecipeManager.getInstance().getRecipes().clear();
    }

    @Property(property = "input", valid = {@Comp(type = Comp.Type.GTE, value = "1"), @Comp(type = Comp.Type.LTE, value = "6")})
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<IRecipe> {

        @Property(valid = @Comp(value = "0",type = Comp.Type.GTE))
        private float xp;
        @Property(valid = @Comp(value = "0",type = Comp.Type.GT))
        private int energy;

        @RecipeBuilderMethodDescription
        public RecipeBuilder xp(float xp) {
            this.xp = xp;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding EnderIO Slice'n'Splice recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            int inputSize = input.getRealSize();
            output.trim();
            msg.add(inputSize < 1 || inputSize > 6, () -> "Must have 1 - 6 inputs, but found " + input.size());
            msg.add(output.size() != 1, () -> "Must have exactly 1 output, but found " + output.size());
            validateFluids(msg);
            if (energy <= 0) energy = 5000;
            if (xp < 0) xp = 0;
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable IRecipe register() {
            if (!validate()) return null;
            RecipeOutput recipeOutput = new RecipeOutput(output.get(0), 1, xp);
            List<IRecipeInput> inputs = new ArrayList<>();
            for (int i = 0; i < input.size(); i++) {
                IIngredient ingredient = input.get(i);
                if (IngredientHelper.isEmpty(ingredient)) continue;
                inputs.add(new RecipeInput(ingredient, i));
            }
            ManyToOneRecipe recipe = new ManyToOneRecipe(recipeOutput, energy, RecipeBonusType.NONE, RecipeLevel.IGNORE, inputs.toArray(new IRecipeInput[0]));
            ModSupport.ENDER_IO.get().sliceNSplice.add(recipe);
            return recipe;
        }
    }
}
