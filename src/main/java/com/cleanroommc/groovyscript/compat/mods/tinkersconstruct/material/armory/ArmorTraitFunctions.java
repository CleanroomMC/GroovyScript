package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.armory;

import c4.conarm.lib.armor.ArmorModifications;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;

public interface ArmorTraitFunctions {
    interface OnArmorTick {
        void handle(ItemStack armor, World world, EntityPlayer player);
    }

    interface GetModifications {
        ArmorModifications handle(EntityPlayer player, ArmorModifications mods, ItemStack armor, DamageSource source, double damage, int slot);
    }

    interface OnItemPickup {
        void handle(ItemStack armor, EntityItem item, EntityItemPickupEvent event);
    }

    interface OnHeal {
        float handle(ItemStack armor, EntityPlayer player, float amount, float newAmount, LivingHealEvent event);
    }

    interface OnHurt {
        float handle(ItemStack armor, EntityPlayer player, DamageSource source, float damage, float newDamage, LivingHurtEvent event);
    }

    interface OnDamaged {
        float handle(ItemStack armor, EntityPlayer player, DamageSource source, float damage, float newDamage, LivingDamageEvent event);
    }

    interface OnKnockback {
        void handle(ItemStack armor, EntityPlayer player, LivingKnockBackEvent event);
    }

    interface OnFalling {
        void handle(ItemStack armor, EntityPlayer player, LivingFallEvent event);
    }

    interface OnJumping {
        void handle(ItemStack armor, EntityPlayer player, LivingEvent.LivingJumpEvent event);
    }

    interface OnAbilityTick {
        void handle(int level, World world, EntityPlayer player);
    }

    interface OnArmorEquipped {
        void handle(ItemStack armor, EntityPlayer player, int slot);
    }

    interface OnArmorRemoved extends OnArmorEquipped {}

    interface OnArmorDamaged {
        int handle(ItemStack armor, DamageSource source, int damage, int newDamage, EntityPlayer player, int slot);
    }

    interface OnArmorHeal extends OnArmorDamaged {}
    interface GetAbilityLevel {
        int handle(ModifierNBT nbt);
    }
}
