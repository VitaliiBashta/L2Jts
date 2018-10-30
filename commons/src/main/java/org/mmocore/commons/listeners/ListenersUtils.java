package org.mmocore.commons.listeners;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import org.mmocore.commons.listeners.ListenerActor.AddListener;
import org.mmocore.commons.listeners.ListenerActor.FireListener;
import org.mmocore.commons.listeners.ListenerActor.RemoveListener;

/**
 * @author Java-man
 */
class ListenersUtils {
    public static void addListener(final ActorRef listenerActor, final Class<? extends Listener> listener) {
        listenerActor.tell(new AddListener(listener), ActorRef.noSender());
    }

    public static void removeListener(final ActorRef listenerActor, final Class<? extends Listener> listener) {
        listenerActor.tell(new RemoveListener(listener), ActorRef.noSender());
    }

    public static void fireEvent(final ActorRef listenerActor, final Listener listener) {
        listenerActor.tell(new FireListener(listener), ActorRef.noSender());
    }

    public static ActorRef createListenerActor(final ActorSystem actorSystem, final String actorName) {
        return actorSystem.actorOf(Props.create(ListenerActor.class), actorName);
    }
}
