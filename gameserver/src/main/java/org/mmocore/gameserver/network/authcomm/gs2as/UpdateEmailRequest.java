package org.mmocore.gameserver.network.authcomm.gs2as;

import org.mmocore.gameserver.network.authcomm.SendablePacket;

public class UpdateEmailRequest extends SendablePacket {
    private final String account;
    private final String l2email;
    private final int checkEmail;

    public UpdateEmailRequest(final String account, final String email, final int checkEmail) {
        this.account = account;
        this.l2email = email;
        this.checkEmail = checkEmail;
    }

    @Override
    protected void writeImpl() {
        writeC(0x13);
        writeS(account);
        writeS(l2email);
        writeC(checkEmail);
    }

}
