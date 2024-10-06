package com.cleanroommc.groovyscript.api;

/**
 * An object that has an amount.
 * Can use '*' in groovy to set amount.
 */
public interface IResourceStack {

    int getAmount();

    void setAmount(int amount);

    IResourceStack exactCopy();

    default IResourceStack withAmount(int amount) {
        IResourceStack resourceStack = exactCopy();
        resourceStack.setAmount(amount);
        return resourceStack;
    }

    /**
     * enables groovy to use '*' operator
     */
    default IResourceStack multiply(Number n) {
        return withAmount(n.intValue());
    }
}
