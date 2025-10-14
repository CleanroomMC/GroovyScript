package com.cleanroommc.groovyscript.compat.inworldcrafting;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.cleanroommc.groovyscript.registry.NamedRegistry;
import com.google.common.collect.ImmutableList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.List;

public class InWorldCrafting extends NamedRegistry implements IScriptReloadable {

    public final FluidToFluid fluidToFluid = new FluidToFluid();
    public final FluidToItem fluidToItem = new FluidToItem();
    public final FluidToBlock fluidToBlock = new FluidToBlock();
    public final Explosion explosion = new Explosion();
    public final Burning burning = new Burning();
    public final PistonPush pistonPush = new PistonPush();

    private final List<IScriptReloadable> registries = ImmutableList.of(fluidToFluid, fluidToItem, fluidToBlock, explosion, burning, pistonPush);

    @GroovyBlacklist
    @Override
    public void onReload() {
        registries.forEach(IScriptReloadable::onReload);
    }

    @GroovyBlacklist
    @Override
    public void afterScriptLoad() {
        registries.forEach(IScriptReloadable::afterScriptLoad);
    }

    public static EntityItem spawnItem(World world, BlockPos pos, ItemStack item) {
        EntityItem entityItem = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, item);
        world.spawnEntity(entityItem);
        return entityItem;
    }
}
