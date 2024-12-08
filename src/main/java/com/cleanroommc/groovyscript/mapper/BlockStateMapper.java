package com.cleanroommc.groovyscript.mapper;

import com.cleanroommc.groovyscript.api.Result;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.server.Completions;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class BlockStateMapper extends AbstractObjectMapper<IBlockState> {

    public static final BlockStateMapper INSTANCE = new BlockStateMapper("blockstate", null);

    protected BlockStateMapper(String name, GroovyContainer<?> mod) {
        super(name, mod, IBlockState.class);
    }

    @Override
    public Result<IBlockState> getDefaultValue() {
        return Result.some(Blocks.AIR.getDefaultState());
    }

    @Override
    public @NotNull Result<IBlockState> parse(String mainArg, Object[] args) {
        return ObjectMappers.parseBlockState(mainArg, args);
    }

    @Override
    public void provideCompletion(int index, Completions items) {
        if (index == 0) items.addAllOfRegistry(ForgeRegistries.BLOCKS);
    }

    @Override
    public void bindTexture(IBlockState iBlockState) {
        ItemStack itemStack = new ItemStack(iBlockState.getBlock(), 1, iBlockState.getBlock().getMetaFromState(iBlockState));
        TextureBinder.ofItem().bindTexture(itemStack);
    }

    @Override
    public @NotNull List<String> getTooltip(IBlockState iBlockState) {
        ItemStack itemStack = new ItemStack(iBlockState.getBlock(), 1, iBlockState.getBlock().getMetaFromState(iBlockState));
        return Collections.singletonList(itemStack.getDisplayName());
    }
}
