package com.cleanroommc.groovyscript.compat.mods.pneumaticcraft;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import me.desht.pneumaticcraft.common.item.ItemAssemblyProgram;
import me.desht.pneumaticcraft.common.recipes.AssemblyRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RegistryDescription
public class AssemblyController extends VirtualizedRegistry<AssemblyRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay') * 3).output(item('minecraft:gold_ingot') * 6).drill()"),
            @Example(".input(item('minecraft:gold_ingot') * 6).output(item('minecraft:diamond')).laser()"),
            @Example(".input(item('minecraft:stone')).output(item('minecraft:clay') * 5).laser()")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    private List<AssemblyRecipe> map(AssemblyRecipe recipe) {
        int type = recipe.getProgramStack().getMetadata();
        if (type < 0 || type > AssemblyType.values().length) {
            GroovyLog.msg("Error getting the recipe map for PneumaticCraft Assembly Controller")
                    .add("type was {}, which is not one of the expected values of 0, 1, or 2", type)
                    .error()
                    .post();
            return new ArrayList<>();
        }
        return map(AssemblyType.values()[type]);
    }

    private List<AssemblyRecipe> map(AssemblyType type) {
        return switch (type) {
            case DRILL -> AssemblyRecipe.drillRecipes;
            case LASER -> AssemblyRecipe.laserRecipes;
        };
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(recipe -> map(recipe).remove(recipe));
        restoreFromBackup().forEach(recipe -> map(recipe).add(recipe));
    }

    @Override
    @GroovyBlacklist
    public void afterScriptLoad() {
        AssemblyRecipe.drillLaserRecipes.clear();
        AssemblyRecipe.calculateAssemblyChain();
    }

    public void add(AssemblyRecipe recipe) {
        map(recipe).add(recipe);
        addScripted(recipe);
    }

    public boolean remove(AssemblyRecipe recipe) {
        addBackup(recipe);
        return map(recipe).remove(recipe);
    }

    @MethodDescription
    public boolean removeByOutput(AssemblyType type, IIngredient output) {
        return map(type).removeIf(entry -> {
            if (output.test(entry.getOutput())) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('pneumaticcraft:pressure_chamber_valve')"))
    public boolean removeByOutput(IIngredient output) {
        return removeByOutput(AssemblyType.DRILL, output) | removeByOutput(AssemblyType.LASER, output);
    }

    @MethodDescription
    public boolean removeByInput(AssemblyType type, IIngredient input) {
        return map(type).removeIf(entry -> {
            if (input.test(entry.getInput())) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:redstone')"))
    public boolean removeByInput(IIngredient input) {
        return removeByOutput(AssemblyType.DRILL, input) | removeByOutput(AssemblyType.LASER, input);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAllDrill() {
        map(AssemblyType.DRILL).forEach(this::addBackup);
        map(AssemblyType.DRILL).clear();
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAllLaser() {
        map(AssemblyType.LASER).forEach(this::addBackup);
        map(AssemblyType.LASER).clear();
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        removeAllDrill();
        removeAllLaser();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<AssemblyRecipe> streamRecipes() {
        return new SimpleObjectStream<>(Arrays.stream(AssemblyType.values()).map(this::map).flatMap(Collection::stream).collect(Collectors.toList()))
                .setRemover(this::remove);
    }

    public enum AssemblyType {
        DRILL,
        LASER
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<AssemblyRecipe> {

        @Property(valid = @Comp(value = "null", type = Comp.Type.NOT))
        private ItemStack programStack;

        @RecipeBuilderMethodDescription(field = "programStack")
        public RecipeBuilder drill() {
            this.programStack = ItemAssemblyProgram.getStackForProgramType(0, 1);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "programStack")
        public RecipeBuilder laser() {
            this.programStack = ItemAssemblyProgram.getStackForProgramType(1, 1);
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding PneumaticCraft Assembly Controller recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(programStack == null, "programStack cannot be null");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable AssemblyRecipe register() {
            if (!validate()) return null;
            AssemblyRecipe recipe = null;
            for (ItemStack stack : input.get(0).getMatchingStacks()) {
                AssemblyRecipe recipe1 = new AssemblyRecipe(stack, output.get(0), programStack);
                ModSupport.PNEUMATIC_CRAFT.get().assemblyController.add(recipe1);
                if (recipe == null) recipe = recipe1;
            }
            return recipe;
        }
    }
}
