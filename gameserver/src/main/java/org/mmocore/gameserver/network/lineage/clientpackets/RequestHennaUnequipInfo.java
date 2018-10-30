package org.mmocore.gameserver.network.lineage.clientpackets;

import org.jts.dataparser.data.holder.DyeDataHolder;
import org.jts.dataparser.data.holder.dyedata.DyeData;
import org.mmocore.gameserver.network.lineage.serverpackets.HennaUnequipInfo;
import org.mmocore.gameserver.object.Player;

public class RequestHennaUnequipInfo extends L2GameClientPacket {
    private int _symbolId;

    /**
     * format: d
     */
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
            player.sendPacket(new HennaUnequipInfo(dye, player));
        }
    }
}