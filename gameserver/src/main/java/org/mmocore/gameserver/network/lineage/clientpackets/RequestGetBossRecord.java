package org.mmocore.gameserver.network.lineage.clientpackets;


import org.mmocore.gameserver.manager.RaidBossSpawnManager;
import org.mmocore.gameserver.network.lineage.serverpackets.ExGetBossRecord;
import org.mmocore.gameserver.network.lineage.serverpackets.ExGetBossRecord.BossRecordInfo;
import org.mmocore.gameserver.object.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Format: (ch) d
 */
public class RequestGetBossRecord extends L2GameClientPacket {

    @Override
    protected void readImpl() {
        int _bossID = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        int totalPoints = 0;
        int ranking = 0;

        if (activeChar == null) {
            return;
        }

        final List<BossRecordInfo> list = new ArrayList<>();
        final Map<Integer, Integer> points = RaidBossSpawnManager.getInstance().getPointsForOwnerId(activeChar.getObjectId());
        if (points != null && !points.isEmpty()) {
            for (final Map.Entry<Integer, Integer> e : points.entrySet()) {
                switch (e.getKey()) {
                    case -1: // RaidBossSpawnManager.KEY_RANK
                        ranking = e.getValue();
                        break;
                    case 0: //  RaidBossSpawnManager.KEY_TOTAL_POINTS
                        totalPoints = e.getValue();
                        break;
                    default:
                        list.add(new BossRecordInfo(e.getKey(), e.getValue(), 0));
                }
            }
        }

        activeChar.sendPacket(new ExGetBossRecord(ranking, totalPoints, list));
    }
}