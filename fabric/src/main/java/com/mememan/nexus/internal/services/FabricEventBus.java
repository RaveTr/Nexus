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
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FabricEventBus implements EventBus {
    private static final Object2ObjectOpenHashMap<ModSide, Object2ObjectOpenHashMap<Class<?>, Event<?>>> MAPPED_EVENTS = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<Class<?>, Int2ObjectOpenHashMap<ObjectArrayList<?>>> LISTENERS_BY_PRIORITY = new Object2ObjectOpenHashMap<>();
    private final Object sideBasedThreadLock = new Object();

    @Override
    public <T> void registerEventHook(Class<T> eventInterface, Function<T[], T> eventListenerMerger, ModSide eventSide) {
        if (!eventInterface.isInterface() || Arrays.stream(eventInterface.getMethods()).filter(curMethod -> Modifier.isAbstract(curMethod.getModifiers()) && !Modifier.isStatic(curMethod.getModifiers())).count() != 1) {
            throw new IllegalArgumentException(String.format("Attempted to declare event of type %s, but it isn't a functional interface!", eventInterface.getName()));
        }

        synchronized (sideBasedThreadLock) {
            Event<?> mappedEvent = MAPPED_EVENTS
                    .computeIfAbsent(eventSide, side -> new Object2ObjectOpenHashMap<>())
                    .putIfAbsent(eventInterface, EventFactory.createArrayBacked(eventInterface, eventListenerMerger));

            if (mappedEvent != null) throw new IllegalArgumentException(String.format("Event of type %s is already mapped to ModSide: %s", eventInterface.getName(), eventSide.getSideName()));
        }
    }

    @Override
    public <T> void onEvent(Class<T> eventInterface, T listener, ModSide listenerExecutionSide, int listenerPriority) {
        Object2ObjectOpenHashMap<Class<?>, Event<?>> eventByType = MAPPED_EVENTS.get(listenerExecutionSide);

        if (eventByType == null || eventByType.isEmpty()) {
            NexusConstants.LOGGER.debug("ModSide {} has no mapped events, skipping listener registration until state changes...", listenerExecutionSide.getSideName());
            return;
        }

        Event<T> mappedEvent = eventByType.get(eventInterface) == null ? null : (Event<T>) eventByType.get(eventInterface);

        if (mappedEvent == null) {
            NexusConstants.LOGGER.warn("Event of type {} is not mapped to ModSide: {}, skipping listener registration until state changes...", eventInterface.getName(), listenerExecutionSide.getSideName());
            return;
        }

        synchronized (sideBasedThreadLock) {
            ObjectArrayList<T> listenersByPriority = (ObjectArrayList<T>) LISTENERS_BY_PRIORITY
                    .computeIfAbsent(eventInterface, eventInterfaceClazz -> new Int2ObjectOpenHashMap<>())
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
    public <T, R> @Nullable R fireEventHook(Class<T> eventInterface, Function<T, R> eventListenerInvokerMapper, ModSide eventSide) {
        Object2ObjectOpenHashMap<Class<?>, Event<?>> eventByType = MAPPED_EVENTS.get(eventSide);

        if (eventByType == null || eventByType.isEmpty()) {
            NexusConstants.LOGGER.debug("ModSide {} has no mapped events, skipping event invocation/posting/firing until state changes...", eventSide.getSideName());
            return null;
        }

        Event<T> mappedEvent = eventByType.get(eventInterface) == null ? null : (Event<T>) eventByType.get(eventInterface);

        if (mappedEvent == null) {
            NexusConstants.LOGGER.warn("Event of type {} is not mapped to ModSide: {}, skipping event invocation/posting/firing until state changes...", eventInterface.getName(), eventSide.getSideName());
            return null;
        }

        if (!NexusServices.PLATFORM_MANAGER.getEnvironmentSide().pertainsTo(eventSide)) {
            NexusConstants.LOGGER.warn("Attempted to invoke listeners for event of type {} on EnvironmentSide {} (physical side), but provided ModSide was {} (logical side). Skipping listener invocation on current side...", eventInterface.getName(), NexusServices.PLATFORM_MANAGER.getEnvironmentSide().getSideName(), eventSide.getSideName());
            return null;
        }

        return eventListenerInvokerMapper.apply(mappedEvent.invoker());
    }

    @Override
    public <T> Map<Integer, List<T>> getListenersFor(Class<T> eventInterface) {
        Int2ObjectOpenHashMap<ObjectArrayList<?>> classifiedListenersByPriority = LISTENERS_BY_PRIORITY.get(eventInterface);
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
