package com.cleanroommc.groovyscript.compat.mods.extrautils2;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.rwtema.extrautils2.api.machine.IMachineRecipe;
import com.rwtema.extrautils2.api.machine.XUMachineFurnace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RegistryDescription(
        admonition = @Admonition(type = Admonition.Type.WARNING,
                                 format = Admonition.Format.STANDARD,
                                 hasTitle = true,
                                 value = "groovyscript.wiki.extrautils2.furnace.removeWarning")
)
public class Furnace extends VirtualizedRegistry<IMachineRecipe> {

    @Override
    public void onReload() {
        removeScripted().forEach(XUMachineFurnace.INSTANCE.recipes_registry::removeRecipe);
        restoreFromBackup().forEach(XUMachineFurnace.INSTANCE.recipes_registry::addRecipe);
    }

    public IMachineRecipe add(IMachineRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            XUMachineFurnace.INSTANCE.recipes_registry.addRecipe(recipe);
        }
        return recipe;
    }

    public boolean remove(IMachineRecipe recipe) {
        if (XUMachineFurnace.INSTANCE.recipes_registry.removeRecipe(recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('minecraft:emerald_ore:*')"))
    public boolean removeByInput(IIngredient input) {
        List<IMachineRecipe> agony = new ArrayList<>();
        for (IMachineRecipe recipe : XUMachineFurnace.INSTANCE.recipes_registry) {
            if (recipe.getJEIInputItemExamples().stream().flatMap(x -> x.getKey().get(XUMachineFurnace.INPUT).stream()).anyMatch(input)) {
                agony.add(recipe);
            }
        }
        for (IMachineRecipe recipe : agony) {
            addBackup(recipe);
            XUMachineFurnace.INSTANCE.recipes_registry.removeRecipe(recipe);
        }
        return !agony.isEmpty();
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<IMachineRecipe> streamRecipes() {
        List<IMachineRecipe> list = new ArrayList<>();
        for (IMachineRecipe recipe : XUMachineFurnace.INSTANCE.recipes_registry) {
            list.add(recipe);
        }
        return new SimpleObjectStream<>(list).setRemover(this::remove);
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        List<IMachineRecipe> agony = new ArrayList<>();
        for (IMachineRecipe recipe : XUMachineFurnace.INSTANCE.recipes_registry) {
            agony.add(recipe);
        }
        for (IMachineRecipe recipe : agony) {
            addBackup(recipe);
            XUMachineFurnace.INSTANCE.recipes_registry.removeRecipe(recipe);
        }
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay')).energy(1000).time(5)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<IMachineRecipe> {

        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int energy;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GT))
        private int time;

        @RecipeBuilderMethodDescription
        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Extra Utilities 2 Furnace recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(energy < 0, () -> "energy must not be negative");
            msg.add(time <= 0, () -> "time must not be less than or equal to 0");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public IMachineRecipe register() {
            if (!validate()) return null;
            com.rwtema.extrautils2.api.machine.RecipeBuilder builder = com.rwtema.extrautils2.api.machine.RecipeBuilder.newbuilder(XUMachineFurnace.INSTANCE);
            builder.setItemInput(XUMachineFurnace.INPUT, Arrays.asList(input.get(0).getMatchingStacks()), input.get(0).getAmount());
            builder.setItemOutput(XUMachineFurnace.OUTPUT, output.get(0));
            builder.setEnergy(energy);
            builder.setProcessingTime(time);

            IMachineRecipe recipe = builder.build();

            ModSupport.EXTRA_UTILITIES_2.get().furnace.add(recipe);
            return recipe;
        }
    }
}
