package org.mmocore.gameserver.object.components.player.community;

import org.mmocore.gameserver.object.Inventory;

/**
 * Created by Hack
 * Date: 10.09.2016 6:13
 */
public class RefineComponent {
    private int selectedSlotId = Inventory.PAPERDOLL_RHAND;

    public int getSelectedSlotId() {
        return selectedSlotId;
    }

    public void setSelectedSlotId(int selectedSlotId) {
        this.selectedSlotId = selectedSlotId;
    }
}
