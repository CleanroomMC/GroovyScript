package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.event.GroovyEventManager;
import com.cleanroommc.groovyscript.event.GroovyReloadEvent;
import com.cleanroommc.groovyscript.event.ScriptRunEvent;
import com.cleanroommc.groovyscript.helper.GroovyHelper;
import com.cleanroommc.groovyscript.helper.MetaClassExpansion;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.cleanroommc.groovyscript.sandbox.engine.ScriptEngine;
import com.cleanroommc.groovyscript.sandbox.expand.ExpansionHelper;
import com.cleanroommc.groovyscript.sandbox.transformer.GroovyScriptCompiler;
import com.cleanroommc.groovyscript.sandbox.transformer.GroovyScriptEarlyCompiler;
import groovy.lang.*;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
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

        ExpansionHelper.mixinClass(MetaClass.class, MetaClassExpansion.class);

        getImportCustomizer().addStaticStars(GroovyHelper.class.getName(), MathHelper.class.getName());
        getImportCustomizer().addImports(
                "com.cleanroommc.groovyscript.api.IIngredient",
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
                "net.minecraft.potion.PotionEffect",
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

    @Override
    protected void preRun() {
        super.preRun();
        // first clear all added events
        GroovyEventManager.INSTANCE.reset();
        if (getCurrentLoader().isReloadable() && !ReloadableRegistryManager.isFirstLoad()) {
            // if this is not the first time this load stage is executed, reload all virtual registries
            ReloadableRegistryManager.onReload();
            // invoke reload event
            MinecraftForge.EVENT_BUS.post(new GroovyReloadEvent());
        }
        GroovyLog.get().infoMC("Running scripts in loader '{}'", getCurrentLoader());
        // this.engine.prepareEngine(this.currentLoadStage);
        // and finally invoke pre script run event
        MinecraftForge.EVENT_BUS.post(new ScriptRunEvent.Pre(getCurrentLoader()));
    }

    @Override
    protected void postRun() {
        if (getCurrentLoader() == LoadStage.POST_INIT) {
            ReloadableRegistryManager.afterScriptRun();
        }
        MinecraftForge.EVENT_BUS.post(new ScriptRunEvent.Post(getCurrentLoader()));
        if (getCurrentLoader() == LoadStage.POST_INIT && ReloadableRegistryManager.isFirstLoad()) {
            ReloadableRegistryManager.setLoaded();
        }
    }
}
