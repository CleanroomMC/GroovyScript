package com.cleanroommc.groovyscript.compat.inworldcrafting;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.cleanroommc.groovyscript.api.documentation.IRegistryDocumentation;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.documentation.Documentation;
import com.cleanroommc.groovyscript.documentation.Exporter;
import com.cleanroommc.groovyscript.documentation.helper.ContainerHolder;
import com.cleanroommc.groovyscript.documentation.helper.LinkIndex;
import com.cleanroommc.groovyscript.registry.NamedRegistry;
import com.cleanroommc.groovyscript.sandbox.LoadStage;
import com.google.common.collect.ImmutableList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InWorldCrafting extends NamedRegistry implements IScriptReloadable, IRegistryDocumentation {

    private static final String NAME = "In-World Crafting";
    private static final String LOCATION = "in_world_crafting";

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

    private ContainerHolder getContainerHolder() {
        List<String> list = new ArrayList<>();
        for (String alias : getAliases()) {
            list.add(alias);
            for (String minecraftAlias : ModSupport.MINECRAFT.getAliases()) {
                list.add(minecraftAlias + "." + alias);
                list.add(ContainerHolder.BASE_ACCESS_COMPAT + "." + minecraftAlias + "." + alias);
            }
        }
        return new ContainerHolder(LOCATION, NAME, LOCATION, importBlock -> importBlock + "\nlog 'running In-World Crafting example'", list, ImmutableList.copyOf(registries));
    }

    @Override
    public void generateWiki(ContainerHolder container, File suggestedFolder, LinkIndex linkIndex) {
        File inWorld = new File(Documentation.WIKI_MINECRAFT, LOCATION);
        Exporter.generateWiki(inWorld, getContainerHolder());
    }

    @Override
    public @NotNull String generateExamples(ContainerHolder container, LoadStage loadStage, List<String> imports) {
        File inWorldCrafting = new File(Documentation.generatedFolder(loadStage), LOCATION + Documentation.GROOVY_FILE_EXTENSION);
        Exporter.generateExamples(inWorldCrafting, loadStage, getContainerHolder());
        return "";
    }

    @Override
    public boolean skipDefaultExamples(ContainerHolder container) {
        return true;
    }

    @Override
    public boolean skipDefaultWiki(ContainerHolder container) {
        return true;
    }
}
