package org.mmocore.gameserver.network.lineage.serverpackets;


import org.mmocore.gameserver.model.team.CommandChannel;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

import java.util.ArrayList;
import java.util.List;


public class ExMultiPartyCommandChannelInfo extends GameServerPacket {
    private final String ChannelLeaderName;
    private final int MemberCount;
    private final List<ChannelPartyInfo> parties;

    public ExMultiPartyCommandChannelInfo(final CommandChannel channel) {
        ChannelLeaderName = channel.getGroupLeader().getName();
        MemberCount = channel.getMemberCount();

        parties = new ArrayList<>();
        for (final Party party : channel.getParties()) {
            final Player leader = party.getGroupLeader();
            if (leader != null) {
                parties.add(new ChannelPartyInfo(leader.getName(), leader.getObjectId(), party.getMemberCount()));
            }
        }
    }

    @Override
    protected void writeData() {
        writeS(ChannelLeaderName); // имя лидера CC
        writeD(0); // Looting type?
        writeD(MemberCount); // общее число человек в СС
        writeD(parties.size()); // общее число партий в СС

        for (final ChannelPartyInfo party : parties) {
            writeS(party.Leader_name); // имя лидера партии
            writeD(party.Leader_obj_id); // ObjId пати лидера
            writeD(party.MemberCount); // количество мемберов в пати
        }
    }

    static class ChannelPartyInfo {
        public final String Leader_name;
        public final int Leader_obj_id;
        public final int MemberCount;

        public ChannelPartyInfo(final String _Leader_name, final int _Leader_obj_id, final int _MemberCount) {
            Leader_name = _Leader_name;
            Leader_obj_id = _Leader_obj_id;
            MemberCount = _MemberCount;
        }
    }
}