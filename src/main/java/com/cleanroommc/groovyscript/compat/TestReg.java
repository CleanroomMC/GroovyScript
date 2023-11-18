package com.cleanroommc.groovyscript.compat;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.Set;

public class TestReg extends VirtualizedRegistry<String> {

    private static final Set<String> REG = new ObjectOpenHashSet<>();

    public TestReg() {
        super(false, generateAliases("Test"));
    }

    @Override
    public void onReload() {
        REG.addAll(restoreFromBackup());
        removeScripted().forEach(REG::remove);
    }

    public void add(String s) {
        REG.add(s);
        addScripted(s);
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public static class RecipeBuilder {

        private final StringBuilder builder = new StringBuilder();

        public RecipeBuilder append(Object o) {
            builder.append(o.toString());
            return this;
        }

        public String register() {
            String s = builder.toString();
            TestModGroovyPlugin.getInstance().test.add(s);
            GroovyLog.get().infoMC("Added String '{}'", s);
            return s;
        }
    }
}
