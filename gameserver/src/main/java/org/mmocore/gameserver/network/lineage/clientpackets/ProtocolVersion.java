package org.mmocore.gameserver.network.lineage.clientpackets;

import org.jts.protection.Protection;
import org.mmocore.gameserver.ProtectType;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.configuration.config.protection.BaseProtectSetting;
import org.mmocore.gameserver.configuration.config.protection.JtsProtectionConfig;
import org.mmocore.gameserver.network.lineage.GameClient;
import org.mmocore.gameserver.network.lineage.serverpackets.SendStatus;
import org.mmocore.gameserver.network.lineage.serverpackets.ServerClose;
import org.mmocore.gameserver.network.lineage.serverpackets.VersionCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.akumu.smartguard.core.GuardConfig;

import java.nio.BufferUnderflowException;

/**
 * packet type id 0x0E
 * format:	cdbd
 */
public class ProtocolVersion extends L2GameClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolVersion.class);
    private static final short BasePacketSize = 4 + 256;
    private int version;
    //Protection section
    private String HWID;
    //Protection section end
    private boolean hasExtraData = false;

    @Override
    protected void readImpl() {
        version = readD();
        if (BaseProtectSetting.protectType == ProtectType.SMART && GuardConfig.ProtectionEnabled && _buf.remaining() >= BasePacketSize + 2) {
            _buf.position(_buf.position() + BasePacketSize);
            int dataLen = readH();
            if (_buf.remaining() >= dataLen) {
                hasExtraData = true;
            }
        } else if (JtsProtectionConfig.PROTECTION_ENABLED_HWID_REQUEST && _buf.remaining() > 260) {
            byte[] data = new byte[260];
            readB(data);
            if (JtsProtectionConfig.PROTECTION_ENABLED_HWID_REQUEST) {
                try {
                    HWID = readS();
                } catch (BufferUnderflowException e) {
                    _log.warn(getClient().toString() + " - trying to connect with broken HWID. Connection closed.");
                    getClient().close(new VersionCheck(null));
                }
            }
        } else if (Protection.isProtectEnabled()) {
            getClient().close(new VersionCheck(null));
        }
    }

    @Override
    protected void runImpl() {
        GameClient client = getClient();
        if (version == -2) {
            _client.closeNow(false);
            return;
        } else if (version == -3) {
            LOGGER.info("Status request from IP : " + getClient().getIpAddr());
            client.close(new SendStatus());
            return;
        } else if (version < ServerConfig.MIN_PROTOCOL_REVISION || version > ServerConfig.MAX_PROTOCOL_REVISION) {
            LOGGER.warn("Unknown protocol revision : " + version + ", client : " + _client);
            client.close(new VersionCheck(null));
            return;
        }

        //Protection section
        if (Protection.isProtectEnabled()) {
            client.setHWID(HWID);
        }
        if (BaseProtectSetting.protectType == ProtectType.SMART && GuardConfig.ProtectionEnabled) {
            if (!hasExtraData) {
                // если нету доп. данных - попытка входа без защиты
                getClient().close(ServerClose.STATIC);
                return;
            }

        }
        //Protection section end
        client.setRevision(version);
        client.setChronicle(ServerConfig.CHRONICLE_VERSION);
        client.sendPacket(new VersionCheck(client.enableCrypt()));
    }
}