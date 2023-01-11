package com.cleanroommc.groovyscript.brackets;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IBracketHandler;
import com.google.common.base.Optional;
import com.google.common.collect.Iterators;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Arrays;
import java.util.Iterator;

import static com.cleanroommc.groovyscript.brackets.BracketHandlerManager.SPLITTER;

public class BlockStateBracketHandler implements IBracketHandler<IBlockState> {

    public static final BlockStateBracketHandler INSTANCE = new BlockStateBracketHandler();

    private static final String COMMA = ",", EQUALS = "=";

    private BlockStateBracketHandler() {
    }

    @Override
    public IBlockState parse(Object[] args) {
        String main = (String) args[0];
        IBlockState blockState = parse(main);
        if (args.length > 1) {
            if (args.length == 2 && args[1] instanceof Integer) {
                return blockState.getBlock().getStateFromMeta((Integer) args[1]);
            }
            for (int i = 1; i < args.length; i++) {
                if (!(args[i] instanceof String)) {
                    throw new IllegalArgumentException("All arguments must be strings in block state bracket handler!");
                }
            }
            String[] stringArgs = (String[]) Arrays.copyOfRange(args, 1, args.length);
            return parseBlockStates(blockState, Iterators.forArray(stringArgs));
        }
        return blockState;
    }

    @Override
    public IBlockState parse(String arg) {
        String[] parts = arg.split(SPLITTER);
        if (parts.length < 2) {
            GroovyLog.get().error("Can't find block for '{}'", arg);
            return null;
        }
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(parts[0], parts[1]));
        if (block == null) {
            GroovyLog.get().error("Can't find block for '{}'", arg);
            return null;
        }
        IBlockState blockState = block.getDefaultState();
        if (parts.length > 2) {
            String[] states = parts[2].split(COMMA);
            if (states.length == 1) {
                try {
                    int meta = Integer.parseInt(states[0]);
                    return blockState.getBlock().getStateFromMeta(meta);
                } catch (NumberFormatException ignored) {
                }
            }
            return parseBlockStates(blockState, Iterators.forArray(states));
        }
        return blockState;
    }

    @SuppressWarnings("all")
    private static IBlockState parseBlockStates(IBlockState defaultState, Iterator<String> iterable) {
        for (Iterator<String> it = iterable; it.hasNext(); ) {
            String state = it.next();
            String[] prop = state.split(EQUALS, 2);
            IProperty property = defaultState.getBlock().getBlockState().getProperty(prop[0]);
            if (property == null) {
                GroovyLog.get().error("Invalid property name '{}' for block '{}'", prop[0], defaultState.getBlock().getRegistryName());
                continue;
            }
            Optional<? extends Comparable> value = property.parseValue(prop[1]);
            if (value.isPresent()) {
                defaultState = defaultState.withProperty(property, value.get());
            } else {
                GroovyLog.get().error("Invalid property value '{}' for block '{}:{}'", prop[1], defaultState.getBlock().getRegistryName());
            }
        }

        return defaultState;
    }
}
