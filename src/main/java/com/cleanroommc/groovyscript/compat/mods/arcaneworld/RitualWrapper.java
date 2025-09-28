package com.cleanroommc.groovyscript.compat.mods.arcaneworld;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import groovy.lang.Closure;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import party.lemons.arcaneworld.crafting.ritual.Ritual;
import party.lemons.arcaneworld.crafting.ritual.impl.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RegistryDescription
public class RitualWrapper extends ForgeRegistryWrapper<Ritual> {

    public RitualWrapper() {
        super(GameRegistry.findRegistry(Ritual.class), Alias.generateOf("Ritual"));
    }

    // TODO
    //  not super happy with the wiki gen this creates, as
    //  theres a fair amount of duplication of code and information.
    //  Perhaps something like a pseudo element should be added?
    //  ... which would merge them somehow. need to figure that out.
    //  applies, inspirations.Cauldron, ID.*, and any new places.

    @RecipeBuilderDescription(example = @Example(".ritualCreateItem().input(item('minecraft:stone') * 5, item('minecraft:diamond'), item('minecraft:clay')).output(item('minecraft:clay')).translationKey('groovyscript.demo_output').name('groovyscript:custom_name')"), override = @RecipeBuilderOverride(requirement = {
            @Property(property = "output"),
            @Property(property = "entity"),
            @Property(property = "time"),
            @Property(property = "weatherType"),
            @Property(property = "command"),
            @Property(property = "onActivate"),
            @Property(property = "ritualType"),
    }))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:stone'), item('minecraft:stone'), item('minecraft:clay')).translationKey('groovyscript.demo_arena').entity(entity('minecraft:chicken'))"), override = @RecipeBuilderOverride(requirement = {
            @Property(property = "ritualType", defaultValue = "RitualType.ARENA"),
            @Property(property = "entity")
    }))
    public RecipeBuilder recipeBuilderArena() {
        return new RecipeBuilder().ritualArena();
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:clay')).translationKey('groovyscript.demo_command').command('say hi', 'give @p minecraft:coal 5')"), override = @RecipeBuilderOverride(requirement = {
            @Property(property = "ritualType", defaultValue = "RitualType.COMMAND"),
            @Property(property = "command")
    }))
    public RecipeBuilder recipeBuilderCommand() {
        return new RecipeBuilder().ritualCommand();
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond')).translationKey('groovyscript.demo_create_item').output(item('minecraft:diamond'))"), override = @RecipeBuilderOverride(requirement = {
            @Property(property = "ritualType", defaultValue = "RitualType.CREATE_ITEM"),
            @Property(property = "output")
    }))
    public RecipeBuilder recipeBuilderCreateItem() {
        return new RecipeBuilder().ritualCreateItem();
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay')).translationKey('groovyscript.demo_dragon_breath')"), override = @RecipeBuilderOverride(requirement = {
            @Property(property = "ritualType", defaultValue = "RitualType.DRAGON_BREATH"),
    }))
    public RecipeBuilder recipeBuilderDragonBreath() {
        return new RecipeBuilder().ritualDragonBreath();
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:diamond'), item('minecraft:clay'), item('minecraft:clay')).translationKey('groovyscript.demo_dungeon')"), override = @RecipeBuilderOverride(requirement = {
            @Property(property = "ritualType", defaultValue = "RitualType.DUNGEON"),
    }))
    public RecipeBuilder recipeBuilderDungeon() {
        return new RecipeBuilder().ritualDungeon();
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:stone'), item('minecraft:clay'), item('minecraft:clay')).translationKey('groovyscript.demo_summon').entity(entity('minecraft:chicken'))"), override = @RecipeBuilderOverride(requirement = {
            @Property(property = "ritualType", defaultValue = "RitualType.SUMMON"),
            @Property(property = "entity")
    }))
    public RecipeBuilder recipeBuilderSummon() {
        return new RecipeBuilder().ritualSummon();
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:diamond'), item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay')).translationKey('groovyscript.demo_time').time(5000)"), override = @RecipeBuilderOverride(requirement = {
            @Property(property = "ritualType", defaultValue = "RitualType.TIME"),
            @Property(property = "time")
    }))
    public RecipeBuilder recipeBuilderTime() {
        return new RecipeBuilder().ritualTime();
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond'), item('minecraft:gold_ingot'), item('minecraft:clay')).translationKey('groovyscript.demo_weather_clear').weatherClear()"),
            @Example(".input(item('minecraft:gold_ingot'), item('minecraft:diamond'), item('minecraft:clay')).translationKey('groovyscript.demo_weather_rain').weatherRain()"),
            @Example(".input(item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:gold_ingot')).translationKey('groovyscript.demo_weather_thunder').weatherThunder()")
    }, override = @RecipeBuilderOverride(requirement = {
            @Property(property = "ritualType", defaultValue = "RitualType.WEATHER"),
            @Property(property = "weatherType")
    }))
    public RecipeBuilder recipeBuilderWeather() {
        return new RecipeBuilder().ritualWeather();
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:clay'), item('minecraft:clay')).translationKey('groovyscript.demo_custom').onActivate({ World world, BlockPos blockPos, EntityPlayer player, ItemStack... itemStacks -> { log.info blockPos } })"), override = @RecipeBuilderOverride(requirement = {
            @Property(property = "ritualType", defaultValue = "RitualType.CUSTOM"),
            @Property(property = "onActivate")
    }))
    public RecipeBuilder recipeBuilderCustom() {
        return new RecipeBuilder().ritualCustom();
    }

    @MethodDescription(example = @Example("item('minecraft:gold_nugget')"))
    public void removeByInput(IIngredient input) {
        for (var recipe : getRegistry()) {
            if (recipe.getRequiredItems().stream().map(Ingredient::getMatchingStacks).flatMap(Arrays::stream).anyMatch(input)) {
                remove(recipe);
            }
        }
    }

    @MethodDescription(example = @Example("item('arcaneworld:biome_crystal')"))
    public void removeByOutput(IIngredient output) {
        for (var recipe : getRegistry()) {
            if (recipe instanceof RitualCreateItem createItem && output.test(createItem.getItemstack())) {
                remove(recipe);
            }
        }
    }

    public enum RitualType {
        ARENA,
        COMMAND,
        CREATE_ITEM,
        DRAGON_BREATH,
        DUNGEON,
        SUMMON,
        TIME,
        WEATHER,
        CUSTOM,
    }

    @Property(property = "input", comp = @Comp(gte = 1, lte = 5))
    @Property(property = "output", comp = @Comp(eq = 1), needsOverride = true)
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<Ritual> {

        private static final Class<?>[] ACCEPTED_CLASSES = new Class[]{
                World.class, BlockPos.class, EntityPlayer.class, ItemStack[].class
        };

        @Property(comp = @Comp(not = "null"), needsOverride = true)
        private final List<String> command = new ArrayList<>();
        @Property(defaultValue = "RitualType.CUSTOM", needsOverride = true)
        private @NotNull RitualType ritualType = RitualType.CUSTOM;
        @Property(comp = @Comp(not = "null"), needsOverride = true)
        private Class<? extends Entity> entity;
        @Property(comp = @Comp(gte = 1), needsOverride = true)
        private int time;
        @Property(comp = @Comp(not = "null"), needsOverride = true)
        private RitualWeather.WeatherType weatherType;
        @Property(comp = @Comp(not = "null"), priority = 150)
        private String translationKey;
        @Property(comp = @Comp(not = "null"), needsOverride = true)
        private Closure<Void> onActivate;

        @RecipeBuilderMethodDescription(priority = 2000)
        public RecipeBuilder ritualType(@NotNull RitualType ritualType) {
            this.ritualType = ritualType;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "ritualType")
        public RecipeBuilder ritualArena() {
            return ritualType(RitualType.ARENA);
        }

        @RecipeBuilderMethodDescription(field = "ritualType")
        public RecipeBuilder ritualCommand() {
            return ritualType(RitualType.COMMAND);
        }

        @RecipeBuilderMethodDescription(field = "ritualType")
        public RecipeBuilder ritualCreateItem() {
            return ritualType(RitualType.CREATE_ITEM);
        }

        @RecipeBuilderMethodDescription(field = "ritualType")
        public RecipeBuilder ritualDragonBreath() {
            return ritualType(RitualType.DRAGON_BREATH);
        }

        @RecipeBuilderMethodDescription(field = "ritualType")
        public RecipeBuilder ritualDungeon() {
            return ritualType(RitualType.DUNGEON);
        }

        @RecipeBuilderMethodDescription(field = "ritualType")
        public RecipeBuilder ritualSummon() {
            return ritualType(RitualType.SUMMON);
        }

        @RecipeBuilderMethodDescription(field = "ritualType")
        public RecipeBuilder ritualTime() {
            return ritualType(RitualType.TIME);
        }

        @RecipeBuilderMethodDescription(field = "ritualType")
        public RecipeBuilder ritualWeather() {
            return ritualType(RitualType.WEATHER);
        }

        @RecipeBuilderMethodDescription(field = "ritualType")
        public RecipeBuilder ritualCustom() {
            return ritualType(RitualType.CUSTOM);
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
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

        @RecipeBuilderMethodDescription(priority = 2000)
        public RecipeBuilder weatherType(RitualWeather.WeatherType weatherType) {
            this.weatherType = weatherType;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "weatherType")
        public RecipeBuilder weatherClear() {
            return weatherType(RitualWeather.WeatherType.CLEAR);
        }

        @RecipeBuilderMethodDescription(field = "weatherType")
        public RecipeBuilder weatherRain() {
            return weatherType(RitualWeather.WeatherType.RAIN);
        }

        @RecipeBuilderMethodDescription(field = "weatherType")
        public RecipeBuilder weatherThunder() {
            return weatherType(RitualWeather.WeatherType.THUNDER);
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder command(String command) {
            this.command.add(command);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder command(String... command) {
            Collections.addAll(this.command, command);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder command(List<String> command) {
            this.command.addAll(command);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder translationKey(String translationKey) {
            this.translationKey = translationKey;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder onActivate(Closure<Void> onActivate) {
            if (onActivate == null) {
                GroovyLog.msg("Arcane World Ritual onActivate closure must be defined")
                        .error()
                        .post();
                return this;
            }
            if (!Arrays.equals(onActivate.getParameterTypes(), ACCEPTED_CLASSES)) {
                GroovyLog.msg("Arcane World Ritual onActivate closure should be a closure with exactly four parameters:")
                        .add("net.minecraft.world.World world, net.minecraft.util.math.BlockPos blockPos, net.minecraft.entity.player.EntityPlayer player, net.minecraft.item.ItemStack[] itemStacks in that order.")
                        .add("but had {}, {}, {}, {} instead", (Object[]) onActivate.getParameterTypes())
                        .debug()
                        .post();
            }
            this.onActivate = onActivate;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Arcane World Ritual Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            input.trim();
            validateCustom(msg, input, 1, 5, "item input");
            validateName();
            msg.add(translationKey == null, "translationKey cannot be null.");

            switch (ritualType) {
                case ARENA, SUMMON -> msg.add(entity == null, "entity must be defined for the ritualType {}", ritualType);
                case COMMAND -> msg.add(command.isEmpty(), "command must be defined for the ritualType {}", ritualType);
                case CREATE_ITEM -> {
                    output.trim();
                    validateCustom(msg, output, 1, 1, "item output");
                }
                case TIME -> msg.add(time <= 0, "time must be greater than 0 for the ritualType {}, yet it was {}", ritualType, time);
                case WEATHER -> msg.add(weatherType == null, "weatherType must be defined for the ritualType {}", ritualType);
                case CUSTOM -> msg.add(onActivate == null, "onActivate must be defined for the ritualType {}", ritualType);
                case DRAGON_BREATH, DUNGEON -> {
                }
                default -> msg.add("ritualType must be defined");
            }
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable Ritual register() {
            if (!validate()) return null;
            var ingredients = input.stream().map(IIngredient::toMcIngredient).toArray(Ingredient[]::new);
            Ritual recipe = switch (ritualType) {
                case ARENA -> new RitualArena(entity, ingredients);
                case COMMAND -> new RitualCommand(command.toArray(new String[0]), ingredients);
                case CREATE_ITEM -> new RitualCreateItem(output.get(0), ingredients);
                case DRAGON_BREATH -> new RitualDragonBreath(ingredients);
                case DUNGEON -> new RitualDungeon(ingredients);
                case SUMMON -> new RitualSummon(entity, ingredients);
                case TIME -> new RitualTime(time, ingredients);
                case WEATHER -> new RitualWeather(weatherType, ingredients);
                case CUSTOM -> new Ritual(ingredients) {

                    @Override
                    public void onActivate(@NotNull World world, @NotNull BlockPos blockPos, EntityPlayer player, ItemStack... itemStacks) {
                        ClosureHelper.call(onActivate, world, blockPos, player, itemStacks);
                    }
                };
            };

            recipe.setRegistryName(super.name);
            recipe.setTranslationKey(translationKey);
            ModSupport.ARCANE_WORLD.get().ritual.add(recipe);
            return recipe;
        }
    }
}
