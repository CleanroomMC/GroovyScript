package com.cleanroommc.groovyscript.compat.inworldcrafting;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.cleanroommc.groovyscript.registry.NamedRegistry;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InWorldCrafting extends NamedRegistry implements IScriptReloadable {

    public final FluidToFluid fluidToFluid = new FluidToFluid();
    public final FluidToItem fluidToItem = new FluidToItem();
    public final FluidToBlock fluidToBlock = new FluidToBlock();
    public final Explosion explosion = new Explosion();
    public final Burning burning = new Burning();
    public final PistonPush pistonPush = new PistonPush();

    @GroovyBlacklist
    @Override
    public void onReload() {
        this.fluidToFluid.onReload();
        this.fluidToItem.onReload();
        this.fluidToBlock.onReload();
        this.explosion.onReload();
        this.burning.onReload();
        this.pistonPush.onReload();
    }

    @GroovyBlacklist
    @Override
    public void afterScriptLoad() {
        this.fluidToFluid.afterScriptLoad();
        this.fluidToItem.afterScriptLoad();
        this.fluidToBlock.afterScriptLoad();
        this.explosion.afterScriptLoad();
        this.burning.afterScriptLoad();
        this.pistonPush.afterScriptLoad();
    }

    public static EntityItem spawnItem(World world, BlockPos pos, ItemStack item) {
        EntityItem entityItem = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, item);
        world.spawnEntity(entityItem);
        return entityItem;
    }
}
