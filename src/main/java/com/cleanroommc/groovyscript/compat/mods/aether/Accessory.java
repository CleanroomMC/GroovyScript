package com.cleanroommc.groovyscript.compat.mods.aether;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IReloadableForgeRegistry;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.gildedgames.the_aether.api.accessories.AccessoryType;
import com.gildedgames.the_aether.api.accessories.AetherAccessory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.Nullable;

public class Accessory {

    private IForgeRegistry<AetherAccessory> accessories = GameRegistry.findRegistry(AetherAccessory.class);
    private static final AccessoryType[] VALUES = AccessoryType.values();

    public void add(AetherAccessory accessory) {
        if (accessory != null) {
            ((IReloadableForgeRegistry<AetherAccessory>) accessories).groovyScript$registerEntry(accessory);
        }
    }

    public void add(ItemStack item, String type) {
        AccessoryType accessoryType = AccessoryType.MISC;
        for (AccessoryType value : VALUES) {
            if (value.getDisplayName().equalsIgnoreCase(type)) {
                accessoryType = value;
            }
        }
        AetherAccessory accessory = new AetherAccessory(item, accessoryType);
        add(accessory);
    }

    public RecipeBuilder recipeBuilder() { return new RecipeBuilder(); }

    public static class RecipeBuilder extends AbstractRecipeBuilder<AetherAccessory> {

        private IForgeRegistry<AetherAccessory> accessories = GameRegistry.findRegistry(AetherAccessory.class);

        private AccessoryType accessoryType = AccessoryType.MISC;

        public RecipeBuilder accessoryType(String type) {
            for (AccessoryType value : VALUES) {
                if (value.getDisplayName().equalsIgnoreCase(type)) {
                    accessoryType = value;
                }
            }
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Aether Accessory";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg);
            msg.add(accessories.getValue(name) != null, "tried to register {}, but it already exists.", name);
        }

        @Override
        public @Nullable AetherAccessory register() {
            if (!validate()) return null;

            AetherAccessory accessory = new AetherAccessory(input.get(0).getMatchingStacks()[0], accessoryType);
            ModSupport.AETHER.get().accessory.add(accessory);
            return accessory;
        }
    }



}
