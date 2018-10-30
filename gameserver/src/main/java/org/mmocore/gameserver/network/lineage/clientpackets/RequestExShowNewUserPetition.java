package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.configuration.config.ExtConfig;
import org.mmocore.gameserver.network.lineage.serverpackets.ExResponseShowStepOne;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 */
public class RequestExShowNewUserPetition extends L2GameClientPacket {
    @Override
    protected void readImpl() {
        //
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null || !ExtConfig.EX_NEW_PETITION_SYSTEM) {
            return;
        }

        player.sendPacket(new ExResponseShowStepOne(player));
    }
}