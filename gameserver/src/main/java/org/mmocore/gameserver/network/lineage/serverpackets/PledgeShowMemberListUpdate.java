package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.pledge.SubUnit;
import org.mmocore.gameserver.model.pledge.UnitMember;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

public class PledgeShowMemberListUpdate extends GameServerPacket {
    private final String name;
    private final int lvl;
    private final int classId;
    private final int sex;
    private final int isOnline;
    private final int objectId;
    private final int pledgeType;
    private int isApprentice;

    public PledgeShowMemberListUpdate(final Player player) {
        name = player.getName();
        lvl = player.getLevel();
        classId = player.getPlayerClassComponent().getClassId().getId();
        sex = player.getSex();
        objectId = player.getObjectId();
        isOnline = player.isOnline() ? 1 : 0;
        pledgeType = player.getPledgeType();
        final SubUnit subUnit = player.getSubUnit();
        final UnitMember member = subUnit == null ? null : subUnit.getUnitMember(objectId);
        if (member != null) {
            isApprentice = member.hasSponsor() ? 1 : 0;
        }
    }

    public PledgeShowMemberListUpdate(final UnitMember cm) {
        name = cm.getName();
        lvl = cm.getLevel();
        classId = cm.getClassId();
        sex = cm.getSex();
        objectId = cm.getObjectId();
        isOnline = cm.isOnline() ? 1 : 0;
        pledgeType = cm.getPledgeType();
        isApprentice = cm.hasSponsor() ? 1 : 0;
    }

    @Override
    protected final void writeData() {
        writeS(name);
        writeD(lvl);
        writeD(classId);
        writeD(sex);
        writeD(objectId);
        writeD(isOnline); // 1=online 0=offline
        writeD(pledgeType);
        writeD(isApprentice); // does a clan member have a sponsor
    }
}