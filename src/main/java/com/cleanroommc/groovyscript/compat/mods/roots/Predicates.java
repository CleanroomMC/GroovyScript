package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.NamedRegistry;
import epicsquid.roots.recipe.transmutation.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@RegistryDescription(
        category = RegistryDescription.Category.ENTRIES
)
public class Predicates extends NamedRegistry {

    public WorldBlockStatePredicate ANY = WorldBlockStatePredicate.TRUE;
    public WorldBlockStatePredicate TRUE = WorldBlockStatePredicate.TRUE;
    public BlocksPredicate LAVA = new LavaPredicate();
    public BlocksPredicate WATER = new WaterPredicate();
    public LeavesPredicate LEAVES = new LeavesPredicate();

    @RecipeBuilderDescription(example = {
            @Example(".blockstate(blockstate('minecraft:red_flower'))"),
            @Example(".block(block('minecraft:red_flower'))"),
            @Example(".blockstate(blockstate('minecraft:red_flower:type=poppy')).properties('type')"),
            @Example(".blockstate(blockstate('minecraft:log:axis=z:variant=oak')).properties('axis').above()"),
            @Example(".blockstate(blockstate('minecraft:log')).below()")
    })
    public StateBuilder stateBuilder() {
        return new StateBuilder();
    }

    StatePredicate create(IBlockState blockState) {
        return new StatePredicate(blockState);
    }

    PropertyPredicate create(IBlockState blockState, String... properties) {
        return new PropertyPredicate(blockState, Arrays.stream(properties).map(x -> blockState.getBlock().getBlockState().getProperty(x)).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    PropertyPredicate create(IBlockState blockState, Collection<String> properties) {
        return new PropertyPredicate(blockState, properties.stream().map(x -> blockState.getBlock().getBlockState().getProperty(x)).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    WorldBlockStatePredicate above(BlockStatePredicate blockState) {
        return new BlockStateAbove(blockState);
    }

    WorldBlockStatePredicate below(BlockStatePredicate blockState) {
        return new BlockStateBelow(blockState);
    }

    public static class StateBuilder extends AbstractRecipeBuilder<MatchingStates> {

        @Property(requirement = "groovyscript.wiki.roots.predicates.properties.required")
        private final Collection<String> properties = new ArrayList<>();
        @Property(valid = @Comp(value = "null", type = Comp.Type.NOT))
        private IBlockState blockstate;
        @Property(requirement = "groovyscript.wiki.roots.predicates.above_or_below.required")
        private boolean above = false;
        @Property(requirement = "groovyscript.wiki.roots.predicates.above_or_below.required")
        private boolean below = false;

        @RecipeBuilderMethodDescription
        public StateBuilder blockstate(IBlockState blockstate) {
            this.blockstate = blockstate;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "blockstate")
        public StateBuilder block(Block block) {
            this.blockstate = block.getDefaultState();
            return this;
        }

        @RecipeBuilderMethodDescription
        public StateBuilder properties(String... properties) {
            Collections.addAll(this.properties, properties);
            return this;
        }

        @RecipeBuilderMethodDescription
        public StateBuilder properties(Collection<String> properties) {
            this.properties.addAll(properties);
            return this;
        }

        @RecipeBuilderMethodDescription
        public StateBuilder above() {
            this.above = true;
            return this;
        }

        @RecipeBuilderMethodDescription
        public StateBuilder below() {
            this.below = true;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error creating Roots Predicate";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg);
            msg.add(above && below, "both above and below cannot be true");
            msg.add(blockstate == null, "blockstate must be defined");

            BlockStateContainer container = blockstate.getBlock().getBlockState();
            properties.forEach(prop -> {
                if (container.getProperty(prop) == null) {
                    msg.add("property {} is not a property of the provided blockstate {}", prop, blockstate);
                }
            });
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable MatchingStates register() {
            if (!validate()) return null;
            BlockStateContainer container = blockstate.getBlock().getBlockState();

            BlockStatePredicate predicate = properties.isEmpty()
                                            ? new StatePredicate(blockstate)
                                            : new PropertyPredicate(blockstate, properties.stream().map(container::getProperty).collect(Collectors.toList()));

            if (above) return new BlockStateAbove(predicate);
            if (below) return new BlockStateBelow(predicate);
            return predicate;
        }
    }
}
