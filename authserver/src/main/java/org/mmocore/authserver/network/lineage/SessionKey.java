package org.mmocore.authserver.network.lineage;

import org.mmocore.commons.utils.Rnd;

/**
 * <p>This class is used to represent session keys used by the client to authenticate in the gameserver</p>
 * <p>A SessionKey is made up of two 8 bytes keys. One is send in the LoginOk packet and the other is sent in PlayOk</p>
 *
 * @author -Wooden-
 */
public class SessionKey {
    public final int playOkID1;
    public final int playOkID2;
    public final int loginOkID1;
    public final int loginOkID2;

    private final int hashCode;

    public SessionKey(int loginOK1, int loginOK2, int playOK1, int playOK2) {
        playOkID1 = playOK1;
        playOkID2 = playOK2;
        loginOkID1 = loginOK1;
        loginOkID2 = loginOK2;

        int hashCode = playOK1;
        hashCode *= 17;
        hashCode += playOK2;
        hashCode *= 37;
        hashCode += loginOK1;
        hashCode *= 51;
        hashCode += loginOK2;

        this.hashCode = hashCode;
    }

    public final static SessionKey create() {
        return new SessionKey(Rnd.nextInt(), Rnd.nextInt(), Rnd.nextInt(), Rnd.nextInt());
    }

    public boolean checkLoginPair(int loginOk1, int loginOk2) {
        return loginOkID1 == loginOk1 && loginOkID2 == loginOk2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() == getClass()) {
            SessionKey skey = (SessionKey) o;
            return playOkID1 == skey.playOkID1 && playOkID2 == skey.playOkID2 && skey.checkLoginPair(loginOkID1, loginOkID2);
        }
        return false;
    }

    public int hashCode() {
        return hashCode;
    }

    public String toString() {
        return "[playOkID1: " + playOkID1 + " playOkID2: " + playOkID2 + " loginOkID1: " + loginOkID1 + " loginOkID2: " + loginOkID2 + ']';
    }
}