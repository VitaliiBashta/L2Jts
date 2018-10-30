package org.mmocore.gameserver.model.team;

import com.google.common.collect.Iterators;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.listener.actor.player.OnAnswerListener;
import org.mmocore.gameserver.listener.actor.player.OnForcedDisconnectListener;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.team.matching.MatchingRoom;
import org.mmocore.gameserver.network.lineage.components.IBroadcastPacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.*;
import org.mmocore.gameserver.object.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CommandChannel implements PlayerGroup {
    public static final int STRATEGY_GUIDE_ID = 8871;
    public static final int CLAN_IMPERIUM_ID = 391;

    private final List<Party> _commandChannelParties = new CopyOnWriteArrayList<>();
    private final OnForcedDisconnectListener changeLeaderOnForcedDisconnect = new ChangeLeaderOnForcedDisconnect();
    private Player _commandChannelLeader;
    private int _commandChannelLvl;
    private Reflection _reflection;
    private MatchingRoom _matchingRoom;

    /**
     * Creates a New Command Channel and Add the Leaders party to the CC
     *
     * @param leader
     */
    public CommandChannel(final Player leader) {
        _commandChannelLeader = leader;
        _commandChannelParties.add(leader.getParty());
        _commandChannelLvl = leader.getParty().getLevel();
        leader.getParty().setCommandChannel(this);
        broadCast(ExMPCCOpen.STATIC);
        _commandChannelLeader.addListener(changeLeaderOnForcedDisconnect);
    }

    /**
     * Проверяет возможность создания командного канала
     */
    public static boolean checkAuthority(final Player creator) {
        // CC могут создавать только лидеры партий, состоящие в клане ранком не ниже барона
        if (creator.getClan() == null || !creator.isInParty() || !creator.getParty().isLeader(creator) || creator.getPledgeClass() < Player.RANK_WISEMAN) {
            creator.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_USE_THE_COMMAND_CHANNEL);
            return false;
        }

        // CC можно создать, если есть клановый скилл Clan Imperium
        final boolean haveSkill = creator.getSkillLevel(CLAN_IMPERIUM_ID) > 0;

        // Ищем Strategy Guide в инвентаре
        final boolean haveItem = creator.getInventory().getItemByItemId(STRATEGY_GUIDE_ID) != null;

        if (!haveSkill && !haveItem) {
            creator.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_USE_THE_COMMAND_CHANNEL);
            return false;
        }

        return true;
    }

    /**
     * Adds a Party to the Command Channel
     *
     * @param party
     */
    public void addParty(final Party party) {
        broadCast(new ExMPCCPartyInfoUpdate(party, 1));
        _commandChannelParties.add(party);
        refreshLevel();
        party.setCommandChannel(this);

        for (final Player $member : party) {
            $member.sendPacket(ExMPCCOpen.STATIC);
            if (_matchingRoom != null) {
                _matchingRoom.broadcastPlayerUpdate($member);
            }
        }
    }

    /**
     * Removes a Party from the Command Channel
     *
     * @param party
     */
    public void removeParty(final Party party) {
        _commandChannelParties.remove(party);
        refreshLevel();
        party.setCommandChannel(null);
        party.broadCast(ExMPCCClose.STATIC);
        final Reflection reflection = getReflection();
        if (reflection != null) {
            for (final Player player : party.getPartyMembers()) {
                player.teleToLocation(reflection.getReturnLoc(), 0);
            }
        }

        if (_commandChannelParties.size() < 2) {
            disbandChannel();
        } else {
            for (final Player $member : party) {
                $member.sendPacket(new ExMPCCPartyInfoUpdate(party, 0));
                if (_matchingRoom != null) {
                    _matchingRoom.broadcastPlayerUpdate($member);
                }
            }
        }
    }

    /**
     * Распускает Command Channel
     */
    public void disbandChannel() {
        broadCast(SystemMsg.THE_COMMAND_CHANNEL_HAS_BEEN_DISBANDED);
        for (final Party party : _commandChannelParties) {
            party.setCommandChannel(null);
            party.broadCast(ExMPCCClose.STATIC);
            if (isInReflection()) {
                party.broadCast(new SystemMessage(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(1));
                for (Player p : party)
                    getReflection().onPlayerExit(p);
            }
        }
        final Reflection reflection = getReflection();
        if (reflection != null) {
            reflection.startCollapseTimer(60000L);
            setReflection(null);
        }

        if (_matchingRoom != null) {
            _matchingRoom.disband();
        }
        _commandChannelParties.clear();
        _commandChannelLeader.removeListener(changeLeaderOnForcedDisconnect);
        _commandChannelLeader = null;
    }

    public void askLeaderAboutDisbandChannel() {
        _commandChannelLeader.ask(new ConfirmDlg(
                        SystemMsg.IF_THE_COMMAND_CHANNEL_LEADER_LEAVES_THE_PARTY_MATCHING_ROOM_THEN_THE_SESSIONS_ENDS, 0),
                new OnAnswerListener() {
                    @Override
                    public void sayYes() {
                        disbandChannel();
                    }

                    @Override
                    public void sayNo() {
                    }
                });
    }

    /**
     * @return overall count members of the Command Channel
     */
    public int getMemberCount() {
        int count = 0;
        for (final Party party : _commandChannelParties) {
            count += party.getMemberCount();
        }
        return count;
    }

    /**
     * Broadcast packet to every channel member
     *
     * @param gsp
     */
    @Override
    public void broadCast(final IBroadcastPacket... gsp) {
        for (final Party party : _commandChannelParties) {
            party.broadCast(gsp);
        }
    }

    /**
     * Broadcast packet to every party leader of command channel
     */
    public void broadcastToChannelPartyLeaders(final L2GameServerPacket gsp) {
        for (final Party party : _commandChannelParties) {
            final Player leader = party.getGroupLeader();
            if (leader != null) {
                leader.sendPacket(gsp);
            }
        }
    }

    /**
     * @return list of Parties in Command Channel
     */
    public List<Party> getParties() {
        return _commandChannelParties;
    }

    @Override
    public Iterator<Player> iterator() {
        final List<Player> players = new ArrayList<>(_commandChannelParties.size());

        for (final Party p : getParties())
            players.addAll(p.getPartyMembers());

        return Iterators.unmodifiableIterator(players.iterator());
    }

    /**
     * @return Level of CC
     */
    public int getLevel() {
        return _commandChannelLvl;
    }

    /**
     * @return the leader of the Command Channel
     */
    @Override
    public Player getGroupLeader() {
        return _commandChannelLeader;
    }

    public Player getChannelLeader() {
        return getGroupLeader();
    }

    /**
     * @param newLeader the leader of the Command Channel
     */
    public void setChannelLeader(final Player newLeader) {
        _commandChannelLeader = newLeader;
        _commandChannelLeader.addListener(changeLeaderOnForcedDisconnect);
        broadCast(new SystemMessage(SystemMsg.COMMAND_CHANNEL_AUTHORITY_HAS_BEEN_TRANSFERRED_TO_C1).addString(newLeader.getName()));
    }

    private void refreshLevel() {
        _commandChannelLvl = 0;
        _commandChannelParties.stream().filter(pty -> pty.getLevel() > _commandChannelLvl).forEach(pty -> {
            _commandChannelLvl = pty.getLevel();
        });
    }

    public boolean isInReflection() {
        return _reflection != null;
    }

    public Reflection getReflection() {
        return _reflection;
    }

    public void setReflection(final Reflection reflection) {
        _reflection = reflection;
    }

    public MatchingRoom getMatchingRoom() {
        return _matchingRoom;
    }

    public void setMatchingRoom(final MatchingRoom matchingRoom) {
        _matchingRoom = matchingRoom;
    }

    private class ChangeLeaderOnForcedDisconnect implements OnForcedDisconnectListener {
        @Override
        public void onForcedDisconnect(final Player player) {
            if (player == null) {
                return;
            }
            if (player.getParty() == null)
                return;
            final Player randomPartyMember = Rnd.get(player.getParty().getPartyMembers());
            setChannelLeader(randomPartyMember);
        }
    }
}