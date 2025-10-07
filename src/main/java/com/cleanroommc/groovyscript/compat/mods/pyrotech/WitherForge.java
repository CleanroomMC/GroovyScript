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
import com.codetaylor.mc.pyrotech.modules.tech.bloomery.recipe.BloomAnvilRecipe;
import com.codetaylor.mc.pyrotech.modules.tech.bloomery.recipe.BloomeryRecipeBase;
import com.codetaylor.mc.pyrotech.modules.tech.bloomery.recipe.WitherForgeRecipe;
import com.codetaylor.mc.pyrotech.modules.tech.bloomery.recipe.WitherForgeRecipeBuilder;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

@RegistryDescription(
        admonition = @Admonition(
                value = "groovyscript.wiki.pyrotech.wither_forge.note0",
                format = Admonition.Format.STANDARD,
                hasTitle = true))
public class WitherForge extends ForgeRegistryWrapper<WitherForgeRecipe> {

    public WitherForge() {
        super(ModuleTechBloomery.Registries.WITHER_FORGE_RECIPE);
    }

    @Override
    public boolean isEnabled() {
        return ModPyrotech.INSTANCE.isModuleEnabled(ModuleTechBloomery.class);
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:minecart')).output(item('minecraft:furnace_minecart')).slag(item('minecraft:iron_ingot')).experience(0.8F).bloomYield(1, 1).burnTime(2000).failureChance(0.5F).failureOutput(item('minecraft:tnt_minecart'), 1).name('minecart_smelting')"),
            @Example(".input(item('minecraft:fishing_rod') | item('minecraft:carrot_on_a_stick')).output(item('minecraft:cooked_fish')).bloomYield(5, 8).langKey(item('minecraft:fishing_rod').getTranslationKey()).name('fishing')"),
            @Example(".input(item('minecraft:paper')).bloom(item('minecraft:book')).tierGranite().tierObsidian().failureChance(0.1F).failureOutput(item('minecraft:milk_bucket'), 5).failureOutput(item('minecraft:bone'), 2).name('knowledge')"),
            @Example(".input(item('minecraft:comparator')).output(item('minecraft:redstone')).bloomYield(12, 15).experience(0.6F).burnTime(4000).tierIronclad(true).anvilHit(5).typePickaxe().failureChance(0.15F).failureOutput(item('minecraft:glowstone_dust'), 5).failureOutput(item('minecraft:sugar'), 4).name('comparator_melting')")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.pyrotech.wither_forge.add0", example = @Example("'flower_pot', item('minecraft:flower_pot'), item('minecraft:clay_ball')"))
    public WitherForgeRecipe add(String name, ItemStack output, IIngredient input) {
        return recipeBuilder()
                .bloom(output)
                .input(input)
                .name(name)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.pyrotech.wither_forge.add1", example = @Example("'hoopify', item('minecraft:hopper') * 4, item('minecraft:chest'), 60"))
    public WitherForgeRecipe add(String name, ItemStack output, IIngredient input, int burnTime) {
        return recipeBuilder()
                .bloom(output)
                .burnTime(burnTime)
                .input(input)
                .name(name)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.pyrotech.wither_forge.addBloom0", example = @Example("'quartz_recipe', item('minecraft:quartz') * 3, ore('oreQuartz')"))
    public WitherForgeRecipe addBloom(String name, ItemStack bloomOutput, IIngredient input) {
        return recipeBuilder()
                .output(bloomOutput)
                .input(input)
                .name(name)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.pyrotech.wither_forge.addBloom1", example = @Example("'feathery', item('minecraft:feather'), 10, 15, item('minecraft:chicken'), 200, 0.1F, 0.25F, item('minecraft:cooked_chicken')"))
    public WitherForgeRecipe addBloom(String name, ItemStack bloomOutput, int minYield, int maxYield, IIngredient input, int burnTime, float experience, float failureChance, ItemStack... failureItems) {
        RecipeBuilder builder = recipeBuilder();
        builder.bloomYield(minYield, maxYield)
                .input(input)
                .output(bloomOutput)
                .name(name);
        for (ItemStack stack : failureItems) {
            builder.failureOutput(stack, 1);
        }
        return builder
                .burnTime(burnTime)
                .experience(experience)
                .failureChance(failureChance)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example(value = "item('minecraft:iron_nugget')", commented = true))
    public void removeByOutput(IIngredient output) {
        if (GroovyLog.msg("Error removing wither forge recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (WitherForgeRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        }
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("item('minecraft:gold_ore')"))
    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing wither forge recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (WitherForgeRecipe recipe : getRegistry()) {
            if (recipe.getInput().test(input)) {
                remove(recipe);
            }
        }
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<WitherForgeRecipe> {

        @Property(value = "groovyscript.wiki.pyrotech.bloomery.bloom.value")
        private ItemStack bloom = ItemStack.EMPTY;
        @Property(value = "groovyscript.wiki.pyrotech.bloomery.slag.value")
        private ItemStack slag = null;
        @Property(value = "groovyscript.wiki.pyrotech.bloomery.burnTime.value", comp = @Comp(gt = 0), defaultValue = "21600")
        private int burnTime = 21600;
        @Property(value = "groovyscript.wiki.pyrotech.bloomery.experience.value", comp = @Comp(gte = 0))
        private float experience;
        @Property(value = "groovyscript.wiki.pyrotech.bloomery.bloomYieldMin.value", comp = @Comp(gte = 0), defaultValue = "12")
        private int bloomYieldMin = 12;
        @Property(value = "groovyscript.wiki.pyrotech.bloomery.bloomYieldMax.value", comp = @Comp(gte = 0), defaultValue = "15")
        private int bloomYieldMax = 15;
        @Property(value = "groovyscript.wiki.pyrotech.bloomery.failureChance.value", comp = @Comp(gte = 0, lte = 1))
        private float failureChance = 0.25F;
        @Property(value = "groovyscript.wiki.pyrotech.bloomery.failureOutput.value")
        private final List<BloomeryRecipeBase.FailureItem> failureOutput = new ArrayList<>(1);
        @Property(value = "groovyscript.wiki.pyrotech.bloomery.anvilTiers.value")
        private final EnumSet<AnvilRecipe.EnumTier> anvilTiers = EnumSet.allOf(AnvilRecipe.EnumTier.class);
        @Property(value = "groovyscript.wiki.pyrotech.bloomery.anvilHit.value", comp = @Comp(gt = 0))
        private int anvilHit = ModuleTechBloomeryConfig.BLOOM.HAMMER_HITS_IN_ANVIL_REQUIRED;
        @Property(value = "groovyscript.wiki.pyrotech.bloomery.anvilType.value", comp = @Comp(not = "null"))
        private AnvilRecipe.EnumType anvilType = AnvilRecipe.EnumType.HAMMER;
        @Property(value = "groovyscript.wiki.pyrotech.bloomery.langKey.value")
        private String langKey;

        private boolean tiersReset = false;

        @RecipeBuilderMethodDescription
        public RecipeBuilder bloom(ItemStack bloom) {
            this.bloom = bloom == null ? ItemStack.EMPTY : bloom;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder slag(ItemStack slag) {
            this.slag = slag == null ? ItemStack.EMPTY : slag;
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
            this.failureOutput.add(new BloomeryRecipeBase.FailureItem(failureOutput, weight));
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder anvilTier(AnvilRecipe.EnumTier tier) {
            if (!tiersReset) {
                anvilTiers.clear();
                tiersReset = true;
            }
            anvilTiers.add(tier);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "anvilTier")
        public RecipeBuilder tierGranite(boolean inherit) {
            anvilTier(AnvilRecipe.EnumTier.GRANITE);
            if (inherit) {
                anvilTier(AnvilRecipe.EnumTier.IRONCLAD);
                anvilTier(AnvilRecipe.EnumTier.OBSIDIAN);
            }
            return this;
        }

        @RecipeBuilderMethodDescription(field = "anvilTier")
        public RecipeBuilder tierGranite() {
            return tierGranite(false);
        }

        @RecipeBuilderMethodDescription(field = "anvilTier")
        public RecipeBuilder tierIronclad(boolean inherit) {
            anvilTier(AnvilRecipe.EnumTier.IRONCLAD);
            if (inherit) {
                anvilTier(AnvilRecipe.EnumTier.OBSIDIAN);
            }
            return this;
        }

        @RecipeBuilderMethodDescription(field = "anvilTier")
        public RecipeBuilder tierIronclad() {
            return tierIronclad(false);
        }

        @RecipeBuilderMethodDescription(field = "anvilTier")
        public RecipeBuilder tierObsidian() {
            return anvilTier(AnvilRecipe.EnumTier.OBSIDIAN);
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder anvilType(AnvilRecipe.EnumType anvilType) {
            this.anvilType = anvilType;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "anvilType")
        public RecipeBuilder typeHammer() {
            return anvilType(AnvilRecipe.EnumType.HAMMER);
        }

        @RecipeBuilderMethodDescription
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

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_wither_forge_";
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
            int minOutput = !bloom.isEmpty() ? 0 : 1;
            if (slag == null) {
                slag = bloom.isEmpty() ? new ItemStack(ModuleTechBloomery.Items.SLAG, 4) : ItemStack.EMPTY;
            }
            if (failureOutput.isEmpty() && failureChance > 0.0F) {
                failureOutput.add(new BloomeryRecipeBase.FailureItem(new ItemStack(ModuleTechBloomery.Items.SLAG), 1));
            }
            validateItems(msg, 1, 1, minOutput, 1);
            msg.add(burnTime <= 0, "burnTime must be a non negative integer that is larger than 0, yet it was {}", burnTime);
            msg.add(experience < 0, "experience must be a non negative float, yet it was {}", experience);
            msg.add(bloomYieldMin < 0 || bloomYieldMin > bloomYieldMax, "bloomYieldMin must be a non negative integer that is smaller than bloomYieldMax, yet it was {}", burnTime);
            msg.add(bloomYieldMax < 0, "bloomYieldMax must be a non negative integer, yet it was {}", burnTime);
            msg.add(failureChance < 0 || failureChance > 1, "failureChance must not be negative nor larger than 1.0, yet it was {}", failureChance);
            msg.add(anvilType == null, "anvilType must not be null");
            msg.add(anvilHit <= 0, "anvilHit must be a non negative integer that is larger than 0, yet it was {}", anvilHit);
            msg.add(ModSupport.PYROTECH.get().witherForge.getRegistry().getValue(super.name) != null, "tried to register {}, but it already exists", super.name);
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable WitherForgeRecipe register() {
            if (!validate()) return null;
            WitherForgeRecipeBuilder builder = new WitherForgeRecipeBuilder(super.name, !output.isEmpty() ? output.get(0) : ItemStack.EMPTY, input.get(0).toMcIngredient());
            failureOutput.forEach(i -> builder.addFailureItem(i.getItemStack(), i.getWeight()));
            WitherForgeRecipe recipe = builder
                    .setSlagItem(slag, slag.getCount())
                    .setBurnTimeTicks(burnTime)
                    .setExperience(experience)
                    .setBloomYield(bloomYieldMin, bloomYieldMax)
                    .setFailureChance(failureChance)
                    .setAnvilTiers(anvilTiers.toArray(new AnvilRecipe.EnumTier[0]))
                    .setLangKey(langKey)
                    .create();
            ((BloomeryRecipeBaseAccessor) recipe).grs$setOutputBloom(bloom.isEmpty() ? null : bloom);
            ModSupport.PYROTECH.get().witherForge.add(recipe);
            if (bloom.isEmpty() && bloomYieldMax != 0) {
                ModSupport.PYROTECH.get().anvil.add(
                        new BloomAnvilRecipe(
                                recipe.getOutput(),
                                com.codetaylor.mc.athenaeum.util.IngredientHelper.fromStackWithNBT(recipe.getOutputBloom()),
                                anvilHit,
                                anvilType,
                                Arrays.copyOf(recipe.getAnvilTiers(), recipe.getAnvilTiers().length),
                                recipe
                        ).setRegistryName(super.name));
            }
            return recipe;
        }
    }
}
