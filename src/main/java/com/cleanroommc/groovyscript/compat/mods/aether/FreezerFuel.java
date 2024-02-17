package com.cleanroommc.groovyscript.compat.mods.aether;

import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.gildedgames.the_aether.api.freezables.AetherFreezableFuel;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

@RegistryDescription
public class FreezerFuel extends ForgeRegistryWrapper<AetherFreezableFuel> {

    public FreezerFuel() {
        super(GameRegistry.findRegistry(AetherFreezableFuel.class), Alias.generateOfClass(AetherFreezableFuel.class));
    }

    public void add(AetherFreezableFuel freezableFuel) {
        if (freezableFuel != null) {
            ReloadableRegistryManager.addRegistryEntry(this.getRegistry(), freezableFuel);
        }
    }

    @MethodDescription(example = @Example("item('minecraft:packed_ice'), 1000"))
    public void add(ItemStack fuel, int timeGiven) {
        AetherFreezableFuel freezableFuel = new AetherFreezableFuel(fuel, timeGiven);
        add(freezableFuel);
    }
}
