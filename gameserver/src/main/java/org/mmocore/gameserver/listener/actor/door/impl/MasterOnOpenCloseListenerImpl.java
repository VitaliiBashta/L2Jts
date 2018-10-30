package org.mmocore.gameserver.listener.actor.door.impl;

import org.mmocore.gameserver.listener.actor.door.OnOpenCloseListener;
import org.mmocore.gameserver.model.instances.DoorInstance;

/**
 * @author VISTALL
 * @date 21:41/04.07.2011
 */
public class MasterOnOpenCloseListenerImpl implements OnOpenCloseListener {
    private final DoorInstance _door;

    public MasterOnOpenCloseListenerImpl(final DoorInstance door) {
        _door = door;
    }

    @Override
    public void onOpen(final DoorInstance doorInstance) {
        _door.openMe();
    }

    @Override
    public void onClose(final DoorInstance doorInstance) {
        _door.closeMe();
    }
}
