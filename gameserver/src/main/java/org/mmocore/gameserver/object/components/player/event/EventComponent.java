package org.mmocore.gameserver.object.components.player.event;

import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.object.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Mangol
 * @since 07.10.2016
 */
public class EventComponent {
    private final Player player;
    private Map<Class<? extends Event>, Enum> eventTeamType = new ConcurrentHashMap<>();

    private EventComponent(Player player) {
        this.player = player;
    }

    public static EventComponent createEventComponent(Player player) {
        return new EventComponent(player);
    }

    public Player getPlayer() {
        return player;
    }

    public void setTeam(final Class<? extends Event> clazz, Enum type) {
        eventTeamType.put(clazz, type);
    }

    @SuppressWarnings("unchecked")
    public <T extends Enum> T removeTeam(final Class<? extends Event> clazz) {
        if (eventTeamType.containsKey(clazz))
            return (T) eventTeamType.remove(clazz);
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends Enum> T getTeam(final Class<? extends Event> clazz) {
        Object o = eventTeamType.get(clazz);
        return o != null ? (T) o : null;
    }

    public boolean isEventTeamNull(final Class<? extends Event> clazz) {
        return !eventTeamType.containsKey(clazz);
    }
}