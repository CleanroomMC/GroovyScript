package com.cleanroommc.groovyscript.compat.mods.bewitchment;

import com.bewitchment.api.BewitchmentAPI;
import com.bewitchment.api.registry.AltarUpgrade;
import com.bewitchment.common.block.tile.entity.TileEntityPlacedItem;
import com.bewitchment.registry.ModObjects;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Predicate;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES, admonition = {
        @Admonition("groovyscript.wiki.bewitchment.altar_upgrade.note0"),
        @Admonition("groovyscript.wiki.bewitchment.altar_upgrade.note1")
})
public class AltarUpgrades extends VirtualizedRegistry<Map.Entry<Predicate<BlockWorldState>, AltarUpgrade>> {

    public AltarUpgrades() {
        super(Alias.generateOf("AltarUpgrade"));
    }

    public static Map<Predicate<BlockWorldState>, AltarUpgrade> getRegistry() {
        return BewitchmentAPI.ALTAR_UPGRADES;
    }

    @Override
    public void onReload() {
        var recipes = getRegistry();
        removeScripted().forEach(x -> recipes.remove(x.getKey(), x.getValue()));
        restoreFromBackup().forEach(x -> recipes.put(x.getKey(), x.getValue()));
    }

    @RecipeBuilderDescription(example = {
            @Example(".cup().predicate(blockstate('minecraft:clay')).gain(-10).multiplier(500)"),
            @Example(".pentacle().predicate(item('minecraft:gold_ingot')).gain(1000)"),
            @Example(".sword().predicate(blockstate('minecraft:gold_block')).multiplier(50)"),
            @Example(".wand().predicate(item('minecraft:iron_ingot')).multiplier(0.5)"),
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public AltarUpgrade addCup(IBlockState state, int gain, double multiplier) {
        return recipeBuilder().type(AltarUpgrade.Type.CUP).predicate(state).gain(gain).multiplier(multiplier).register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public AltarUpgrade addCup(IIngredient stack, int gain, double multiplier) {
        return recipeBuilder().type(AltarUpgrade.Type.CUP).predicate(stack).gain(gain).multiplier(multiplier).register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public AltarUpgrade addCup(Predicate<BlockWorldState> predicate, int gain, double multiplier) {
        return recipeBuilder().type(AltarUpgrade.Type.CUP).predicate(predicate).gain(gain).multiplier(multiplier).register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public AltarUpgrade addPentacle(IBlockState state, int gain) {
        return recipeBuilder().type(AltarUpgrade.Type.PENTACLE).predicate(state).gain(gain).register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public AltarUpgrade addPentacle(IIngredient stack, int gain) {
        return recipeBuilder().type(AltarUpgrade.Type.PENTACLE).predicate(stack).gain(gain).register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public AltarUpgrade addPentacle(Predicate<BlockWorldState> predicate, int gain) {
        return recipeBuilder().type(AltarUpgrade.Type.PENTACLE).predicate(predicate).gain(gain).register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public AltarUpgrade addSword(IBlockState state, double multiplier) {
        return recipeBuilder().type(AltarUpgrade.Type.SWORD).predicate(state).multiplier(multiplier).register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public AltarUpgrade addSword(IIngredient stack, double multiplier) {
        return recipeBuilder().type(AltarUpgrade.Type.SWORD).predicate(stack).multiplier(multiplier).register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public AltarUpgrade addSword(Predicate<BlockWorldState> predicate, double multiplier) {
        return recipeBuilder().type(AltarUpgrade.Type.SWORD).predicate(predicate).multiplier(multiplier).register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public AltarUpgrade addWand(IBlockState state, double multiplier) {
        return recipeBuilder().type(AltarUpgrade.Type.WAND).predicate(state).multiplier(multiplier).register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public AltarUpgrade addWand(IIngredient stack, double multiplier) {
        return recipeBuilder().type(AltarUpgrade.Type.WAND).predicate(stack).multiplier(multiplier).register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public AltarUpgrade addWand(Predicate<BlockWorldState> predicate, double multiplier) {
        return recipeBuilder().type(AltarUpgrade.Type.WAND).predicate(predicate).multiplier(multiplier).register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public boolean add(Predicate<BlockWorldState> state, AltarUpgrade upgrade) {
        getRegistry().put(state, upgrade);
        doAddScripted(Pair.of(state, upgrade));
        return true;
    }

    @MethodDescription
    public boolean remove(BlockWorldState state) {
        return getRegistry().entrySet().removeIf(entry -> entry.getKey().test(state) && doAddBackup(entry));
    }

    @MethodDescription(example = @Example("item('bewitchment:garnet')"))
    public boolean remove(ItemStack stack) {
        var placedItem = new TileEntityPlacedItem();
        placedItem.getInventories()[0].insertItem(0, stack, false);
        //noinspection DataFlowIssue
        return remove(new BlockWorldState(null, null, false) {
            @Override
            public @NotNull IBlockState getBlockState() {
                return ModObjects.placed_item.getDefaultState();
            }

            @Override
            public TileEntity getTileEntity() {
                return placedItem;
            }
        });
    }

    @MethodDescription(example = @Example("blockstate('bewitchment:goblet')"))
    public boolean remove(IBlockState state) {
        //noinspection DataFlowIssue
        return remove(new BlockWorldState(null, null, false) {
            @Override
            public @NotNull IBlockState getBlockState() {
                return state;
            }

            @Override
            public TileEntity getTileEntity() {
                return null;
            }
        });
    }

    @MethodDescription(example = @Example("com.bewitchment.api.registry.AltarUpgrade.Type.WAND"))
    public void removeByType(AltarUpgrade.Type type) {
        getRegistry().entrySet().removeIf(x -> x.getValue().type == type && doAddBackup(x));
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        getRegistry().entrySet().removeIf(this::doAddBackup);
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<AltarUpgrade> {

        @Property(comp = @Comp(not = "null"))
        private Predicate<BlockWorldState> predicate;
        @Property(comp = @Comp(not = "null"))
        private AltarUpgrade.Type type;
        @Property(comp = @Comp(unique = "groovyscript.wiki.bewitchment.altar_upgrade.gain.required"))
        private int gain;
        @Property(comp = @Comp(unique = "groovyscript.wiki.bewitchment.altar_upgrade.multiplier.required"))
        private double multiplier;

        @RecipeBuilderMethodDescription
        public RecipeBuilder predicate(Predicate<BlockWorldState> predicate) {
            this.predicate = predicate;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder predicate(IBlockState state) {
            return predicate(s -> s.getBlockState().equals(state));
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder predicate(IIngredient stack) {
            return predicate(state -> {
                if (state.getBlockState().getBlock() == ModObjects.placed_item && state.getTileEntity() instanceof TileEntityPlacedItem placed) {
                    return stack.test(placed.getInventories()[0].getStackInSlot(0));
                }
                return false;
            });
        }

        @RecipeBuilderMethodDescription(field = "type")
        public RecipeBuilder cup() {
            return type(AltarUpgrade.Type.CUP);
        }

        @RecipeBuilderMethodDescription(field = "type")
        public RecipeBuilder pentacle() {
            return type(AltarUpgrade.Type.PENTACLE);
        }

        @RecipeBuilderMethodDescription(field = "type")
        public RecipeBuilder sword() {
            return type(AltarUpgrade.Type.SWORD);
        }

        @RecipeBuilderMethodDescription(field = "type")
        public RecipeBuilder wand() {
            return type(AltarUpgrade.Type.WAND);
        }

        @RecipeBuilderMethodDescription(priority = 1002)
        public RecipeBuilder type(String type) {
            return type(AltarUpgrade.Type.valueOf(type));
        }

        @RecipeBuilderMethodDescription(priority = 1001)
        public RecipeBuilder type(AltarUpgrade.Type type) {
            this.type = type;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder gain(int gain) {
            this.gain = gain;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder multiplier(double multiplier) {
            this.multiplier = multiplier;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Bewitchment Altar Upgrade";
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        /**
         * What the types are used for is set in {@link com.bewitchment.common.block.tile.entity.TileEntityWitchesAltar#scan}
         */
        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg);
            msg.add(predicate == null, "predicate cannot be null, but it was null");
            if (type == null) {
                msg.add("type cannot be null, but it was null");
            } else {
                switch (type) {
                    case PENTACLE -> msg.add(multiplier != 0, "multiplier was used for the type {}, has no use for that type", type);
                    case SWORD, WAND -> msg.add(gain != 0, "gain was used for the type {}, has no use for that type", type);
                }
                // gain and/or multipliers can be negative, so we can only check for unused stats.
                //switch (type) {
                //    case CUP -> msg.add(gain <= 0 && multiplier <= 0, "either gain or multiplier must be greater than 0 with the type {}, but gain was {} and multiplier was {}", type, gain, multiplier);
                //    case PENTACLE -> msg.add(gain <= 0, "gain must be greater than 0 with the type {}, but it was {}", type, gain);
                //    case SWORD, WAND -> msg.add(multiplier <= 0, "multiplier must be greater than 0 with the type {}, but it was {}", type, multiplier);
                //}
            }
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable AltarUpgrade register() {
            if (!validate()) return null;
            var upgrade = new AltarUpgrade(type, gain, multiplier);
            ModSupport.BEWITCHMENT.get().altarUpgrade.add(predicate, upgrade);
            return upgrade;
        }
    }
}
