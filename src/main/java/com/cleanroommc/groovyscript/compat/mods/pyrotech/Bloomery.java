package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.pyrotech.BloomeryRecipeBaseAccessor;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.codetaylor.mc.pyrotech.ModPyrotech;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.AnvilRecipe;
import com.codetaylor.mc.pyrotech.modules.tech.bloomery.ModuleTechBloomery;
import com.codetaylor.mc.pyrotech.modules.tech.bloomery.ModuleTechBloomeryConfig;
import com.codetaylor.mc.pyrotech.modules.tech.bloomery.init.recipe.WitherForgeRecipesAdd;
import com.codetaylor.mc.pyrotech.modules.tech.bloomery.recipe.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@RegistryDescription
public class Bloomery extends ForgeRegistryWrapper<BloomeryRecipe> {

    public Bloomery() {
        super(ModuleTechBloomery.Registries.BLOOMERY_RECIPE);
    }

    @Override
    public boolean isEnabled() {
        return ModPyrotech.INSTANCE.isModuleEnabled(ModuleTechBloomery.class);
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:iron_block')).bloom(item('minecraft:apple')).failureChance(0.0F).slag(item('minecraft:carrot')).inherit(true).name('metal_vegetation')"),
            @Example(".input(item('minecraft:noteblock')).output(item('minecraft:record_13')).experience(0.25F).tierIronclad().bloomYield(1, 1).burnTime(2000).failureChance(0.5F).failureOutput(item('minecraft:record_11'), 1).inherit(true).name('recipe_for_soundphiles')"),
            @Example(".input(item('minecraft:sponge')).output(item('minecraft:sponge')).bloomYield(2, 5).typePickaxe().langKey(item('minecraft:stick').getTranslationKey()).inherit(true).name('sponge_duplication')"),
            @Example(".input(item('minecraft:birch_boat')).bloom(item('minecraft:dark_oak_boat')).tierObsidian().failureChance(0.1).failureOutput(item('minecraft:spruce_boat'), 5).failureOutput(item('minecraft:jungle_boat'), 2).failureOutput(item('minecraft:boat'), 1).name('boat_smelting')"),
            @Example(".input(item('minecraft:sand')).output(item('minecraft:glass')).bloomYield(3, 5).experience(0.1F).burnTime(4000).tierGranite().tierObsidian().anvilHit(2).typePickaxe().failureChance(0.05).failureOutput(item('minecraft:nether_star'), 1).failureOutput(item('minecraft:gold_ingot'), 10).name('glasswork')")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.pyrotech.bloomery.add0")
    public BloomeryRecipe add(String name, ItemStack output, IIngredient input) {
        return add(name, output, input, false);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.pyrotech.bloomery.add0.inherit", example = @Example("'loreming_the_ipsum', item('minecraft:redstone'), item('minecraft:lava_bucket'), false"))
    public BloomeryRecipe add(String name, ItemStack output, IIngredient input, boolean inherit) {
        return recipeBuilder()
                .inherit(inherit)
                .bloom(output)
                .input(input)
                .name(name)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.pyrotech.bloomery.add1")
    public BloomeryRecipe add(String name, ItemStack output, IIngredient input, int burnTime) {
        return add(name, output, input, burnTime, false);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.pyrotech.bloomery.add1.inherit", example = @Example("'cooking_a_story', item('minecraft:written_book'), item('minecraft:book'), 200, true"))
    public BloomeryRecipe add(String name, ItemStack output, IIngredient input, int burnTime, boolean inherit) {
        return recipeBuilder()
                .inherit(inherit)
                .bloom(output)
                .burnTime(burnTime)
                .input(input)
                .name(name)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.pyrotech.bloomery.addBloom0")
    public BloomeryRecipe addBloom(String name, ItemStack bloomOutput, IIngredient input) {
        return addBloom(name, bloomOutput, input, false);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.pyrotech.bloomery.addBloom0.inherit", example = @Example("'cyanide', item('minecraft:poisonous_potato'), item('minecraft:potato'), true"))
    public BloomeryRecipe addBloom(String name, ItemStack bloomOutput, IIngredient input, boolean inherit) {
        return recipeBuilder()
                .inherit(inherit)
                .output(bloomOutput)
                .input(input)
                .name(name)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example(value = "item('minecraft:iron_nugget')", commented = true))
    public void removeByOutput(IIngredient output) {
        if (GroovyLog.msg("Error removing bloomery recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (BloomeryRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        }
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("item('minecraft:gold_ore')"))
    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing bloomery recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (BloomeryRecipe recipe : getRegistry()) {
            if (recipe.getInput().test(input)) {
                remove(recipe);
            }
        }
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<BloomeryRecipe> {

        @Property
        private ItemStack bloom = ItemStack.EMPTY;
        @Property
        private ItemStack slag;
        @Property(comp = @Comp(gt = 0), defaultValue = "21600")
        private int burnTime = 21600;
        @Property(comp = @Comp(gte = 0))
        private float experience;
        @Property(comp = @Comp(gte = 0), defaultValue = "12")
        private int bloomYieldMin = 12;
        @Property(comp = @Comp(gte = 0), defaultValue = "15")
        private int bloomYieldMax = 15;
        @Property(comp = @Comp(gte = 0, lte = 1), defaultValue = "0.25")
        private float failureChance = 0.25F;
        @Property
        private final List<BloomeryRecipeBase.FailureItem> failureOutput = new ArrayList<>(1);
        @Property
        private final EnumSet<AnvilRecipe.EnumTier> anvilTiers = EnumSet.noneOf(AnvilRecipe.EnumTier.class);
        @Property(comp = @Comp(gt = 0), defaultValue = "ModuleTechBloomeryConfig.BLOOM.HAMMER_HITS_IN_ANVIL_REQUIRED")
        private int anvilHit = ModuleTechBloomeryConfig.BLOOM.HAMMER_HITS_IN_ANVIL_REQUIRED;
        @Property(comp = @Comp(not = "null"), defaultValue = "hammer")
        private AnvilRecipe.EnumType anvilType = AnvilRecipe.EnumType.HAMMER;
        @Property
        private String langKey;
        @Property
        private boolean inherit;

        @RecipeBuilderMethodDescription
        public RecipeBuilder bloom(ItemStack bloom) {
            this.bloom = bloom;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder slag(ItemStack slag) {
            this.slag = slag;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder burnTime(int burnTime) {
            this.burnTime = burnTime;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder experience(float experience) {
            this.experience = experience;
            return this;
        }

        @RecipeBuilderMethodDescription(field = {
                "bloomYieldMin", "bloomYieldMax"
        })
        public RecipeBuilder bloomYield(int bloomYieldMin, int bloomYieldMax) {
            this.bloomYieldMin = bloomYieldMin;
            this.bloomYieldMax = bloomYieldMax;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder failureChance(float failureChance) {
            this.failureChance = failureChance;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder failureOutput(ItemStack failureOutput, int weight) {
            this.failureOutput.add(new BloomeryRecipeBase.FailureItem(failureOutput == null ? ItemStack.EMPTY : failureOutput, weight));
            return this;
        }

        @RecipeBuilderMethodDescription(field = "anvilTiers")
        public RecipeBuilder anvilTier(AnvilRecipe.EnumTier tier) {
            anvilTiers.add(tier);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "anvilTiers")
        public RecipeBuilder tierGranite() {
            return anvilTier(AnvilRecipe.EnumTier.GRANITE);
        }

        @RecipeBuilderMethodDescription(field = "anvilTiers")
        public RecipeBuilder tierIronclad() {
            return anvilTier(AnvilRecipe.EnumTier.IRONCLAD);
        }

        @RecipeBuilderMethodDescription(field = "anvilTiers")
        public RecipeBuilder tierObsidian() {
            return anvilTier(AnvilRecipe.EnumTier.OBSIDIAN);
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder anvilType(AnvilRecipe.EnumType type) {
            anvilType = type;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "anvilType")
        public RecipeBuilder typeHammer() {
            return anvilType(AnvilRecipe.EnumType.HAMMER);
        }

        @RecipeBuilderMethodDescription(field = "anvilType")
        public RecipeBuilder typePickaxe() {
            return anvilType(AnvilRecipe.EnumType.PICKAXE);
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder anvilHit(int hit) {
            this.anvilHit = hit;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder langKey(String langKey) {
            this.langKey = langKey;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder inherit(boolean inherit) {
            this.inherit = inherit;
            return this;
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_bloomery_";
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Bloomery Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateName();
            if (bloom == null) {
                bloom = ItemStack.EMPTY;
            }
            int minOutput = bloom.isEmpty() ? 1 : 0;
            if (slag == null) {
                slag = bloom.isEmpty() ? new ItemStack(ModuleTechBloomery.Items.SLAG, 4) : ItemStack.EMPTY;
            }
            if (failureOutput.isEmpty() && failureChance > 0.0F) {
                failureOutput.add(new BloomeryRecipeBase.FailureItem(new ItemStack(ModuleTechBloomery.Items.SLAG), 1));
            }
            if (anvilTiers.isEmpty()) {
                anvilTiers.addAll(EnumSet.allOf(AnvilRecipe.EnumTier.class));
            }
            validateItems(msg, 1, 1, minOutput, 1);
            msg.add(burnTime <= 0, "burnTime must be a non negative integer that is larger than 0, yet it was {}", burnTime);
            msg.add(experience < 0, "experience must be a non negative float, yet it was {}", experience);
            msg.add(bloomYieldMin < 0 || bloomYieldMin > bloomYieldMax, "bloomYieldMin must be a non negative integer that is smaller than bloomYieldMax, yet it was {}", burnTime);
            msg.add(bloomYieldMax < 0, "bloomYieldMax must be a non negative integer, yet it was {}", burnTime);
            msg.add(failureChance < 0 || failureChance > 1, "failureChance must not be negative nor larger than 1.0, yet it was {}", failureChance);
            msg.add(anvilType == null, "anvilType must not be null");
            msg.add(anvilHit <= 0, "anvilHit must be a non negative integer that is larger than 0, yet it was {}", anvilHit);
            msg.add(ModSupport.PYROTECH.get().bloomery.getRegistry().getValue(super.name) != null, "tried to register {}, but it already exists", super.name);
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable BloomeryRecipe register() {
            if (!validate()) return null;
            BloomeryRecipeBuilder builder = new BloomeryRecipeBuilder(super.name, output.getOrEmpty(0), input.get(0).toMcIngredient());
            failureOutput.forEach(i -> builder.addFailureItem(i.getItemStack(), i.getWeight()));
            BloomeryRecipe recipe = builder
                    .setSlagItem(slag, slag.getCount())
                    .setBurnTimeTicks(burnTime)
                    .setExperience(experience)
                    .setBloomYield(bloomYieldMin, bloomYieldMax)
                    .setFailureChance(failureChance)
                    .setAnvilTiers(anvilTiers.toArray(new AnvilRecipe.EnumTier[0]))
                    .setLangKey(langKey)
                    .create();
            ((BloomeryRecipeBaseAccessor) recipe).grs$setOutputBloom(bloom.isEmpty() ? null : bloom);
            ModSupport.PYROTECH.get().bloomery.add(recipe);
            if (inherit) {
                WitherForgeRecipe witherForgeRecipe = WitherForgeRecipesAdd.INHERIT_TRANSFORMER.apply(recipe).setRegistryName(new ResourceLocation(super.name.getNamespace(), "bloomery/" + super.name.getPath()));
                ((BloomeryRecipeBaseAccessor) recipe).grs$setOutputBloom(((BloomeryRecipeBaseAccessor) recipe).grs$getOutputBloom());
                ModSupport.PYROTECH.get().witherForge.add(witherForgeRecipe);
            }
            if (bloom.isEmpty() && bloomYieldMax != 0) {
                ModSupport.PYROTECH.get().anvil.add(
                        new BloomAnvilRecipe(
                                recipe.getOutput(),
                                com.codetaylor.mc.athenaeum.util.IngredientHelper.fromStackWithNBT(recipe.getOutputBloom()),
                                anvilHit,
                                anvilType,
                                recipe.getAnvilTiers(),
                                recipe
                        ).setRegistryName(super.name));
            }
            return recipe;
        }
    }
}
