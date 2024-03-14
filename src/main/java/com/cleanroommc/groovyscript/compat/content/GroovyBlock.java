package com.cleanroommc.groovyscript.compat.content;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.helper.JsonHelper;
import com.cleanroommc.groovyscript.sandbox.FileUtil;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.util.Map;

public class GroovyBlock extends Block {

    private static boolean initialised = false;
    private static final String nullTranslationKey = "tile.null";

    private static final Map<String, Pair<Block, ItemBlock>> BLOCKS = new Object2ObjectLinkedOpenHashMap<>();

    @GroovyBlacklist
    public static void register(Block block, ItemBlock itemBlock) {
        if (initialised) {
            GroovyLog.get().errorMC("Items must registered in preInit. Tried to register {} too late!", block.getRegistryName());
            return;
        }
        if (itemBlock.getBlock() != block) {
            GroovyLog.get().exception(new IllegalArgumentException("The item block must the item form of the block!"));
            return;
        }
        ResourceLocation itemLoc = itemBlock.getRegistryName();
        ResourceLocation blockLoc = block.getRegistryName();
        if (itemLoc == null || !itemLoc.equals(blockLoc) || BLOCKS.containsKey(itemLoc.getPath())) {
            GroovyLog.get().exception(new IllegalArgumentException("The registry name of the block and the item must be non-null and equal!"));
            return;
        }
        BLOCKS.put(itemLoc.getPath(), Pair.of(block, itemBlock));
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    public static void initItems(IForgeRegistry<Item> registry) {
        for (Pair<Block, ItemBlock> pair : BLOCKS.values()) {
            registry.register(pair.getRight());
            checkItemModel(pair.getRight().getRegistryName());
        }
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    public static void initBlocks(IForgeRegistry<Block> registry) {
        for (Pair<Block, ItemBlock> pair : BLOCKS.values()) {
            Block block = pair.getLeft();
            registry.register(block);
            checkBlockModel(block.getRegistryName());
            if (block.getTranslationKey().equals(nullTranslationKey)) {
                block.setTranslationKey(block.getRegistryName().getNamespace() + "." + block.getRegistryName().getPath());
            }
        }
        initialised = true;
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    @SideOnly(Side.CLIENT)
    public static void registerModels() {
        for (Pair<Block, ItemBlock> pair : BLOCKS.values()) {
            Item item = pair.getRight();
            ResourceLocation rl = item.getRegistryName();
            ModelBakery.registerItemVariants(item, rl);
            ModelResourceLocation mrl = new ModelResourceLocation(rl, "inventory");
            ModelLoader.setCustomModelResourceLocation(item, 0, mrl);
        }
    }

    private final ItemBlock itemBlock;

    public GroovyBlock(String name, Material blockMaterialIn) {
        super(blockMaterialIn);
        this.itemBlock = new ItemBlock(this);
        setRegistryName(GroovyScript.getRunConfig().getPackId(), name);
        this.itemBlock.setRegistryName(GroovyScript.getRunConfig().getPackId(), name);
        setHardness(2.0F);
        setResistance(10.0F);
        setSoundType(SoundType.STONE);
        if (VanillaModule.content.getDefaultTab() != null) {
            setCreativeTab(VanillaModule.content.getDefaultTab());
        }
    }

    public GroovyBlock register() {
        register(this, this.itemBlock);
        return this;
    }

    private static void checkBlockModel(ResourceLocation loc) {
        File file = FileUtil.makeFile(GroovyScript.getResourcesFile().getPath(), loc.getNamespace(), "blockstates", loc.getPath() + ".json");
        if (!file.exists()) {
            JsonObject stateJson = new JsonObject();
            JsonObject variantsJson = new JsonObject();
            JsonObject modelJson = new JsonObject();
            modelJson.addProperty("model", loc.toString());
            variantsJson.add("normal", modelJson);
            stateJson.add("variants", variantsJson);
            JsonHelper.saveJson(file, stateJson);
        }

        file = FileUtil.makeFile(GroovyScript.getResourcesFile().getPath(), loc.getNamespace(), "models", "block", loc.getPath() + ".json");
        if (!file.exists()) {
            JsonObject modelJson = new JsonObject();
            modelJson.addProperty("parent", "block/cube_all");
            JsonObject texturesJson = new JsonObject();
            texturesJson.addProperty("all", loc.getNamespace() + ":blocks/" + loc.getPath());
            modelJson.add("textures", texturesJson);
            JsonHelper.saveJson(file, modelJson);
        }
    }

    private static void checkItemModel(ResourceLocation loc) {
        File file = FileUtil.makeFile(GroovyScript.getResourcesFile().getPath(), loc.getNamespace(), "models", "item", loc.getPath() + ".json");
        if (!file.exists()) {
            JsonObject modelJson = new JsonObject();
            modelJson.addProperty("parent", loc.getNamespace() + ":block/" + loc.getPath());
            JsonHelper.saveJson(file, modelJson);
        }
    }
}
