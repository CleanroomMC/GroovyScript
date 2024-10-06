package com.cleanroommc.groovyscript.api;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public interface IMarkable {

    @Nullable
    String getMark();

    void setMark(String mark);

    default boolean hasMark() {
        return !StringUtils.isEmpty(getMark());
    }

    default IMarkable mark(String mark) {
        setMark(mark);
        return this;
    }
}
