package org.mmocore.gameserver.network.lineage.clientpackets;

import org.jts.dataparser.data.holder.DyeDataHolder;
import org.jts.dataparser.data.holder.dyedata.DyeData;
import org.mmocore.gameserver.network.lineage.serverpackets.HennaItemInfo;
import org.mmocore.gameserver.object.Player;

public class RequestHennaItemInfo extends L2GameClientPacket {
    // format  cd
    private int _symbolId;

    @Override
    protected void readImpl() {
        _symbolId = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        final DyeData dye = DyeDataHolder.getInstance().getDye(_symbolId);
        if (dye != null) {
            player.sendPacket(new HennaItemInfo(dye, player));
        }
    }
}