package com.cleanroommc.groovyscript.compat.inworldcrafting;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.INamed;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.cleanroommc.groovyscript.api.documentation.IRegistryDocumentation;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.documentation.Documentation;
import com.cleanroommc.groovyscript.documentation.Exporter;
import com.cleanroommc.groovyscript.documentation.helper.ContainerHolder;
import com.cleanroommc.groovyscript.documentation.helper.LinkIndex;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.sandbox.LoadStage;
import com.google.common.collect.ImmutableList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class InWorldCrafting extends GroovyPropertyContainer implements INamed, IScriptReloadable, IRegistryDocumentation {

    private static final String NAME = "In-World Crafting";
    private static final String LOCATION = "in_world_crafting";

    public final FluidToFluid fluidToFluid = new FluidToFluid();
    public final FluidToItem fluidToItem = new FluidToItem();
    public final FluidToBlock fluidToBlock = new FluidToBlock();
    public final Explosion explosion = new Explosion();
    public final Burning burning = new Burning();
    public final PistonPush pistonPush = new PistonPush();

    private final String name;
    private final List<String> aliases;

    private final List<IScriptReloadable> reloadable = ImmutableList.of(fluidToFluid, fluidToItem, fluidToBlock, explosion, burning, pistonPush);

    public InWorldCrafting() {
        this.aliases = Collections.unmodifiableList(Alias.generateOfClass(this));
        this.name = this.aliases.get(0).toLowerCase(Locale.ENGLISH);
    }

    public static EntityItem spawnItem(World world, BlockPos pos, ItemStack item) {
        EntityItem entityItem = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, item);
        world.spawnEntity(entityItem);
        return entityItem;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @GroovyBlacklist
    @Override
    public void onReload() {
        reloadable.forEach(IScriptReloadable::onReload);
    }

    @GroovyBlacklist
    @Override
    public void afterScriptLoad() {
        reloadable.forEach(IScriptReloadable::afterScriptLoad);
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
        return new ContainerHolder(LOCATION, NAME, LOCATION, importBlock -> importBlock + "\nlog 'running In-World Crafting example'", list, getRegistries());
    }

    @Override
    public void generateWiki(ContainerHolder container, File suggestedFolder, LinkIndex linkIndex) {
        File inWorld = new File(Documentation.WIKI_MINECRAFT, LOCATION);
        Exporter.generateWiki(inWorld, getContainerHolder());
    }

    @Override
    public @NotNull String generateExamples(ContainerHolder container, LoadStage loadStage, List<String> imports) {
        File inWorldCrafting = Documentation.generatedExampleFile(loadStage, LOCATION);
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
