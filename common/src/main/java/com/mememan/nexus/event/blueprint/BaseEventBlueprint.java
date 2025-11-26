package com.mememan.nexus.event.blueprint;

import com.mememan.nexus.event.object.BaseEvent;
import com.mememan.nexus.event.result.EventResult;
import com.mememan.nexus.loader.ModSide;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;
import java.util.Optional;

public abstract class BaseEventBlueprint<BE extends BaseEvent, R> extends WrappedEventBlueprint<BE, R> {
    protected static final Map<Class<? extends BaseEvent>, BaseEventBlueprint<?, ?>> MAPPED_BLUEPRINTS = new Object2ObjectOpenHashMap<>();

    protected BaseEventBlueprint(Class<BE> eventInterface, R defaultResult, boolean isCancellable, ModSide eventSide) {
        super(eventInterface, defaultResult, isCancellable, eventSide);

        MAPPED_BLUEPRINTS.put(eventInterface, this);
    }

    public EventResult<R> fireEvent(BE event) {
        return fireEvent(finalListener -> finalListener.getResult(event));
    }

    public static <BE extends BaseEvent, R> Optional<BaseEventBlueprint<BE, R>> getBlueprintFor(Class<?> eventClazz) {
        return Optional.ofNullable((BaseEventBlueprint<BE, R>) MAPPED_BLUEPRINTS.get(eventClazz));
    }
}
