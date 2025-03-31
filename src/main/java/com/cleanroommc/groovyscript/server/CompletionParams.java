package com.cleanroommc.groovyscript.server;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CompletionParams {

    private static final Object UNPARSABLE_MARKER = new Object();
    public static final CompletionParams EMPTY = new CompletionParams();

    public static CompletionParams addParam(CompletionParams params, Object param) {
        if (params == EMPTY) params = new CompletionParams();
        params.params.add(param);
        return params;
    }

    public static CompletionParams addUnparsableParam(CompletionParams params) {
        return addParam(params, UNPARSABLE_MARKER);
    }

    private final List<Object> params = new ArrayList<>(4);

    private CompletionParams() {}

    public Object getParam(int i) {
        if (getParamCount() <= i) return null;
        Object o = params.get(i);
        return o == UNPARSABLE_MARKER ? null : o;
    }

    public int getParamCount() {
        return params.size();
    }

    public boolean isParamUnparsable(int index) {
        return getParamCount() > index && params.get(index) == UNPARSABLE_MARKER;
    }

    public boolean isParamType(int index, @Nullable Class<?> type) {
        if (getParamCount() <= index) return false;
        Object o = params.get(index);
        return o == null || o == UNPARSABLE_MARKER ? type == null : type != null && type.isAssignableFrom(o.getClass());
    }

    public <T> T getParamAsType(int index, @NotNull Class<T> type) {
        if (getParamCount() <= index) return null;
        Object o = params.get(index);
        return o != null && o != UNPARSABLE_MARKER && type.isAssignableFrom(o.getClass()) ? type.cast(o) : null;
    }
}
