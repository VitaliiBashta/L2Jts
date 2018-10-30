package org.mmocore.gameserver.listener.actor.door;

import org.mmocore.gameserver.listener.CharListener;
import org.mmocore.gameserver.model.instances.DoorInstance;

/**
 * @author VISTALL
 * @date 21:03/04.07.2011
 */
public interface OnOpenCloseListener extends CharListener {
    void onOpen(DoorInstance doorInstance);

    void onClose(DoorInstance doorInstance);
}
