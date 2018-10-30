package org.mmocore.gameserver.model.entity.events.objects;


import org.mmocore.gameserver.model.entity.events.Event;

import java.io.Serializable;

/**
 * @author VISTALL
 * @date 16:28/10.12.2010
 */
public interface SpawnableObject extends Serializable {
    void spawnObject(Event event);

    void despawnObject(Event event);

    void respawnObject(Event event);

    void refreshObject(Event event);
}
