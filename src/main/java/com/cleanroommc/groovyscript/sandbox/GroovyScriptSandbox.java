package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.event.GroovyEventManager;
import com.cleanroommc.groovyscript.event.GroovyReloadEvent;
import com.cleanroommc.groovyscript.event.ScriptRunEvent;
import com.cleanroommc.groovyscript.helper.GroovyHelper;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.cleanroommc.groovyscript.sandbox.transformer.GroovyScriptCompiler;
import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Objects;

public class GroovyScriptSandbox extends GroovySandbox {

    private static final String[] DEFAULT_IMPORTS = {
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
            "com.cleanroommc.groovyscript.event.EventBusType"
    };

    private LoadStage currentLoadStage;

    public GroovyScriptSandbox(URL... scriptEnvironment) {
        super(scriptEnvironment);
        registerBinding("mods", ModSupport.INSTANCE);
        registerBinding("log", GroovyLog.get());
        registerBinding("EventManager", GroovyEventManager.INSTANCE);
        registerBinding("eventManager", GroovyEventManager.INSTANCE);
        registerBinding("event_manager", GroovyEventManager.INSTANCE);
    }

    public void run(LoadStage currentLoadStage) {
        this.currentLoadStage = Objects.requireNonNull(currentLoadStage);
        try {
            super.run();
        } catch (IOException | ScriptException | ResourceException e) {
            GroovyLog.get().errorMC("An Exception occurred trying to run groovy!");
            GroovyScript.LOGGER.throwing(e);
        } catch (Exception e) {
            GroovyLog.get().exception(e);
        } finally {
            this.currentLoadStage = null;
        }
    }

    @ApiStatus.Internal
    @Override
    public void run() throws Exception {
        throw new UnsupportedOperationException("Use run(Loader loader) instead!");
    }

    @Override
    public <T> T runClosure(Closure<T> closure, Object... args) {
        startRunning();
        T result = null;
        try {
            result = closure.call(args);
        } catch (Exception e) {
            GroovyLog.get().error("An exception occurred while running a closure!");
            GroovyLog.get().exception(e);
        } finally {
            stopRunning();
        }
        return result;
    }

    @Override
    protected void postInitBindings(Binding binding) {
        binding.setProperty("out", GroovyLog.get().getWriter());
        binding.setVariable("globals", getBindings());
    }

    @Override
    protected void initEngine(GroovyScriptEngine engine, CompilerConfiguration config) {
        config.addCompilationCustomizers(GroovyScriptCompiler.transformer());
        ImportCustomizer importCustomizer = new ImportCustomizer();
        importCustomizer.addStaticStars(GroovyHelper.class.getName(), MathHelper.class.getName());
        importCustomizer.addImports(DEFAULT_IMPORTS);
        config.addCompilationCustomizers(importCustomizer);
    }

    @Override
    protected void preRun() {
        GroovyLog.get().info("Running scripts in loader '{}'", this.currentLoadStage);
        if (Loader.instance().activeModContainer() == null) Loader.instance().setActiveModContainer(Loader.instance().getIndexedModList().get("groovyscript"));
        MinecraftForge.EVENT_BUS.post(new ScriptRunEvent.Pre());
        if (this.currentLoadStage.isReloadable() && !ReloadableRegistryManager.isFirstLoad()) {
            ReloadableRegistryManager.onReload();
            MinecraftForge.EVENT_BUS.post(new GroovyReloadEvent());
        }
        GroovyEventManager.INSTANCE.reset();
    }

    @Override
    protected boolean shouldRunFile(File file) {
        GroovyLog.get().info(" - executing {}", file.toString());
        return true;
    }

    @Override
    protected void postRun() {
        if (this.currentLoadStage == LoadStage.POST_INIT) {
            ReloadableRegistryManager.afterScriptRun();
        }
        MinecraftForge.EVENT_BUS.post(new ScriptRunEvent.Post());
        if (this.currentLoadStage == LoadStage.POST_INIT && ReloadableRegistryManager.isFirstLoad()) {
            ReloadableRegistryManager.setLoaded();
        }
    }

    @Override
    public Collection<File> getClassFiles() {
        return GroovyScript.getRunConfig().getClassFiles();
    }

    @Override
    public Collection<File> getScriptFiles() {
        return GroovyScript.getRunConfig().getSortedFiles(this.currentLoadStage.getName());
    }

    @Nullable
    public LoadStage getCurrentLoader() {
        return currentLoadStage;
    }
}
