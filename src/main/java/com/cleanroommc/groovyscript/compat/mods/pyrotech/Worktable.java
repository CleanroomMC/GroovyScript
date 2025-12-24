package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.vanilla.ShapedCraftingRecipe;
import com.cleanroommc.groovyscript.compat.vanilla.ShapelessCraftingRecipe;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.AbstractCraftingRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.codetaylor.mc.pyrotech.ModPyrotech;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.WorktableRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

@RegistryDescription(
        admonition = @Admonition(
                value = "groovyscript.wiki.minecraft.crafting.note0",
                type = Admonition.Type.WARNING,
                format = Admonition.Format.STANDARD,
                hasTitle = true))
public class Worktable extends ForgeRegistryWrapper<WorktableRecipe> {

    public Worktable() {
        super(ModuleTechBasic.Registries.WORKTABLE_RECIPE);
    }

    @Override
    public boolean isEnabled() {
        return ModPyrotech.INSTANCE.isModuleEnabled(ModuleTechBasic.class);
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

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.crafting.addShaped0", example = @Example(value = "item('minecraft:gold_block'), item('minecraft:diamond_pickaxe'), 2, [[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],[null, null, null],[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')]]", commented = true))
    public void addShaped(ItemStack output, IIngredient tool, int damage, List<List<IIngredient>> input) {
        shapedBuilder()
                .tool(tool, damage)
                .matrix(input)
                .output(output)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.crafting.addShaped1", example = @Example(value = "'gold_v_to_clay', item('minecraft:clay'), item('minecraft:iron_pickaxe'), 3, [[item('minecraft:gold_ingot'),null,item('minecraft:gold_ingot')],[null,item('minecraft:gold_ingot'),null]]", commented = true))
    public void addShaped(String name, ItemStack output, IIngredient tool, int damage, List<List<IIngredient>> input) {
        shapedBuilder()
                .tool(tool, damage)
                .matrix(input)
                .output(output)
                .name(name)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.crafting.addShaped1", example = @Example(value = "resource('example:resource_location'), item('minecraft:clay'), item('minecraft:iron_shovel'), 2, [[item('minecraft:cobblestone')],[item('minecraft:nether_star')],[item('minecraft:cobblestone')]]", commented = true))
    public void addShaped(ResourceLocation name, ItemStack output, IIngredient tool, int damage, List<List<IIngredient>> input) {
        shapedBuilder()
                .tool(tool, damage)
                .matrix(input)
                .output(output)
                .name(name)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.crafting.addShapeless0", example = @Example(value = "item('minecraft:clay'), item('minecraft:stone_shovel'), 3, [item('minecraft:cobblestone'),item('minecraft:nether_star'),item('minecraft:gold_ingot')]", commented = true))
    public void addShapeless(ItemStack output, IIngredient tool, int damage, List<IIngredient> input) {
        shapelessBuilder()
                .tool(tool, damage)
                .input(input)
                .output(output)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.crafting.addShapeless1", example = @Example(value = "'precious_to_clay', item('minecraft:clay'), item('minecraft:iron_shovel'), 2, [item('minecraft:diamond'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')]", commented = true))
    public void addShapeless(String name, ItemStack output, IIngredient tool, int damage, List<IIngredient> input) {
        shapelessBuilder()
                .tool(tool, damage)
                .input(input)
                .output(output)
                .name(name)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.crafting.addShapeless1", example = @Example(value = "resource('example:resource_location2'), item('minecraft:clay'), item('minecraft:stone_shovel'), 3, [item('minecraft:cobblestone'), item('minecraft:gold_ingot')]", commented = true))
    public void addShapeless(ResourceLocation name, ItemStack output, IIngredient tool, int damage, List<IIngredient> input) {
        shapelessBuilder()
                .tool(tool, damage)
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

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.crafting.replaceShapeless1", example = @Example(value = "'minecraft:pink_dye_from_pink_tulip', item('minecraft:clay'), [item('minecraft:nether_star')]", commented = true))
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

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.crafting.replaceShapeless0", example = @Example(value = "item('minecraft:ender_eye'), item('minecraft:shears'), 3, [item('minecraft:ender_pearl'),item('minecraft:nether_star')]", commented = true))
    public void replaceShapeless(ItemStack output, IIngredient tool, int damage, List<IIngredient> input) {
        shapelessBuilder()
                .tool(tool, damage)
                .input(input)
                .output(output)
                .replace()
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.crafting.replaceShapeless1", example = @Example(value = "'minecraft:pink_dye_from_pink_tulip', item('minecraft:iron_axe'), 2, item('minecraft:clay'), [item('minecraft:nether_star')]", commented = true))
    public void replaceShapeless(String name, ItemStack output, IIngredient tool, int damage, List<IIngredient> input) {
        shapelessBuilder()
                .tool(tool, damage)
                .input(input)
                .output(output)
                .name(name)
                .replaceByName()
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.crafting.replaceShapeless1", example = @Example(value = "resource('minecraft:pink_dye_from_peony'), item('minecraft:clay'), item('minecraft:stone_axe'), 2, [item('minecraft:cobblestone'), item('minecraft:gold_ingot')]", commented = true))
    public void replaceShapeless(ResourceLocation name, ItemStack output, IIngredient tool, int damage, List<IIngredient> input) {
        shapelessBuilder()
                .tool(tool, damage)
                .input(input)
                .output(output)
                .name(name)
                .replaceByName()
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.crafting.replaceShaped0", example = @Example(value = "item('minecraft:chest'), item('minecraft:iron_axe') | item('minecraft:stone_axe'), 3, [[ore('logWood'),ore('logWood'),ore('logWood')],[ore('logWood'),null,ore('logWood')],[ore('logWood'),ore('logWood'),ore('logWood')]]", commented = true))
    public void replaceShaped(ItemStack output, IIngredient tool, int damage, List<List<IIngredient>> input) {
        shapedBuilder()
                .tool(tool, damage)
                .matrix(input)
                .output(output)
                .replace()
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.crafting.replaceShaped1", example = @Example(value = "'gold_to_diamonds', item('minecraft:diamond') * 8, item('minecraft:diamond_pickaxe'), 4, [[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],[item('minecraft:gold_ingot'),null,item('minecraft:gold_ingot')],[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')]]", commented = true))
    public void replaceShaped(String name, ItemStack output, IIngredient tool, int damage, List<List<IIngredient>> input) {
        shapedBuilder()
                .tool(tool, damage)
                .matrix(input)
                .output(output)
                .name(name)
                .replaceByName()
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.crafting.replaceShaped1", example = @Example(value = "resource('minecraft:sea_lantern'), item('minecraft:diamond_pickaxe'), 3, item('minecraft:clay'), [[item('minecraft:glowstone')],[item('minecraft:glowstone')],[item('minecraft:glowstone')]]", commented = true))
    public void replaceShaped(ResourceLocation name, ItemStack output, IIngredient tool, int damage, List<List<IIngredient>> input) {
        shapedBuilder()
                .tool(tool, damage)
                .matrix(input)
                .output(output)
                .name(name)
                .replaceByName()
                .register();
    }

    @MethodDescription(example = @Example("item('pyrotech:iron_hunters_knife')"))
    public void removeByOutput(IIngredient output) {
        removeByOutput(output, true);
    }

    @GroovyBlacklist
    public void removeByOutput(IIngredient output, boolean log) {
        if (IngredientHelper.isEmpty(output)) {
            if (log) {
                GroovyLog.msg("Error removing Pyrotech Worktable recipe")
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
                GroovyLog.msg("Error removing Pyrotech Worktable recipe")
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

    @GroovyBlacklist
    public void removeByInput(IIngredient input, boolean log) {
        if (IngredientHelper.isEmpty(input)) {
            if (log) {
                GroovyLog.msg("Error removing Pyrotech Worktable recipe")
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
                GroovyLog.msg("Error removing Pyrotech Worktable recipe")
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
            @Example(".name('irons_to_dirts').output(item('minecraft:dirt') * 8).shape([[item('minecraft:iron_ingot'),item('minecraft:iron_ingot'),item('minecraft:iron_ingot')],[item('minecraft:iron_ingot'),null,item('minecraft:iron_ingot')],[item('minecraft:iron_ingot'),item('minecraft:iron_ingot'),item('minecraft:iron_ingot')]]).replaceByName()"),
            @Example(".name(resource('minecraft:sea_lantern')).output(item('minecraft:clay')).shape([[ore('blockRedstone')],[ore('blockRedstone')],[ore('blockRedstone')]]).replaceByName()"),
            @Example(".output(item('minecraft:nether_star')).row('TXT').row('X X').row('!X!').key('T', item('minecraft:gravel')).key('X', item('minecraft:clay').reuse()).key('!', item('minecraft:gunpowder').transform({ _ -> item('minecraft:diamond') })).tool(item('minecraft:diamond_sword'), 5)"),
            @Example(".output(item('minecraft:clay_ball') * 3).shape('S S', ' G ', 'SWS').key([S: ore('ingotIron').reuse(), G: ore('gemDiamond'), W: fluid('water') * 1000]).tool(item('minecraft:diamond_axe'), 3)"),
            @Example(".name('gold_duplication_with_tnt').output(item('minecraft:gold_block')).row('!!!').row('!S!').row('!!!').key([S: ore('blockGold').reuse(), '!': item('minecraft:tnt').transform(item('minecraft:diamond'))]).tool(item('minecraft:iron_shovel'), 2)"),
            @Example(".output(item('minecraft:clay')).row(' B').key('B', item('minecraft:glass_bottle')).tool(item('minecraft:stone_sword'), 3)"),
            @Example(".output(item('minecraft:clay')).row(' 1 ').row(' 0 ').row(' 1 ').key('1', item('minecraft:iron_sword')).key('0', item('minecraft:diamond_sword').withNbt([display:[Name:'Sword with Specific NBT data']])).tool(item('minecraft:iron_axe'), 4)"),
    })
    public Shaped shapedBuilder() {
        return new Shaped();
    }

    @RecipeBuilderDescription(example = {
            @Example(".output(item('minecraft:string')).input([item('minecraft:cobblestone'),item('minecraft:feather'),item('minecraft:gold_ingot')])"),
            @Example(".name('precious_to_clay').output(item('minecraft:clay')).input([item('minecraft:emerald'),item('minecraft:iron_ore'),item('minecraft:gold_ingot')])"),
            @Example(".name(resource('example:resource_location2')).output(item('minecraft:stone')).input([item('minecraft:gold_ore'), item('minecraft:gold_ingot')])"),
            @Example(".output(item('minecraft:ender_eye')).input([item('minecraft:ender_pearl'),item('minecraft:bowl')]).replace().tool(item('minecraft:iron_sword'), 4)"),
            @Example(".name('minecraft:pink_dye_from_pink_tulip').output(item('minecraft:clay')).input([item('minecraft:stick')]).replaceByName().tool(item('minecraft:iron_pickaxe'), 2)"),
            @Example(".name(resource('minecraft:pink_dye_from_peony')).output(item('minecraft:coal')).input([item('minecraft:stone'), item('minecraft:iron_ingot')]).replaceByName().tool(item('minecraft:stone_axe'), 2)"),
    })
    public Shapeless shapelessBuilder() {
        return new Shapeless();
    }

    @Property(property = "mirrored")
    @Property(property = "recipeFunction")
    @Property(property = "recipeAction")
    @Property(property = "name")
    @Property(property = "replace")
    public static class Shaped extends AbstractCraftingRecipeBuilder.AbstractShaped<WorktableRecipe> {

        @Property(comp = @Comp(not = "null"))
        private IIngredient tool = IIngredient.EMPTY;
        @Property(comp = @Comp(gte = 0))
        private int damage;

        public Shaped() {
            super(3, 3);
        }

        @RecipeBuilderMethodDescription(field = {
                "tool", "damage"
        })
        public Shaped tool(IIngredient tool, int damage) {
            this.tool = tool;
            this.damage = damage;
            return this;
        }

        @Override
        protected void handleReplace() {
            if (replace == 1) {
                ModSupport.PYROTECH.get().worktable.removeByOutput(IngredientHelper.toIIngredient(output), false);
            } else if (replace == 2) {
                if (super.name == null) {
                    GroovyLog.msg("Error replacing Pyrotech Worktable recipe")
                            .add("Name must not be null when replacing by name")
                            .error()
                            .post();
                    return;
                }
                ReloadableRegistryManager.removeRegistryEntry(ModSupport.PYROTECH.get().worktable.getRegistry(), super.name);
            }
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_worktable_shaped";
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public WorktableRecipe register() {
            validateName();
            GroovyLog.Msg msg = GroovyLog.msg("Error adding Pyrotech Worktable Shaped recipe '{}'", this.name)
                    .error()
                    .add((keyBasedMatrix == null || keyBasedMatrix.length == 0) && (ingredientMatrix == null || ingredientMatrix.isEmpty()), () -> "No matrix was defined")
                    .add(keyBasedMatrix != null && ingredientMatrix != null, () -> "A key based matrix AND a ingredient based matrix was defined. This is not allowed!")
                    .add(IngredientHelper.isEmpty(this.output), () -> "Output must not be empty")
                    .add(tool == null, "Tool must not be null")
                    .add(damage < 0, "Damage must not be a negative integer, yet it was {}", damage);
            if (msg.postIfNotEmpty()) return null;

            ShapedCraftingRecipe recipe = null;
            if (keyBasedMatrix != null) {
                recipe = validateShape(msg, errors, keyBasedMatrix, keyMap, ((width1, height1, ingredients) -> new ShapedCraftingRecipe(output, ingredients, width1, height1, mirrored, recipeFunction, recipeAction)));
            } else if (ingredientMatrix != null) {
                recipe = validateShape(msg, ingredientMatrix, ((width1, height1, ingredients) -> new ShapedCraftingRecipe(output.copy(), ingredients, width1, height1, mirrored, recipeFunction, recipeAction)));
            }

            if (recipe != null) {
                handleReplace();
                WorktableRecipe rec = ModSupport.PYROTECH.get().worktable.getRegistry().getValue(this.name);
                msg.add(rec != null && rec.getRecipe().canFit(1000, 1000), () -> "a recipe with that name already exists! Either replace or remove the recipe first");
                if (msg.postIfNotEmpty()) return null;
                WorktableRecipe worktableRecipe = new WorktableRecipe(recipe, tool == IIngredient.EMPTY ? null : tool.toMcIngredient(), damage, null).setRegistryName(super.name);
                ModSupport.PYROTECH.get().worktable.add(worktableRecipe);
                return worktableRecipe;
            }

            return null;
        }
    }

    @Property(property = "recipeFunction")
    @Property(property = "recipeAction")
    @Property(property = "name")
    @Property(property = "replace")
    public static class Shapeless extends AbstractCraftingRecipeBuilder.AbstractShapeless<WorktableRecipe> {

        @Property
        private IIngredient tool = IIngredient.EMPTY;
        @Property(comp = @Comp(gte = 0))
        private int damage;

        public Shapeless() {
            super(3, 3);
        }

        @RecipeBuilderMethodDescription(field = {
                "tool", "damage"
        })
        public Shapeless tool(IIngredient tool, int damage) {
            this.tool = tool;
            this.damage = damage;
            return this;
        }

        @Override
        protected void handleReplace() {
            if (replace == 1) {
                ModSupport.PYROTECH.get().worktable.removeByOutput(IngredientHelper.toIIngredient(output), false);
            } else if (replace == 2) {
                if (super.name == null) {
                    GroovyLog.msg("Error replacing Pyrotech Worktable recipe")
                            .add("Name must not be null when replacing by name")
                            .error()
                            .post();
                    return;
                }
                ReloadableRegistryManager.removeRegistryEntry(ModSupport.PYROTECH.get().worktable.getRegistry(), super.name);
            }
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_worktable_shapeless_";
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public WorktableRecipe register() {
            validateName();
            IngredientHelper.trim(ingredients);
            GroovyLog.Msg msg = GroovyLog.msg("Error adding Pyrotech Worktable Shapeless recipe '{}'", this.name);
            if (msg.add(IngredientHelper.isEmpty(this.output), () -> "Output must not be empty")
                    .add(ingredients.isEmpty(), () -> "inputs must not be empty")
                    .add(ingredients.size() > width * height, () -> "maximum inputs are " + (width * height) + " but found " + ingredients.size())
                    .add(tool == null, "Tool must not be null")
                    .add(damage < 0, "Damage must not be a negative integer, yet it was {}", damage)
                    .error()
                    .postIfNotEmpty()) {
                return null;
            }
            handleReplace();
            WorktableRecipe rec = ModSupport.PYROTECH.get().worktable.getRegistry().getValue(this.name);
            msg.add(rec != null && rec.getRecipe().canFit(1000, 1000), () -> "a recipe with that name already exists! Either replace or remove the recipe first");
            if (msg.postIfNotEmpty()) return null;
            ShapelessCraftingRecipe recipe = new ShapelessCraftingRecipe(output.copy(), ingredients, recipeFunction, recipeAction);
            rec = new WorktableRecipe(recipe, tool == IIngredient.EMPTY ? null : tool.toMcIngredient(), damage, null);
            ReloadableRegistryManager.addRegistryEntry(ModSupport.PYROTECH.get().worktable.getRegistry(), super.name, rec);
            return rec;
        }
    }
}
