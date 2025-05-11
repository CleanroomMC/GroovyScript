package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

@RegistryDescription(admonition = {
        @Admonition(type = Admonition.Type.TIP, value = "groovyscript.wiki.minecraft.crafting.note0"),
        @Admonition(type = Admonition.Type.NOTE, value = "groovyscript.wiki.minecraft.crafting.note1"),
        @Admonition(type = Admonition.Type.TIP, value = "groovyscript.wiki.minecraft.crafting.note2"),
}, override = @MethodOverride(method = {
        @MethodDescription(method = "remove(Lnet/minecraft/util/ResourceLocation;)V", example = @Example("resource('minecraft:stonebrick')")),
        @MethodDescription(method = "remove(Ljava/lang/String;)V", example = @Example("'minecraft:mossy_stonebrick'")),
}))
public class Crafting extends ForgeRegistryWrapper<IRecipe> {

    private static final Char2ObjectOpenHashMap<IIngredient> fallbackChars = new Char2ObjectOpenHashMap<>();

    public Crafting() {
        super(ForgeRegistries.RECIPES);
    }

    @GroovyBlacklist
    public static IIngredient getFallback(char c) {
        return fallbackChars.get(c);
    }

    @MethodDescription(type = MethodDescription.Type.VALUE)
    public void setFallback(char key, IIngredient ingredient) {
        fallbackChars.put(key, ingredient);
    }

