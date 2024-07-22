package com.cleanroommc.groovyscript.compat.mods.aetherlegacy;

import com.cleanroommc.groovyscript.GroovyScriptConfig;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.gildedgames.the_aether.api.enchantments.AetherEnchantmentFuel;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

@RegistryDescription
public class EnchanterFuel extends ForgeRegistryWrapper<AetherEnchantmentFuel> {

    public EnchanterFuel() {
        super(GameRegistry.findRegistry(AetherEnchantmentFuel.class));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:blaze_rod'), 1000"))
    public void add(ItemStack fuel, int timeGiven) {
        // Ignores stack size
        if (GroovyScriptConfig.compat.checkInputStackCounts && fuel.getCount() > 1) {
            GroovyLog.Msg msg = GroovyLog.msg("Error adding Enchanter Fuel").error();
            msg.add("Expected input stack size of 1, got {}", fuel.getCount());
            msg.post();
            return;
        }
        AetherEnchantmentFuel enchantmentFuel = new AetherEnchantmentFuel(fuel, timeGiven);
        add(enchantmentFuel);
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('aether_legacy:ambrosium_shard')"))
    public void removeByItem(IIngredient fuel) {
        this.getRegistry().getValuesCollection().forEach(enchantmentFuel -> {
            if (fuel.test(enchantmentFuel.getFuelStack())) {
                remove(enchantmentFuel);
            }
        });
    }
}
