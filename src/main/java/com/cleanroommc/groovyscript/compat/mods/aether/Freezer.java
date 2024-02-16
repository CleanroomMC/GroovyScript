package com.cleanroommc.groovyscript.compat.mods.aether;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IReloadableForgeRegistry;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.gildedgames.the_aether.api.freezables.AetherFreezable;
import com.gildedgames.the_aether.api.freezables.AetherFreezableFuel;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.Nullable;

public class Freezer {
    private IForgeRegistry<AetherFreezable> freezables = GameRegistry.findRegistry(AetherFreezable.class);
    private IForgeRegistry<AetherFreezableFuel> freezableFuels = GameRegistry.findRegistry(AetherFreezableFuel.class);

    public RecipeBuilder recipeBuilder() { return new RecipeBuilder(); }

    public void add(AetherFreezable freezable) {
        if (freezable != null) {
            ((IReloadableForgeRegistry<AetherFreezable>) freezables).groovyScript$registerEntry(freezable);
        }
    }
    public void add(ItemStack input, ItemStack output, int timeRequired) {
        AetherFreezable freezable = new AetherFreezable(input, output, timeRequired);
        add(freezable);
    }
    public boolean remove(AetherFreezable freezable) {
        if (freezable == null) return false;
        ReloadableRegistryManager.removeRegistryEntry(freezables, freezable.getRegistryName());
        return true;
    }

    public void removeByOutput(ItemStack output) {
        freezables.getValuesCollection().forEach(freezable -> {
            if (freezable.getOutput().isItemEqual(output)) {
                ReloadableRegistryManager.removeRegistryEntry(freezables,freezable.getRegistryName());
            }
        });
    }

    public void addFuel(AetherFreezableFuel freezableFuel) {
        if (freezableFuel != null) {
            ((IReloadableForgeRegistry<AetherFreezableFuel>) freezableFuels).groovyScript$registerEntry(freezableFuel);
        }
    }

    public void addFuel(ItemStack fuel, int timeGiven) {
        AetherFreezableFuel freezableFuel = new AetherFreezableFuel(fuel, timeGiven);
        addFuel(freezableFuel);
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<AetherFreezable> {

        private IForgeRegistry<AetherFreezable> freezables = GameRegistry.findRegistry(AetherFreezable.class);
        private int time;

        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Aether Freezer Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(time < 0, "time must be a non-negative integer, yet it was {}", time);
            msg.add(freezables.getValue(name) != null, "tried to register {}, but it already exists.", name);
        }

        @Override
        public @Nullable AetherFreezable register() {
            if (!validate()) return null;
            AetherFreezable freezable = new AetherFreezable(input.get(0).getMatchingStacks()[0], output.get(0), time);
            ModSupport.AETHER.get().freezer.add(freezable);
            return freezable;
        }
    }

}
