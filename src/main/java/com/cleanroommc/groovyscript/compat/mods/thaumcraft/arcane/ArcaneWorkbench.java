package com.cleanroommc.groovyscript.compat.mods.thaumcraft.arcane;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ArcaneWorkbench {

    public static final ResourceLocation DEFAULT = new ResourceLocation("");

    public void remove(String name) {
        ReloadableRegistryManager.removeRegistryEntry(ForgeRegistries.RECIPES, name);
    }

    public void removeByOutput(IIngredient output) {
        VanillaModule.crafting.removeByOutput(output, true);
    }

    public ArcaneRecipeBuilder.Shaped shapedBuilder() {
        return new ArcaneRecipeBuilder.Shaped();
    }

    public ArcaneRecipeBuilder.Shapeless shapelessBuilder() {
        return new ArcaneRecipeBuilder.Shapeless();
    }
}
