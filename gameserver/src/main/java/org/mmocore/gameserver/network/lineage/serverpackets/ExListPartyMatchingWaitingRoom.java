package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.commons.lang.ArrayUtils;
import org.mmocore.gameserver.manager.MatchingRoomManager;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Format:(ch) d [sdd]
 */
public class ExListPartyMatchingWaitingRoom extends GameServerPacket {
    private final int fullSize;
    private List<PartyMatchingWaitingInfo> waitingList = Collections.emptyList();

    public ExListPartyMatchingWaitingRoom(final Player searcher, final int minLevel, final int maxLevel, final int page, final int[] classes) {
        final int first = (page - 1) * 64;
        final int firstNot = page * 64;
        int i = 0;

        final List<Player> temp = MatchingRoomManager.getInstance().getWaitingList(minLevel, maxLevel, classes);
        fullSize = temp.size();

        waitingList = new ArrayList<>(fullSize);
        for (final Player pc : temp) {
            if (i < first || i >= firstNot) {
                continue;
            }
            waitingList.add(new PartyMatchingWaitingInfo(pc));
            i++;
        }
    }

    @Override
    protected void writeData() {
        writeD(fullSize);
        writeD(waitingList.size());
        for (final PartyMatchingWaitingInfo waiting_info : waitingList) {
            writeS(waiting_info.name);
            writeD(waiting_info.classId);
            writeD(waiting_info.level);
            writeD(waiting_info.currentInstance);
            writeD(waiting_info.instanceReuses.length);
            for (final int i : waiting_info.instanceReuses) {
                writeD(i);
            }
        }
    }

    static class PartyMatchingWaitingInfo {
        public final int classId, level, currentInstance;
        public final String name;
        public final int[] instanceReuses;

        public PartyMatchingWaitingInfo(final Player member) {
            name = member.getName();
            classId = member.getPlayerClassComponent().getClassId().getId();
            level = member.getLevel();
            final Reflection ref = member.getReflection();
            currentInstance = ref == null ? 0 : ref.getInstancedZoneId();
            instanceReuses = ArrayUtils.toArray(member.getInstanceReuses().keySet());
        }
    }
}