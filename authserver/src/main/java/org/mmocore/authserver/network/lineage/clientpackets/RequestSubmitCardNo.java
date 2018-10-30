package org.mmocore.authserver.network.lineage.clientpackets;

import org.mmocore.authserver.network.lineage.serverpackets.LoginOk;

import javax.crypto.Cipher;
import java.security.GeneralSecurityException;
import java.util.Arrays;

/**
 * @author savormix, KilRoy
 */
public class RequestSubmitCardNo extends L2LoginClientPacket {
    private final byte[] _raw = new byte[128];

    @Override
    protected void readImpl() {
        if (super._buf.remaining() == 151)
            readB(_raw);
        else
            return;
    }

    @Override
    protected void runImpl() {
        byte[] decrypted = null;
        try {
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
            rsaCipher.init(Cipher.DECRYPT_MODE, getClient().getRSAPrivateKey());
            decrypted = rsaCipher.doFinal(_raw, 0x00, 0x80);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return;
        }
        System.out.println(Arrays.toString(_raw));
        getClient().sendPacket(new LoginOk(getClient().getSessionKey()));
    }
}