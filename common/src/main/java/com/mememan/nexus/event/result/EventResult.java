package com.mememan.nexus.event.result;

import java.util.function.Consumer;

/**
 *
 * @param actualResult
 * @param cancelled
 * @param shortCircuit
 * @param <R>
 */
public record EventResult<R>(R actualResult, boolean cancelled, boolean shortCircuit) {

    public EventResult(R actualResult, boolean cancelled) {
        this(actualResult, cancelled, false);
    }

    public static <R> EventResult<R> cancelled(R actualResult, boolean shortCircuit) {
        return new EventResult<>(actualResult, true, shortCircuit);
    }

    public static <R> EventResult<R> cancelled(R actualResult) {
        return new EventResult<>(actualResult, true);
    }

    public static <R> EventResult<R> success(R actualResult) {
        return new EventResult<>(actualResult, false);
    }

    public void ifCancelled(Consumer<R> actionOnCancellation) {
        if (cancelled) actionOnCancellation.accept(actualResult);
    }

    public void ifTerminated(Consumer<R> actionOnTermination) {
        if (cancelled && shortCircuit) actionOnTermination.accept(actualResult);
    }

    public void ifSuccessful(Consumer<R> actionOnSuccess) {
        if (!cancelled) actionOnSuccess.accept(actualResult);
    }

    public void ifShortCircuit(Consumer<R> actionOnShortCircuit) {
        if (shortCircuit) actionOnShortCircuit.accept(actualResult);
    }
}
