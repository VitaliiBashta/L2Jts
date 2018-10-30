package org.mmocore.gameserver.network.authcomm.gs2as;

import org.mmocore.gameserver.network.authcomm.SendablePacket;

/**
 * @author cyberhuman
 * @date 22:26/28.04.2012
 */

public class CheckEmailRequest extends SendablePacket {
    private final String account;

    public CheckEmailRequest(final String account) {
        this.account = account;
    }

    @Override
    protected void writeImpl() {
        writeC(0x12);
        writeS(account);
    }

}
