package org.kohsuke.groovy.sandbox;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.Set;

/**
 * Keep track of in-scope variables on the stack.
 * <p>
 * In groovy, various statements implicitly create new scopes (as in Java), so we track them
 * in a chain.
 * <p>
 * This only tracks variables on stack (as opposed to field access and closure accessing variables
 * in the calling context.)
 *
 * @author Kohsuke Kawaguchi
 */
public final class StackVariableSet implements AutoCloseable {

    final ScopeTrackingClassCodeExpressionTransformer owner;
    final StackVariableSet parent;

    private final Set<String> names = new ObjectOpenHashSet<>();

    public StackVariableSet(ScopeTrackingClassCodeExpressionTransformer owner) {
        this.owner = owner;
        this.parent = owner.varScope;
        owner.varScope = this;
    }

    void declare(String name) {
        names.add(name);
    }

    /**
     * Is the variable of the given name in scope?
     */
    boolean has(String name) {
        for (StackVariableSet s = this; s != null; s = s.parent)
            if (s.names.contains(name))
                return true;
        return false;
    }

    @Override
    public void close() {
        owner.varScope = parent;
    }
}
