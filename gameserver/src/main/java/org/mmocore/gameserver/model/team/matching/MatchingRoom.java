package org.mmocore.gameserver.model.team.matching;

import org.mmocore.gameserver.listener.actor.player.OnPlayerPartyInviteListener;
import org.mmocore.gameserver.listener.actor.player.OnPlayerPartyLeaveListener;
import org.mmocore.gameserver.manager.MatchingRoomManager;
import org.mmocore.gameserver.model.team.PlayerGroup;
import org.mmocore.gameserver.network.lineage.components.IBroadcastPacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.L2GameServerPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author VISTALL
 * @date 18:38/11.06.2011
 */
public abstract class MatchingRoom implements PlayerGroup {
    public static final int PARTY_MATCHING = 0;
    public static final int CC_MATCHING = 1;
    //
    public static final int WAIT_PLAYER = 0;
    public static final int ROOM_MASTER = 1;
    public static final int PARTY_MEMBER = 2;
    public static final int UNION_LEADER = 3;
    public static final int UNION_PARTY = 4;
    public static final int WAIT_PARTY = 5;
    public static final int WAIT_NORMAL = 6;
    protected final Set<Player> _members = new CopyOnWriteArraySet<Player>();
    private final int _id;
    private final PartyListenerImpl _listener = new PartyListenerImpl();
    protected Player _leader;
    private int _minLevel;
    private int _maxLevel;
    private int _maxMemberSize;
    private int _lootType;
    private String _topic;
    public MatchingRoom(final Player leader, final int minLevel, final int maxLevel, final int maxMemberSize, final int lootType, final String topic) {
        _leader = leader;
        _id = MatchingRoomManager.getInstance().addMatchingRoom(this);
        _minLevel = minLevel;
        _maxLevel = maxLevel;
        _maxMemberSize = maxMemberSize;
        _lootType = lootType;
        _topic = topic;
        addMember0(leader, null, true);
    }

    // ===============================================================================================================================================
    // Add/Remove Member
    // ===============================================================================================================================================
    public boolean addMember(final Player player) {
        if (_members.contains(player)) {
            return true;
        }
        if (player.getLevel() < getMinLevel() || player.getLevel() > getMaxLevel() || getPlayers().size() >= getMaxMembersSize()) {
            player.sendPacket(notValidMessage());
            return false;
        }
        return addMember0(player, new SystemMessage(enterMessage()).addName(player), true);
    }

    public boolean addMemberForce(final Player player) {
        if (_members.contains(player)) {
            return true;
        }
        if (getPlayers().size() >= getMaxMembersSize()) {
            player.sendPacket(notValidMessage());
            return false;
        }
        return addMember0(player, new SystemMessage(enterMessage()).addName(player), false);
    }

    private boolean addMember0(final Player player, final L2GameServerPacket p, final boolean sendInfo) {
        if (!_members.isEmpty()) {
            player.addListener(_listener);
        }
        _members.add(player);
        player.setMatchingRoom(this);
        for (Player $member : this) {
            if ($member != player && $member.isMatchingRoomWindowOpened()) {
                $member.sendPacket(p, addMemberPacket($member, player));
            }
        }
        MatchingRoomManager.getInstance().removeFromWaitingList(player);
        if (sendInfo) {
            player.setMatchingRoomWindowOpened(true);
            player.sendPacket(infoRoomPacket(), membersPacket(player));
        }
        player.sendChanges();
        return true;
    }

    public void removeMember(final Player member, final boolean oust) {
        if (!_members.remove(member)) {
            return;
        }
        member.removeListener(_listener);
        member.setMatchingRoom(null);
        if (_members.isEmpty()) {
            disband();
        } else {
            final L2GameServerPacket infoPacket = infoRoomPacket();
            final SystemMsg exitMessage0 = exitMessage(true, oust);
            final L2GameServerPacket exitMessage = exitMessage0 != null ? new SystemMessage(exitMessage0).addName(member) : null;
            for (final Player player : this) {
                if (player.isMatchingRoomWindowOpened()) {
                    player.sendPacket(infoPacket, removeMemberPacket(player, member), exitMessage, managerRoomPacket(member, 2));
                }
            }
        }
        member.sendPacket(closeRoomPacket(), exitMessage(false, oust), managerRoomPacket(member, 2));
        member.setMatchingRoomWindowOpened(false);
        // MatchingRoomManager.getInstance().addToWaitingList(member); // не нужно, клиент добавляет сам
        member.sendChanges();
    }

