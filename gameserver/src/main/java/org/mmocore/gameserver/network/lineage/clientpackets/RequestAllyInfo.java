package org.mmocore.gameserver.network.lineage.clientpackets;


import org.mmocore.gameserver.model.pledge.Alliance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.IBroadcastPacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.tables.ClanTable;

import java.util.ArrayList;
import java.util.List;


public class RequestAllyInfo extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        final Alliance ally = player.getAlliance();
        if (ally == null) {
            return;
        }

        int clancount = 0;
        final Clan leaderclan = player.getAlliance().getLeader();
        clancount = ClanTable.getInstance().getAlliance(leaderclan.getAllyId()).getMembers().length;
        final int[] online = new int[clancount + 1];
        final int[] count = new int[clancount + 1];
        final Clan[] clans = player.getAlliance().getMembers();
        for (int i = 0; i < clancount; i++) {
            online[i + 1] = clans[i].getOnlineMembers(0).size();
            count[i + 1] = clans[i].getAllSize();
            online[0] += online[i + 1];
            count[0] += count[i + 1];
        }

        final List<IBroadcastPacket> packets = new ArrayList<>(7 + 5 * clancount);
        packets.add(SystemMsg._ALLIANCE_INFORMATION_);
        packets.add(new SystemMessage(SystemMsg.ALLIANCE_NAME_S1).addString(player.getClan().getAlliance().getAllyName()));
        packets.add(new SystemMessage(SystemMsg.CONNECTION_S1_TOTAL_S2).addNumber(online[0]).addNumber(count[0])); //Connection
        packets.add(new SystemMessage(SystemMsg.ALLIANCE_LEADER_S2_OF_S1).addString(leaderclan.getName()).addString(leaderclan.getLeaderName()));
        packets.add(new SystemMessage(SystemMsg.AFFILIATED_CLANS_TOTAL_S1_CLAN_S).addNumber(clancount)); //clan count
        packets.add(SystemMsg._CLAN_INFORMATION_);
        for (int i = 0; i < clancount; i++) {
            packets.add(new SystemMessage(SystemMsg.CLAN_NAME_S1).addString(clans[i].getName()));
            packets.add(new SystemMessage(SystemMsg.CLAN_LEADER_S1).addString(clans[i].getLeaderName()));
            packets.add(new SystemMessage(SystemMsg.CLAN_LEVEL_S1).addNumber(clans[i].getLevel()));
            packets.add(new SystemMessage(SystemMsg.CONNECTION_S1_TOTAL_S2).addNumber(online[i + 1]).addNumber(count[i + 1]));
            packets.add(SystemMsg.__DASHES__);
        }
        packets.add(SystemMsg.__EQUALS__);

        player.sendPacket(packets);
    }
}