package com.mememan.nexus.event.result;

import com.mememan.nexus.event.blueprint.EventBlueprint;
import com.mememan.nexus.event.blueprint.WrappedEventBlueprint;
import com.mememan.nexus.event.object.BaseEvent;

import java.util.function.Consumer;

/**
 * {@code record} representing a result of some given event (typically wrapped and used in {@link WrappedEventBlueprint}).
 *
 * @param actualResult The actual object stored within this result.
 * @param cancelled Whether this result should be marked as cancelled.
 * @param shortCircuit Whether this result should short-circuit/halt listener execution if any listener happens to return
 *                     it.
 *
 * @param <R> The actual object type of this result.
 *
 * @see EventBlueprint
 * @see WrappedEventBlueprint
 * @see BaseEvent
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

    public static <R> EventResult<R> pass() {
        return new EventResult<>(null, false);
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
