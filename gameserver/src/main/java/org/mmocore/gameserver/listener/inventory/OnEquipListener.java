package org.mmocore.gameserver.listener.inventory;

import org.mmocore.commons.listener.Listener;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.components.items.ItemInstance;

public interface OnEquipListener extends Listener<Playable> {
    void onEquip(int slot, ItemInstance item, Playable actor);

    void onUnequip(int slot, ItemInstance item, Playable actor);
}
