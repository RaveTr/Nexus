package com.mememan.nexus.event.base;

import com.mememan.nexus.loader.ModSide;
import org.jetbrains.annotations.Nullable;

public interface EventBlueprint<T> {

    Class<T> getEventInterface();

    @Nullable
    T mergeListeners(T[] existingListeners);

    ModSide getEventSide();
}
