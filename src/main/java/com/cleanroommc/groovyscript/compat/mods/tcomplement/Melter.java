package com.cleanroommc.groovyscript.compat.mods.tcomplement;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.tcomplement.recipe.IngredientBlacklist;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.recipe.MeltingRecipeBuilder;
import com.cleanroommc.groovyscript.core.mixin.tcomplement.TCompRegistryAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.AbstractReloadableStorage;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import knightminer.tcomplement.library.IBlacklist;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

import java.util.Collection;

public class Melter extends StandardListRegistry<MeltingRecipe> {

    @GroovyBlacklist
    protected final AbstractReloadableStorage<IBlacklist> backupStorage = new AbstractReloadableStorage<>();

    public MeltingRecipeBuilder recipeBuilder() {
        return new MeltingRecipeBuilder(this, "Tinkers Complement Melter override");
    }

    @Override
    public Collection<MeltingRecipe> getRecipes() {
        return TCompRegistryAccessor.getMeltingOverrides();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        super.onReload();
        backupStorage.removeScripted().forEach(TCompRegistryAccessor.getMeltingBlacklist()::remove);
        backupStorage.restoreFromBackup().forEach(TCompRegistryAccessor.getMeltingBlacklist()::add);
    }

    public IBlacklist addBlacklist(IIngredient ingredient) {
        IngredientBlacklist blacklist = new IngredientBlacklist(ingredient);
        addBlacklist(blacklist);
        return blacklist;
    }

    public MeltingRecipe add(IIngredient input, FluidStack output, int temp) {
        MeltingRecipe recipe = new MeltingRecipe(MeltingRecipeBuilder.recipeMatchFromIngredient(input, output.amount), output, temp);
        add(recipe);
        return recipe;
    }

    public void addBlacklist(IBlacklist blacklist) {
        if (blacklist == null) return;
        backupStorage.addScripted(blacklist);
        TCompRegistryAccessor.getMeltingBlacklist().add(blacklist);
    }

    public boolean removeBlacklist(IBlacklist blacklist) {
        if (blacklist == null) return false;
        backupStorage.addBackup(blacklist);
        TCompRegistryAccessor.getMeltingBlacklist().remove(blacklist);
        return true;
    }

    public boolean removeFromBlacklist(ItemStack item) {
        if (TCompRegistryAccessor.getMeltingBlacklist().removeIf(b -> {
            boolean found = b.matches(item);
            if (found) backupStorage.addBackup(b);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Tinkers Complement Melter blacklist")
                .add("could not find a blacklist for item {}", item)
                .error()
                .post();
        return false;
    }

    public boolean removeByInput(IIngredient input) {
        NonNullList<ItemStack> list = NonNullList.from(ItemStack.EMPTY, input.getMatchingStacks());
        if (getRecipes().removeIf(recipe -> {
            boolean found = recipe.input.matches(list).isPresent();
            list.clear();
            if (found) addBackup(recipe);
            return found;
        })) return true;

        list.clear();
        GroovyLog.msg("Error removing Tinkers Complement Melter override")
                .add("could not find a override for input {}", input)
                .error()
                .post();
        return false;
    }

    public boolean removeByOutput(FluidStack stack) {
        if (getRecipes().removeIf(recipe -> {
            boolean found = recipe.output.isFluidEqual(stack);
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Tinkers Complement Melter override")
                .add("could not find a override for output {}", stack)
                .error()
                .post();
        return false;
    }

    public boolean removeByInputAndOutput(IIngredient input, FluidStack output) {
        NonNullList<ItemStack> list = NonNullList.from(ItemStack.EMPTY, input.getMatchingStacks());
        if (getRecipes().removeIf(recipe -> {
            boolean found = recipe.output.isFluidEqual(output) && recipe.input.matches(list).isPresent();
            list.clear();
            if (found) addBackup(recipe);
            return found;
        })) return true;

        list.clear();
        GroovyLog.msg("Error removing Tinkers Complement Melter override")
                .add("could not find a override for input {} and output {}", input, output)
                .error()
                .post();
        return false;
    }

    public void removeAllBlacklists() {
        TCompRegistryAccessor.getMeltingBlacklist().forEach(backupStorage::addBackup);
        TCompRegistryAccessor.getMeltingBlacklist().forEach(TCompRegistryAccessor.getMeltingBlacklist()::remove);
    }

    public SimpleObjectStream<IBlacklist> streamBlacklists() {
        return new SimpleObjectStream<>(TCompRegistryAccessor.getMeltingBlacklist()).setRemover(this::removeBlacklist);
    }
}
