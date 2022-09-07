package com.cleanroommc.groovyscript.brackets;

import com.cleanroommc.groovyscript.api.IBracketHandler;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
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
    public ItemStack parse(Object[] args) {
        if (args.length > 2 || (args.length == 2 && !(args[1] instanceof Integer))) {
            throw new IllegalArgumentException("Arguments not valid for bracket handler. Use 'item(String)' or 'item(String, int meta)'");
        }
        String main = (String) args[0];
        String[] parts = main.split(SPLITTER);
        if (parts.length < 2) {
            GroovyLog.LOG.error("Can't find item for '%s'", main);
            return ItemStack.EMPTY;
        }
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(parts[0], parts[1]));
        if (item == null) {
            GroovyLog.LOG.error("Can't find item for '%s'", main);
            return ItemStack.EMPTY;
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
        if (args.length == 2) {
            if (meta != 0) {
                throw new IllegalArgumentException("Defined meta value twice for item bracket handler");
            }
            meta = (int) args[1];
        }
        return new ItemStack(item, 1, meta);
    }

    @Override
    public ItemStack parse(String arg) {
        String[] parts = arg.split(SPLITTER);
        if (parts.length < 2) {
            GroovyLog.LOG.error("Can't find item for '%s'", arg);
            return null;
        }
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(parts[0], parts[1]));
        if (item == null) {
            GroovyLog.LOG.error("Can't find item for '%s'", arg);
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
