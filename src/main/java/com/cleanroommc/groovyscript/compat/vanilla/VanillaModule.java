package com.cleanroommc.groovyscript.compat.vanilla;

import groovy.lang.Binding;

public class VanillaModule {

    public static void initializeBinding(Binding binding) {
        binding.setVariable("crafting", new Crafting());
    }

}
