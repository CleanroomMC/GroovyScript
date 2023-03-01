package com.cleanroommc.groovyscript.brackets;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IBracketHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import static com.cleanroommc.groovyscript.brackets.BracketHandlerManager.SPLITTER;
import static com.cleanroommc.groovyscript.brackets.BracketHandlerManager.WILDCARD;

public class ItemBracketHandler implements IBracketHandler<ItemStack> {

    public static final ItemBracketHandler INSTANCE = new ItemBracketHandler();

    private ItemBracketHandler() {
    }

    @Override
    public ItemStack parse(String mainArg, Object[] args) {
        if (args.length > 1 || (args.length == 1 && !(args[0] instanceof Integer))) {
            throw new IllegalArgumentException("Arguments not valid for bracket handler. Use 'item(String)' or 'item(String, int meta)'");
        }
        String[] parts = mainArg.split(SPLITTER);
        if (parts.length < 2) {
            return null;
        }
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(parts[0], parts[1]));
        if (item == null) {
            return null;
        }
        int meta = 0;
        if (parts.length > 2) {
            if (WILDCARD.equals(parts[2])) {
                meta = Short.MAX_VALUE;
            } else {
                try {
                    meta = Integer.parseInt(parts[2]);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        if (args.length == 1) {
            if (meta != 0) {
                GroovyLog.get().error("Defined meta value twice for item bracket handler");
                return new ItemStack(item, 1, meta);
            }
            meta = (int) args[0];
        }
        return new ItemStack(item, 1, meta);
    }

    @Override
    public ItemStack parse(String arg) {
        String[] parts = arg.split(SPLITTER);
        if (parts.length < 2) {
            return null;
        }
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(parts[0], parts[1]));
        if (item == null) {
            return null;
        }
        int meta = 0;
        if (parts.length > 2) {
            if (WILDCARD.equals(parts[2])) {
                meta = Short.MAX_VALUE;
            } else {
                try {
                    meta = Integer.parseInt(parts[2]);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return new ItemStack(item, 1, meta);
    }
}
