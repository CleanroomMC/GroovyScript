package com.cleanroommc.groovyscript.mapper;

import com.cleanroommc.groovyscript.api.Result;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.server.CompletionParams;
import com.cleanroommc.groovyscript.server.Completions;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.prominic.groovyls.util.CompletionItemFactory;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class BlockStateMapper extends AbstractObjectMapper<IBlockState> {

    public static final BlockStateMapper INSTANCE = new BlockStateMapper("blockstate", null);

    protected BlockStateMapper(String name, GroovyContainer<?> mod) {
        super(name, mod, IBlockState.class);
        addSignature(String.class, int.class);
        addSignature(String.class, String[].class);
        this.documentation = docOfType("block state");
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
    public void provideCompletion(int index, CompletionParams params, Completions items) {
        if (index == 0) items.addAllOfRegistry(ForgeRegistries.BLOCKS);
        if (index >= 1 && params.isParamType(0, String.class)) {
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(params.getParamAsType(0, String.class)));
            if (block != null) {
                // TODO completions for ints doesnt work properly
                /*items.addAll(block.getBlockState().getValidStates(), state -> {
                    return CompletionItemFactory.createCompletion(CompletionItemKind.Value, String.valueOf(state.getBlock().getMetaFromState(state)));
                });*/
                for (IProperty property : block.getBlockState().getProperties()) {
                    items.addAll(property.getAllowedValues(), val -> {
                        CompletionItem item = CompletionItemFactory.createCompletion(CompletionItemKind.Constant, property.getName() + "=" + property.getName((Comparable) val));
                        return item;
                    });
                }
            }
        }
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

    @Override
    public boolean hasTextureBinder() {
        return true;
    }
}
