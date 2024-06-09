package com.cleanroommc.groovyscript.compat.mods.techreborn;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;
import reborncore.api.praescriptum.recipes.Recipe;
import reborncore.api.praescriptum.recipes.RecipeHandler;

import java.util.Arrays;

public abstract class AbstractPraescriptumRegistry extends VirtualizedRegistry<Recipe> {

    abstract RecipeHandler handler();

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        handler().getRecipes().removeAll(removeScripted());
        handler().getRecipes().addAll(restoreFromBackup());
    }

    public void add(Recipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            recipe.register();
        }
    }

    public boolean remove(Recipe recipe) {
        if (handler().getRecipes().removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription
    public void removeByInput(IIngredient input) {
        handler().getRecipes().removeIf(recipe -> {
            if (recipe.getInputIngredients().stream().map(x -> x.ingredient).anyMatch(x -> {
                if (x instanceof ItemStack itemStack) return input.test(itemStack);
                if (x instanceof FluidStack fluidStack) return input.test(fluidStack);
                if (x instanceof String s) return Arrays.stream(new OreDictIngredient(s).getMatchingStacks()).anyMatch(input);
                return false;
            })) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription
    public void removeByOutput(IIngredient output) {
        handler().getRecipes().removeIf(recipe -> {
            if (Arrays.stream(recipe.getItemOutputs()).anyMatch(output) || Arrays.stream(recipe.getFluidOutputs()).anyMatch(output::test)) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Recipe> streamRecipes() {
        return new SimpleObjectStream<>(handler().getRecipes()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        handler().getRecipes().forEach(this::addBackup);
        handler().getRecipes().clear();
    }

}
