package com.cleanroommc.groovyscript.compat.mods.draconicevolution;

import com.brandon3055.brandonscore.lib.MultiBlockStorage;
import com.brandon3055.draconicevolution.world.EnergyCoreStructure;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.draconicevolution.EnergyCoreStructureAccessor;
import com.cleanroommc.groovyscript.core.mixin.draconicevolution.MultiBlockStorageAccessor;
import com.cleanroommc.groovyscript.helper.Alias;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.Collection;

@RegistryDescription
public class EnergyCore implements IScriptReloadable {

    private static final String DRACONIUM = "draconicevolution:draconium_block";
    private static final String DRACONIC = "draconicevolution:draconic_block";
    private static final String REDSTONE = "minecraft:redstone_block";

    private int version = 0;
    private MultiBlockStorage[] original;
    private MultiBlockStorage[] edited;
    private String[] inner;
    private String[] outer;

    private void init() {
        if (this.original != null) return;
        EnergyCoreStructure ecs = new EnergyCoreStructure();
        ecs.initialize(null);
        this.original = ((EnergyCoreStructureAccessor) ecs).getStructureTiers();
        this.edited = Arrays.copyOf(this.original, this.original.length);
        this.inner = new String[this.original.length];
        this.outer = new String[this.original.length];
        onReload(); // increases version to 1
    }

    @Override
    public Collection<String> getAliases() {
        return Alias.generateOfClass(EnergyCore.class);
    }

    @Override
    public void onReload() {
        if (this.original == null) return;
        this.edited = Arrays.copyOf(this.original, this.original.length);
        Arrays.fill(this.inner, REDSTONE);
        Arrays.fill(this.outer, DRACONIUM);
        this.inner[this.inner.length - 1] = DRACONIUM;
        this.outer[this.outer.length - 1] = DRACONIC;
        this.version++;
    }

    @Override
    public void afterScriptLoad() {}

    @GroovyBlacklist
    public int getVersion() {
        return version;
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    public void applyEdit(MultiBlockStorage[] mbs) {
        for (int i = 0; i < mbs.length; i++) {
            ((MultiBlockStorageAccessor) mbs[i]).setBlockStorage(((MultiBlockStorageAccessor) this.edited[i]).getBlockStorage());
        }
    }

    private void replaceBlock(int tier, String edit, boolean inner) {
        if (tier < 1 || tier > 8) {
            GroovyLog.msg("Error setting block of Draconic Evolution Energy Core")
                    .add("Tier {} is invalid. Must be between 1 and 8")
                    .error()
                    .post();
            return;
        }
        init();
        String old = inner ? this.inner[tier - 1] : this.outer[tier - 1];
        String[][][] blocks = ((MultiBlockStorageAccessor) this.edited[tier - 1]).getBlockStorage();
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {
                for (int k = 0; k < blocks[i][j].length; k++) {
                    if (old.equals(blocks[i][j][k])) {
                        blocks[i][j][k] = edit;
                    }
                }
            }
        }
        (inner ? this.inner : this.outer)[tier - 1] = edit;
    }

    @MethodDescription(description = "groovyscript.wiki.draconicevolution.inner_block", type = MethodDescription.Type.VALUE, example = {
            @Example("7, block('minecraft:clay')")
    })
    public EnergyCore setInnerBlock(int tier, Block block) {
        if (block == null) {
            GroovyLog.msg("Error setting inner block of tier {} Draconic Evolution Energy Core", tier)
                    .add("block must not be null")
                    .error()
                    .post();
            return this;
        }
        replaceBlock(tier, block.getRegistryName().toString(), true);
        return this;
    }

    @MethodDescription(description = "groovyscript.wiki.draconicevolution.outer_block", type = MethodDescription.Type.VALUE, example = {
            @Example("7, block('minecraft:diamond_block')"),
            @Example("2, block('minecraft:diamond_block')")
    })
    public EnergyCore setOuterBlock(int tier, Block block) {
        if (block == null) {
            GroovyLog.msg("Error setting outer block of tier {} Draconic Evolution Energy Core", tier)
                    .add("block must not be null")
                    .error()
                    .post();
            return this;
        }
        replaceBlock(tier, block.getRegistryName().toString(), false);
        return this;
    }
}
