package com.cleanroommc.groovyscript.compat.mods.aetherlegacy;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.gildedgames.the_aether.api.freezables.AetherFreezableFuel;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

@RegistryDescription
public class FreezerFuel extends ForgeRegistryWrapper<AetherFreezableFuel> {

    public FreezerFuel() {
        super(GameRegistry.findRegistry(AetherFreezableFuel.class));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:packed_ice'), 1000"))
    public void add(ItemStack fuel, int timeGiven) {
        if (IngredientHelper.overMaxSize(fuel, 1)) {
            GroovyLog.msg("Error adding Freezer Fuel").error()
                     .add("Fuel must have stack size of 1, got {}", fuel.getCount())
                     .post();
            return;
        }
        AetherFreezableFuel freezableFuel = new AetherFreezableFuel(fuel, timeGiven);
        add(freezableFuel);
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('aether_legacy:icestone')"))
    public void removeByItem(IIngredient fuel) {
        this.getRegistry().getValuesCollection().forEach(freezableFuel -> {
            if (fuel.test(freezableFuel.getFuelStack())) {
                remove(freezableFuel);
            }
        });
    }
}
