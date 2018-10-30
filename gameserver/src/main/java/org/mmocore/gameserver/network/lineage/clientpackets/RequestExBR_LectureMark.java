package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.configuration.config.ExtConfig;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 */
public class RequestExBR_LectureMark extends L2GameClientPacket {
    private int _mark;

    @Override
    protected void readImpl() {
        _mark = readC();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }
        if (!ExtConfig.EX_LECTURE_MARK) {
            player.sendAdminMessage("Lecture Not Support, Check Server");
            return;
        }

        player.setLectureMark(_mark, true);
    }
}