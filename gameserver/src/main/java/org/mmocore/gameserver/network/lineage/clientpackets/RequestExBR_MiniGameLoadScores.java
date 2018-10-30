package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.configuration.config.ExtConfig;
import org.mmocore.gameserver.network.lineage.serverpackets.ExBR_MiniGameLoadScores;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 * @date 19:57:41/25.05.2010
 */
public class RequestExBR_MiniGameLoadScores extends L2GameClientPacket {
    @Override
    protected void readImpl() throws Exception {
        //
    }

    @Override
    protected void runImpl() throws Exception {
        final Player player = getClient().getActiveChar();
        if (player == null || !ExtConfig.EX_JAPAN_MINIGAME) {
            return;
        }

        player.sendPacket(new ExBR_MiniGameLoadScores(player));
    }
}