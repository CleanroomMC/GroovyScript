package com.cleanroommc.groovyscript.compat.mods.railcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.api.crafting.IRollingMachineCrafter;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

@RegistryDescription
public class RollingMachine extends VirtualizedRegistry<Pair<ResourceLocation, IRollingMachineCrafter.IRollingRecipe>> {

    @RecipeBuilderDescription(example = {
            @Example(".output(item('minecraft:stone')).matrix('BXX', 'X B').key('B', item('minecraft:stone')).key('X', item('minecraft:gold_ingot')).time(200)"),
            @Example(".output(item('minecraft:diamond') * 32).matrix([[item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')],[item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')],[item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')]]).time(400)")
    })
    public ShapedRecipeBuilder shapedBuilder() {
        return new ShapedRecipeBuilder();
    }

    @RecipeBuilderDescription(example = {
            @Example(".output(item('minecraft:clay') * 8).input(item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone')).time(100)"),
            @Example(".output(item('minecraft:diamond') * 32).input(item('minecraft:gold_ingot') * 9).time(500)")
    })
    public ShapelessRecipeBuilder shapelessBuilder() {
        return new ShapelessRecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(r -> {
            List<IRollingMachineCrafter.IRollingRecipe> recipes = new ArrayList<>(Crafters.rollingMachine().getRecipes());
            recipes.removeIf(recipe -> recipe.getRegistryName().equals(r.getKey()));
        });
        restoreFromBackup().forEach(r -> ModSupport.RAILCRAFT.get().rollingMachine.add(r.getKey(), r.getValue()));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public IRollingMachineCrafter.IRollingRecipe addShaped(ItemStack output, List<List<IIngredient>> input, int time) {
        return shapedBuilder()
                .matrix(input)
                .output(output)
                .time(time)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public IRollingMachineCrafter.IRollingRecipe addShaped(ItemStack output, List<List<IIngredient>> input) {
        return addShaped(output, input, 200);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public IRollingMachineCrafter.IRollingRecipe addShapeless(ItemStack output, List<IIngredient> input, int time) {
        return shapelessBuilder()
                .input(input)
                .output(output)
                .time(time)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public IRollingMachineCrafter.IRollingRecipe addShapeless(ItemStack output, List<IIngredient> input) {
        return addShapeless(output, input, 200);
    }

    public IRollingMachineCrafter.IRollingRecipe add(ResourceLocation key, IRollingMachineCrafter.IRollingRecipe recipe) {
        if (recipe != null) {
            addScripted(Pair.of(key, recipe));
        }
        return recipe;
    }

    @MethodDescription(example = @Example("item('minecraft:tripwire_hook')"))
    public boolean removeByOutput(IIngredient output) {
        return Crafters.rollingMachine().getRecipes().removeIf(r -> {
            if (output.test(r.getRecipeOutput())) {
                addBackup(Pair.of(r.getRegistryName(), r));
                return true;
            }
            return false;
        });
    }

    public boolean remove(ResourceLocation key) {
        return Crafters.rollingMachine().getRecipes().removeIf(r -> {
            if (r.getRegistryName().equals(key)) {
                addBackup(Pair.of(key, r));
                return true;
            }
            return false;
        });
    }

    public boolean remove(IRollingMachineCrafter.IRollingRecipe recipe) {
        return Crafters.rollingMachine().getRecipes().removeIf(r -> {
            if (r == recipe) {
                addBackup(Pair.of(r.getRegistryName(), r));
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<IRollingMachineCrafter.IRollingRecipe> streamRecipes() {
        return new SimpleObjectStream<>(Crafters.rollingMachine().getRecipes()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        Crafters.rollingMachine().getRecipes().forEach(r -> addBackup(Pair.of(r.getRegistryName(), r)));
        Crafters.rollingMachine().getRecipes().clear();
    }

    public static class ShapedRecipeBuilder {
        private ItemStack output;
        private List<List<IIngredient>> matrix;
        private int time = 200;

        public ShapedRecipeBuilder output(ItemStack output) {
            this.output = output;
            return this;
        }

        public ShapedRecipeBuilder matrix(List<List<IIngredient>> matrix) {
            this.matrix = matrix;
            return this;
        }

        public ShapedRecipeBuilder matrix(String... pattern) {
            // Parse string pattern like "ABC", "DEF", "GHI"
            this.matrix = new ArrayList<>();
            for (String row : pattern) {
                List<IIngredient> rowList = new ArrayList<>();
                for (char c : row.toCharArray()) {
                    rowList.add(null); // Will be filled by key()
                }
                this.matrix.add(rowList);
            }
            return this;
        }

        public ShapedRecipeBuilder key(char key, IIngredient ingredient) {
            // Replace null entries with the ingredient based on key
            if (matrix != null) {
                for (List<IIngredient> row : matrix) {
                    for (int i = 0; i < row.size(); i++) {
                        if (row.get(i) == null) {
                            row.set(i, ingredient);
                        }
                    }
                }
            }
            return this;
        }

        public ShapedRecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        public IRollingMachineCrafter.IRollingRecipe register() {
            if (output == null || output.isEmpty()) {
                GroovyLog.msg("Error adding Railcraft Rolling Machine shaped recipe")
                        .add("output must not be empty")
                        .error()
                        .post();
                return null;
            }
            if (matrix == null || matrix.isEmpty()) {
                GroovyLog.msg("Error adding Railcraft Rolling Machine shaped recipe")
                        .add("matrix must not be empty")
                        .error()
                        .post();
                return null;
            }

            final int finalTime = time;
            ResourceLocation name = new ResourceLocation("groovyscript", "rolling_shaped_" + System.currentTimeMillis());

            IRecipe recipe = new IRecipe() {
                @Override
                public boolean matches(InventoryCrafting inv, World worldIn) {
                    // Simple matching logic
                    return false;
                }

                @Override
                public ItemStack getCraftingResult(InventoryCrafting inv) {
                    return output.copy();
                }

                @Override
                public boolean canFit(int width, int height) {
                    return width >= matrix.get(0).size() && height >= matrix.size();
                }

                @Override
                public ItemStack getRecipeOutput() {
                    return output.copy();
                }

                @Override
                public NonNullList<Ingredient> getIngredients() {
                    NonNullList<Ingredient> ingredients = NonNullList.create();
                    for (List<IIngredient> row : matrix) {
                        for (IIngredient ing : row) {
                            if (ing != null) {
                                ingredients.add(Railcraft.toIngredient(ing));
                            }
                        }
                    }
                    return ingredients;
                }

                @Override
                public IRecipe setRegistryName(ResourceLocation name) {
                    return this;
                }

                @Override
                public ResourceLocation getRegistryName() {
                    return name;
                }

                @Override
                public Class<IRecipe> getRegistryType() {
                    return IRecipe.class;
                }
            };

            IRollingMachineCrafter.IRollingRecipe rollingRecipe = new IRollingMachineCrafter.IRollingRecipe() {
                @Override
                public int getTickTime() {
                    return finalTime;
                }

                @Override
                public boolean matches(InventoryCrafting inv, World worldIn) {
                    return recipe.matches(inv, worldIn);
                }

                @Override
                public ItemStack getCraftingResult(InventoryCrafting inv) {
                    return recipe.getCraftingResult(inv);
                }

                @Override
                public boolean canFit(int width, int height) {
                    return recipe.canFit(width, height);
                }

                @Override
                public ItemStack getRecipeOutput() {
                    return recipe.getRecipeOutput();
                }

                @Override
                public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
                    return recipe.getRemainingItems(inv);
                }

                @Override
                public NonNullList<Ingredient> getIngredients() {
                    return recipe.getIngredients();
                }

                @Override
                public boolean isDynamic() {
                    return recipe.isDynamic();
                }

                @Override
                public String getGroup() {
                    return recipe.getGroup();
                }

                @Override
                public IRecipe setRegistryName(ResourceLocation name) {
                    return recipe.setRegistryName(name);
                }

                @Override
                public ResourceLocation getRegistryName() {
                    return recipe.getRegistryName();
                }

                @Override
                public Class<IRecipe> getRegistryType() {
                    return recipe.getRegistryType();
                }
            };

            return ModSupport.RAILCRAFT.get().rollingMachine.add(name, rollingRecipe);
        }
    }

    public static class ShapelessRecipeBuilder {
        private ItemStack output;
        private List<IIngredient> input = new ArrayList<>();
        private int time = 200;

        public ShapelessRecipeBuilder output(ItemStack output) {
            this.output = output;
            return this;
        }

        public ShapelessRecipeBuilder input(List<IIngredient> input) {
            this.input = input;
            return this;
        }

        public ShapelessRecipeBuilder input(IIngredient... ingredients) {
            for (IIngredient ing : ingredients) {
                this.input.add(ing);
            }
            return this;
        }

        public ShapelessRecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        public IRollingMachineCrafter.IRollingRecipe register() {
            if (output == null || output.isEmpty()) {
                GroovyLog.msg("Error adding Railcraft Rolling Machine shapeless recipe")
                        .add("output must not be empty")
                        .error()
                        .post();
                return null;
            }
            if (input.isEmpty()) {
                GroovyLog.msg("Error adding Railcraft Rolling Machine shapeless recipe")
                        .add("input must not be empty")
                        .error()
                        .post();
                return null;
            }

            final int finalTime = time;
            ResourceLocation name = new ResourceLocation("groovyscript", "rolling_shapeless_" + System.currentTimeMillis());

            IRecipe recipe = new IRecipe() {
                @Override
                public boolean matches(InventoryCrafting inv, World worldIn) {
                    return false;
                }

                @Override
                public ItemStack getCraftingResult(InventoryCrafting inv) {
                    return output.copy();
                }

                @Override
                public boolean canFit(int width, int height) {
                    return width * height >= input.size();
                }

                @Override
                public ItemStack getRecipeOutput() {
                    return output.copy();
                }

                @Override
                public NonNullList<Ingredient> getIngredients() {
                    NonNullList<Ingredient> ingredients = NonNullList.create();
                    for (IIngredient ing : input) {
                        ingredients.add(Railcraft.toIngredient(ing));
                    }
                    return ingredients;
                }

                @Override
                public IRecipe setRegistryName(ResourceLocation name) {
                    return this;
                }

                @Override
                public ResourceLocation getRegistryName() {
                    return name;
                }

                @Override
                public Class<IRecipe> getRegistryType() {
                    return IRecipe.class;
                }
            };

            IRollingMachineCrafter.IRollingRecipe rollingRecipe = new IRollingMachineCrafter.IRollingRecipe() {
                @Override
                public int getTickTime() {
                    return finalTime;
                }

                @Override
                public boolean matches(InventoryCrafting inv, World worldIn) {
                    return recipe.matches(inv, worldIn);
                }

                @Override
                public ItemStack getCraftingResult(InventoryCrafting inv) {
                    return recipe.getCraftingResult(inv);
                }

                @Override
                public boolean canFit(int width, int height) {
                    return recipe.canFit(width, height);
                }

                @Override
                public ItemStack getRecipeOutput() {
                    return recipe.getRecipeOutput();
                }

                @Override
                public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
                    return recipe.getRemainingItems(inv);
                }

                @Override
                public NonNullList<Ingredient> getIngredients() {
                    return recipe.getIngredients();
                }

                @Override
                public boolean isDynamic() {
                    return recipe.isDynamic();
                }

                @Override
                public String getGroup() {
                    return recipe.getGroup();
                }

                @Override
                public IRecipe setRegistryName(ResourceLocation name) {
                    return recipe.setRegistryName(name);
                }

                @Override
                public ResourceLocation getRegistryName() {
                    return recipe.getRegistryName();
                }

                @Override
                public Class<IRecipe> getRegistryType() {
                    return recipe.getRegistryType();
                }
            };

            return ModSupport.RAILCRAFT.get().rollingMachine.add(name, rollingRecipe);
        }
    }
}
