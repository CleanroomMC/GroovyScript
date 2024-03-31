package com.cleanroommc.groovyscript.compat.mods.alchemistry;

import al132.alchemistry.recipes.*;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RegistryDescription
public class Dissolver extends VirtualizedRegistry<DissolverRecipe> {

    public Dissolver() {
        super(Alias.generateOfClass(Dissolver.class).andGenerate("ChemicalDissolver"));
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> ModRecipes.INSTANCE.getDissolverRecipes().removeIf(r -> r == recipe));
        ModRecipes.INSTANCE.getDissolverRecipes().addAll(restoreFromBackup());
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:gold_ingot')).probabilityOutput(item('minecraft:clay')).reversible().rolls(1)"),
            @Example(".input(item('minecraft:diamond')).probabilityOutput(30, item('minecraft:clay')).probabilityOutput(30, item('minecraft:clay')).probabilityOutput(30, item('minecraft:clay')).rolls(10)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public DissolverRecipe add(DissolverRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            ModRecipes.INSTANCE.getDissolverRecipes().add(recipe);
        }
        return recipe;
    }

    public boolean remove(DissolverRecipe recipe) {
        if (ModRecipes.INSTANCE.getDissolverRecipes().removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('alchemistry:compound:1')"))
    public boolean removeByInput(IIngredient input) {
        return ModRecipes.INSTANCE.getDissolverRecipes().removeIf(r -> {
            for (ItemStack itemstack : r.getInputs()) {
                if (input.test(itemstack)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<DissolverRecipe> streamRecipes() {
        return new SimpleObjectStream<>(ModRecipes.INSTANCE.getDissolverRecipes()).setRemover(this::remove);
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ModRecipes.INSTANCE.getDissolverRecipes().forEach(this::addBackup);
        ModRecipes.INSTANCE.getDissolverRecipes().clear();
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<DissolverRecipe> {

        @Property(valid = @Comp(value = "1", type = Comp.Type.GTE))
        private final ArrayList<ProbabilityGroup> probabilityGroup = new ArrayList<>();
        @Property
        private boolean reversible;
        @Property
        private boolean relativeProbability;
        @Property(defaultValue = "1", valid = @Comp(value = "1", type = Comp.Type.GTE))
        private int rolls = 1;

        @RecipeBuilderMethodDescription(field = "probabilityGroup")
        public RecipeBuilder probabilityOutput(double probability, ItemStack... probabilityOutputs) {
            probabilityGroup.add(new ProbabilityGroup(Arrays.asList(probabilityOutputs), probability));
            return this;
        }

        @RecipeBuilderMethodDescription(field = "probabilityGroup")
        public RecipeBuilder probabilityOutput(ItemStack... probabilityOutputs) {
            return this.probabilityOutput(100, probabilityOutputs);
        }

        @RecipeBuilderMethodDescription(field = "probabilityGroup")
        public RecipeBuilder probabilityOutput(double probability, Collection<ItemStack> probabilityOutputs) {
            probabilityGroup.add(new ProbabilityGroup((List<ItemStack>) probabilityOutputs, probability));
            return this;
        }

        @RecipeBuilderMethodDescription(field = "probabilityGroup")
        public RecipeBuilder probabilityOutput(Collection<ItemStack> probabilityOutputs) {
            return this.probabilityOutput(100, probabilityOutputs);
        }

        @RecipeBuilderMethodDescription(field = "probabilityGroup")
        public RecipeBuilder output(ItemStack... probabilityOutputs) {
            return this.probabilityOutput(100, probabilityOutputs);
        }

        @RecipeBuilderMethodDescription(field = "probabilityGroup")
        public RecipeBuilder output(Collection<ItemStack> probabilityOutputs) {
            return this.probabilityOutput(100, probabilityOutputs);
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder reversible(boolean reversible) {
            this.reversible = reversible;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder reversible() {
            this.reversible = !reversible;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder relativeProbability(boolean relativeProbability) {
            this.relativeProbability = relativeProbability;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder relativeProbability() {
            this.relativeProbability = !relativeProbability;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder rolls(int rolls) {
            this.rolls = rolls;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Alchemistry Dissolver recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg);
            validateCustom(msg, probabilityGroup, 1, Integer.MAX_VALUE, "probability group");
            msg.add(rolls < 1, "rolls must be greater than or equal to 1, yet it was {}", rolls);
        }

        @Nullable
        @Override
        @RecipeBuilderRegistrationMethod
        public DissolverRecipe register() {
            if (!validate()) return null;

            DissolverRecipe recipe = new DissolverRecipe(input.get(0).toMcIngredient(), false, new ProbabilitySet(probabilityGroup, relativeProbability, rolls));
            if (reversible) {
                ModSupport.ALCHEMISTRY.get().combiner.add(new CombinerRecipe(recipe.getInputs().get(0), probabilityGroup.stream().map(ProbabilityGroup::getOutput).flatMap(Collection::stream).collect(Collectors.toList()), ""));
            }
            ModSupport.ALCHEMISTRY.get().dissolver.add(recipe);
            return recipe;
        }
    }
}
