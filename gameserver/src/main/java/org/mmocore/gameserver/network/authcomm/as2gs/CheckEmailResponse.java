package org.mmocore.gameserver.network.authcomm.as2gs;

import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.ReceivablePacket;
import org.mmocore.gameserver.network.lineage.GameClient;
import org.mmocore.gameserver.object.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cyberhuman
 * @date 22:26/28.04.2012
 */

public class CheckEmailResponse extends ReceivablePacket {
    private static final Logger _log = LoggerFactory.getLogger(CheckEmailResponse.class);

    private String account;
    private int checkEmail;

    @Override
    protected void readImpl() {
        account = readS();
        checkEmail = readC();
    }

    @Override
    protected void runImpl() {
        if (checkEmail < 0) {
            _log.warn(account + ": checkEmail is less than zero (problems on login server?)");
            return;
        }
        if (checkEmail != 0) {
            _log.debug(account + ": checkEmail is already checked");
            return;
        }

        final GameClient client = AuthServerCommunication.getInstance().getAuthedClient(account);
        if (client != null) {
            final Player player = client.getActiveChar();
            if (player != null) {
                player.getListeners().onMailActivation();
            } else {
                _log.warn(account + ": checkEmail has no active character");
            }
        } else {
            _log.warn(account + ": checkEmail is not authed anymore"); // Флудит на персонажей на офф торге
        }
    }

}
