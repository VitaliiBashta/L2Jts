package org.mmocore.gameserver.model.entity.events.impl;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.gameserver.database.dao.impl.SiegeClanDAO;
import org.mmocore.gameserver.model.entity.events.objects.SiegeClanObject;
import org.mmocore.gameserver.model.entity.residence.ClanHall;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.PlaySound;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 * @date 15:23/14.02.2011
 */
public class ClanHallSiegeEvent extends SiegeEvent<ClanHall, SiegeClanObject> {
    public static final String BOSS = "boss";

    public ClanHallSiegeEvent(final MultiValueSet<String> set) {
        super(set);
    }

    @Override
    public void startEvent() {
        if (getObjects(ATTACKERS).isEmpty()) {
            broadcastInZone2(new SystemMessage(SystemMsg.THE_SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST).addResidenceName(getResidence()));
            reCalcNextTime(false);
            return;
        }

        _oldOwner = getResidence().getOwner();
        if (_oldOwner != null) {
            getResidence().changeOwner(null);

            addObject(ATTACKERS, new SiegeClanObject(ATTACKERS, _oldOwner, 0));
        }

        SiegeClanDAO.getInstance().delete(getResidence());

        updateParticles(true, ATTACKERS);

        broadcastTo(new SystemMessage(SystemMsg.THE_SIEGE_TO_CONQUER_S1_HAS_BEGUN).addResidenceName(getResidence()), ATTACKERS);

        super.startEvent();
    }

    @Override
    public void stopEvent(final boolean step) {
        final Clan newOwner = getResidence().getOwner();
        if (newOwner != null) {
            newOwner.broadcastToOnlineMembers(PlaySound.SIEGE_VICTORY);

            newOwner.incReputation(1700, false, toString());

            broadcastTo(new SystemMessage(SystemMsg.S1_CLAN_HAS_DEFEATED_S2).addString(newOwner.getName()).addResidenceName(getResidence()), ATTACKERS);
            broadcastTo(new SystemMessage(SystemMsg.THE_SIEGE_OF_S1_IS_FINISHED).addResidenceName(getResidence()), ATTACKERS);
        } else {
            broadcastTo(new SystemMessage(SystemMsg.THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW).addResidenceName(getResidence()), ATTACKERS);
        }

        updateParticles(false, ATTACKERS);

        removeObjects(ATTACKERS);

        super.stopEvent(step);

        _oldOwner = null;
    }

    @Override
    public void removeState(final int val) {
        super.removeState(val);

        if (val == REGISTRATION_STATE) {
            broadcastTo(new SystemMessage(SystemMsg.THE_DEADLINE_TO_REGISTER_FOR_THE_SIEGE_OF_S1_HAS_PASSED).addResidenceName(getResidence()), ATTACKERS);
        }
    }

    @Override
    public void processStep(final Clan clan) {
        if (clan != null) {
            getResidence().changeOwner(clan);
        }

        stopEvent(true);
    }

    @Override
    public void loadSiegeClans() {
        addObjects(ATTACKERS, SiegeClanDAO.getInstance().load(getResidence(), ATTACKERS));
    }

    @Override
    public int getUserRelation(final Player thisPlayer, final int result) {
        return result;
    }

    @Override
    public int getRelation(final Player thisPlayer, final Player targetPlayer, final int result) {
        return result;
    }

    @Override
    public boolean canResurrect(final Creature active, final Creature target, final boolean force, final boolean quiet) {
        final boolean playerInZone = checkIfInZone(active);
        final boolean targetInZone = checkIfInZone(target);
        // если оба вне зоны - рес разрешен
        // если таргет вне осадный зоны - рес разрешен
        if (!playerInZone && !targetInZone || !targetInZone) {
            return true;
        }

        final Player resurectPlayer = active.getPlayer();
        final Player targetPlayer = target.getPlayer();

        // если оба незареганы - невозможно ресать
        // если таргет незареган - невозможно ресать
        final ClanHallSiegeEvent siegeEvent1 = resurectPlayer.getEvent(ClanHallSiegeEvent.class);
        final ClanHallSiegeEvent siegeEvent2 = targetPlayer.getEvent(ClanHallSiegeEvent.class);
        if (siegeEvent1 == null && siegeEvent2 == null || !siegeEvent2.equals(this)) {
            if (!quiet) {
                if (force) {
                    targetPlayer.sendPacket(SystemMsg.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEFIELDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE);
                }
                active.sendPacket(force ? SystemMsg.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEFIELDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE : SystemMsg.INVALID_TARGET);
            }
            return false;
        }

        final SiegeClanObject targetSiegeClan = siegeEvent2.getSiegeClan(ATTACKERS, targetPlayer.getClan());

        // если нету флага - рес запрещен
        if (targetSiegeClan == null || targetSiegeClan.getFlag() == null) {
            if (!quiet) {
                if (force) {
                    targetPlayer.sendPacket(SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE);
                }
                active.sendPacket(force ? SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE : SystemMsg.INVALID_TARGET);
            }
            return false;
        }

        if (force) {
            return true;
        } else {
            if (!quiet) {
                active.sendPacket(SystemMsg.INVALID_TARGET);
            }
            return false;
        }
    }
}
