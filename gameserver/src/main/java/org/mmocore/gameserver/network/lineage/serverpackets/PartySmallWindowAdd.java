package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

public class PartySmallWindowAdd extends GameServerPacket {
    private final int objectId;
    private final int lootType;
    private final PartySmallWindowAll.PartySmallWindowMemberInfo member;

    public PartySmallWindowAdd(final Party party, final Player member) {
        objectId = party.getGroupLeader().getObjectId();
        lootType = party.getLootDistribution();
        this.member = new PartySmallWindowAll.PartySmallWindowMemberInfo(member);
    }

    @Override
    protected final void writeData() {
        writeD(objectId); // c3
        writeD(lootType);
        writeD(member.id);
        writeS(member.name);
        writeD(member.curCp);
        writeD(member.maxCp);
        writeD(member.curHp);
        writeD(member.maxHp);
        writeD(member.curMp);
        writeD(member.maxMp);
        writeD(member.level);
        writeD(member.class_id);
        writeD(0);//sex
        writeD(member.race_id);
        writeD(member.isDisguised);
        writeD(member.dominionId);
    }
}