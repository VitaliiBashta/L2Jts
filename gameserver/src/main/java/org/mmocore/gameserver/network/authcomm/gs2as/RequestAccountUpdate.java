package org.mmocore.gameserver.network.authcomm.gs2as;

import org.mmocore.gameserver.network.authcomm.SendablePacket;

/**
 * @author Mangol
 * @since 11.09.2016
 */
public final class RequestAccountUpdate extends SendablePacket {
    private final String accountName;
    private final String lastIp;
    private final String updatePasswordHash;

    private RequestAccountUpdate(final String accountName, final String lastIp, final String updatePasswordHash) {
        this.accountName = accountName;
        this.lastIp = lastIp;
        this.updatePasswordHash = updatePasswordHash;
    }

    public static RequestAccountUpdate create(final String accountName, final String lastIp, final String updatePasswordHash) {
        return new RequestAccountUpdate(accountName, lastIp, updatePasswordHash);
    }

    @Override
    protected void writeImpl() {
        writeC(0x15);
        writeS(accountName);
        writeS(lastIp);
        writeS(updatePasswordHash);
    }
}
