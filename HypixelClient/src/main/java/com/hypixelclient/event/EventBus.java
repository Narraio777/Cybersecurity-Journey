package com.hypixelclient.event;

import java.util.*;
import java.util.function.Consumer;

public class EventBus {
    private final Map<Class<?>, List<Consumer<Object>>> listeners = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> void subscribe(Class<T> eventClass, Consumer<T> listener) {
        listeners.computeIfAbsent(eventClass, k -> new ArrayList<>())
                 .add((Consumer<Object>) listener);
    }

    public void post(Object event) {
        List<Consumer<Object>> handlers = listeners.get(event.getClass());
        if (handlers != null) handlers.forEach(h -> h.accept(event));
    }
}
