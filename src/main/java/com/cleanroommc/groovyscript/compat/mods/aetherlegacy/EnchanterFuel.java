package com.cleanroommc.groovyscript.compat.mods.aetherlegacy;

import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.gildedgames.the_aether.api.enchantments.AetherEnchantmentFuel;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

@RegistryDescription
public class EnchanterFuel extends ForgeRegistryWrapper<AetherEnchantmentFuel> {

    public EnchanterFuel() {
        super(GameRegistry.findRegistry(AetherEnchantmentFuel.class), Alias.generateOf("EnchanterFuel"));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:blaze_rod'), 1000"))
    public void add(ItemStack fuel, int timeGiven) {
        AetherEnchantmentFuel enchantmentFuel = new AetherEnchantmentFuel(fuel, timeGiven);
        add(enchantmentFuel);
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('aether_legacy:ambrosium_shard')"))
    public void removeByItem(ItemStack fuel) {
        this.getRegistry().getValuesCollection().forEach(enchantmentFuel -> {
            if (enchantmentFuel.getFuelStack().isItemEqual(fuel)) {
                ReloadableRegistryManager.removeRegistryEntry(this.getRegistry(), enchantmentFuel.getRegistryName());
            }
        });
    }
}
