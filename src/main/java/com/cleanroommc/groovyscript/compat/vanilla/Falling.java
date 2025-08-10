package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.registry.NamedRegistry;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES)
public class Falling extends NamedRegistry implements IScriptReloadable {

    @ApiStatus.Internal
    public static void fall(World world, BlockPos pos) {
        BlockPos down = pos.down();
        if (world.isAirBlock(down) || (pos.getY() >= 0 && BlockFalling.canFallThrough(world.getBlockState(down)))) {
            if (!BlockFalling.fallInstantly && world.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32))) {
                EntityFallingBlock entityfallingblock = new EntityFallingBlock(world, (double) pos.getX() + 0.5D, (double) pos.getY(), (double) pos.getZ() + 0.5D, world.getBlockState(pos));
                world.spawnEntity(entityfallingblock);
            } else {
                IBlockState state = world.getBlockState(pos);
                world.setBlockToAir(pos);
                BlockPos blockpos;

                for (blockpos = down; (world.isAirBlock(blockpos) || BlockFalling.canFallThrough(world.getBlockState(blockpos))) && blockpos.getY() > 0; blockpos = blockpos.down())
                {
                    ;
                }
                if (blockpos.getY() > 0)
                {
                    world.setBlockState(blockpos.up(), state); //Forge: Fix loss of state information during world gen.
                }
            }
        }
    }

    private final Set<IBlockState> enabledFalling = new ObjectOpenHashSet<>();

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public boolean isEnabled(IBlockState state) {
        return ((BlockMixinExpansion) state.getBlock()).grs$isFallingEnabled(state);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("blockstate('minecraft:diamond_block')"))
    public void enable(IBlockState state) {
        ((BlockMixinExpansion) state.getBlock()).grs$setFalling(state, true);
        enabledFalling.add(state);
    }

    @GroovyBlacklist
    @Override
    public void onReload() {
        for (IBlockState state : enabledFalling) {
            ((BlockMixinExpansion) state.getBlock()).grs$setFalling(state, false);
        }
        this.enabledFalling.clear();
    }

    @Override
    public void afterScriptLoad() {}

}
