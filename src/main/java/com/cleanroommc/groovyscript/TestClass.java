package com.cleanroommc.groovyscript;

public class TestClass {

    private static int next = 20;

    private final Inner inner;
    private final int id;

    public TestClass(String name) {
        this.inner = new Inner(name);
        this.id = next++;
    }

    public Inner getInner() {
        return inner;
    }

    public int getId() {
        return id;
    }

    public class Inner {

        private final String name;

        public Inner(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }
    }
}
