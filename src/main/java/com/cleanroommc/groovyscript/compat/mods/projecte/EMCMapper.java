package com.cleanroommc.groovyscript.compat.mods.projecte;

import com.cleanroommc.groovyscript.compat.vanilla.ShapedCraftingRecipe;
import com.cleanroommc.groovyscript.compat.vanilla.ShapelessCraftingRecipe;
import moze_intel.projecte.emc.mappers.CraftingMapper;
import net.minecraft.item.crafting.IRecipe;

public class EMCMapper implements CraftingMapper.IRecipeMapper {

    @Override
    public String getName() {
        return "GroovyScriptEMCMapper";
    }

    @Override
    public String getDescription() {
        return "Supporting GroovyScript class 'ShapedCraftingRecipe' and 'ShapelessCraftingRecipe'";
    }

    @Override
    public boolean canHandle(IRecipe iRecipe) {
        return iRecipe instanceof ShapedCraftingRecipe || iRecipe instanceof ShapelessCraftingRecipe;
    }
}
