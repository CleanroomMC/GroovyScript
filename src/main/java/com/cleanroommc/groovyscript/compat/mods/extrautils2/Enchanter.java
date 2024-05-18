package com.cleanroommc.groovyscript.compat.mods.extrautils2;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.rwtema.extrautils2.api.machine.IMachineRecipe;
import com.rwtema.extrautils2.api.machine.XUMachineEnchanter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RegistryDescription
public class Enchanter extends VirtualizedRegistry<IMachineRecipe> {

    @Override
    public void onReload() {
        removeScripted().forEach(XUMachineEnchanter.INSTANCE.recipes_registry::removeRecipe);
        restoreFromBackup().forEach(XUMachineEnchanter.INSTANCE.recipes_registry::addRecipe);
    }

    public IMachineRecipe add(IMachineRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            XUMachineEnchanter.INSTANCE.recipes_registry.addRecipe(recipe);
        }
        return recipe;
    }

    public boolean remove(IMachineRecipe recipe) {
        if (XUMachineEnchanter.INSTANCE.recipes_registry.removeRecipe(recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(example = @Example("item('minecraft:bookshelf')"))
    public boolean removeByInput(IIngredient input) {
        List<IMachineRecipe> agony = new ArrayList<>();
        for (IMachineRecipe recipe : XUMachineEnchanter.INSTANCE.recipes_registry) {
            if (recipe.getJEIInputItemExamples().stream().flatMap(x -> x.getKey().get(XUMachineEnchanter.INPUT).stream()).anyMatch(input)) {
                agony.add(recipe);
            }
        }
        for (IMachineRecipe recipe : agony) {
            addBackup(recipe);
            XUMachineEnchanter.INSTANCE.recipes_registry.removeRecipe(recipe);
        }
        return !agony.isEmpty();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<IMachineRecipe> streamRecipes() {
        List<IMachineRecipe> list = new ArrayList<>();
        for (IMachineRecipe recipe : XUMachineEnchanter.INSTANCE.recipes_registry) {
            list.add(recipe);
        }
        return new SimpleObjectStream<>(list).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        List<IMachineRecipe> agony = new ArrayList<>();
        for (IMachineRecipe recipe : XUMachineEnchanter.INSTANCE.recipes_registry) {
            agony.add(recipe);
        }
        for (IMachineRecipe recipe : agony) {
            addBackup(recipe);
            XUMachineEnchanter.INSTANCE.recipes_registry.removeRecipe(recipe);
        }
    }


    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:gold_ingot'), item('minecraft:diamond')).output(item('minecraft:clay')).energy(1000).time(5)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "2", type = Comp.Type.LTE)})
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
            return "Error adding Extra Utilities 2 Enchanter recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 2, 2, 1, 1);
            validateFluids(msg);
            msg.add(energy < 0, () -> "energy must not be negative");
            msg.add(time <= 0, () -> "time must not be less than or equal to 0");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public IMachineRecipe register() {
            if (!validate()) return null;
            com.rwtema.extrautils2.api.machine.RecipeBuilder builder = com.rwtema.extrautils2.api.machine.RecipeBuilder.newbuilder(XUMachineEnchanter.INSTANCE);
            builder.setItemInput(XUMachineEnchanter.INPUT, Arrays.asList(input.get(0).getMatchingStacks()), input.get(0).getAmount());
            builder.setItemOutput(XUMachineEnchanter.OUTPUT, output.get(0));
            if (input.get(1).getAmount() > 0) {
                builder.setItemInput(XUMachineEnchanter.INPUT_LAPIS, Arrays.asList(input.get(1).getMatchingStacks()), input.get(1).getAmount());
            }
            builder.setEnergy(energy);
            builder.setProcessingTime(time);

            IMachineRecipe recipe = builder.build();

            ModSupport.EXTRA_UTILITIES_2.get().enchanter.add(recipe);
            return recipe;
        }
    }
}
