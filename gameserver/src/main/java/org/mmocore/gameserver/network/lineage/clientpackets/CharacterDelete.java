package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.database.mysql;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.GameClient;
import org.mmocore.gameserver.network.lineage.serverpackets.CharacterDeleteFail;
import org.mmocore.gameserver.network.lineage.serverpackets.CharacterDeleteSuccess;
import org.mmocore.gameserver.network.lineage.serverpackets.CharacterSelectionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CharacterDelete extends L2GameClientPacket {
    private static final Logger _log = LoggerFactory.getLogger(CharacterDelete.class);

    // cd
    private int _charSlot;

    @Override
    protected void readImpl() {
        _charSlot = readD();
    }

    @Override
    protected void runImpl() {
        final int clan = clanStatus();
        final int online = onlineStatus();
        if (clan > 0 || online > 0) {
            if (clan == 2) {
                sendPacket(new CharacterDeleteFail(CharacterDeleteFail.REASON_CLAN_LEADERS_MAY_NOT_BE_DELETED));
            } else if (clan == 1) {
                sendPacket(new CharacterDeleteFail(CharacterDeleteFail.REASON_YOU_MAY_NOT_DELETE_CLAN_MEMBER));
            } else if (online > 0) {
                sendPacket(new CharacterDeleteFail(CharacterDeleteFail.REASON_DELETION_FAILED));
            }
            return;
        }

        final GameClient client = getClient();

        try {
            if (ServerConfig.DELETE_DAYS == 0) {
                client.deleteChar(_charSlot);
            } else {
                client.markToDeleteChar(_charSlot);
            }
        } catch (Exception e) {
            _log.error("Error:", e);
        }

        sendPacket(new CharacterDeleteSuccess());

        final CharacterSelectionInfo cl = new CharacterSelectionInfo(client.getLogin(), client.getSessionKey().playOkID1);
        sendPacket(cl);
        client.setCharSelection(cl.getCharInfo());
    }

    private int clanStatus() {
        final int obj = getClient().getObjectIdByIndex(_charSlot);
        if (obj == -1) {
            return 0;
        }
        if (mysql.simple_get_int("clanid", "characters", "obj_Id=" + obj) > 0) {
            if (mysql.simple_get_int("leader_id", "clan_subpledges", "leader_id=" + obj + " AND type = " + Clan.SUBUNIT_MAIN_CLAN) > 0) {
                return 2;
            }
            return 1;
        }
        return 0;
    }

    private int onlineStatus() {
        final int obj = getClient().getObjectIdByIndex(_charSlot);
        if (obj == -1) {
            return 0;
        }
        if (mysql.simple_get_int("online", "characters", "obj_Id=" + obj) > 0) {
            return 1;
        }
        return 0;
    }
}
