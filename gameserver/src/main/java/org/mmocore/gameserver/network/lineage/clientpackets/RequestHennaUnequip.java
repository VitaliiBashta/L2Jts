package org.mmocore.gameserver.network.lineage.clientpackets;

import org.jts.dataparser.data.holder.dyedata.DyeData;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;

public class RequestHennaUnequip extends L2GameClientPacket {
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

        for (int i = 1; i <= 3; i++) {
            final DyeData dye = player.getDye(i);
            if (dye == null) {
                continue;
            }

            if (dye.dye_id == _symbolId) {
                final long price = dye.cancel_fee;
                if (player.getAdena() < price) {
                    player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                    break;
                }

                player.reduceAdena(price);

                player.removeDye(i);

                player.sendPacket(SystemMsg.THE_SYMBOL_HAS_BEEN_DELETED);
                break;
            }
        }
    }
}