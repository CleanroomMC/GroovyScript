package com.cleanroommc.groovyscript.compat.mods.industrialforegoing;

import com.buuz135.industrial.api.recipe.SludgeEntry;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.industrialforegoing.SludgeRefinerBlockAccessor;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;

import java.util.Collection;

@RegistryDescription
public class SludgeRefiner extends StandardListRegistry<SludgeEntry> {

    @Override
    public Collection<SludgeEntry> getRecipes() {
        return SludgeEntry.SLUDGE_RECIPES;
    }

    @Override
    @GroovyBlacklist
    public void afterScriptLoad() {
        // Clear the list and cause the sludge refiner to recompute outputs
        SludgeRefinerBlockAccessor.setOutputs(null);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:gold_ingot'), 5"))
    public SludgeEntry add(ItemStack output, int weight) {
        SludgeEntry recipe = new SludgeEntry(output, weight);
        add(recipe);
        return recipe;
    }

    @MethodDescription(example = @Example("item('minecraft:clay_ball')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(recipe -> {
            if (output.test(recipe.getStack())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

}
