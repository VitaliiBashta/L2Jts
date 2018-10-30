package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.pledge.RankPrivs;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

public class ManagePledgePower extends GameServerPacket {
    private final int action;
    private final int clanId;
    private final int privs;

    public ManagePledgePower(final Player player, final int action, final int rank) {
        clanId = player.getClanId();
        this.action = action;
        final RankPrivs temp = player.getClan().getRankPrivs(rank);
        privs = temp == null ? 0 : temp.getPrivs();
        player.sendPacket(new PledgeReceiveUpdatePower(privs));
    }

    @Override
    protected final void writeData() {
        writeD(clanId);
        writeD(action);
        writeD(privs);
    }
}