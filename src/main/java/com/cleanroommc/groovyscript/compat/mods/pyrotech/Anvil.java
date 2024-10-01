package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.EnumHelper;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.AnvilRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@RegistryDescription
public class Anvil extends ForgeRegistryWrapper<AnvilRecipe> {

    public Anvil() {
        super(ModuleTechBasic.Registries.ANVIL_RECIPE);
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond') * 4).output(item('minecraft:emerald') * 2).hits(5).typeHammer().tierGranite().name('diamond_to_emerald_granite_anvil')"),
            @Example(".input(item('minecraft:diamond') * 8).output(item('minecraft:nether_star') * 1).hits(10).typePickaxe().tierIronclad().name('diamond_to_nether_star_ironclad_anvil')"),
            @Example(".input(item('minecraft:diamond') * 4).output(item('minecraft:gold_ingot') * 16).hits(5).typePickaxe().tierObsidian().name('diamond_to_gold_obsidian_anvil')")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'iron_to_clay', ore('ingotIron'), item('minecraft:clay_ball'), 9, 'granite', 'hammer'"))
    public AnvilRecipe add(String name, IIngredient input, ItemStack output, int hits, String tier, String type) {
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
                .name(name)
                .input(input)
                .output(output)
                .register();
    }

    @MethodDescription(example = @Example("item('minecraft:stone_slab', 3)"))
    public void removeByOutput(ItemStack output) {
        if (GroovyLog.msg("Error removing pyrotech anvil recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (AnvilRecipe recipe : getRegistry()) {
            if (recipe.getOutput().isItemEqual(output)) {
                remove(recipe);
            }
        }
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<AnvilRecipe> {

        @Property(valid = @Comp(type = Comp.Type.GT, value = "0"))
        private int hits;

        @Property
        private AnvilRecipe.EnumType type;

        @Property
        private AnvilRecipe.EnumTier tier;


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

        @RecipeBuilderMethodDescription
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

        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Anvil Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(hits < 0, "duration must be a non negative integer, yet it was {}", hits);
            msg.add(type == null, "type cannot be null. ");
            msg.add(tier == null, "tier cannot be null.");
            msg.add(super.name == null, "name cannot be null.");
            msg.add(ModuleTechBasic.Registries.ANVIL_RECIPE.getValue(super.name) != null, "tried to register {}, but it already exists.", super.name);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable AnvilRecipe register() {
            if (!validate()) return null;

            AnvilRecipe recipe = new AnvilRecipe(output.get(0), input.get(0).toMcIngredient(), hits, type, tier).setRegistryName(super.name);
            PyroTech.anvil.add(recipe);
            return recipe;
        }
    }
}