    public void broadcastPlayerUpdate(final Player player) {
        for (final Player $member : MatchingRoom.this) {
            if ($member.isMatchingRoomWindowOpened()) {
                $member.sendPacket(updateMemberPacket($member, player));
            }
        }
    }

    public void disband() {
        for (final Player player : this) {
            player.removeListener(_listener);
            if (player.isMatchingRoomWindowOpened()) {
                player.sendPacket(closeRoomMessage());
                player.sendPacket(closeRoomPacket());
            }
            player.setMatchingRoom(null);
            player.sendChanges();
        }
        _members.clear();
        MatchingRoomManager.getInstance().removeMatchingRoom(this);
    }

    // ===============================================================================================================================================
    // Abstracts
    // ===============================================================================================================================================
    public abstract SystemMsg notValidMessage();

    public abstract SystemMsg enterMessage();

    public abstract SystemMsg exitMessage(final boolean toOthers, final boolean kick);

    public abstract SystemMsg closeRoomMessage();

    public abstract SystemMsg changeLeaderMessage();

    public abstract L2GameServerPacket closeRoomPacket();

    public abstract L2GameServerPacket infoRoomPacket();

    public abstract L2GameServerPacket addMemberPacket(final Player $member, final Player active);

    public abstract L2GameServerPacket removeMemberPacket(final Player $member, final Player active);

    public abstract L2GameServerPacket updateMemberPacket(final Player $member, final Player active);

    public abstract L2GameServerPacket managerRoomPacket(final Player $member, final int mode);

    public abstract L2GameServerPacket membersPacket(final Player active);

    public abstract int getType();

    public abstract int getMemberType(final Player member);

    // ===============================================================================================================================================
    // Broadcast
    // ===============================================================================================================================================
    @Override
    public void broadCast(final IBroadcastPacket... arg) {
        for (final Player player : this) {
            player.sendPacket(arg);
        }
    }

    // ===============================================================================================================================================
    // Getters
    // ===============================================================================================================================================
    public int getId() {
        return _id;
    }

    public int getMinLevel() {
        return _minLevel;
    }

    // ===============================================================================================================================================
    // Setters
    // ===============================================================================================================================================
    public void setMinLevel(final int minLevel) {
        _minLevel = minLevel;
    }

    public int getMaxLevel() {
        return _maxLevel;
    }

    public void setMaxLevel(final int maxLevel) {
        _maxLevel = maxLevel;
    }

    public String getTopic() {
        return _topic;
    }

    public void setTopic(final String topic) {
        _topic = topic;
    }

    public int getMaxMembersSize() {
        return _maxMemberSize;
    }

    public int getLocationId() {
        return MatchingRoomManager.getInstance().getLocation(_leader);
    }

    public Player getLeader() {
        return _leader;
    }

    public void setLeader(final Player leader) {
        final Player oldLeader = _leader;
        _leader = leader;
        if (!_members.contains(leader)) {
            addMember0(leader, null, true);
        } else {
            if (!leader.isMatchingRoomWindowOpened()) {
                leader.setMatchingRoomWindowOpened(true);
                leader.sendPacket(infoRoomPacket(), membersPacket(leader));
            }
            final SystemMsg changeLeaderMessage = changeLeaderMessage();
            for (final Player $member : this) {
                if ($member.isMatchingRoomWindowOpened()) {
                    $member.sendPacket(/*updateMemberPacket($member, leader),*/ managerRoomPacket(leader, 1), managerRoomPacket(oldLeader, 1), changeLeaderMessage);
                }
            }
        }
    }

    public Collection<Player> getPlayers() {
        return _members;
    }

    public int getLootType() {
        return _lootType;
    }

    public void setLootType(final int lootType) {
        _lootType = lootType;
    }

    @Override
    public int getMemberCount() {
        return getPlayers().size();
    }

    @Override
    public Player getGroupLeader() {
        return getLeader();
    }

    @Override
    public Iterator<Player> iterator() {
        return _members.iterator();
    }

    public void setMaxMemberSize(final int maxMemberSize) {
        _maxMemberSize = maxMemberSize;
    }

    private class PartyListenerImpl implements OnPlayerPartyInviteListener, OnPlayerPartyLeaveListener {
        @Override
        public void onPartyInvite(final Player player) {
            broadcastPlayerUpdate(player);
        }

        @Override
        public void onPartyLeave(final Player player) {
            broadcastPlayerUpdate(player);
        }
    }
}