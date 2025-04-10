package com.cleanroommc.groovyscript.event;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.command.CustomClickAction;
import com.cleanroommc.groovyscript.compat.WarningScreen;
import com.cleanroommc.groovyscript.compat.content.GroovyBlock;
import com.cleanroommc.groovyscript.compat.content.GroovyFluid;
import com.cleanroommc.groovyscript.compat.content.GroovyItem;
import com.cleanroommc.groovyscript.compat.vanilla.CraftingInfo;
import com.cleanroommc.groovyscript.compat.vanilla.ICraftingRecipe;
import com.cleanroommc.groovyscript.compat.vanilla.Player;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.core.mixin.InventoryCraftingAccess;
import com.cleanroommc.groovyscript.core.mixin.SlotCraftingAccess;
import com.cleanroommc.groovyscript.network.NetworkHandler;
import com.cleanroommc.groovyscript.network.SReloadScripts;
import com.cleanroommc.groovyscript.packmode.Packmode;
import com.cleanroommc.groovyscript.packmode.PackmodeSaveData;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import groovy.lang.Closure;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.*;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EventHandler {

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        GroovyItem.initItems(event.getRegistry());
        GroovyBlock.initItems(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        GroovyBlock.initBlocks(event.getRegistry());
        GroovyFluid.initBlocks(event.getRegistry());
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerModels(ModelRegistryEvent event) {
        GroovyItem.registerModels();
        GroovyBlock.registerModels();
        GroovyFluid.registerModels();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerTextures(TextureStitchEvent.Post event) {
        GroovyFluid.initTextures(event.getMap());
    }

    @SubscribeEvent
    public static void createSpawnPosition(WorldEvent.CreateSpawnPosition event) {
        // only want to execute this for the overworld
        var target = DimensionManager.getWorld(0);
        if (event.getWorld() == target) VanillaModule.gameRule.applyDefaultGameRules(event.getWorld().getGameRules());
    }

    @SubscribeEvent
    public static void playerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        // clear all errors and post
        if (!event.player.world.isRemote) {
            GroovyScript.postScriptRunResult(event.player, true, true, false, 0);
        }

        // check world packmode and change if needed
        if (!event.player.world.isRemote && Packmode.needsPackmode()) {
            PackmodeSaveData saveData = PackmodeSaveData.get(event.player.world);
            if (Packmode.hasPackmode() && saveData.isDedicatedServer()) {
                // if the world is a dedicated server or a lan server the packmode should be synced with each player
                NetworkHandler.sendToPlayer(new SReloadScripts(saveData.getPackmode(), true, true), (EntityPlayerMP) event.player);
            } else if (!Packmode.getPackmode().equals(saveData.getPackmode()) && !saveData.isDedicatedServer()) {
                // otherwise we are on a single player world, and we can just set and reload the packmode
                SReloadScripts.updatePackmode(event.player, saveData.getPackmode());
                NetworkHandler.sendToPlayer(new SReloadScripts(null, true, true), (EntityPlayerMP) event.player);
            }
        }

        // give starter items to player
        NBTTagCompound tag = event.player.getEntityData();
        NBTTagCompound data = new NBTTagCompound();
        if (tag.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
            data = tag.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        }
        if (VanillaModule.player.isTestingStartingItems() || !data.getBoolean(Player.GIVEN_ITEMS)) {
            VanillaModule.player.addToInventory(event.player.inventory);
            data.setBoolean(Player.GIVEN_ITEMS, true);
            tag.setTag(EntityPlayer.PERSISTED_NBT_TAG, data);
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onClientChatEvent(ClientChatEvent event) {
        if (event.getOriginalMessage().startsWith(CustomClickAction.PREFIX) && CustomClickAction.runActionHook(event.getOriginalMessage().substring(CustomClickAction.PREFIX.length()))) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (event.craftMatrix instanceof InventoryCrafting inventoryCrafting) {
            InventoryCraftResult craftResult = null;
            EntityPlayer player = null;
            Container container = ((InventoryCraftingAccess) inventoryCrafting).getEventHandler();
            if (container != null) {
                for (Slot slot : container.inventorySlots) {
                    if (slot instanceof SlotCrafting && slot.inventory instanceof InventoryCraftResult result) {
                        craftResult = result;
                        player = ((SlotCraftingAccess) slot).getPlayer();
                        break;
                    }
                }
            }
            if (craftResult != null) {
                IRecipe recipe = craftResult.getRecipeUsed();
                if (recipe instanceof ICraftingRecipe iCraftingRecipe) {
                    Closure<Void> recipeAction = iCraftingRecipe.getRecipeAction();
                    if (recipeAction != null) {
                        GroovyLog.get().infoMC("Fire Recipe Action");
                        ClosureHelper.call(recipeAction, event.crafting, new CraftingInfo(inventoryCrafting, player));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onExplosion(ExplosionEvent.Detonate event) {
        for (Entity entity : event.getAffectedEntities()) {
            if (entity instanceof EntityItem entityItem) {
                VanillaModule.inWorldCrafting.explosion.findAndRunRecipe(entityItem);
            }
        }
    }

    @SubscribeEvent
    public static void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(GroovyScript.ID)) {
            ConfigManager.sync(GroovyScript.ID, Config.Type.INSTANCE);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    @SideOnly(Side.CLIENT)
    public static void onGuiOpen(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiMainMenu && !WarningScreen.wasOpened) {
            WarningScreen.wasOpened = true;
            List<String> warnings = new ArrayList<>();
            if (!FMLLaunchHandler.isDeobfuscatedEnvironment()) {
                if (!Loader.isModLoaded("universaltweaks")) {
                    warnings.add("UniversalTweaks is not loaded! It fixes a recipe book bug by removing it.\n" + "Consider adding UniversalTweaks to your mods and make sure to enable recipe book removal in the config");
                } else if (isUTRecipeBookEnabled()) {
                    warnings.add("UniversalTweaks is loaded, but the recipe book is still enabled. This will cause issue with Groovyscript!\n" + "Please set 'Remove Recipe Book' to true in the misc category!");
                }
                if (Loader.isModLoaded("inworldcrafting")) {
                    warnings.add("InWorldCrafting mod was detected. InWorldCrafting is obsolete since GroovyScript implements its functionality on its own.\n" + "Consider using GroovyScript and removing InWorldCrafting.");
                }
            }
            if ((GroovyScript.getRunConfig().getPackmodeConfigState() & 1) != 0) {
                warnings.add("Integration with the packmode mod is enabled, but the packmode mod is not installed.\n" + "Please disable integration or install the mod.");
            }
            if ((GroovyScript.getRunConfig().getPackmodeConfigState() & 2) != 0) {
                warnings.add("Integration with the packmode mod is enabled, but packmodes are also configured in GroovyScript.\n" + "You should use the packmode mod to configure packmodes if integration is enabled.");
            }
            if (!warnings.isEmpty()) {
                event.setGui(new WarningScreen(warnings));
            }
        }
    }

    private static boolean isUTRecipeBookEnabled() {
        Field miscField;
        try {
            Class<?> utConfig = Class.forName("mod.acgaming.universaltweaks.config.UTConfigTweaks");
            miscField = utConfig.getField("MISC");
        } catch (ClassNotFoundException e) {
            // try using an older version
            try {
                Class<?> utConfig = Class.forName("mod.acgaming.universaltweaks.config.UTConfig");
                miscField = utConfig.getField("TWEAKS_MISC");
            } catch (ClassNotFoundException ex) {
                return false;
            } catch (NoSuchFieldException ex) {
                throw new RuntimeException(ex);
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        try {
            Object misc = miscField.get(null);
            Field bookToggleField = misc.getClass().getField("utRecipeBookToggle");
            return !(boolean) bookToggleField.get(misc);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
