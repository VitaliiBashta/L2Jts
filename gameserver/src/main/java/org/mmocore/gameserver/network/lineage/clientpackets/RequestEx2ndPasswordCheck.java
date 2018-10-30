package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.configuration.config.ExtConfig;
import org.mmocore.gameserver.network.lineage.serverpackets.Ex2ndPasswordCheck;

/**
 * @author VISTALL
 */
public class RequestEx2ndPasswordCheck extends L2GameClientPacket {
    @Override
    protected void readImpl() {
        //
    }

    @Override
    protected void runImpl() {
        if (ExtConfig.EX_2ND_PASSWORD_CHECK) {
            sendPacket(Ex2ndPasswordCheck.PASSWORD_OK);
        }
    }
}
