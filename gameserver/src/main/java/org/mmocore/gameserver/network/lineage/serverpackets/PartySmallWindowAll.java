package org.mmocore.gameserver.network.lineage.serverpackets;


import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeEvent;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.Servitor;

import java.util.ArrayList;
import java.util.List;


/**
 * format   ddd+[dSddddddddddddd{ddSddddd}]
 */
public class PartySmallWindowAll extends GameServerPacket {
    private final int leaderId;
    private final int loot;
    private final List<PartySmallWindowMemberInfo> members = new ArrayList<>();

    public PartySmallWindowAll(final Party party, final Player exclude) {
        leaderId = party.getGroupLeader().getObjectId();
        loot = party.getLootDistribution();

        for (final Player member : party.getPartyMembers()) {
            if (member != exclude) {
                members.add(new PartySmallWindowMemberInfo(member));
            }
        }
    }

    @Override
    protected final void writeData() {
        writeD(leaderId);
        writeD(loot);
        writeD(members.size());
        for (final PartySmallWindowMemberInfo member : members) {
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

            writeD(member.pet_id);
            if (member.pet_id != 0) {
                writeD(member.pet_NpcId);
                writeD(member.pet_type);
                writeS(member.pet_Name);
                writeD(member.pet_curHp);
                writeD(member.pet_maxHp);
                writeD(member.pet_curMp);
                writeD(member.pet_maxMp);
                writeD(member.pet_level);
            }
        }
    }

    public static class PartySmallWindowMemberInfo {
        public final String name;
        public final int id;
        public final int curCp;
        public final int maxCp;
        public final int curHp;
        public final int maxHp;
        public final int curMp;
        public final int maxMp;
        public final int level;
        public final int class_id;
        public final int race_id;
        public String pet_Name;
        public boolean isDisguised;
        public int dominionId;
        public int pet_id, pet_type, pet_NpcId, pet_curHp, pet_maxHp, pet_curMp, pet_maxMp, pet_level;

        public PartySmallWindowMemberInfo(final Player member) {
            name = member.getName();
            id = member.getObjectId();
            curCp = (int) member.getCurrentCp();
            maxCp = member.getMaxCp();
            curHp = (int) member.getCurrentHp();
            maxHp = (int) member.getMaxHp();
            curMp = (int) member.getCurrentMp();
            maxMp = member.getMaxMp();
            level = member.getLevel();
            class_id = member.getPlayerClassComponent().getClassId().getId();
            race_id = member.getPlayerTemplateComponent().getPlayerRace().ordinal();

            final DominionSiegeEvent siegeEvent = member.getEvent(DominionSiegeEvent.class);
            if (siegeEvent != null) {
                isDisguised = siegeEvent.getObjects(DominionSiegeEvent.DISGUISE_PLAYERS).contains(id);
                dominionId = isDisguised ? siegeEvent.getId() : 0;
            }

            final Servitor pet = member.getServitor();
            if (pet != null) {
                pet_id = pet.getObjectId();
                pet_type = pet.getServitorType();
                pet_NpcId = pet.getNpcId() + 1000000;
                pet_Name = pet.getName();
                pet_curHp = (int) pet.getCurrentHp();
                pet_maxHp = (int) pet.getMaxHp();
                pet_curMp = (int) pet.getCurrentMp();
                pet_maxMp = pet.getMaxMp();
                pet_level = pet.getLevel();
            }
        }
    }
}