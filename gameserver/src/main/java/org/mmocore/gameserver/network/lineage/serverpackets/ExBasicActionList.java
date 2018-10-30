package org.mmocore.gameserver.network.lineage.serverpackets;

import org.jts.dataparser.data.holder.UserBasicActionHolder;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExBasicActionList extends GameServerPacket {
    private final int[] actions;

    public ExBasicActionList() {
        actions = UserBasicActionHolder.getInstance().getActionIds();
    }

    public ExBasicActionList(final int[] actionIds) {
        actions = actionIds;
    }

    @Override
    protected void writeData() {
        writeDD(actions, true);
    }
}