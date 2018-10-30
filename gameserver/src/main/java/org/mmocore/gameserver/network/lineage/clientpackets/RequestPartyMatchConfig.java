package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.manager.MatchingRoomManager;
import org.mmocore.gameserver.model.team.CommandChannel;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.model.team.matching.CCMatchingRoom;
import org.mmocore.gameserver.model.team.matching.MatchingRoom;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ListPartyWaiting;
import org.mmocore.gameserver.object.Player;

public class RequestPartyMatchConfig extends L2GameClientPacket {
    private int _page;
    private int _region;
    private int _allLevels;

    /**
     * Format: ddd
     */
    @Override
    protected void readImpl() {
        _page = readD();
        _region = readD(); // 0 to 15, or -1
        _allLevels = readD(); // 1 -> all levels, 0 -> only levels matching my level
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        final Party party = player.getParty();
        final CommandChannel channel = party != null ? party.getCommandChannel() : null;

        if (channel != null && channel.getGroupLeader() == player) {
            if (channel.getMatchingRoom() == null) {
                final CCMatchingRoom room = new CCMatchingRoom(player, 1, player.getLevel(), 50, party.getLootDistribution(), player.getName());
                channel.setMatchingRoom(room);
            }
        } else if (channel != null && !channel.getParties().contains(party)) {
            player.sendPacket(SystemMsg.THE_COMMAND_CHANNEL_AFFILIATED_PARTYS_PARTY_MEMBER_CANNOT_USE_THE_MATCHING_SCREEN);
        } else if (party != null && !party.isLeader(player)) {
            final MatchingRoom room = player.getMatchingRoom();
            if (room != null && room.getType() == MatchingRoom.PARTY_MATCHING) {
                player.setMatchingRoomWindowOpened(true);
                player.sendPacket(room.infoRoomPacket(), room.membersPacket(player));
            } else {
                player.sendPacket(SystemMsg.THE_LIST_OF_PARTY_ROOMS_CAN_ONLY_BE_VIEWED_BY_A_PERSON_WHO_IS_NOT_PART_OF_A_PARTY);
            }

        } else {
            if (party == null) {
                MatchingRoomManager.getInstance().addToWaitingList(player);
            }
            player.sendPacket(new ListPartyWaiting(_region, _allLevels == 1, _page, player));
        }
    }
}