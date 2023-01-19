package com.cleanroommc.groovyscript.event;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.vanilla.CraftingInfo;
import com.cleanroommc.groovyscript.compat.vanilla.ICraftingRecipe;
import com.cleanroommc.groovyscript.compat.vanilla.Player;
import com.cleanroommc.groovyscript.core.mixin.InventoryCraftingAccess;
import com.cleanroommc.groovyscript.core.mixin.SlotCraftingAccess;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import groovy.lang.Closure;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.items.ItemHandlerHelper;

@Mod.EventBusSubscriber(modid = GroovyScript.ID)
public class EventHandler {

    @SubscribeEvent
    public static void playerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        NBTTagCompound tag = event.player.getEntityData();
        NBTTagCompound data;
        if (!tag.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
            data = new NBTTagCompound();
        } else {
            data = tag.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        }
        if (!data.getBoolean(Player.givenItems)) {
            Player.itemMap.forEach((stack, slot) -> {
                if (slot <= -1) {
                    ItemHandlerHelper.giveItemToPlayer(event.player, stack.copy());
                } else {
                    if (event.player.inventory.getStackInSlot(slot) != ItemStack.EMPTY) {
                        ItemHandlerHelper.giveItemToPlayer(event.player, stack.copy());
                    } else {
                        event.player.inventory.setInventorySlotContents(slot, stack.copy());
                    }
                }

            });
            data.setBoolean(Player.givenItems, true);
            tag.setTag(EntityPlayer.PERSISTED_NBT_TAG, data);
        }
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (event.craftMatrix instanceof InventoryCrafting) {
            InventoryCrafting inventoryCrafting = (InventoryCrafting) event.craftMatrix;
            InventoryCraftResult craftResult = null;
            EntityPlayer player = null;
            Container container = ((InventoryCraftingAccess) inventoryCrafting).getEventHandler();
            if (container != null) {
                for (Slot slot : container.inventorySlots) {
                    if (slot instanceof SlotCrafting) {
                        craftResult = (InventoryCraftResult) slot.inventory;
                        player = ((SlotCraftingAccess) slot).getPlayer();
                        break;
                    }
                }
            }
            if (craftResult != null) {
                IRecipe recipe = craftResult.getRecipeUsed();
                if (recipe instanceof ICraftingRecipe) {
                    Closure<Void> recipeAction = ((ICraftingRecipe) recipe).getRecipeAction();
                    if (recipeAction != null) {
                        GroovyLog.get().infoMC("Fire Recipe Action");
                        ClosureHelper.call(recipeAction, event.crafting, new CraftingInfo(inventoryCrafting, player));
                    }
                }
            }
        }
    }

}
