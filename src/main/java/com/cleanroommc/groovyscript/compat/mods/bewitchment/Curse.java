package com.cleanroommc.groovyscript.compat.mods.bewitchment;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodOverride;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Arrays;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES, override = @MethodOverride(method = @MethodDescription(method = "remove(Lnet/minecraft/util/ResourceLocation;)V", example = @Example("resource('bewitchment:berserker')"))))
public class Curse extends ForgeRegistryWrapper<com.bewitchment.api.registry.Curse> {

    public Curse() {
        super(GameRegistry.findRegistry(com.bewitchment.api.registry.Curse.class));
    }

    @MethodDescription(example = @Example("item('minecraft:blaze_rod')"))
    public void removeByInput(IIngredient input) {
        getRegistry().forEach(recipe -> {
            if (recipe.input != null && recipe.input.stream().map(Ingredient::getMatchingStacks).flatMap(Arrays::stream).anyMatch(input)) {
                remove(recipe);
            }
        });
    }
}
