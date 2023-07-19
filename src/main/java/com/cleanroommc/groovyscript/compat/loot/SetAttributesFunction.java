package com.cleanroommc.groovyscript.compat.loot;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class SetAttributesFunction extends LootFunction {

    private final SetAttributesFunction.Modifier[] modifiers;

    public SetAttributesFunction(LootCondition[] conditionsIn, SetAttributesFunction.Modifier[] modifiersIn) {
        super(conditionsIn);
        this.modifiers = modifiersIn;
    }

    public @NotNull ItemStack apply(@NotNull ItemStack stack, @NotNull Random rand, @NotNull LootContext context) {
        for (SetAttributesFunction.Modifier modifier : this.modifiers) {
            EntityEquipmentSlot entityequipmentslot = (modifier.slots.length == 0) ? null : modifier.slots[rand.nextInt(modifier.slots.length)];
            stack.addAttributeModifier(modifier.attributeName, new AttributeModifier(modifier.modifierName, modifier.amount.generateFloat(rand), modifier.operation), entityequipmentslot);
        }

        return stack;
    }

    public static class Modifier {

        public final String modifierName;
        public final String attributeName;
        public final int operation;
        public final RandomValueRange amount;
        public final EntityEquipmentSlot[] slots;

        public Modifier(String attrName, String modifName, int operationIn, RandomValueRange randomAmount, EntityEquipmentSlot[] slotsIn) {
            this.modifierName = modifName;
            this.attributeName = attrName;
            this.operation = operationIn;
            this.amount = randomAmount;
            this.slots = slotsIn;
        }
    }

}
