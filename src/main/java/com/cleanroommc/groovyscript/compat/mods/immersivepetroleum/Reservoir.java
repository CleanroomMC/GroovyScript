package com.cleanroommc.groovyscript.compat.mods.immersivepetroleum;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import flaxbeard.immersivepetroleum.api.crafting.PumpjackHandler;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RegistryDescription(
        category = RegistryDescription.Category.ENTRIES,
        admonition = @Admonition(
                value = "groovyscript.wiki.immersivepetroleum.reservoir.note0",
                type = Admonition.Type.WARNING,
                format = Admonition.Format.STANDARD)
)
public class Reservoir extends VirtualizedRegistry<Pair<PumpjackHandler.ReservoirType, Integer>> {

    @RecipeBuilderDescription(example = {
            @Example(".name('demo').fluidOutput(fluid('water')).weight(20000).minSize(100).maxSize(100).dimension(0, 1).biome('hot')"),
            @Example(".name('demo').fluidOutput(fluid('lava')).weight(2000).minSize(1000).maxSize(5000).replenishRate(100).dimension(-1, 1).dimensionBlacklist().biome('cold').biomeBlacklist()")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> PumpjackHandler.reservoirList.entrySet().removeIf(r -> r.getKey() == recipe.getKey()));
        restoreFromBackup().forEach(recipe -> PumpjackHandler.reservoirList.put(recipe.getKey(), recipe.getValue()));
    }

    public void add(PumpjackHandler.ReservoirType recipe, Integer weight) {
        if (recipe != null) {
            addScripted(Pair.of(recipe, weight));
            PumpjackHandler.reservoirList.put(recipe, weight);
        }
    }

    public boolean remove(PumpjackHandler.ReservoirType recipe) {
        int weight = PumpjackHandler.reservoirList.get(recipe);
        if (weight > 0) {
            PumpjackHandler.reservoirList.entrySet().removeIf(r -> r.getKey() == recipe);
            addBackup(Pair.of(recipe, weight));
            return true;
        }
        return false;
    }

    @MethodDescription(example = @Example("'aquifer'"))
    public void removeByName(String name) {
        PumpjackHandler.reservoirList.entrySet().removeIf(r -> {
            if (name.equals(r.getKey().name)) {
                addBackup(Pair.of(r.getKey(), r.getValue()));
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("fluid('oil')"))
    public void removeByOutput(FluidStack output) {
        PumpjackHandler.reservoirList.entrySet().removeIf(r -> {
            if (output.getFluid().getName().equals(r.getKey().fluid)) {
                addBackup(Pair.of(r.getKey(), r.getValue()));
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Map.Entry<PumpjackHandler.ReservoirType, Integer>> streamRecipes() {
        return new SimpleObjectStream<>(PumpjackHandler.reservoirList.entrySet()).setRemover(r -> this.remove(r.getKey()));
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        PumpjackHandler.reservoirList.forEach((r, l) -> addBackup(Pair.of(r, l)));
        PumpjackHandler.reservoirList.clear();
    }


    @Property(property = "fluidOutput", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<PumpjackHandler.ReservoirType> {

        @Property
        private final IntArrayList dimensions = new IntArrayList();
        @Property
        private final List<String> biomes = new ArrayList<>();
        @Property(ignoresInheritedMethods = true)
        private String name;
        @Property
        private int weight;
        @Property(comp = @Comp(gte = 1, unique = "groovyscript.wiki.immersivepetroleum.reservoir.maxSize.required"))
        private int minSize;
        @Property(comp = @Comp(gte = 1, unique = "groovyscript.wiki.immersivepetroleum.reservoir.minSize.required"))
        private int maxSize;
        @Property(comp = @Comp(gte = 1))
        private int replenishRate;
        @Property
        private boolean dimensionBlacklist = true;
        @Property
        private boolean biomeBlacklist = true;

        @Override
        @RecipeBuilderMethodDescription
        public RecipeBuilder name(String name) {
            this.name = name;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder weight(int weight) {
            this.weight = weight;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder minSize(int minSize) {
            this.minSize = minSize;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder maxSize(int maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder replenishRate(int replenishRate) {
            this.replenishRate = replenishRate;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "dimensions")
        public RecipeBuilder dimension(int dimension) {
            this.dimensions.add(dimension);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "dimensions")
        public RecipeBuilder dimension(int... dimensions) {
            for (int dimension : dimensions) {
                dimension(dimension);
            }
            return this;
        }

        @RecipeBuilderMethodDescription(field = "dimensions")
        public RecipeBuilder dimension(Collection<Integer> dimensions) {
            for (int dimension : dimensions) {
                dimension(dimension);
            }
            return this;
        }

        @RecipeBuilderMethodDescription(field = "biomes")
        public RecipeBuilder biome(String biome) {
            this.biomes.add(biome);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "biomes")
        public RecipeBuilder biome(String... biomes) {
            for (String biome : biomes) {
                biome(biome);
            }
            return this;
        }

        @RecipeBuilderMethodDescription(field = "biomes")
        public RecipeBuilder biome(Collection<String> biomes) {
            for (String biome : biomes) {
                biome(biome);
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder dimensionBlacklist(boolean dimensionBlacklist) {
            this.dimensionBlacklist = dimensionBlacklist;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder dimensionBlacklist() {
            this.dimensionBlacklist = !dimensionBlacklist;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder biomeBlacklist(boolean biomeBlacklist) {
            this.biomeBlacklist = biomeBlacklist;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder biomeBlacklist() {
            this.biomeBlacklist = !biomeBlacklist;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Petroleum Reservoir entry";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg, 0, 0, 1, 1);
            msg.add(weight < 1, "weight must be greater than or equal to 1, yet it was {}", weight);
            msg.add(minSize < 1, "minSize must be greater than or equal to 1, yet it was {}", minSize);
            msg.add(maxSize < minSize, "maxSize must be larger than or equal to minSize, yet maxSize was {} and minSize was {}", maxSize, minSize);
            msg.add(replenishRate < 0, "replenishRate must be a non-negative integer, yet it was {}", replenishRate);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable PumpjackHandler.ReservoirType register() {
            if (!validate()) return null;

            PumpjackHandler.ReservoirType recipe = new PumpjackHandler.ReservoirType(name, fluidOutput.getOrEmpty(0).getFluid().getName(), minSize, maxSize, replenishRate);

            int[] dims = dimensions.elements();
            if (dims != null) {
                if (dimensionBlacklist) recipe.dimensionBlacklist = dims;
                else recipe.dimensionWhitelist = dims;
            }

            String[] biomesArray = biomes.toArray(new String[0]);
            if (biomeBlacklist) recipe.biomeBlacklist = biomesArray;
            else recipe.biomeWhitelist = biomesArray;

            ModSupport.IMMERSIVE_PETROLEUM.get().reservoir.add(recipe, weight);
            return recipe;
        }

    }

}
