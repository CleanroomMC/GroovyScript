package com.cleanroommc.groovyscript.compat.mods.aether;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.gildedgames.the_aether.api.enchantments.AetherEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.Nullable;

public class Enchanter extends ForgeRegistryWrapper<AetherEnchantment> {


    public Enchanter() {
        super(GameRegistry.findRegistry(AetherEnchantment.class), Alias.generateOfClass(AetherEnchantment.class));
    }

    public RecipeBuilder recipeBuilder() { return new RecipeBuilder(); }

    public void add(AetherEnchantment enchantment) {
        if (enchantment != null) {
            ReloadableRegistryManager.addRegistryEntry(this.getRegistry(), enchantment);
        }
    }

    public void add(ItemStack input, ItemStack output, int timeRequired) {
        AetherEnchantment enchantment = new AetherEnchantment(input,output,timeRequired);
        add(enchantment);
    }

    public boolean remove(AetherEnchantment enchantment) {
        if (enchantment == null) return false;

        ReloadableRegistryManager.removeRegistryEntry(this.getRegistry(), enchantment.getRegistryName());
        return true;
    }

    public void removeByOutput(ItemStack output) {
        this.getRegistry().getValuesCollection().forEach(enchantment -> {
            if (enchantment.getOutput().isItemEqual(output)) {
                ReloadableRegistryManager.removeRegistryEntry(this.getRegistry(),enchantment.getRegistryName());
            }
        });
    }



    public static class RecipeBuilder extends AbstractRecipeBuilder<AetherEnchantment> {


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
            msg.add(Aether.enchanter.getRegistry().getValue(name) != null, "tried to register {}, but it already exists.", name);
        }

        @Override
        public @Nullable AetherEnchantment register() {
            if (!validate()) return null;

            AetherEnchantment enchantment = new AetherEnchantment(input.get(0).getMatchingStacks()[0],output.get(0),time);
            Aether.enchanter.add(enchantment);
            return  enchantment;
        }
    }

}
