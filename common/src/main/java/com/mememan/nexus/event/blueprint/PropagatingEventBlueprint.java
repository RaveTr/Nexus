package com.mememan.nexus.event.blueprint;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.event.listener.EventListener;
import com.mememan.nexus.event.object.BaseEvent;
import com.mememan.nexus.loader.ModSide;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class PropagatingEventBlueprint<BE extends BaseEvent, R> extends BaseEventBlueprint<BE, R> {

    protected PropagatingEventBlueprint(Class<BE> eventInterface, R defaultResult, boolean isCancellable, ModSide eventSide) {
        super(eventInterface, defaultResult, isCancellable, eventSide);
    }

    @Override
    public <U> U fireEvent(Function<EventListener<BE, R>, U> eventMapper) {
        Class<? super BE> eventSuperclass = eventInterface.getSuperclass();

        while (BaseEvent.class.isAssignableFrom(eventSuperclass)) {
            Class<? super BE> finalEventSuperclass = eventSuperclass;
            Optional<BaseEventBlueprint<BaseEvent, Object>> potentiallyMappedBlueprint = getBlueprintFor(eventSuperclass);

            potentiallyMappedBlueprint
                    .ifPresentOrElse(
                            inferredBlueprint -> ((BaseEventBlueprint<BE, R>) inferredBlueprint).fireEvent(eventMapper),
                            () -> getListeners().values().stream()
                                    .map(eventListeners -> eventListeners.stream()
                                            .filter(curListener -> {
                                                try {
                                                    Type[] genericInterfaces = curListener.getClass().getGenericInterfaces();

                                                    for (Type genericInterface : genericInterfaces) {
                                                        if (genericInterface instanceof ParameterizedType paramType) {
                                                            if (paramType.getRawType() == EventListener.class) {
                                                                Type typeArg = paramType.getActualTypeArguments()[0];

                                                                if (typeArg instanceof Class)
                                                                    return typeArg == finalEventSuperclass;
                                                                else if (typeArg instanceof ParameterizedType paramTypeArg)
                                                                    return paramTypeArg.getRawType() == finalEventSuperclass;
                                                            }
                                                        }
                                                    }

                                                    return false;
                                                } catch (Exception e) {
                                                    NexusConstants.LOGGER.warn("Failed to compare parent listener type for event blueprint '{}' (mapped to event supertype: {}, original child event type: {}). Skipping...", getClass().getName(), finalEventSuperclass.getName(), getEventInterface().getName(), e);
                                                    return false;
                                                }
                                            })
                                            .collect(Collectors.toCollection(ObjectArrayList::new)))
                                    .forEach(listeners -> listeners.forEach(eventMapper::apply)));

            eventSuperclass = eventSuperclass.getSuperclass();
        }

        return super.fireEvent(eventMapper);
    }
}
