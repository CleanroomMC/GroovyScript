package com.cleanroommc.groovyscript.api;

/**
 * An object that has an amount.
 * Can use '*' in groovy to set amount.
 */
public interface IResourceStack {

    int getAmount();

    void setAmount(int amount);

    default IResourceStack withAmount(int amount) {
        setAmount(amount);
        return this;
    }

    /**
     * enables groovy to use '*' operator
     */
    default IResourceStack multiply(Number n) {
        return withAmount(n.intValue());
    }
}
