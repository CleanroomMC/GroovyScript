package com.cleanroommc.groovyscript.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;

/**
 * An interface that either holds a value or an error.
 *
 * @param <T> value type
 */
public interface Result<T> {

    static <T> Result<T> error() {
        return error(null);
    }

    static <T> Result<T> error(String error) {
        return new Error<>(error);
    }

    static <T> Result<T> error(@NotNull String error, Object... arguments) {
        return error(GroovyLog.format(error, arguments));
    }

    static <T> Result<T> some(@NotNull T value) {
        return new Some<>(value);
    }

    @Nullable
    String getError();

    boolean hasError();

    @NotNull
    T getValue();

    @SuppressWarnings("ClassCanBeRecord")
    class Some<T> implements Result<T> {

        private final T value;

        public Some(@NotNull T value) {
            this.value = value;
        }

        @Override
        public boolean hasError() {
            return false;
        }

        @Override
        public @Nullable String getError() {
            return null;
        }

        @Override
        public @NotNull T getValue() {
            return this.value;
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    class Error<T> implements Result<T> {

        private final String error;

        public Error(String error) {
            this.error = error;
        }

        @Override
        public boolean hasError() {
            return true;
        }

        @Override
        public @Nullable String getError() {
            return this.error;
        }

        @Override
        public @NotNull T getValue() {
            throw new NoSuchElementException();
        }
    }
}
