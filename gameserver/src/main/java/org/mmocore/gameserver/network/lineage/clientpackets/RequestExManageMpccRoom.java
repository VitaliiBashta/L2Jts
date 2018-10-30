package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.model.team.matching.MatchingRoom;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 */
public class RequestExManageMpccRoom extends L2GameClientPacket {
    private int _id;
    private int _memberSize;
    private int _minLevel;
    private int _maxLevel;
    private String _topic;

    @Override
    protected void readImpl() {
        _id = readD();  // id
        _memberSize = readD();  // member size
        _minLevel = readD();  //min level
        _maxLevel = readD();  //max level
        readD();  //lootType
        _topic = readS();  //topic
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        final MatchingRoom room = player.getMatchingRoom();
        if (room == null || room.getId() != _id || room.getType() != MatchingRoom.CC_MATCHING) {
            return;
        }

        if (room.getGroupLeader() != player) {
            return;
        }

        room.setTopic(_topic);
        room.setMaxMemberSize(_memberSize);
        room.setMinLevel(_minLevel);
        room.setMaxLevel(_maxLevel);
        room.broadCast(room.infoRoomPacket());

        player.sendPacket(SystemMsg.THE_COMMAND_CHANNEL_MATCHING_ROOM_INFORMATION_WAS_EDITED);
    }
}