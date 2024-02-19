package com.cleanroommc.groovyscript.compat.mods.aether_legacy;

import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
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

    public void add(AetherEnchantmentFuel enchantmentFuel) {
        if (enchantmentFuel != null) {
            ReloadableRegistryManager.addRegistryEntry(this.getRegistry(), enchantmentFuel);
        }
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:blaze_rod'), 1000"))
    public void add(ItemStack fuel, int timeGiven) {
        AetherEnchantmentFuel enchantmentFuel = new AetherEnchantmentFuel(fuel, timeGiven);
        add(enchantmentFuel);
    }

    public boolean remove(AetherEnchantmentFuel enchantmentFuel) {
        if (enchantmentFuel == null) return false;
        ReloadableRegistryManager.removeRegistryEntry(this.getRegistry(), enchantmentFuel.getRegistryName());
        return true;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('aether_legacy:ambrosium_shard')"))
    public void removeByItem(ItemStack fuel) {
        this.getRegistry().getValuesCollection().forEach(enchantmentFuel -> {
            if (enchantmentFuel.getFuelStack().isItemEqual(fuel)) {
                ReloadableRegistryManager.removeRegistryEntry(this.getRegistry(), enchantmentFuel.getRegistryName());
            }
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        this.getRegistry().getValuesCollection().forEach(enchantmentFuel -> {
            ReloadableRegistryManager.removeRegistryEntry(this.getRegistry(), enchantmentFuel.getRegistryName());
        });
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<AetherEnchantmentFuel> streamEntries() {
        return new SimpleObjectStream<>(this.getRegistry().getValuesCollection()).setRemover(this::remove);
    }
}
