package org.mmocore.gameserver.model.entity.events.impl.reflection.object;

import org.mmocore.gameserver.model.entity.events.objects.AbstractSnapshotObject;
import org.mmocore.gameserver.object.Player;

/**
 * @author Mangol
 * @since 05.10.2016
 */
public class RSnapshotObject extends AbstractSnapshotObject {
    private int countPoint = 0;

    public RSnapshotObject(Player player) {
        super(player);
    }

    public synchronized void addPoint(final int countPoint) {
        this.countPoint += countPoint;
    }

    public int getCountPoint() {
        return countPoint;
    }
}
