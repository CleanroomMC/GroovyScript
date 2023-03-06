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
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.traits.ITrait;

import java.util.Arrays;
import java.util.List;

public class GroovyTrait extends ModifierTrait implements ITrait {

    public TraitFunctions.OnUpdate onUpdate;
    public TraitFunctions.MiningSpeed miningSpeed;
    public TraitFunctions.BeforeBlockBreak beforeBlockBreak;
    public TraitFunctions.AfterBlockBreak afterBlockBreak;
    public TraitFunctions.BlockHarvestDrops blockHarvestDrops;
    public TraitFunctions.IsCriticalHit isCriticalHit;
    public TraitFunctions.Damage damage;
    public TraitFunctions.OnHit onHit;
    public TraitFunctions.AfterHit afterHit;
    public TraitFunctions.KnockBack knockBack;
    public TraitFunctions.OnBlock onBlock;
    public TraitFunctions.OnToolDamage onToolDamage;
    public TraitFunctions.OnToolHeal onToolHeal;
    public TraitFunctions.OnRepair onRepair;
    public TraitFunctions.OnPlayerHurt onPlayerHurt;
    public TraitFunctions.CanApplyTogether canApplyTogether;
    public TraitFunctions.CanApplyTogetherEnchantment canApplyTogetherEnchantment;
    public TraitFunctions.ExtraInfo extraInfo;
    public boolean hidden;
    public String displayName;
    public String description;

    public GroovyTrait(String identifier, int color, int maxLevel, int countPerLevel) {
        super(identifier, color, maxLevel, countPerLevel);
    }

    public GroovyTrait(String identifier, int color) {
        this(identifier, color, 0, 0);
    }

    @Override
    public String getLocalizedName() {
        return displayName != null ? displayName : super.getLocalizedName();
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public String getLocalizedDesc() {
        return description != null ? description : super.getLocalizedDesc();
    }

    @Override
    public void onPlayerHurt(ItemStack tool, EntityPlayer player, EntityLivingBase attacker, LivingHurtEvent event) {
        if (onPlayerHurt != null) onPlayerHurt.handle(tool, player, attacker, event);
    }

    @Override
    public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (onUpdate != null) onUpdate.handle(tool, world, entity, itemSlot, isSelected);
    }

    @Override
    public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
        if (miningSpeed != null) miningSpeed.handle(tool, event);
    }

    @Override
    public void beforeBlockBreak(ItemStack tool, BlockEvent.BreakEvent event) {
        if (beforeBlockBreak != null) beforeBlockBreak.handle(tool, event);
    }

    @Override
    public void afterBlockBreak(ItemStack tool, World world, IBlockState state, BlockPos pos, EntityLivingBase player, boolean wasEffective) {
        if (afterBlockBreak != null) afterBlockBreak.handle(tool, world, state, pos, player, wasEffective);
    }

    @Override
    public void blockHarvestDrops(ItemStack tool, BlockEvent.HarvestDropsEvent event) {
        if (blockHarvestDrops != null) blockHarvestDrops.handle(tool, event);
    }

    @Override
    public boolean isCriticalHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target) {
        return isCriticalHit != null ? isCriticalHit.handle(tool, player, target) : super.isCriticalHit(tool, player, target);
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
        return this.damage != null ? this.damage.handle(tool, player, target, damage, newDamage, isCritical) : super.damage(tool, player, target, damage, newDamage, isCritical);
    }

    @Override
    public void onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, boolean isCritical) {
        if (onHit != null) onHit.handle(tool, player, target, damage, isCritical);
    }

    @Override
    public float knockBack(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float knockback, float newKnockback, boolean isCritical) {
        return this.knockBack != null ? this.knockBack.handle(tool, player, target, damage, knockback, newKnockback, isCritical): super.knockBack(tool, player, target, damage, knockback, newKnockback, isCritical);
    }

    @Override
    public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
        if (afterHit != null) afterHit.handle(tool, player, target, damageDealt, wasCritical, wasHit);
    }

    @Override
    public void onBlock(ItemStack tool, EntityPlayer player, LivingHurtEvent event) {
        if (onBlock != null) onBlock.handle(tool, player, event);
    }

    @Override
    public int onToolDamage(ItemStack tool, int damage, int newDamage, EntityLivingBase entity) {
        return onToolDamage != null ? onToolDamage.handle(tool, damage, newDamage, entity) : super.onToolDamage(tool, damage, newDamage, entity);
    }

    @Override
    public int onToolHeal(ItemStack tool, int amount, int newAmount, EntityLivingBase entity) {
        return onToolHeal != null ? onToolHeal.handle(tool, amount, newAmount, entity) : super.onToolHeal(tool, amount, newAmount, entity);
    }

    @Override
    public void onRepair(ItemStack tool, int amount) {
        if (onRepair != null) onRepair.handle(tool, amount);
    }

    @Override
    public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
        return extraInfo != null ? Arrays.asList(extraInfo.handle(tool, modifierTag)) : super.getExtraInfo(tool, modifierTag);
    }

    @Override
    public boolean canApplyTogether(IToolMod iToolMod) {
        return canApplyTogether != null ? canApplyTogether.handle(iToolMod) : super.canApplyTogether(iToolMod);
    }

    @Override
    public boolean canApplyTogether(Enchantment enchantment) {
        return canApplyTogetherEnchantment != null ? canApplyTogetherEnchantment.handle(enchantment) : super.canApplyTogether(enchantment);
    }
}
