package com.cleanroommc.groovyscript.compat.mods.aether;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IReloadableForgeRegistry;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.gildedgames.the_aether.api.enchantments.AetherEnchantment;
import com.gildedgames.the_aether.api.enchantments.AetherEnchantmentFuel;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.Nullable;

public class Enchanter {

    private IForgeRegistry<AetherEnchantment> enchantments = GameRegistry.findRegistry(AetherEnchantment.class);
    private IForgeRegistry<AetherEnchantmentFuel> enchantmentFuels = GameRegistry.findRegistry(AetherEnchantmentFuel.class);

    public RecipeBuilder recipeBuilder() { return new RecipeBuilder(); }

    public void add(AetherEnchantment enchantment) {
        if (enchantment != null) {
            ((IReloadableForgeRegistry<AetherEnchantment>) enchantments).groovyScript$registerEntry(enchantment);
        }
    }

    public void add(ItemStack input, ItemStack output, int timeRequired) {
        AetherEnchantment enchantment = new AetherEnchantment(input,output,timeRequired);
        add(enchantment);
    }

    public boolean remove(AetherEnchantment enchantment) {
        if (enchantment == null) return false;

        ReloadableRegistryManager.removeRegistryEntry(enchantments, enchantment.getRegistryName());
        return true;
    }

    public void removeByOutput(ItemStack output) {
        enchantments.getValuesCollection().forEach(enchantment -> {
            if (enchantment.getOutput().isItemEqual(output)) {
                ReloadableRegistryManager.removeRegistryEntry(enchantments,enchantment.getRegistryName());
            }
        });
    }

    public void addFuel(AetherEnchantmentFuel enchantmentFuel) {
        if (enchantmentFuel != null) {
            ((IReloadableForgeRegistry<AetherEnchantmentFuel>) enchantmentFuels).groovyScript$registerEntry(enchantmentFuel);
        }
    }

    public void addFuel(ItemStack fuel, int timeGiven) {
        AetherEnchantmentFuel enchantmentFuel = new AetherEnchantmentFuel(fuel, timeGiven);
        addFuel(enchantmentFuel);
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<AetherEnchantment> {

        private IForgeRegistry<AetherEnchantment> enchantments = GameRegistry.findRegistry(AetherEnchantment.class);

        private int time;

        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Aether Enchanter Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1,1,1,1);
            validateFluids(msg);
            msg.add(time < 0, "time must be a non-negative integer, yet it was {}", time);
            msg.add(enchantments.getValue(name) != null, "tried to register {}, but it already exists.", name);
        }

        @Override
        public @Nullable AetherEnchantment register() {
            if (!validate()) return null;

            AetherEnchantment enchantment = new AetherEnchantment(input.get(0).getMatchingStacks()[0],output.get(0),time);
            ModSupport.AETHER.get().enchanter.add(enchantment);
            return  enchantment;
        }
    }

}
