package com.cleanroommc.groovyscript.compat.content;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.helper.JsonHelper;
import com.cleanroommc.groovyscript.sandbox.FileUtil;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;

@Property(property = "maxStackSize", defaultValue = "64")
@Property(property = "hasSubtypes")
@Property(property = "maxDamage")
@Property(property = "bFull3D")
@Property(property = "translationKey", defaultValue = "registryName", value = "groovyscript.wiki.minecraft.content.block.translationKey.value")
@Property(property = "containerItem")//getContainerItem(ItemStack) // should we add overrides for the ItemStack version?
@Property(property = "tabToDisplayOn", defaultValue = "content.getDefaultTab()")
@Property(property = "canRepair", defaultValue = "true")
@RecipeBuilderOverride(method = {
        @RecipeBuilderMethodDescription(method = "setMaxStackSize", field = "maxStackSize"),
        @RecipeBuilderMethodDescription(method = "setHasSubtypes", field = "hasSubtypes"),
        @RecipeBuilderMethodDescription(method = "setMaxDamage", field = "maxDamage"),
        @RecipeBuilderMethodDescription(method = "setFull3D", field = "bFull3D"),
        @RecipeBuilderMethodDescription(method = "setTranslationKey", field = "translationKey"),
        @RecipeBuilderMethodDescription(method = "setContainerItem", field = "containerItem"),
        @RecipeBuilderMethodDescription(method = "setCreativeTab", field = "tabToDisplayOn"),
        @RecipeBuilderMethodDescription(method = "setNoRepair", field = "canRepair"),
})
public class GroovyItem extends Item {

    private static boolean initialised;
    private static final String nullTranslationKey = "item.null";

    @GroovyBlacklist
    private static final Map<String, Item> ITEMS = new Object2ObjectLinkedOpenHashMap<>();

    @GroovyBlacklist
    public static void registerItem(Item item) {
        if (initialised) {
            GroovyLog.get().errorMC("Items must registered in preInit. Tried to register {} too late!", item.getRegistryName());
            return;
        }
        if (item.getCreativeTab() == null && VanillaModule.INSTANCE.content.getDefaultTab() != null) {
            item.setCreativeTab(VanillaModule.INSTANCE.content.getDefaultTab());
        }
        ResourceLocation key = item.getRegistryName();
        if (key == null || ITEMS.containsKey(key.getPath())) {
            GroovyLog.get().exception(new IllegalArgumentException("The registry name of the item must be non-null and not match an already registered item!"));
            return;
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

    @Property
    private boolean effect;
    @Property
    private int enchantability;
    @Property
    private IRarity rarity;

    public GroovyItem(String loc) {
        setRegistryName(GroovyScript.getRunConfig().getPackId(), loc);
        if (VanillaModule.INSTANCE.content.getDefaultTab() != null) {
            setCreativeTab(VanillaModule.INSTANCE.content.getDefaultTab());
        }
    }

    @RecipeBuilderMethodDescription(field = "effect")
    public GroovyItem setEnchantedEffect() {
        this.effect = true;
        return this;
    }

    @RecipeBuilderMethodDescription(field = "enchantability")
    public GroovyItem setEnchantability(int enchantability) {
        this.enchantability = enchantability;
        return this;
    }

    @RecipeBuilderMethodDescription(field = "rarity")
    public GroovyItem setRarity(IRarity rarity) {
        this.rarity = rarity;
        return this;
    }

    @RecipeBuilderRegistrationMethod(description = "groovyscript.wiki.minecraft.content.item.register.description")
    public GroovyItem register() {
        registerItem(this);
        return this;
    }

    @Override
    public int getItemEnchantability() {
        return this.enchantability;
    }

    @Override
    public boolean hasEffect(@NotNull ItemStack stack) {
        return this.effect || super.hasEffect(stack);
    }

    @Override
    public @NotNull IRarity getForgeRarity(@NotNull ItemStack stack) {
        return this.rarity == null ? super.getForgeRarity(stack) : this.rarity;
    }

    private static void checkModelFile(ResourceLocation loc) {
        File modelFile = FileUtil.makeFile(GroovyScript.getResourcesFile().getPath(), loc.getNamespace(), "models", "item", loc.getPath() + ".json");
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
