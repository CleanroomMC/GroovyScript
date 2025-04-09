package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.event.GroovyEventManager;
import com.cleanroommc.groovyscript.helper.GroovyHelper;
import com.cleanroommc.groovyscript.sandbox.engine.ScriptEngine;
import com.cleanroommc.groovyscript.sandbox.transformer.GroovyScriptCompiler;
import com.cleanroommc.groovyscript.sandbox.transformer.GroovyScriptEarlyCompiler;
import net.minecraft.util.math.MathHelper;
import org.codehaus.groovy.control.CompilerConfiguration;

public class GroovyScriptSandbox extends AbstractGroovySandbox {

    @Override
    protected ScriptEngine createEngine(CompilerConfiguration config) {
        return new ScriptEngine(SandboxData.getRootUrls(), SandboxData.getStandardScriptCachePath(), SandboxData.getScriptFile(), config);
    }

    @Override
    protected void initConfig(CompilerConfiguration config) {
        registerGlobal("Mods", ModSupport.INSTANCE);
        registerGlobal("Log", GroovyLog.get());
        registerGlobal("EventManager", GroovyEventManager.INSTANCE);

        getImportCustomizer().addStaticStars(GroovyHelper.class.getName(), MathHelper.class.getName());
        getImportCustomizer().addImports(
                "net.minecraft.world.World",
                "net.minecraft.block.state.IBlockState",
                "net.minecraft.block.Block",
                "net.minecraft.block.SoundType",
                "net.minecraft.enchantment.Enchantment",
                "net.minecraft.entity.Entity",
                "net.minecraft.entity.player.EntityPlayer",
                "net.minecraft.init.Biomes",
                "net.minecraft.init.Blocks",
                "net.minecraft.init.Enchantments",
                "net.minecraft.init.Items",
                "net.minecraft.init.MobEffects",
                "net.minecraft.init.PotionTypes",
                "net.minecraft.init.SoundEvents",
                "net.minecraft.item.EnumRarity",
                "net.minecraft.item.Item",
                "net.minecraft.item.ItemStack",
                "net.minecraft.nbt.NBTTagCompound",
                "net.minecraft.nbt.NBTTagList",
                "net.minecraft.tileentity.TileEntity",
                "net.minecraft.util.math.BlockPos",
                "net.minecraft.util.DamageSource",
                "net.minecraft.util.EnumHand",
                "net.minecraft.util.EnumHandSide",
                "net.minecraft.util.EnumFacing",
                "net.minecraft.util.ResourceLocation",
                "net.minecraftforge.fml.common.eventhandler.EventPriority",
                "com.cleanroommc.groovyscript.event.EventBusType",
                "net.minecraftforge.fml.relauncher.Side",
                "net.minecraftforge.fml.relauncher.SideOnly");
        super.initConfig(config);
        config.addCompilationCustomizers(new GroovyScriptCompiler());
        config.addCompilationCustomizers(new GroovyScriptEarlyCompiler());
    }

    @Override
    public boolean canRunInStage(LoadStage stage) {
        return !stage.isMixin();
    }
}
