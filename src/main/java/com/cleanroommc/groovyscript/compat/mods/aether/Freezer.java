package com.cleanroommc.groovyscript.compat.mods.aether;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.gildedgames.the_aether.api.freezables.AetherFreezable;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.Nullable;

public class Freezer extends ForgeRegistryWrapper<AetherFreezable> {

    public Freezer() {
        super(GameRegistry.findRegistry(AetherFreezable.class), Alias.generateOfClass(AetherFreezable.class));
    }

    public RecipeBuilder recipeBuilder() { return new RecipeBuilder(); }

    public void add(AetherFreezable freezable) {
        if (freezable != null) {
            ReloadableRegistryManager.addRegistryEntry(this.getRegistry(),freezable);
        }
    }
    public void add(ItemStack input, ItemStack output, int timeRequired) {
        AetherFreezable freezable = new AetherFreezable(input, output, timeRequired);
        add(freezable);
    }
    public boolean remove(AetherFreezable freezable) {
        if (freezable == null) return false;
        ReloadableRegistryManager.removeRegistryEntry(this.getRegistry(), freezable.getRegistryName());
        return true;
    }

    public void removeByOutput(ItemStack output) {
        this.getRegistry().getValuesCollection().forEach(freezable -> {
            if (freezable.getOutput().isItemEqual(output)) {
                ReloadableRegistryManager.removeRegistryEntry(this.getRegistry(),freezable.getRegistryName());
            }
        });
    }



    public static class RecipeBuilder extends AbstractRecipeBuilder<AetherFreezable> {

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
            msg.add(Aether.freezer.getRegistry().getValue(name) != null, "tried to register {}, but it already exists.", name);
        }

        @Override
        public @Nullable AetherFreezable register() {
            if (!validate()) return null;
            AetherFreezable freezable = new AetherFreezable(input.get(0).getMatchingStacks()[0], output.get(0), time);
            Aether.freezer.add(freezable);
            return freezable;
        }
    }

}
