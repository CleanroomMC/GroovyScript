package com.cleanroommc.groovyscript.compat.mods.industrialforegoing;

import com.buuz135.industrial.api.recipe.IReactorEntry;
import com.buuz135.industrial.api.recipe.ProteinReactorEntry;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import com.google.common.base.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class ProteinReactor extends StandardListRegistry<IReactorEntry> {

    @Override
    public Collection<IReactorEntry> getRecipes() {
        return ProteinReactorEntry.PROTEIN_REACTOR_ENTRIES;
    }

    @MethodDescription(description = "groovyscript.wiki.industrialforegoing.protein_reactor.add0", type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:clay')"))
    public IReactorEntry add(ItemStack input) {
        return add(input, null);
    }

    @MethodDescription(description = "groovyscript.wiki.industrialforegoing.protein_reactor.add1", type = MethodDescription.Type.ADDITION)
    public IReactorEntry add(ItemStack input, @Nullable Predicate<NBTTagCompound> nbtCheck) {
        IReactorEntry recipe = new ProteinReactorEntry(input, nbtCheck);
        add(recipe);
        return recipe;
    }

    @MethodDescription(example = @Example("item('minecraft:porkchop')"))
    public boolean removeByInput(IIngredient input) {
        return ProteinReactorEntry.PROTEIN_REACTOR_ENTRIES.removeIf(recipe -> {
            if (input.test(recipe.getStack())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

}
