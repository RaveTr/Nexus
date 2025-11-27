package com.mememan.nexus.event.blueprint;

import com.mememan.nexus.event.listener.EventListener;
import com.mememan.nexus.event.object.BaseEvent;
import com.mememan.nexus.event.result.EventResult;
import com.mememan.nexus.loader.ModSide;
import com.mememan.nexus.util.ReflectionUtil;
import it.unimi.dsi.fastutil.booleans.BooleanObjectMutablePair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class PropagatingEventBlueprint<BE extends BaseEvent, R> extends BaseEventBlueprint<BE, R> {
    protected static final Map<Class<? extends BaseEvent>, BooleanObjectMutablePair<Map<Class<? extends BaseEvent>, List<EventListener<?, ?>>>>> RAW_EVENT_SUPERCLASS_LISTENERS = new Object2ObjectOpenHashMap<>();

    protected PropagatingEventBlueprint(Class<BE> eventInterface, R defaultResult, boolean isCancellable, ModSide eventSide) {
        super(eventInterface, defaultResult, isCancellable, eventSide);
    }

    @Override
    public void onEvent(EventListener<BE, R> listener, int listenerPriority) {
        super.onEvent(listener, listenerPriority);
    }

    @Override
    public <U> U fireEvent(Function<EventListener<BE, R>, U> eventMapper, ModSide firingSide) {
        Class<? super BE> eventSuperclass = eventInterface.getSuperclass();
        AtomicBoolean cancelledThroughPropagation = new AtomicBoolean(false);
        AtomicBoolean shortCircuitedThroughPropagation = new AtomicBoolean(false);
        AtomicReference<R> mutResult = new AtomicReference<>(defaultResult);

        while (BaseEvent.class.isAssignableFrom(eventSuperclass)) {
            Optional<BaseEventBlueprint<BaseEvent, Object>> potentiallyMappedBlueprint = getBlueprintFor(eventSuperclass);
            final Class<? super BE> eventSuperclassCopy = eventSuperclass;

            if (potentiallyMappedBlueprint.isPresent()) {
                BaseEventBlueprint<BaseEvent, Object> inferredBlueprint = potentiallyMappedBlueprint.get();

                U parentResult = ((BaseEventBlueprint<BE, R>) inferredBlueprint).fireEvent(eventMapper, firingSide);

                if (parentResult instanceof EventResult<?> parentEventResult) {
                    mutResult.set(mergeListenerResults(mutResult.get(), (R) parentEventResult.actualResult()));
                    cancelledThroughPropagation.set(cancelledThroughPropagation.get() || parentEventResult.cancelled());
                    shortCircuitedThroughPropagation.set(shortCircuitedThroughPropagation.get() || parentEventResult.shortCircuit());
                }

                if (inferredBlueprint instanceof PropagatingEventBlueprint<BaseEvent, Object>) break; // It'll handle the recursive traversal up on its own atp, so break to avoid firing multiple times
            } else {
                BooleanObjectMutablePair<Map<Class<? extends BaseEvent>, List<EventListener<?, ?>>>> superClazzListeners = RAW_EVENT_SUPERCLASS_LISTENERS.computeIfAbsent(getActualEventType(), oK -> new BooleanObjectMutablePair<>(true, new Object2ObjectOpenHashMap<>()));

                superClazzListeners.right()
                        .compute((Class<? extends BaseEvent>) eventSuperclassCopy, (eventSuperClazz, mappedListeners) -> {
                            if (mappedListeners != null && !superClazzListeners.leftBoolean()) {
                                return mappedListeners;
                            } else {
                                if (superClazzListeners.leftBoolean()) superClazzListeners.first(false);

                                return getAllListeners().stream()
                                        .filter(curListener -> ReflectionUtil.compareGenericInterfaceType(EventListener.class, eventSuperClazz))
                                        .collect(Collectors.toCollection(ObjectArrayList::new));
                            }
                        })
                        .forEach(listener -> {
                            U propagatedResult = eventMapper.apply((EventListener<BE, R>) listener);

                            if (propagatedResult instanceof EventResult<?> eventResult) {
                                mutResult.set(mergeListenerResults(mutResult.get(), (R) eventResult.actualResult()));
                                cancelledThroughPropagation.set(cancelledThroughPropagation.get() || eventResult.cancelled());
                                shortCircuitedThroughPropagation.set(shortCircuitedThroughPropagation.get() || eventResult.shortCircuit());
                            }
                        });
            }

            eventSuperclass = eventSuperclass.getSuperclass();
        }

        U result = super.fireEvent(eventMapper, firingSide);

        if (result instanceof EventResult<?> eventResult) { // Direct handling here to shortcut annoying overrides that would otherwise be required
            return (U) new EventResult<>(mergeListenerResults(mutResult.get(), (R) eventResult.actualResult()), eventResult.cancelled() || cancelledThroughPropagation.get(), eventResult.shortCircuit() || shortCircuitedThroughPropagation.get());
        }

        return result;
    }
}
