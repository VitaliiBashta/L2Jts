package org.mmocore.gameserver.model.entity.events;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author VISTALL
 * @date 10:27/24.02.2011
 */
public abstract class EventOwner implements Serializable {
    private final Set<Event> _events = new ConcurrentSkipListSet<>(EventComparator.getInstance());

    @SuppressWarnings("unchecked")
    public <E extends Event> E getEvent(final Class<E> eventClass) {
        for (final Event e : _events) {
            if (e.getClass() == eventClass) // fast hack
            {
                return (E) e;
            }
            if (eventClass.isAssignableFrom(e.getClass())) //FIXME [VISTALL] какойто другой способ определить
            {
                return (E) e;
            }
        }

        return null;
    }

    public void addEvent(final Event event) {
        _events.add(event);
    }

    public void removeEvent(final Event event) {
        _events.remove(event);
    }

    public Set<Event> getEvents() {
        return _events;
    }
}
