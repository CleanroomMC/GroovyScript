package com.cleanroommc.groovyscript.compat.mods.betterwithmods;

import betterwithmods.common.registry.HopperInteractions;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.ItemStackList;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class Hopper extends VirtualizedRegistry<HopperInteractions.HopperRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".name('betterwithmods:iron_bar').input(ore('sand')).output(item('minecraft:clay')).inWorldItemOutput(item('minecraft:gold_ingot'))"),
            @Example(".name('betterwithmods:wicker').input(item('minecraft:clay')).inWorldItemOutput(item('minecraft:gold_ingot'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> HopperInteractions.RECIPES.removeIf(r -> r == recipe));
        HopperInteractions.RECIPES.addAll(restoreFromBackup());
    }

    public HopperInteractions.HopperRecipe add(HopperInteractions.HopperRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            HopperInteractions.RECIPES.add(recipe);
        }
        return recipe;
    }

    public boolean remove(HopperInteractions.HopperRecipe recipe) {
        if (HopperInteractions.RECIPES.removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example("item('minecraft:gunpowder')"))
    public boolean removeByOutput(IIngredient output) {
        return HopperInteractions.RECIPES.removeIf(r -> {
            for (ItemStack itemstack : r.getOutputs()) {
                if (output.test(itemstack)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('minecraft:gunpowder')"))
    public boolean removeByInput(IIngredient input) {
        return HopperInteractions.RECIPES.removeIf(r -> {
            for (ItemStack item : r.getInputs().getMatchingStacks()) {
                if (input.test(item)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<HopperInteractions.HopperRecipe> streamRecipes() {
        return new SimpleObjectStream<>(HopperInteractions.RECIPES).setRemover(this::remove);
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        HopperInteractions.RECIPES.forEach(this::addBackup);
        HopperInteractions.RECIPES.clear();
    }

    @Property(property = "name", value = "groovyscript.wiki.betterwithmods.hopper.name.value", valid = @Comp(value = "null", type = Comp.Type.NOT))
    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = {@Comp(value = "0", type = Comp.Type.GTE), @Comp(value = "2", type = Comp.Type.LTE)})
    public static class RecipeBuilder extends AbstractRecipeBuilder<HopperInteractions.HopperRecipe> {

        @Property(valid = {@Comp(value = "0", type = Comp.Type.GTE), @Comp(value = "2", type = Comp.Type.LTE)})
        protected final ItemStackList inWorldItemOutput = new ItemStackList();

        @RecipeBuilderMethodDescription
        public RecipeBuilder inWorldItemOutput(ItemStack inWorldItemOutput) {
            this.inWorldItemOutput.add(inWorldItemOutput);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder inWorldItemOutput(ItemStack... inWorldItemOutputs) {
            for (ItemStack inWorldItemOutput : inWorldItemOutputs) {
                inWorldItemOutput(inWorldItemOutput);
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder inWorldItemOutput(Collection<ItemStack> inWorldItemOutputs) {
            for (ItemStack inWorldItemOutput : inWorldItemOutputs) {
                inWorldItemOutput(inWorldItemOutput);
            }
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Better With Mods Filtered Hopper recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            msg.add(name == null, "name cannot be null");
            validateItems(msg, 1, 1, 0, 2);
            validateCustom(msg, inWorldItemOutput, 0, 2, "item in world output");
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable HopperInteractions.HopperRecipe register() {
            if (!validate()) return null;

            HopperInteractions.HopperRecipe recipe = new HopperInteractions.HopperRecipe(name.toString(), input.get(0).toMcIngredient(), output, inWorldItemOutput);
            ModSupport.BETTER_WITH_MODS.get().hopper.add(recipe);
            return recipe;
        }
    }

}
