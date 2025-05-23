package com.cleanroommc.groovyscript.compat.mods.pneumaticcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import me.desht.pneumaticcraft.api.recipe.IPressureChamberRecipe;
import me.desht.pneumaticcraft.api.recipe.ItemIngredient;
import me.desht.pneumaticcraft.common.recipes.PressureChamberRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class PressureChamber extends StandardListRegistry<IPressureChamberRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay') * 3).output(item('minecraft:gold_ingot')).pressure(4)"),
            @Example(".input(item('minecraft:clay'), item('minecraft:gold_ingot'), item('minecraft:gold_block'), item('minecraft:gold_nugget'), item('minecraft:diamond'), item('minecraft:diamond_block'), item('minecraft:obsidian'), item('minecraft:stone'), item('minecraft:stone:1'), item('minecraft:stone:2'), item('minecraft:stone:3'), item('minecraft:stone:4'), item('minecraft:stone:5'), item('minecraft:stone:6')).output(item('minecraft:cobblestone')).pressure(4)")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<IPressureChamberRecipe> getRecipes() {
        return PressureChamberRecipe.recipes;
    }

    @MethodDescription(example = @Example("item('minecraft:diamond')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(entry -> {
            if (entry.getResult().stream().anyMatch(output)) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:iron_block')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(entry -> {
            if (entry.getInput().stream().map(ItemIngredient::getStacks).flatMap(Collection::stream).anyMatch(input)) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @Property(property = "input", comp = @Comp(gte = 1))
    @Property(property = "output", comp = @Comp(gte = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<IPressureChamberRecipe> {

        @Property
        private float pressure;

        @RecipeBuilderMethodDescription
        public RecipeBuilder pressure(float pressure) {
            this.pressure = pressure;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding PneumaticCraft Pressure Chamber recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, Integer.MAX_VALUE, 1, Integer.MAX_VALUE);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable IPressureChamberRecipe register() {
            if (!validate()) return null;
            IPressureChamberRecipe recipe = new PressureChamberRecipe.SimpleRecipe(input.stream().map(PneumaticCraft::toItemIngredient).toArray(ItemIngredient[]::new), pressure, output.toArray(new ItemStack[0]));
            ModSupport.PNEUMATIC_CRAFT.get().pressureChamber.add(recipe);
            return recipe;
        }
    }
}
