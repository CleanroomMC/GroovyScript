package com.cleanroommc.groovyscript.compat.mods.aether_legacy;

import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.gildedgames.the_aether.api.freezables.AetherFreezableFuel;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

@RegistryDescription
public class FreezerFuel extends ForgeRegistryWrapper<AetherFreezableFuel> {

    public FreezerFuel() {
        super(GameRegistry.findRegistry(AetherFreezableFuel.class), Alias.generateOf("freezerFuel"));
    }

    public void add(AetherFreezableFuel freezableFuel) {
        if (freezableFuel != null) {
            ReloadableRegistryManager.addRegistryEntry(this.getRegistry(), freezableFuel);
        }
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:packed_ice'), 1000"))
    public void add(ItemStack fuel, int timeGiven) {
        AetherFreezableFuel freezableFuel = new AetherFreezableFuel(fuel, timeGiven);
        add(freezableFuel);
    }

    public boolean remove(AetherFreezableFuel freezableFuel) {
        if (freezableFuel == null) return false;
        ReloadableRegistryManager.removeRegistryEntry(this.getRegistry(), freezableFuel.getRegistryName());
        return true;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('aether_legacy:icestone')"))
    public void removeByItem(ItemStack fuel) {
        this.getRegistry().getValuesCollection().forEach(freezableFuel -> {
            if (freezableFuel.getFuelStack().isItemEqual(fuel)) {
                ReloadableRegistryManager.removeRegistryEntry(this.getRegistry(), freezableFuel.getRegistryName());
            }
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        this.getRegistry().getValuesCollection().forEach(freezableFuel -> {
            ReloadableRegistryManager.removeRegistryEntry(this.getRegistry(), freezableFuel.getRegistryName());
        });
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<AetherFreezableFuel> streamEntries() {
        return new SimpleObjectStream<>(this.getRegistry().getValuesCollection()).setRemover(this::remove);
    }
}
