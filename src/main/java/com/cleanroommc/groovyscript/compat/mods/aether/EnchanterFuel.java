package com.cleanroommc.groovyscript.compat.mods.aether;

import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.gildedgames.the_aether.api.enchantments.AetherEnchantmentFuel;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class EnchanterFuel extends ForgeRegistryWrapper<AetherEnchantmentFuel> {

    public EnchanterFuel() {
        super(GameRegistry.findRegistry(AetherEnchantmentFuel.class), Alias.generateOfClass(AetherEnchantmentFuel.class));
    }

    public void add(AetherEnchantmentFuel enchantmentFuel) {
        if (enchantmentFuel != null) {
            ReloadableRegistryManager.addRegistryEntry(this.getRegistry(), enchantmentFuel);
        }
    }

    public void add(ItemStack fuel, int timeGiven) {
        AetherEnchantmentFuel enchantmentFuel = new AetherEnchantmentFuel(fuel, timeGiven);
        add(enchantmentFuel);
    }
}
