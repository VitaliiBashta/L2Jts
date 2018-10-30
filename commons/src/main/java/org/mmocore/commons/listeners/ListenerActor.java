package org.mmocore.commons.listeners;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * @author Java-man
 */
public class ListenerActor extends UntypedActor {
    private final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    private final Collection<Class<? extends Listener>> listeners = new ArrayList<>();

    @Override
    public void onReceive(final Object message) {
        if (message instanceof FireListener) {
            final FireListener event = (FireListener) message;
            final Optional<Class<? extends Listener>> listener = listeners.stream()
                    .filter(event.listener.getClass()::isAssignableFrom).findFirst();
            if (listener.isPresent())
                event.listener.handle();
            else
                logger.info("Can't find listener {} in {} actor", event.listener.getClass().getName(), getSelf().path().name());

            return;
        }

        if (message instanceof AddListener) {
            final AddListener listener = (AddListener) message;
            listeners.add(listener.listener);

            return;
        }

        if (message instanceof RemoveListener) {
            final RemoveListener listener = (RemoveListener) message;
            listeners.remove(listener.listener);

            return;
        }

        unhandled(message);
    }

    public static class AddListener implements Serializable {
        private static final long serialVersionUID = 8905887475859089252L;

        private final Class<? extends Listener> listener;

        public AddListener(final Class<? extends Listener> listener) {
            this.listener = listener;
        }
    }

    public static class RemoveListener implements Serializable {
        private static final long serialVersionUID = 5927520021723611996L;

        private final Class<? extends Listener> listener;

        public RemoveListener(final Class<? extends Listener> listener) {
            this.listener = listener;
        }
    }

    public static class FireListener implements Serializable {
        private static final long serialVersionUID = -7823274325835086834L;

        private final Listener listener;

        public FireListener(final Listener listener) {
            this.listener = listener;
        }
    }
}
