package org.mmocore.gameserver.model.entity.events.objects;


import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;

import java.util.List;

/**
 * @author VISTALL
 * @date 11:40/30.06.2011
 */
public class ZoneObject implements InitableObject {
    private final String _name;
    private Zone _zone;

    public ZoneObject(final String name) {
        _name = name;
    }

    @Override
    public void initObject(final Event e) {
        final Reflection r = e.getReflection();

        _zone = r.getZone(_name);
    }

    public void setActive(final boolean a) {
        _zone.setActive(a);
    }

    public void setActive(final boolean a, final Event event) {
        setActive(a);

        if (a) {
            _zone.addEvent(event);
        } else {
            _zone.removeEvent(event);
        }
    }

    public Zone getZone() {
        return _zone;
    }

    public List<Player> getInsidePlayers() {
        return _zone.getInsidePlayers();
    }

    public boolean checkIfInZone(final Creature c) {
        return _zone.checkIfInZone(c);
    }
}
