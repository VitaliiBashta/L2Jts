package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.utils.Util;

public class UseItem extends L2GameClientPacket {
    private int _objectId;
    private boolean _ctrlPressed;

    @Override
    protected void readImpl() {
        _objectId = readD();
        _ctrlPressed = readD() == 1;
    }

    @Override
    protected void runImpl() {
        Util.useItem(getClient().getActiveChar(), _objectId, _ctrlPressed);
    }
}