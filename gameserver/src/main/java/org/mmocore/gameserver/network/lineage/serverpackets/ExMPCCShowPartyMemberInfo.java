package org.mmocore.gameserver.network.lineage.serverpackets;


import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

import java.util.ArrayList;
import java.util.List;


/**
 * Format: ch d[Sdd]
 *
 * @author SYS
 */
public class ExMPCCShowPartyMemberInfo extends GameServerPacket {
    private final List<PartyMemberInfo> members;

    public ExMPCCShowPartyMemberInfo(final Party party) {
        members = new ArrayList<>();
        for (final Player _member : party.getPartyMembers()) {
            members.add(new PartyMemberInfo(_member.getName(), _member.getObjectId(), _member.getPlayerClassComponent().getClassId().getId()));
        }
    }

    @Override
    protected final void writeData() {
        writeD(members.size()); // Количество членов в пати
        for (final PartyMemberInfo member : members) {
            writeS(member.name); // Имя члена пати
            writeD(member.object_id); // object Id члена пати
            writeD(member.class_id); // id класса члена пати
        }

        members.clear();
    }

    static class PartyMemberInfo {
        public final String name;
        public final int object_id;
        public final int class_id;

        public PartyMemberInfo(final String _name, final int _object_id, final int _class_id) {
            name = _name;
            object_id = _object_id;
            class_id = _class_id;
        }
    }
}