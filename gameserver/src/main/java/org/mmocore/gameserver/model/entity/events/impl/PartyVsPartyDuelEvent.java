package org.mmocore.gameserver.model.entity.events.impl;

import com.google.common.collect.Iterators;
import org.mmocore.commons.collections.CollectionUtils;
import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.data.xml.holder.InstantZoneHolder;
import org.mmocore.gameserver.model.Request;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.entity.events.objects.DuelSnapshotObject;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.network.lineage.components.IBroadcastPacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.*;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.InstantZone;
import org.mmocore.gameserver.world.World;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;

/**
 * @author VISTALL
 * @date 3:22/29.06.2011
 */
public class PartyVsPartyDuelEvent extends DuelEvent {
    public PartyVsPartyDuelEvent(final MultiValueSet<String> set) {
        super(set);
    }

    protected PartyVsPartyDuelEvent(final int id, final String name) {
        super(id, name);
    }

    @Override
    public void stopEvent() {
        if (!_isInProgress) {
            return;
        }
        _isInProgress = false;

        clearActions();

        updatePlayers(false, false);

        for (final DuelSnapshotObject d : this) {
            d.blockUnblock();

            d.getPlayer().sendPacket(new ExDuelEnd(this));
            final GameObject target = d.getPlayer().getTarget();
            if (target != null) {
                d.getPlayer().getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, target);
            }
        }

        switch (_winner) {
            case NONE:
                sendPacket(SystemMsg.THE_DUEL_HAS_ENDED_IN_A_TIE);
                break;
            case RED:
            case BLUE:
                final List<DuelSnapshotObject> winners = getObjects(_winner);
                final List<DuelSnapshotObject> lossers = getObjects(_winner.revert());

                final DuelSnapshotObject winner = CollectionUtils.safeGet(winners, 0);
                if (winner != null) {
                    sendPacket(new SystemMessage(SystemMsg.C1S_PARTY_HAS_WON_THE_DUEL).addName(winners.get(0).getPlayer()));

                    for (final DuelSnapshotObject d : lossers) {
                        d.getPlayer().broadcastPacket(new SocialAction(d.getPlayer().getObjectId(), SocialAction.BOW));
                    }
                } else {
                    sendPacket(SystemMsg.THE_DUEL_HAS_ENDED_IN_A_TIE);
                }
                break;
        }

        updatePlayers(false, true);
        removeObjects(TeamType.RED);
        removeObjects(TeamType.BLUE);
    }

    @Override
    public void teleportPlayers(final String name, final ZoneType zoneType) {
        final InstantZone instantZone = InstantZoneHolder.getInstance().getInstantZone(1);

        final Reflection reflection = new Reflection();
        reflection.init(instantZone);

        List<DuelSnapshotObject> team = getObjects(TeamType.BLUE);

        for (int i = 0; i < team.size(); i++) {
            final DuelSnapshotObject $member = team.get(i);

            $member.getPlayer().addEvent(this);
            $member.getPlayer()._stablePoint = $member.getLoc();
            $member.getPlayer().teleToLocation(instantZone.getTeleportCoords().get(i), reflection);
        }

        team = getObjects(TeamType.RED);

        for (int i = 0; i < team.size(); i++) {
            final DuelSnapshotObject $member = team.get(i);

            $member.getPlayer().addEvent(this);
            $member.getPlayer()._stablePoint = $member.getLoc();
            $member.getPlayer().teleToLocation(instantZone.getTeleportCoords().get(9 + i), reflection);
        }
    }

    @Override
    public boolean canDuel(final Player player, final Player target, final boolean first) {
        if (player.getParty() == null) {
            player.sendPacket(SystemMsg.YOU_ARE_UNABLE_TO_REQUEST_A_DUEL_AT_THIS_TIME);
            return false;
        }

        if (target.getParty() == null) {
            player.sendPacket(SystemMsg.SINCE_THE_PERSON_YOU_CHALLENGED_IS_NOT_CURRENTLY_IN_A_PARTY_THEY_CANNOT_DUEL_AGAINST_YOUR_PARTY);
            return false;
        }

        final Party party1 = player.getParty();
        final Party party2 = target.getParty();
        if (player != party1.getGroupLeader() || target != party2.getGroupLeader()) {
            player.sendPacket(SystemMsg.YOU_ARE_UNABLE_TO_REQUEST_A_DUEL_AT_THIS_TIME);
            return false;
        }

        final Iterator<Player> iterator = Iterators.concat(party1.iterator(), party2.iterator());
        while (iterator.hasNext()) {
            final Player member = iterator.next();

            final IBroadcastPacket packet = canDuel0(player, member, false);
            if (packet != null) {
                player.sendPacket(packet);
                target.sendPacket(packet);
                return false;
            }
        }
        return true;
    }

    @Override
    public void askDuel(final Player player, final Player target, final int arenaId) {
        final Request request = new Request(Request.L2RequestType.DUEL, player, target).setTimeout(10000L);
        request.set("duelType", 1);
        player.setRequest(request);
        target.setRequest(request);

        player.sendPacket(new SystemMessage(SystemMsg.C1S_PARTY_HAS_BEEN_CHALLENGED_TO_A_DUEL).addName(target));
        target.sendPacket(new SystemMessage(SystemMsg.C1S_PARTY_HAS_CHALLENGED_YOUR_PARTY_TO_A_DUEL).addName(player), new ExDuelAskStart(player.getName(), 1));
    }

    @Override
    public void createDuel(final Player player, final Player target, final int arenaId) {
        final PartyVsPartyDuelEvent duelEvent = new PartyVsPartyDuelEvent(getDuelType(), player.getObjectId() + "_" + target.getObjectId() + "_duel");
        cloneTo(duelEvent);

        for (final Player $member : player.getParty()) {
            duelEvent.addObject(TeamType.BLUE, new DuelSnapshotObject($member, TeamType.BLUE, true));
            $member.addEvent(duelEvent);
        }

        for (final Player $member : target.getParty()) {
            duelEvent.addObject(TeamType.RED, new DuelSnapshotObject($member, TeamType.RED, true));
            $member.addEvent(duelEvent);
        }

        duelEvent.sendPacket(new ExDuelReady(this));
        duelEvent.reCalcNextTime(false);
    }

    @Override
    public void packetSurrender(final Player player) {
        //
    }

    @Override
    public void onDie(final Player player) {
        final TeamType team = player.getTeam();
        if (team == TeamType.NONE || _aborted) {
            return;
        }

        sendPacket(SystemMsg.THE_OTHER_PARTY_IS_FROZEN, team.revert());

        player.stopAttackStanceTask();
        player.startFrozen();
        player.setTeam(TeamType.NONE);

        for (final Player $player : World.getAroundPlayers(player)) {
            $player.getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, player);
            if (player.getServitor() != null) {
                $player.getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, player.getServitor());
            }
        }
        player.sendChanges();

        playerLost(player);
    }

    @Override
    public int getDuelType() {
        return 1;
    }

    @Override
    protected Instant startTime() {
        return Instant.now().plusSeconds(30);
    }
}
