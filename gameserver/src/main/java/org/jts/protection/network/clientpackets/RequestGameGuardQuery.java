package org.jts.protection.network.clientpackets;

import org.jts.protection.Protection;
import org.jts.protection.Protection.BotResponse;
import org.jts.protection.Protection.GameGuardResponse;
import org.jts.protection.manager.HWIDBanManager;
import org.jts.protection.manager.HWIDBanManager.BanType;
import org.mmocore.gameserver.configuration.config.protection.JtsProtectionConfig;
import org.mmocore.gameserver.network.lineage.GameClient;
import org.mmocore.gameserver.network.lineage.clientpackets.L2GameClientPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.VersionCheck;
import org.mmocore.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestGameGuardQuery extends L2GameClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestGameGuardQuery.class);
    private static final int revisionNumber = JtsProtectionConfig.PROTECTION_REVISION_NUMBER;

    private int code;
    private int section;
    private int index;
    private int revision;
    private String procBuf;

    @Override
    protected void readImpl() {
        code = readD();
        section = readD();
        index = readD();
        revision = readD();
        if (code == 0x01) {
            procBuf = readS(); //TODO[K] - сторадж в базу, либо на диск, для дальнейшего анализа!
        }
    }

    @Override
    protected void runImpl() {
        final GameClient client = getClient();
        if (client == null) {
            return;
        }

        if (revision != revisionNumber) {
            Log.audit("[ProtectionLog", "Client: " + client.getIpAddr() + " requested not correct revision from protect. Current revision: " + JtsProtectionConfig.PROTECTION_REVISION_NUMBER + " Client revision: " + revision);
            client.close(new VersionCheck(null));
            return;
        }

        final GameGuardResponse gg = GameGuardResponse.values()[code];

        switch (gg) {
            case NONE:
                LOGGER.warn("Game Guard response NONE. Check this. Maybe hucked client(bot, antiGuard, etc)");
                break;
            case NORMAL_RESPONSE:
                //client.sendPacket(new NetPing(client.getAccountId()));
                if (procBuf != null && !procBuf.isEmpty()) {
                    Protection.getInstance().storeProcBufInfo(client.getLogin(), procBuf);
                }
                break;
            case KICK_RESPONSE:
                HWIDBanManager.getInstance().systemBan(client, GameGuardResponse.KICK_RESPONSE.toString(), BanType.NONE);
                break;
            case USED_BOT_RESPONS:
                final BotResponse bot = BotResponse.values()[section];
                //Client senede info from bot detection. Value in BotResponse enume. Use bot enum, and ban/kick him :)
                HWIDBanManager.getInstance().systemBan(client, GameGuardResponse.USED_BOT_RESPONS.toString(), BanType.PLAYER_BAN);
                break;
            case GET_SN_IS_FALSE_RESPONSE:
                HWIDBanManager.getInstance().systemBan(client, GameGuardResponse.GET_SN_IS_FALSE_RESPONSE.toString(), BanType.ACCOUNT_BAN);
                break;
            case SN_NULL_LENGHT_RESPONSE:
                HWIDBanManager.getInstance().systemBan(client, GameGuardResponse.SN_NULL_LENGHT_RESPONSE.toString(), BanType.ACCOUNT_BAN);
                break;
            case SP_OBJECT_CHANGED_RESPONSE:
                LOGGER.warn("Game Guard response SP_OBJECT_CHANGED_RESPONSE. Check this. Maybe hucked client(bot, antiGuard, etc)");
                HWIDBanManager.getInstance().systemBan(client, GameGuardResponse.SP_OBJECT_CHANGED_RESPONSE.toString(), BanType.ACCOUNT_BAN);
                break;
            case REQUEST_REVISION_VALIDATE:
                break;
            case NOT_VALID_HOST_INFO:
                Log.audit("[ProtectionLog", "Client: " + client.getIpAddr() + " changed host info.");
                client.close(new VersionCheck(null));
                break;
        }
    }
}