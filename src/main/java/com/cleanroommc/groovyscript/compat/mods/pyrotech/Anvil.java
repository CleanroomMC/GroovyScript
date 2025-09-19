package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.EnumHelper;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.init.recipe.AnvilIroncladRecipesAdd;
import com.codetaylor.mc.pyrotech.modules.tech.basic.init.recipe.AnvilObsidianRecipesAdd;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.AnvilRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;

@RegistryDescription
public class Anvil extends ForgeRegistryWrapper<AnvilRecipe> {

    public Anvil() {
        super(ModuleTechBasic.Registries.ANVIL_RECIPE);
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:emerald') * 2).hits(8).typeHammer().tierGranite().name('diamond_to_emerald_granite_anvil')"),
            @Example(".input(item('minecraft:bedrock')).output(item('minecraft:nether_star') * 1).hits(10).typePickaxe().tierIronclad().inherit(true).name('bedrock_to_nether_star')"),
            @Example(".input(item('minecraft:gold_block')).output(item('minecraft:gold_ingot') * 16).hits(5).typePickaxe().tierObsidian().name('gold_block_to_gold_obsidian_anvil')")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public AnvilRecipe add(String name, IIngredient input, ItemStack output, int hits, String tier, String type) {
        return add(name, input, output, hits, tier, type, false);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'flint_from_gravel', ore('gravel'), item('minecraft:flint'), 5, 'granite', 'pickaxe', true"))
    public AnvilRecipe add(String name, IIngredient input, ItemStack output, int hits, String tier, String type, boolean inherit) {
        AnvilRecipe.EnumTier enumTier = EnumHelper.valueOfNullable(AnvilRecipe.EnumTier.class, tier, false);
        AnvilRecipe.EnumType enumType = EnumHelper.valueOfNullable(AnvilRecipe.EnumType.class, type, false);
        if (enumTier == null || enumType == null) {
            GroovyLog.msg("Error adding pyrotech anvil recipe")
                    .add(enumTier == null, "tier with name {} does not exist. Valid values are {}.", tier, Arrays.toString(AnvilRecipe.EnumTier.values()))
                    .add(enumTier == null, "type with name {} does not exist. Valid values are {}.", tier, Arrays.toString(AnvilRecipe.EnumType.values()))
                    .error()
                    .post();
            return null;
        }
        return recipeBuilder()
                .hits(hits)
                .tier(enumTier)
                .type(enumType)
                .inherit(inherit)
                .name(name)
                .input(input)
                .output(output)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("item('pyrotech:material:37')"))
    public void removeByInput(ItemStack input)  {
        if (GroovyLog.msg("Error removing pyrotech anvil recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (AnvilRecipe recipe : getRegistry()) {
            if (recipe.getInput().test(input)) {
                remove(recipe);
            }
        }
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("item('minecraft:stone_slab:3') * 2"))
    public void removeByOutput(IIngredient output) {
        if (GroovyLog.msg("Error removing pyrotech anvil recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (AnvilRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        }
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<AnvilRecipe> {

        @Property(comp = @Comp(gt = 0))
        private int hits;
        @Property
        private AnvilRecipe.EnumType type;
        @Property
        private AnvilRecipe.EnumTier tier;
        @Property
        private boolean inherit;

        @RecipeBuilderMethodDescription
        public RecipeBuilder hits(int hits) {
            this.hits = hits;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder type(AnvilRecipe.EnumType type) {
            this.type = type;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "type")
        public RecipeBuilder typeHammer() {
            return type(AnvilRecipe.EnumType.HAMMER);
        }

        @RecipeBuilderMethodDescription(field = "type")
        public RecipeBuilder typePickaxe() {
            return type(AnvilRecipe.EnumType.PICKAXE);
        }

        public RecipeBuilder tier(AnvilRecipe.EnumTier tier) {
            this.tier = tier;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "tier")
        public RecipeBuilder tierGranite() {
            return tier(AnvilRecipe.EnumTier.GRANITE);
        }

        @RecipeBuilderMethodDescription(field = "tier")
        public RecipeBuilder tierIronclad() {
            return tier(AnvilRecipe.EnumTier.IRONCLAD);
        }

        @RecipeBuilderMethodDescription(field = "tier")
        public RecipeBuilder tierObsidian() {
            return tier(AnvilRecipe.EnumTier.OBSIDIAN);
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder inherit(boolean inherit) {
            this.inherit = inherit;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Anvil Recipe";
        }

        @Override
        protected int getMaxItemInput() {
            // More than 1 item cannot be placed
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(hits < 0, "duration must be a non negative integer, yet it was {}", hits);
            msg.add(type == null, "type cannot be null.");
            msg.add(tier == null, "tier cannot be null.");
            msg.add(super.name == null, "name cannot be null.");
            msg.add(ModuleTechBasic.Registries.ANVIL_RECIPE.getValue(super.name) != null, "tried to register {}, but it already exists.", super.name);
            msg.add(tier == AnvilRecipe.EnumTier.OBSIDIAN && inherit, "nothing can inherit from obsidian anvil.");
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable AnvilRecipe register() {
            if (!validate()) return null;
            AnvilRecipe recipe = new AnvilRecipe(output.get(0), input.get(0).toMcIngredient(), hits, type, tier).setRegistryName(super.name);
            ModSupport.PYROTECH.get().anvil.add(recipe);
            if (inherit) {
                String name = null;
                if (tier.ordinal() < 2) {
                    name = tier.name().toLowerCase(Locale.US) + "_anvil";
                    AnvilRecipe obsidianRecipe = AnvilObsidianRecipesAdd.INHERIT_TRANSFORMER.apply(recipe);
                    obsidianRecipe.setRegistryName(new ResourceLocation(super.name.getNamespace(), name + "/" + super.name.getPath()));
                    ModSupport.PYROTECH.get().anvil.add(obsidianRecipe);
                }
                if (tier.ordinal() < 1) {
                    AnvilRecipe ironcladRecipe = AnvilIroncladRecipesAdd.INHERIT_TRANSFORMER.apply(recipe);
                    ironcladRecipe.setRegistryName(new ResourceLocation(super.name.getNamespace(), name + "/" + super.name.getPath()));
                    ModSupport.PYROTECH.get().anvil.add(ironcladRecipe);
                }
            }
            return recipe;
        }
    }
}
