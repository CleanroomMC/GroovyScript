package com.cleanroommc.groovyscript.compat.mods.industrialforegoing;

import com.buuz135.industrial.api.recipe.BioReactorEntry;
import com.buuz135.industrial.api.recipe.IReactorEntry;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import com.google.common.base.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class BioReactor extends StandardListRegistry<IReactorEntry> {

    @Override
    public Collection<IReactorEntry> getRecipes() {
        return BioReactorEntry.BIO_REACTOR_ENTRIES;
    }

    @MethodDescription(description = "groovyscript.wiki.industrialforegoing.bio_reactor.add0", type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:clay')"))
    public IReactorEntry add(ItemStack input) {
        return add(input, null);
    }

    @MethodDescription(description = "groovyscript.wiki.industrialforegoing.bio_reactor.add1", type = MethodDescription.Type.ADDITION)
    public IReactorEntry add(ItemStack input, @Nullable Predicate<NBTTagCompound> nbtCheck) {
        if (IngredientHelper.overMaxSize(input, 1)) {
            GroovyLog.msg("Error adding Bioreactor recipe")
                    .error()
                    .add("Input stack size must be 1")
                    .post();
            return null;
        }
        IReactorEntry recipe = new BioReactorEntry(input, nbtCheck);
        add(recipe);
        return recipe;
    }

    @MethodDescription(example = @Example("item('minecraft:wheat_seeds')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(recipe -> {
            if (input.test(recipe.getStack())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

}
