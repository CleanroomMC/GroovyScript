package com.cleanroommc.groovyscript.compat.mods.astralsorcery.starlightaltar;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.astralsorcery.Utils;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class AltarRecipeBuilder extends AbstractRecipeBuilder<AbstractAltarRecipe> {
    protected String name;
    protected ItemHandle[] inputs = null;
    protected ItemHandle[] outerInputs = null;
    protected int starlightRequired = 0;
    protected int craftingTickTime = 0;
    private TileAltar.AltarLevel altarLevel;
    private String[] keyBasedMatrix;
    private IConstellation requiredConstellation = null;
    private final ArrayList<IIngredient> outerIngredients = new ArrayList<>();
    private final Char2ObjectOpenHashMap<IIngredient> keyMap = new Char2ObjectOpenHashMap<>();

    public AltarRecipeBuilder name(String name) {
        this.name = name;
        return this;
    }

    public AltarRecipeBuilder input(ItemHandle[] inputs) {
        this.inputs = inputs;
        return this;
    }

    public AltarRecipeBuilder row(String row) {
        if (this.keyBasedMatrix == null)
            this.keyBasedMatrix = new String[]{row};
        else
            this.keyBasedMatrix = ArrayUtils.add(this.keyBasedMatrix, row);
        return this;
    }

    public AltarRecipeBuilder matrix(String... matrix) {
        this.keyBasedMatrix = matrix;
        return this;
    }

    public AltarRecipeBuilder shape(String... matrix) {
        this.keyBasedMatrix = matrix;
        return this;
    }

    public AltarRecipeBuilder key(String c, IIngredient item) {
        if (c == null || c.length() != 1) {
            GroovyLog.msg("Error adding Starlight Altar recipe: ")
                    .add("key must be a single char but found '" + c + "'")
                    .error()
                    .post();
            return this;
        }
        this.keyMap.put(c.charAt(0), item);
        return this;
    }

    public AltarRecipeBuilder altarLevel(final int level) {
        switch (level) {
            case 0:
                return this.setDiscovery();
            case 1:
                return this.setAttunement();
            case 2:
                return this.setConstellationCraft();
            case 3:
                return this.setTraitCraft();
            default:
                GroovyLog.msg("Error adding Astral Sorcery Altar recipe")
                        .add("Altar tier out of bounds (use 0-3)")
                        .error()
                        .post();
                return null;
        }
    }

    public AltarRecipeBuilder setDiscovery() {
        this.altarLevel = TileAltar.AltarLevel.DISCOVERY;
        return this;
    }

    public AltarRecipeBuilder setAttunement() {
        this.altarLevel = TileAltar.AltarLevel.ATTUNEMENT;
        return this;
    }

    public AltarRecipeBuilder setConstellationCraft() {
        this.altarLevel = TileAltar.AltarLevel.CONSTELLATION_CRAFT;
        return this;
    }

    public AltarRecipeBuilder setTraitCraft() {
        this.altarLevel = TileAltar.AltarLevel.TRAIT_CRAFT;
        return this;
    }

    public AltarRecipeBuilder starlight(int starlight) {
        this.starlightRequired = starlight;
        return this;
    }

    public AltarRecipeBuilder craftTime(int ticks) {
        this.craftingTickTime = ticks;
        return this;
    }

    public AltarRecipeBuilder constellation(IConstellation constellation) {
        this.requiredConstellation = constellation;
        return this;
    }

    public AltarRecipeBuilder outerInput(IIngredient ing) {
        this.outerIngredients.add(ing);
        return this;
    }

    public AltarRecipeBuilder outerInput(IIngredient... ings) {
        this.outerIngredients.addAll(Arrays.asList(ings));
        return this;
    }

    public AltarRecipeBuilder outerInput(Collection<IIngredient> ings) {
        this.outerIngredients.addAll(ings);
        return this;
    }

    private String flattenMatrix() {
        ItemHandle[] in = AltarInputOrder.initInputList(this.altarLevel);
        int[][] map = AltarInputOrder.getMap(this.altarLevel);
        if (map == null || in == null) return "Altar level not valid.";

        for (int i = 0; i < this.keyBasedMatrix.length; i++) {
            String row = this.keyBasedMatrix[i];
            if (row.length() != this.keyBasedMatrix.length) return "Recipe matrix is not 3x3 or 5x5.";
            if (this.altarLevel == TileAltar.AltarLevel.DISCOVERY) {
                if (row.length() != 3) return "Wrong size recipe for the given table.";
            } else {
                if (row.length() != 5) return "Wrong size recipe for the given table.";
            }
            for (int j = 0; j < row.length(); j++) {
                if (map[i][j] >= 0 && row.charAt(j) != ' ') {
                    in[map[i][j]] = Utils.convertToItemHandle( keyMap.get(row.charAt(j)) );
                }
            }
        }

        this.input(in);

        ItemHandle[] outerIn = new ItemHandle[this.outerIngredients.size()];
        for (int j = 0; j < this.outerIngredients.size(); j++)
            outerIn[j] = Utils.convertToItemHandle(this.outerIngredients.get(j));
        this.outerInputs = outerIn;

        return "";
    }

    @Override
    public String getErrorMsg() {
        return "Error adding Astral Sorcery Starlight Alter recipe";
    }

    @Override
    public void validate(GroovyLog.Msg msg) {
        validateItems(msg, 0, 0, 1, 1);
        if (this.starlightRequired < 0) this.starlightRequired = 0;
        if (this.craftingTickTime < 0) this.craftingTickTime = 0;

        switch(this.altarLevel) {
            case DISCOVERY:
                if (this.starlightRequired > 1000)
                    GroovyLog.msg("Warning: Discovery Altar recipe cannot exceed 1000 starlight, clamping starlight to max table value.").warn().post();
                this.starlightRequired = Math.min(starlightRequired, 1000);
            case ATTUNEMENT:
                if (this.starlightRequired > 2000)
                    GroovyLog.msg("Warning: Attunement Altar recipe cannot exceed 2000 starlight, clamping starlight to max table value.").warn().post();
                this.starlightRequired = Math.min(starlightRequired, 2000);
            case CONSTELLATION_CRAFT:
                if (this.starlightRequired > 4000)
                    GroovyLog.msg("Warning: Constellation Altar recipe cannot exceed 4000 starlight, clamping starlight to max table value.").warn().post();
                this.starlightRequired = Math.min(starlightRequired, 4000);
            case TRAIT_CRAFT:
                if (this.starlightRequired > 8000)
                    GroovyLog.msg("Warning: Trait Altar recipe cannot exceed 8000 starlight, clamping starlight to max table value.").warn().post();
                this.starlightRequired = Math.min(starlightRequired, 8000);
        }

        String errors = "";
        if (inputs == null) errors = this.flattenMatrix();
        String finalErrors = errors;
        msg.add(!errors.equals(""), () -> finalErrors);
    }

    @Override
    public @Nullable AbstractAltarRecipe register() {
        if (!validate()) return null;
        return ModSupport.ASTRAL_SORCERY.get().altar.add(this.name, this.output.get(0), this.inputs, this.starlightRequired, this.craftingTickTime, this.altarLevel, this.requiredConstellation, this.outerInputs);
    }
}