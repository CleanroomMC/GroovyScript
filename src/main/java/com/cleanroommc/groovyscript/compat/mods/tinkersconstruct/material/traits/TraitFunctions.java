package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.traits;

import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import slimeknights.tconstruct.library.modifiers.IToolMod;

public interface TraitFunctions {
    interface OnUpdate {
        void handle(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected);
    }

    interface MiningSpeed {
        void handle(ItemStack tool, PlayerEvent.BreakSpeed event);
    }

    interface BeforeBlockBreak {
        void handle(ItemStack tool, BlockEvent.BreakEvent event);
    }

    interface AfterBlockBreak {
        void handle(ItemStack tool, World world, IBlockState state, BlockPos pos, EntityLivingBase player, boolean wasEffective);
    }

    interface BlockHarvestDrops {
        void handle(ItemStack tool, BlockEvent.HarvestDropsEvent event);
    }

    interface IsCriticalHit {
        boolean handle(ItemStack tool, EntityLivingBase player, EntityLivingBase target);
    }

    interface Damage {
        float handle(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical);
    }

    interface OnHit {
        void handle(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, boolean isCritical);
    }

    interface AfterHit {
        void handle(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit);
    }

    interface KnockBack {
        float handle(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float knockback, float newKnockback, boolean isCritical);
    }

    interface OnBlock {
        void handle(ItemStack tool, EntityLivingBase player, LivingHurtEvent event);
    }

    interface OnToolDamage {
        int handle(ItemStack tool, int damage, int newDamage, EntityLivingBase entity);
    }

    interface OnToolHeal {
        int handle(ItemStack tool, int amount, int newAmount, EntityLivingBase entity);
    }

    interface OnRepair {
        void handle(ItemStack tool, int amount);
    }

    interface OnPlayerHurt {
        void handle(ItemStack tool, EntityPlayer player, EntityLivingBase attacker, LivingHurtEvent event);
    }

    interface CanApplyTogether {
        boolean handle(IToolMod mod);
    }

    interface CanApplyTogetherEnchantment {
        boolean handle(Enchantment enchantment);
    }

    interface ExtraInfo {
        String[] handle(ItemStack tool, NBTTagCompound modifierTag);
    }
}
