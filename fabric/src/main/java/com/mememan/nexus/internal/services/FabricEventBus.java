package com.mememan.nexus.internal.services;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.loader.ModSide;
import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.platform.services.EventBus;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FabricEventBus implements EventBus {
    private static final Object2ObjectOpenHashMap<ModSide, Object2ObjectOpenHashMap<EventKey<?>, Event<?>>> MAPPED_EVENTS = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<EventKey<?>, Int2ObjectOpenHashMap<ObjectArrayList<?>>> LISTENERS_BY_PRIORITY = new Object2ObjectOpenHashMap<>();
    private final Object sideBasedThreadLock = new Object();

    @Override
    public <T> void registerEventHook(EventKey<T> eventKey, Function<T[], T> eventListenerMerger, ModSide eventSide) {
        Class<T> eventInterface = eventKey.getEventInterface();

        synchronized (sideBasedThreadLock) {
            Event<T> mappedEvent = (Event<T>) MAPPED_EVENTS
                    .computeIfAbsent(eventSide, side -> new Object2ObjectOpenHashMap<>())
                    .putIfAbsent(eventKey, EventFactory.createArrayBacked(eventInterface, eventListenerMerger));

            if (mappedEvent != null) {
                throw new IllegalArgumentException(String.format(
                        "Event of type %s%s is already mapped to ModSide: %s",
                        eventInterface.getName(), eventKey.getAssociatedTypes().isEmpty()
                                ? ""
                                : "[" + eventKey.getAssociatedTypes().stream()
                                .map(Class::getName)
                                .collect(Collectors.joining(", ")) + "]",
                        eventSide.getSideName()
                ));
            }
        }
    }

    @Override
    public <T> void onEvent(Class<T> eventInterface, T listener, ModSide listenerTypeSide, int listenerPriority, Class<?>... associatedEventTypes) {
        Object2ObjectOpenHashMap<EventKey<?>, Event<?>> eventByType = MAPPED_EVENTS.get(listenerTypeSide);

        if (eventByType == null || eventByType.isEmpty()) {
            NexusConstants.LOGGER.debug("ModSide {} has no mapped events, skipping listener registration until state changes...", listenerTypeSide.getSideName());
            return;
        }

        EventKey<T> eventKey = new EventKey<>(eventInterface, associatedEventTypes);
        Event<T> mappedEvent = eventByType.get(eventKey) == null ? null : (Event<T>) eventByType.get(eventKey);

        if (mappedEvent == null) {
            NexusConstants.LOGGER.warn("Event of type {}{} is not mapped to ModSide: {}, skipping listener registration until state changes...",
                    eventInterface.getName(), eventKey.getAssociatedTypes().isEmpty()
                            ? ""
                            : "[" + eventKey.getAssociatedTypes().stream()
                            .map(Class::getName)
                            .collect(Collectors.joining(", ")) + "]",
                    listenerTypeSide.getSideName());
            return;
        }

        synchronized (sideBasedThreadLock) {
            ObjectArrayList<T> listenersByPriority = (ObjectArrayList<T>) LISTENERS_BY_PRIORITY
                    .computeIfAbsent(new EventKey<>(eventInterface, associatedEventTypes), eventInterfaceClazz -> new Int2ObjectOpenHashMap<>())
                    .computeIfAbsent(listenerPriority, priority -> {
                        mappedEvent.addPhaseOrdering(NexusConstants.prefix("event_priority_" + listenerPriority), NexusConstants.prefix("event_priority_" + (listenerPriority + 1)));
                        return new ObjectArrayList<T>();
                    });

            if (!listenersByPriority.contains(listener)) {
                mappedEvent.register(NexusConstants.prefix("event_priority_" + listenerPriority), listener);

                listenersByPriority.add(listener);
            }
        }
    }

    @Override
    public <T, R> R fireEventHook(Class<T> eventInterface, Function<T, R> eventListenerInvokerMapper, ModSide eventSide, Class<?>... associatedEventTypes) {
        Object2ObjectOpenHashMap<EventKey<?>, Event<?>> eventByType = MAPPED_EVENTS.get(eventSide);

        if (eventByType == null || eventByType.isEmpty()) {
            NexusConstants.LOGGER.debug("ModSide {} has no mapped events, skipping event invocation/posting/firing until state changes...", eventSide.getSideName());
            return null;
        }

        EventKey<T> eventKey = new EventKey<>(eventInterface, associatedEventTypes);
        Event<T> mappedEvent = eventByType.get(eventKey) == null ? null : (Event<T>) eventByType.get(eventKey);

        if (mappedEvent == null && eventSide != ModSide.COMMON) {
            mappedEvent = (Event<T>) MAPPED_EVENTS.getOrDefault(ModSide.COMMON, new Object2ObjectOpenHashMap<>()).get(eventKey);
        }

        if (mappedEvent == null) {
            NexusConstants.LOGGER.warn("Event of type {}{} is not mapped to ModSide: {}, skipping event invocation/posting/firing until state changes...",
                    eventInterface.getName(), eventKey.getAssociatedTypes().isEmpty()
                            ? ""
                            : "[" + eventKey.getAssociatedTypes().stream()
                            .map(Class::getName)
                            .collect(Collectors.joining(", ")) + "]",
                    eventSide.getSideName());
            return null;
        }

        if (!NexusServices.PLATFORM_MANAGER.getEnvironmentSide().pertainsTo(eventSide)) {
            NexusConstants.LOGGER.warn("Attempted to invoke listeners for event of type {}{} on EnvironmentSide {} (physical side), but provided ModSide was {} (logical side). Skipping listener invocation on current side...",
                    eventInterface.getName(), eventKey.getAssociatedTypes().isEmpty()
                            ? ""
                            : "[" + eventKey.getAssociatedTypes().stream()
                            .map(Class::getName)
                            .collect(Collectors.joining(", ")) + "]",
                    NexusServices.PLATFORM_MANAGER.getEnvironmentSide().getSideName(), eventSide.getSideName());
            return null;
        }

        return eventListenerInvokerMapper.apply(mappedEvent.invoker());
    }

    @Override
    public <T> Map<Integer, List<T>> getListenersFor(Class<T> eventInterface, Class<?>... associatedEventTypes) {
        Int2ObjectOpenHashMap<ObjectArrayList<?>> classifiedListenersByPriority = LISTENERS_BY_PRIORITY.get(new EventKey<>(eventInterface, associatedEventTypes));
        return classifiedListenersByPriority == null ? Map.of() : new Int2ObjectOpenHashMap<>(classifiedListenersByPriority).int2ObjectEntrySet().stream()
                .map(curEntry -> new Int2ObjectMap.Entry<ObjectArrayList<T>>() {
                    @Override
                    public ObjectArrayList<T> getValue() {
                        return (ObjectArrayList<T>) curEntry.getValue();
                    }

                    @Override
                    public ObjectArrayList<T> setValue(ObjectArrayList<T> value) {
                        return (ObjectArrayList<T>) curEntry.setValue(value);
                    }

                    @Override
                    public int getIntKey() {
                        return curEntry.getIntKey();
                    }
                })
                .collect(Collectors.toMap(Int2ObjectMap.Entry::getIntKey, Int2ObjectMap.Entry::getValue, (a, b) -> a, Int2ObjectOpenHashMap::new));
    }
}
