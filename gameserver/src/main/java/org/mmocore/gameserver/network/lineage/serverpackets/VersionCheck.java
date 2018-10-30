package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.ProtectType;
import org.mmocore.gameserver.configuration.config.protection.BaseProtectSetting;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import ru.akumu.smartguard.core.GuardConfig;
import ru.akumu.smartguard.core.SmartCore;

public class VersionCheck extends GameServerPacket {
    private final byte[] key;

    public VersionCheck(final byte[] key) {
        this.key = key;
        if (key != null && GuardConfig.ProtectionEnabled && BaseProtectSetting.protectType == ProtectType.SMART)
            SmartCore.cryptInternalKey(key);
    }

    @Override
    public void writeData() {
        if (key == null || key.length == 0) {
            writeC(0x00);
            return;
        }
        writeC(0x01);
        writeB(key);
        writeD(0x01);
        writeD(0x00);
        writeC(0x00);
        writeD(0x00); // Seed (obfuscation key)
    }
}