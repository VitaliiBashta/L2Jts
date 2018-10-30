package org.mmocore.commons.listeners;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Java-man
 */
public class ListenersContainer<E extends Enum<?>> {
    private final ActorSystem actorSystem;

    private Map<E, ActorRef> listenerMap = new HashMap<>();

    public ListenersContainer(final ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
    }

    public void createListenerActor(final E listenerType, final String listenerName) {
        final ActorRef actor = ListenersUtils.createListenerActor(actorSystem, listenerName);
        listenerMap.put(listenerType, actor);
    }

    public void fireEvent(final E listenerType, final Listener listener) {
        final ActorRef actor = listenerMap.get(listenerType);
        ListenersUtils.fireEvent(actor, listener);
    }

    public void addListener(final E listenerType, final Class<? extends Listener> listener) {
        final ActorRef actor = listenerMap.get(listenerType);
        ListenersUtils.addListener(actor, listener);
    }

    public void removeListener(final E listenerType, final Class<? extends Listener> listener) {
        final ActorRef actor = listenerMap.get(listenerType);
        ListenersUtils.removeListener(actor, listener);
    }
}