    @MethodDescription(type = MethodDescription.Type.VALUE)
    public void setFallback(String key, IIngredient ingredient) {
        if (key == null || key.length() != 1) {
            GroovyLog.get().error("Fallback key must be a single character");
            return;
        }
        fallbackChars.put(key.charAt(0), ingredient);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.crafting.addShaped0", example = @Example(value = "item('minecraft:gold_block'), [[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],[null, null, null],[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')]]", commented = true))
    public void addShaped(ItemStack output, List<List<IIngredient>> input) {
        shapedBuilder()
                .matrix(input)
                .output(output)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.crafting.addShaped1", example = @Example(value = "'gold_v_to_clay', item('minecraft:clay'), [[item('minecraft:gold_ingot'),null,item('minecraft:gold_ingot')],[null,item('minecraft:gold_ingot'),null]]", commented = true))
    public void addShaped(String name, ItemStack output, List<List<IIngredient>> input) {
        shapedBuilder()
                .matrix(input)
                .output(output)
                .name(name)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.crafting.addShaped1", example = @Example(value = "resource('example:resource_location'), item('minecraft:clay'), [[item('minecraft:cobblestone')],[item('minecraft:nether_star')],[item('minecraft:cobblestone')]]", commented = true))
    public void addShaped(ResourceLocation name, ItemStack output, List<List<IIngredient>> input) {
        shapedBuilder()
                .matrix(input)
                .output(output)
                .name(name)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.crafting.addShapeless0", example = @Example(value = "item('minecraft:clay'), [item('minecraft:cobblestone'),item('minecraft:nether_star'),item('minecraft:gold_ingot')]", commented = true))
    public void addShapeless(ItemStack output, List<IIngredient> input) {
        shapelessBuilder()
                .input(input)
                .output(output)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.crafting.addShapeless1", example = @Example(value = "'precious_to_clay', item('minecraft:clay'), [item('minecraft:diamond'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')]", commented = true))
    public void addShapeless(String name, ItemStack output, List<IIngredient> input) {
        shapelessBuilder()
                .input(input)
                .output(output)
                .name(name)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.crafting.addShapeless1", example = @Example(value = "resource('example:resource_location2'), item('minecraft:clay'), [item('minecraft:cobblestone'), item('minecraft:gold_ingot')]", commented = true))
    public void addShapeless(ResourceLocation name, ItemStack output, List<IIngredient> input) {
        shapelessBuilder()
                .input(input)
                .output(output)
                .name(name)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.crafting.replaceShapeless0", example = @Example(value = "item('minecraft:ender_eye'), [item('minecraft:ender_pearl'),item('minecraft:nether_star')]", commented = true))
    public void replaceShapeless(ItemStack output, List<IIngredient> input) {
        shapelessBuilder()
                .input(input)
                .output(output)
                .replace()
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.crafting.replaceShapeless1", example = @Example(value = "'minecraft:pink_dye_from_pink_tulp', item('minecraft:clay'), [item('minecraft:nether_star')]", commented = true))
    public void replaceShapeless(String name, ItemStack output, List<IIngredient> input) {
        shapelessBuilder()
                .input(input)
                .output(output)
                .name(name)
                .replaceByName()
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.crafting.replaceShapeless1", example = @Example(value = "resource('minecraft:pink_dye_from_peony'), item('minecraft:clay'), [item('minecraft:cobblestone'), item('minecraft:gold_ingot')]", commented = true))
    public void replaceShapeless(ResourceLocation name, ItemStack output, List<IIngredient> input) {
        shapelessBuilder()
                .input(input)
                .output(output)
                .name(name)
                .replaceByName()
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.crafting.replaceShaped0", example = @Example(value = "item('minecraft:chest'), [[ore('logWood'),ore('logWood'),ore('logWood')],[ore('logWood'),null,ore('logWood')],[ore('logWood'),ore('logWood'),ore('logWood')]]", commented = true))
    public void replaceShaped(ItemStack output, List<List<IIngredient>> input) {
        shapedBuilder()
                .matrix(input)
                .output(output)
                .replace()
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.crafting.replaceShaped1", example = @Example(value = "'gold_to_diamonds', item('minecraft:diamond') * 8, [[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],[item('minecraft:gold_ingot'),null,item('minecraft:gold_ingot')],[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')]]", commented = true))
    public void replaceShaped(String name, ItemStack output, List<List<IIngredient>> input) {
        shapedBuilder()
                .matrix(input)
                .output(output)
                .name(name)
                .replaceByName()
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.crafting.replaceShaped1", example = @Example(value = "resource('minecraft:sea_lantern'), item('minecraft:clay'), [[item('minecraft:glowstone')],[item('minecraft:glowstone')],[item('minecraft:glowstone')]]", commented = true))
    public void replaceShaped(ResourceLocation name, ItemStack output, List<List<IIngredient>> input) {
        shapedBuilder()
                .matrix(input)
                .output(output)
                .name(name)
                .replaceByName()
                .register();
    }

    @MethodDescription(example = @Example("item('minecraft:gold_ingot')"))
    public void removeByOutput(IIngredient output) {
        removeByOutput(output, true);
    }

    public void removeByOutput(IIngredient output, boolean log) {
        if (IngredientHelper.isEmpty(output)) {
            if (log) {
                GroovyLog.msg("Error removing Minecraft Crafting recipe")
                        .add("Output must not be empty")
                        .error()
                        .post();
            }
            return;
        }
        List<ResourceLocation> recipesToRemove = new ArrayList<>();
        for (IRecipe recipe : ForgeRegistries.RECIPES) {
            if (recipe.getRegistryName() != null && output.test(recipe.getRecipeOutput())) {
                recipesToRemove.add(recipe.getRegistryName());
            }
        }
        if (recipesToRemove.isEmpty()) {
            if (log) {
                GroovyLog.msg("Error removing Minecraft Crafting recipe")
                        .add("No recipes found for {}", output)
                        .error()
                        .post();
            }
            return;
        }
        for (ResourceLocation rl : recipesToRemove) {
            ReloadableRegistryManager.removeRegistryEntry(ForgeRegistries.RECIPES, rl);
        }
    }

    @MethodDescription
    public void removeByInput(IIngredient input) {
        removeByInput(input, true);
    }

    public void removeByInput(IIngredient input, boolean log) {
        if (IngredientHelper.isEmpty(input)) {
            if (log) {
                GroovyLog.msg("Error removing Minecraft Crafting recipe")
                        .add("Input must not be empty")
                        .error()
                        .post();
            }
            return;
        }
        List<ResourceLocation> recipesToRemove = new ArrayList<>();
        for (IRecipe recipe : ForgeRegistries.RECIPES) {
            if (recipe.getRegistryName() != null && !recipe.getIngredients().isEmpty() && recipe.getIngredients().stream().anyMatch(i -> i.getMatchingStacks().length > 0 && input.test(i.getMatchingStacks()[0]))) {
                recipesToRemove.add(recipe.getRegistryName());
            }
        }
        if (recipesToRemove.isEmpty()) {
            if (log) {
                GroovyLog.msg("Error removing Minecraft Crafting recipe")
                        .add("No recipes found for {}", input)
                        .error()
                        .post();
            }
            return;
        }
        for (ResourceLocation location : recipesToRemove) {
            ReloadableRegistryManager.removeRegistryEntry(ForgeRegistries.RECIPES, location);
        }
    }

    @RecipeBuilderDescription(example = {
            @Example(".output(item('minecraft:nether_star')).row('TXT').row('X X').row('!X!').key('T', item('minecraft:tnt')).key('X', item('minecraft:clay').reuse()).key('!', item('minecraft:tnt').transform({ _ -> item('minecraft:diamond') }))"),
            @Example(".output(item('minecraft:clay_ball') * 3).shape('S S', ' G ', 'SWS').key([S: ore('netherStar').reuse(), G: ore('ingotGold'), W: fluid('water') * 1000])"),
            @Example(".name('nether_star_duplication_with_tnt').output(item('minecraft:nether_star')).row('!!!').row('!S!').row('!!!').key([S: ore('netherStar').reuse(), '!': item('minecraft:tnt').transform(item('minecraft:diamond'))])"),
            @Example(".output(item('minecraft:clay')).row(' B').key('B', item('minecraft:glass_bottle'))"),
            @Example(".output(item('minecraft:clay')).row('   ').row(' 0 ').row('   ').key('0', item('minecraft:diamond_sword').withNbt([display:[Name:'Sword with Specific NBT data']]))"),
            @Example(".output(item('minecraft:gold_block')).shape([[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],[null, null, null],[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')]])"),
            @Example(".name('gold_v_to_clay').output(item('minecraft:clay')).shape([[item('minecraft:gold_ingot'),null,item('minecraft:gold_ingot')],[null,item('minecraft:stone_pickaxe').transformDamage(2).whenAnyDamage(),null]])"),
            @Example(".name(resource('example:resource_location')).output(item('minecraft:clay')).shape([[item('minecraft:cobblestone')],[item('minecraft:nether_star')],[item('minecraft:cobblestone')]])"),
            @Example(".output(item('minecraft:chest')).shape([[ore('logWood'),ore('logWood'),ore('logWood')],[ore('logWood'),null,ore('logWood')],[ore('logWood'),ore('logWood'),ore('logWood')]]).replace()"),
            @Example(".name('gold_to_diamonds').output(item('minecraft:diamond') * 8).shape([[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],[item('minecraft:gold_ingot'),null,item('minecraft:gold_ingot')],[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')]]).replaceByName()"),
            @Example(".name(resource('minecraft:sea_lantern')).output(item('minecraft:clay')).shape([[item('minecraft:glowstone')],[item('minecraft:glowstone')],[item('minecraft:glowstone')]]).replaceByName()"),
    })
    public CraftingRecipeBuilder.Shaped shapedBuilder() {
        return new CraftingRecipeBuilder.Shaped();
    }

    @RecipeBuilderDescription(example = {
            @Example(".output(item('minecraft:clay')).input([item('minecraft:cobblestone'),item('minecraft:nether_star'),item('minecraft:gold_ingot')])"),
            @Example(".name('precious_to_clay').output(item('minecraft:clay')).input([item('minecraft:diamond'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')])"),
            @Example(".name(resource('example:resource_location2')).output(item('minecraft:clay')).input([item('minecraft:cobblestone'), item('minecraft:gold_ingot')])"),
            @Example(".output(item('minecraft:ender_eye')).input([item('minecraft:ender_pearl'),item('minecraft:nether_star')]).replace()"),
            @Example(".name('minecraft:pink_dye_from_pink_tulp').output(item('minecraft:clay')).input([item('minecraft:nether_star')]).replaceByName()"),
            @Example(".name(resource('minecraft:pink_dye_from_peony')).output(item('minecraft:clay')).input([item('minecraft:cobblestone'), item('minecraft:gold_ingot')]).replaceByName()"),
    })
    public CraftingRecipeBuilder.Shapeless shapelessBuilder() {
        return new CraftingRecipeBuilder.Shapeless();
    }
}
