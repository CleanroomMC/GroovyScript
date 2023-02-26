package com.cleanroommc.groovyscript.compat.content;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.JsonHelper;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;

public class GroovyItem extends Item {

    private static boolean initialised = false;
    private static final String nullTranslationKey = "item.null";

    @GroovyBlacklist
    private static final Map<String, Item> ITEMS = new Object2ObjectOpenHashMap<>();

    @GroovyBlacklist
    public static void registerItem(Item item) {
        if (initialised && GroovyScript.getSandbox().isRunning()) {
            GroovyLog.get().errorMC("Items must registered in preInit. Tried to register {} too late!", item.getRegistryName());
            return;
        }
        ResourceLocation key = item.getRegistryName();
        if (key == null || ITEMS.containsKey(key.getPath())) {
            throw new IllegalArgumentException();
        }
        ITEMS.put(key.getPath(), item);
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    public static void registerModels() {
        for (Item item : ITEMS.values()) {
            ResourceLocation rl = item.getRegistryName();
            ModelBakery.registerItemVariants(item, rl);
            ModelResourceLocation mrl = new ModelResourceLocation(rl, "inventory");
            ModelLoader.setCustomModelResourceLocation(item, 0, mrl);
        }
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    public static void initItems(IForgeRegistry<Item> registry) {
        for (Item item : ITEMS.values()) {
            registry.register(item);
            checkModelFile(item.getRegistryName());
            if (item.getTranslationKey().equals(nullTranslationKey)) {
                item.setTranslationKey(item.getRegistryName().getNamespace() + "." + item.getRegistryName().getPath());
            }
        }
        initialised = true;
    }

    public GroovyItem(String loc) {
        setRegistryName(GroovyScript.getRunConfig().getPackId(), loc);

    }

    public void register() {
        registerItem(this);
    }

    @SideOnly(Side.CLIENT)
    public ResourceLocation createItemModelPath(String postfix) {
        return new ResourceLocation(getRegistryName().getNamespace(), getRegistryName().getPath() + postfix);
    }

    @Override
    public int getItemEnchantability() {
        return super.getItemEnchantability();
    }

    @Override
    public int getMaxItemUseDuration(@NotNull ItemStack stack) {
        return super.getMaxItemUseDuration(stack);
    }

    @Override
    public boolean hasEffect(@NotNull ItemStack stack) {
        return super.hasEffect(stack);
    }

    @Override
    public @NotNull IRarity getForgeRarity(@NotNull ItemStack stack) {
        return super.getForgeRarity(stack);
    }

    @Override
    public void setHarvestLevel(@NotNull String toolClass, int level) {
        super.setHarvestLevel(toolClass, level);
    }

    private static void checkModelFile(ResourceLocation loc) {
        File modelFile = GroovyScript.makeFile(GroovyScript.getResourcesFile(), loc.getNamespace(), "models", "item", loc.getPath() + ".json");
        if (!modelFile.exists()) {
            JsonObject modelJson = new JsonObject();
            modelJson.addProperty("parent", "item/generated");
            JsonObject texturesJson = new JsonObject();
            texturesJson.addProperty("layer0", loc.getNamespace() + ":items/" + loc.getPath());
            modelJson.add("textures", texturesJson);
            JsonHelper.saveJson(modelFile, modelJson);
        }
    }
}
