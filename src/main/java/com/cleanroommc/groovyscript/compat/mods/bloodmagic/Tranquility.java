package com.cleanroommc.groovyscript.compat.mods.bloodmagic;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.incense.EnumTranquilityType;
import WayofTime.bloodmagic.incense.TranquilityStack;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.bloodmagic.BloodMagicValueManagerAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class Tranquility extends VirtualizedRegistry<Pair<IBlockState, TranquilityStack>> {

    public Tranquility() {
        super();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(pair -> ((BloodMagicValueManagerAccessor) BloodMagicAPI.INSTANCE.getValueManager()).getTranquility().remove(pair.getKey(), pair.getValue()));
        restoreFromBackup().forEach(pair -> ((BloodMagicValueManagerAccessor) BloodMagicAPI.INSTANCE.getValueManager()).getTranquility().put(pair.getKey(), pair.getValue()));
    }

    public void add(Block block, String tranquility, double value) {
        for (IBlockState state : block.getBlockState().getValidStates()) {
            add(state, tranquility, value);
        }
    }

    public void add(Block block, TranquilityStack tranquility) {
        for (IBlockState state : block.getBlockState().getValidStates()) {
            add(state, tranquility);
        }
    }

    public void add(IBlockState blockstate, String tranquility, double value) {
        for (EnumTranquilityType type : EnumTranquilityType.values()) {
            if (type.name().equalsIgnoreCase(tranquility)) {
                add(blockstate, new TranquilityStack(type, value));
                return;
            }
        }
        GroovyLog.msg("Error adding or adjusting Blood Magic Tranquility")
                .add("could not find tranquility type with string {}", tranquility)
                .error()
                .post();
    }

    public void add(IBlockState blockstate, TranquilityStack tranquility) {
        addScripted(Pair.of(blockstate, tranquility));
        ((BloodMagicValueManagerAccessor) BloodMagicAPI.INSTANCE.getValueManager()).getTranquility().put(blockstate, tranquility);
    }

    public void remove(Block block, String tranquility) {
        for (IBlockState state : block.getBlockState().getValidStates()) {
            remove(state, tranquility);
        }
    }

    public void remove(Block block, EnumTranquilityType tranquility) {
        for (IBlockState state : block.getBlockState().getValidStates()) {
            remove(state, tranquility);
        }
    }

    public void remove(IBlockState blockstate, String tranquility) {
        for (EnumTranquilityType type : EnumTranquilityType.values()) {
            if (type.name().equalsIgnoreCase(tranquility)) {
                remove(blockstate, type);
                return;
            }
        }
        GroovyLog.msg("Error removing Blood Magic Tranquility")
                .add("could not find tranquility type with string {}", tranquility)
                .error()
                .post();
    }

    public boolean remove(IBlockState blockstate, EnumTranquilityType tranquility) {
        for (Map.Entry<IBlockState, TranquilityStack> entry : BloodMagicAPI.INSTANCE.getValueManager().getTranquility().entrySet()) {
            if (entry.getKey() == blockstate && entry.getValue().type == tranquility) {
                TranquilityStack stack = entry.getValue();
                addBackup(Pair.of(entry.getKey(), entry.getValue()));
                ((BloodMagicValueManagerAccessor) BloodMagicAPI.INSTANCE.getValueManager()).getTranquility().remove(blockstate, stack);
                return true;
            }
        }
        GroovyLog.msg("Error removing Blood Magic Tranquility")
                .add("could not find tranquility entry with blockstate {} and enum {}", blockstate, tranquility.name())
                .error()
                .post();
        return false;
    }

    public void removeAll() {
        ((BloodMagicValueManagerAccessor) BloodMagicAPI.INSTANCE.getValueManager()).getTranquility().forEach((l, r) -> this.addBackup(Pair.of(l, r)));
        ((BloodMagicValueManagerAccessor) BloodMagicAPI.INSTANCE.getValueManager()).getTranquility().clear();
    }

    public SimpleObjectStream<Map.Entry<IBlockState, TranquilityStack>> streamRecipes() {
        return new SimpleObjectStream<>(((BloodMagicValueManagerAccessor) BloodMagicAPI.INSTANCE.getValueManager()).getTranquility().entrySet())
                .setRemover(r -> this.remove(r.getKey(), r.getValue().type));
    }


    public static class RecipeBuilder {

        private IBlockState blockstate;
        private Block block;
        private EnumTranquilityType tranquility;
        private double value;

        public RecipeBuilder blockstate(IBlockState blockstate) {
            this.blockstate = blockstate;
            if (this.block != null) {
                this.block = null;
                GroovyLog.msg("Setting via blockstate removes the block from the builder")
                        .warn()
                        .post();
            }
            return this;
        }

        public RecipeBuilder block(Block block) {
            this.block = block;
            if (this.blockstate != null) {
                this.blockstate = null;
                GroovyLog.msg("Setting via block removes the blockstate from the builder")
                        .warn()
                        .post();
            }
            return this;
        }

        public RecipeBuilder tranquility(EnumTranquilityType tranquility) {
            this.tranquility = tranquility;
            return this;
        }

        public RecipeBuilder tranquility(String tranquility) {
            StringBuilder names = new StringBuilder();
            for (EnumTranquilityType type : EnumTranquilityType.values()) {
                names.append(type.name().toLowerCase(Locale.ROOT)).append(", ");
                if (type.name().equalsIgnoreCase(tranquility)) {
                    this.tranquility = type;
                    return this;
                }
            }
            GroovyLog.msg("Tranquility string not found. The options are: {}, yet found {}", names.deleteCharAt(names.length() - 1).toString(), tranquility)
                    .warn()
                    .post();
            return this;
        }

        public RecipeBuilder value(double value) {
            this.value = value;
            return this;
        }

        public String getErrorMsg() {
            return "Error adding Blood Magic Tranquility key recipe";
        }

        public boolean validate() {
            GroovyLog.Msg msg = GroovyLog.msg(getErrorMsg()).error();

            msg.add(blockstate == null && block == null, "either blockstate or block must be non null");
            msg.add(tranquility == null, "tranquility must be a string matching one of the Enums ({})", Arrays.stream(EnumTranquilityType.values()).map(Enum::name).collect(Collectors.joining(", ")));
            msg.add(value < 0, "value must be a nonnegative integer, yet it was {}", value);
            return !msg.postIfNotEmpty();
        }

        public @Nullable Object register() {
            if (!validate()) return null;
            TranquilityStack stack = new TranquilityStack(tranquility, value);
            if (block != null) ModSupport.BLOOD_MAGIC.get().tranquility.add(block, stack);
            else if (blockstate != null) ModSupport.BLOOD_MAGIC.get().tranquility.add(blockstate, stack);
            return null;
        }
    }
}
