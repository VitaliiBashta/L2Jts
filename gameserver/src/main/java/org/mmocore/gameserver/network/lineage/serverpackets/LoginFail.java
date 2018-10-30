package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class LoginFail extends GameServerPacket {
    public static final int SYSTEM_ERROR_LOGIN_LATER = 1;
    public static final int ACCESS_FAILED_TRY_LATER = 4;
    public static int NO_TEXT = 0;
    public static int PASSWORD_DOES_NOT_MATCH_THIS_ACCOUNT = 2;
    public static int PASSWORD_DOES_NOT_MATCH_THIS_ACCOUNT2 = 3;
    public static int INCORRECT_ACCOUNT_INFO_CONTACT_CUSTOMER_SUPPORT = 5;
    public static int ACCESS_FAILED_TRY_LATER2 = 6;
    public static int ACOUNT_ALREADY_IN_USE = 7;
    public static int ACCESS_FAILED_TRY_LATER3 = 8;
    public static int ACCESS_FAILED_TRY_LATER4 = 9;
    public static int ACCESS_FAILED_TRY_LATER5 = 10;

    private final int reason;

    public LoginFail(final int reason) {
        this.reason = reason;
    }

    @Override
    protected final void writeData() {
        writeD(reason);
    }
}