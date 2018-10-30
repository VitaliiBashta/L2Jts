package org.mmocore.gameserver.data.xml.holder;

import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.object.Player;

import java.util.Objects;

/**
 * @author VISTALL
 * @date 12:55/10.12.2010
 */
public final class EventHolder extends AbstractHolder {
    private static final EventHolder INSTANCE = new EventHolder();
    private final TIntObjectMap<Event> events = new TIntObjectHashMap<>();

    public static EventHolder getInstance() {
        return INSTANCE;
    }

    public TIntObjectMap<Event> getEvents() {
        return TCollections.unmodifiableMap(events);
    }

    public void addEvent(final Event event) {
        events.put(event.getType().step() + event.getId(), event);
    }

    @SuppressWarnings("unchecked")
    public <E extends Event> E getEvent(final EventType type, final int id) {
        return (E) events.get(type.step() + id);
    }

    @SuppressWarnings("unchecked")
    public <E extends Event> E getEvent(Class clazz) {
        for (Event e : events.valueCollection()) {
            if (Objects.equals(clazz.getSimpleName(), e.getClass().getSimpleName())) {
                return (E) e;
            }
        }
        return null;
    }

    public void findEvent(final Player player) {
        for (final Event event : events.valueCollection()) {
            event.findEvent(player);
        }
    }

    public void callInit() {
        events.valueCollection().forEach(Event::initEvent);
    }

    @Override
    public int size() {
        return events.size();
    }

    @Override
    public void clear() {
        events.clear();
    }
}
