package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

public class MagicSkillCanceled extends GameServerPacket {

    private final int objectId;

    public MagicSkillCanceled(final int objectId) {
        this.objectId = objectId;
    }

    @Override
    protected final void writeData() {
        writeD(objectId);
    }

    @Override
    public L2GameServerPacket packet(final Player player) {
        if (player != null && player.isNotShowBuffAnim()) {
            return null;
        }

        return super.packet(player);
    }
}