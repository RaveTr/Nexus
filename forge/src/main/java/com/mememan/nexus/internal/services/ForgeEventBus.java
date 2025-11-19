package com.mememan.nexus.internal.services;

import com.mememan.nexus.loader.ModSide;
import com.mememan.nexus.platform.services.EventBus;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ForgeEventBus implements EventBus {
    private static final Object2ObjectOpenHashMap<ModSide, Object2ObjectOpenHashMap<Class<?>, Event>> MAPPED_EVENTS = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<Class<?>, Int2ObjectOpenHashMap<ObjectArrayList<?>>> LISTENERS_BY_PRIORITY = new Object2ObjectOpenHashMap<>();
    private final Object sideBasedThreadLock = new Object();

    @Override
    public <T> void registerEventHook(Class<T> eventInterface, Function<T[], T> eventListenerMerger, ModSide eventSide) {

    }

    @Override
    public <T> void onEvent(Class<T> eventInterface, T listener, ModSide listenerExecutionSide, int listenerPriority) {

    }

    @Override
    public <T, R> R fireEventHook(Class<T> eventInterface, Function<T, R> eventListenerInvokerMapper, ModSide eventSide) {
        return null;
    }

    @Override
    public <T> Map<Integer, List<T>> getListenersFor(Class<T> eventInterface) {
        return Map.of();
    }
}
