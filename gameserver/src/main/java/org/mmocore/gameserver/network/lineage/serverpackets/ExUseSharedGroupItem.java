package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.skills.TimeStamp;

public class ExUseSharedGroupItem extends GameServerPacket {
    private final int itemId;
    private final int grpId;
    private final int remainedTime;
    private final int totalTime;

    public ExUseSharedGroupItem(final int grpId, final TimeStamp timeStamp) {
        this.grpId = grpId;
        itemId = timeStamp.getId();
        remainedTime = (int) (timeStamp.getReuseCurrent() / 1000);
        totalTime = (int) (timeStamp.getReuseBasic() / 1000);
    }

    @Override
    protected final void writeData() {
        writeD(itemId);
        writeD(grpId);
        writeD(remainedTime);
        writeD(totalTime);
    }
}