package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.model.team.matching.MatchingRoom;
import org.mmocore.gameserver.model.team.matching.PartyMatchingRoom;
import org.mmocore.gameserver.object.Player;

public class RequestPartyMatchList extends L2GameClientPacket {
    private int _lootDist;
    private int _maxMembers;
    private int _minLevel;
    private int _maxLevel;
    private int _roomId;
    private String _roomTitle;

    /**
     * Format:(ch) dddddS
     */
    @Override
    protected void readImpl() {
        _roomId = readD();
        _maxMembers = readD();
        _minLevel = readD();
        _maxLevel = readD();
        _lootDist = readD();
        _roomTitle = readS(64);
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        final Party party = player.getParty();
        if (party != null && party.getGroupLeader() != player) {
            return;
        }

        MatchingRoom room = player.getMatchingRoom();
        if (room == null) {
            room = new PartyMatchingRoom(player, _minLevel, _maxLevel, _maxMembers, _lootDist, _roomTitle);
            if (party != null) {
                for (Player member : party.getPartyMembers()) {
                    if (member != null && member != player) {
                        room.addMemberForce(member);
                    }
                }
            }
        } else if (room.getId() == _roomId && room.getType() == MatchingRoom.PARTY_MATCHING && room.getGroupLeader() == player) {
            room.setMinLevel(_minLevel);
            room.setMaxLevel(_maxLevel);
            room.setMaxMemberSize(_maxMembers);
            room.setTopic(_roomTitle);
            room.setLootType(_lootDist);
            room.broadCast(room.infoRoomPacket());
        }
    }
}