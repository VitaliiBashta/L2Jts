package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExAskCoupleAction extends GameServerPacket {
    private final int objectId;
    private final int socialId;

    public ExAskCoupleAction(final int objectId, final int socialId) {
        this.objectId = objectId;
        this.socialId = socialId;
    }

    @Override
    protected void writeData() {
        writeD(socialId);
        writeD(objectId);
    }
}