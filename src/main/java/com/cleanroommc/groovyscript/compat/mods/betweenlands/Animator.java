package com.cleanroommc.groovyscript.compat.mods.betweenlands;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.fml.common.registry.EntityEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.recipes.IAnimatorRecipe;
import thebetweenlands.common.recipe.misc.AnimatorRecipe;

import java.util.Collection;
import java.util.List;

@RegistryDescription
public class Animator extends StandardListRegistry<IAnimatorRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond')).life(1).fuel(1)"),
            @Example(".input(item('minecraft:gold_ingot')).lootTable(resource('minecraft:entities/zombie')).life(5).fuel(1)"),
            @Example(".input(item('minecraft:gold_block')).entity(entity('minecraft:zombie').getEntityClass()).life(1).fuel(5)"),
            @Example(".input(item('minecraft:diamond')).entity(entity('minecraft:enderman')).output(item('minecraft:clay')).life(3).fuel(10)"),
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<IAnimatorRecipe> getRecipes() {
        return AnimatorRecipe.getRecipes();
    }

    @MethodDescription(example = @Example("item('thebetweenlands:bone_leggings')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> r instanceof AnimatorRecipe recipe && input.test(recipe.getInput()) && doAddBackup(r));
    }

    @MethodDescription(example = @Example("item('thebetweenlands:items_misc:46')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> r instanceof AnimatorRecipe recipe && output.test(recipe.getResult(recipe.getInput())) && doAddBackup(r));
    }

    @MethodDescription(example = @Example("resource('thebetweenlands:animator/scroll')"))
    public boolean removeByLootTable(ResourceLocation lootTable) {
        return getRecipes().removeIf(r -> r instanceof AnimatorRecipe recipe && lootTable.equals(recipe.getLootTable()) && doAddBackup(r));
    }

    @MethodDescription
    public boolean removeByEntity(Class<? extends Entity> entity) {
        return getRecipes().removeIf(r -> r instanceof AnimatorRecipe recipe && entity.equals(recipe.getSpawnEntityClass(recipe.getInput())) && doAddBackup(r));
    }

    @MethodDescription(example = @Example("entity('thebetweenlands:sporeling')"))
    public boolean removeByEntity(EntityEntry entity) {
        return removeByEntity(entity.getEntityClass());
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<IAnimatorRecipe> {

        @Property(comp = @Comp(gte = 0))
        private int life;
        @Property(comp = @Comp(gte = 0))
        private int fuel;
        @Property
        private ResourceLocation lootTable;
        @Property
        private Class<? extends Entity> entity;
        @Property
        private ResourceLocation render;

        @RecipeBuilderMethodDescription
        public RecipeBuilder life(int life) {
            this.life = life;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder fuel(int fuel) {
            this.fuel = fuel;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder lootTable(ResourceLocation lootTable) {
            this.lootTable = lootTable;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder entity(Class<? extends Entity> entity) {
            this.entity = entity;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder entity(EntityEntry entity) {
            return entity(entity.getEntityClass());
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder render(ResourceLocation render) {
            this.render = render;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Betweenlands Animator recipe";
        }

        @Override
        @GroovyBlacklist
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 1);
            validateFluids(msg);
            msg.add(life < 0, "life must be a positive integer greater than 0, yet it was {}", life);
            msg.add(fuel < 0, "fuel must be a positive integer greater than 0, yet it was {}", fuel);
            if (lootTable != null) {
                validateCustom(msg, output, 0, 0, "item output");
                msg.add(entity != null, "entity was defined even though lootTable was defined");
                msg.add(!output.isEmpty(), "output was defined even though lootTable was defined");
            }
            msg.add(output.isEmpty() && lootTable == null && entity == null, "output, lootTable, and entity were all not defined. one of them should be defined to properly create the recipe");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable IAnimatorRecipe register() {
            if (!validate()) return null;
            AnimatorRecipe recipe = null;
            if (lootTable != null) {
                for (var stack : input.get(0).getMatchingStacks()) {
                    recipe = new AnimatorRecipe(stack, fuel, life, lootTable) {

                        /**
                         * In order to get Loot Table recipes to display properly we need to do this.
                         * This snippet of code is copied a few times in {@link thebetweenlands.common.registries.RecipeRegistry#registerAnimatorRecipes}
                         * with the only difference being the `lootTable` being used here.
                         * Don't know why Betweenlands doesn't just make a class to handle it? Seems like it would make sense...
                         */
                        @Override
                        public @NotNull ItemStack onAnimated(World world, BlockPos pos, ItemStack stack) {
                            LootTable table = world.getLootTableManager().getLootTableFromLocation(lootTable);
                            LootContext.Builder lootBuilder = new LootContext.Builder((WorldServer) world);
                            List<ItemStack> loot = table.generateLootForPools(world.rand, lootBuilder.build());
                            return loot.isEmpty() ? ItemStack.EMPTY : loot.get(world.rand.nextInt(loot.size()));
                        }
                    };
                    ModSupport.BETWEENLANDS.get().animator.add(recipe);
                }
            } else if (entity != null) {
                if (output.isEmpty()) {
                    for (var stack : input.get(0).getMatchingStacks()) {
                        recipe = new AnimatorRecipe(stack, fuel, life, entity);
                        recipe.setRenderEntity(render);
                        ModSupport.BETWEENLANDS.get().animator.add(recipe);
                    }
                } else {
                    for (var stack : input.get(0).getMatchingStacks()) {
                        recipe = new AnimatorRecipe(stack, fuel, life, output.get(0), entity);
                        recipe.setRenderEntity(render);
                        ModSupport.BETWEENLANDS.get().animator.add(recipe);
                    }
                }
            }
            ModSupport.BETWEENLANDS.get().animator.add(recipe);
            return recipe;
        }
    }
}
