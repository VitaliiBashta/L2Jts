package org.jts.protection.network;

import org.jts.protection.Protection;
import org.jts.protection.network.crypt.ProtectionCrypt;
import org.mmocore.gameserver.ProtectType;
import org.mmocore.gameserver.configuration.config.protection.BaseProtectSetting;
import ru.akumu.smartguard.core.GuardConfig;
import ru.akumu.smartguard.core.manager.LicenseManager;
import ru.akumu.smartguard.core.utils.crypt.KeyObject;
import ru.akumu.smartguard.core.utils.crypt.SCrypt;

/**
 * @author ALF
 */
public class ProtectionGameCrypt {
    private final byte[] inKey = new byte[16], outKey = new byte[16];
    private final ProtectionCrypt cryptIn = new ProtectionCrypt();
    private final ProtectionCrypt cryptOut = new ProtectionCrypt();
    private ru.akumu.smartguard.core.network.GameCrypt crypt = new ru.akumu.smartguard.core.network.GameCrypt();
    private boolean isEnabled = false;
    private KeyObject inKo;
    private KeyObject outKo;

    public void setKey(final byte[] key) {
        if (GuardConfig.ProtectionEnabled && BaseProtectSetting.protectType == ProtectType.SMART) {
            inKo = new KeyObject();
            outKo = new KeyObject();
            inKo = LicenseManager.getInstance().makeNetworkKey(LicenseManager.KeyType.IN, key);
            outKo = LicenseManager.getInstance().makeNetworkKey(LicenseManager.KeyType.OUT, key);
        } else {
            System.arraycopy(key, 0, inKey, 0, 16);
            System.arraycopy(key, 0, outKey, 0, 16);
            if (Protection.isProtectEnabled()) {
                cryptIn.setKey(key);
                cryptOut.setKey(key);
            }
        }
    }

    public void setKey(byte[] key, boolean value) {
        setKey(key);
    }

    public boolean decrypt(final byte[] raw, final int offset, final int size) {
        if (!isEnabled)
            return true;

        if (Protection.isProtectEnabled()) {
            cryptIn.chiper(raw, offset, size);
            return true;
        }
        if (GuardConfig.ProtectionEnabled && BaseProtectSetting.protectType == ProtectType.SMART) {
            try {
                SCrypt.crypt(raw, offset, size, inKo);
            } catch (NullPointerException e) {
                //[Hack]: smrt-core can throw an NPE (right here) probably due nullable args (byte array?)
                // TODO: check conditions, fix, and remove try-catch block
            }
            return true;
        }
        int temp = 0;
        for (int i = 0; i < size; i++) {
            final int temp2 = raw[offset + i] & 0xFF;
            raw[offset + i] = (byte) (temp2 ^ inKey[i & 15] ^ temp);
            temp = temp2;
        }

        int old = inKey[8] & 0xff;
        old |= inKey[9] << 8 & 0xff00;
        old |= inKey[10] << 0x10 & 0xff0000;
        old |= inKey[11] << 0x18 & 0xff000000;

        old += size;

        inKey[8] = (byte) (old & 0xff);
        inKey[9] = (byte) (old >> 0x08 & 0xff);
        inKey[10] = (byte) (old >> 0x10 & 0xff);
        inKey[11] = (byte) (old >> 0x18 & 0xff);
        return true;
    }

    public boolean encrypt(final byte[] raw, final int offset, final int size) {
        if (!isEnabled) {
            isEnabled = true;
            return true;
        }

        if (Protection.isProtectEnabled()) {
            cryptOut.chiper(raw, offset, size);
            return true;
        }
        if (GuardConfig.ProtectionEnabled && BaseProtectSetting.protectType == ProtectType.SMART) {
            try {
                SCrypt.crypt(raw, offset, size, outKo);
            } catch (NullPointerException e) {
                //[Hack]: smrt-core can throw an NPE probably due nullable args (byte array?)
                // TODO: check conditions, fix, and remove try-catch block
            }
            return true;
        }
        int temp = 0;
        for (int i = 0; i < size; i++) {
            final int temp2 = raw[offset + i] & 0xFF;
            temp = temp2 ^ outKey[i & 15] ^ temp;
            raw[offset + i] = (byte) temp;
        }

        int old = outKey[8] & 0xff;
        old |= outKey[9] << 8 & 0xff00;
        old |= outKey[10] << 0x10 & 0xff0000;
        old |= outKey[11] << 0x18 & 0xff000000;

        old += size;

        outKey[8] = (byte) (old & 0xff);
        outKey[9] = (byte) (old >> 0x08 & 0xff);
        outKey[10] = (byte) (old >> 0x10 & 0xff);
        outKey[11] = (byte) (old >> 0x18 & 0xff);

        return true;
    }
}