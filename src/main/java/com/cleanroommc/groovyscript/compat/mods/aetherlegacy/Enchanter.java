package com.cleanroommc.groovyscript.compat.mods.aetherlegacy;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.gildedgames.the_aether.api.enchantments.AetherEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class Enchanter extends ForgeRegistryWrapper<AetherEnchantment> {

    public Enchanter() {
        super(GameRegistry.findRegistry(AetherEnchantment.class));
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond')).time(200)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(ItemStack input, ItemStack output, int timeRequired) {
        AetherEnchantment enchantment = new AetherEnchantment(input, output, timeRequired);
        add(enchantment);
    }

    @MethodDescription(example = @Example("item('aether_legacy:enchanted_gravitite')"))
    public void removeByOutput(IIngredient output) {
        this.getRegistry().getValuesCollection().forEach(enchantment -> {
            if (output.test(enchantment.getOutput())) {
                remove(enchantment);
            }
        });
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<AetherEnchantment> {

        @Property(valid = @Comp(type = Comp.Type.GTE, value = "0"))
        private int time;

        @RecipeBuilderMethodDescription
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
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(time < 0, "time must be a non-negative integer, yet it was {}", time);
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable AetherEnchantment register() {
            if (!validate()) return null;

            AetherEnchantment enchantment = new AetherEnchantment(input.get(0).getMatchingStacks()[0], output.get(0), time);
            Aether.enchanter.add(enchantment);
            return enchantment;
        }
    }

}
