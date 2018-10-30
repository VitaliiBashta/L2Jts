package org.mmocore.gameserver.model.entity.events.impl;

import org.mmocore.commons.collections.CollectionUtils;
import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.gameserver.database.dao.impl.SiegeClanDAO;
import org.mmocore.gameserver.model.entity.events.objects.CMGSiegeClanObject;
import org.mmocore.gameserver.model.entity.events.objects.SiegeClanObject;
import org.mmocore.gameserver.model.entity.residence.ClanHall;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.PlaySound;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.tables.ClanTable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author VISTALL
 * @date 15:14/14.02.2011
 */
public class ClanHallMiniGameEvent extends SiegeEvent<ClanHall, CMGSiegeClanObject> {
    public static final String NEXT_STEP = "next_step";
    public static final String REFUND = "refund";

    private boolean _arenaClosed = true;

    public ClanHallMiniGameEvent(final MultiValueSet<String> set) {
        super(set);
    }

    @Override
    public void startEvent() {
        _oldOwner = getResidence().getOwner();

        final List<CMGSiegeClanObject> siegeClans = getObjects(ATTACKERS);
        if (siegeClans.size() < 2) {
            final CMGSiegeClanObject siegeClan = CollectionUtils.safeGet(siegeClans, 0);
            if (siegeClan != null) {
                final CMGSiegeClanObject oldSiegeClan = getSiegeClan(REFUND, siegeClan.getObjectId());
                if (oldSiegeClan != null) {
                    SiegeClanDAO.getInstance().delete(getResidence(), siegeClan); // удаляем с базы старое

                    oldSiegeClan.setParam(oldSiegeClan.getParam() + siegeClan.getParam());

                    SiegeClanDAO.getInstance().update(getResidence(), oldSiegeClan);
                } else {
                    siegeClan.setType(REFUND);
                    // удаляем с аттакеров
                    siegeClans.remove(siegeClan);
                    // добавляем к рефунд
                    addObject(REFUND, siegeClan);

                    SiegeClanDAO.getInstance().update(getResidence(), siegeClan);
                }
            }
            siegeClans.clear();

            broadcastTo(SystemMsg.THIS_CLAN_HALL_WAR_HAS_BEEN_CANCELLED, ATTACKERS);
            broadcastInZone2(new SystemMessage(SystemMsg.THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW).addResidenceName(getResidence()));
            reCalcNextTime(false);
            return;
        }

        final CMGSiegeClanObject[] clans = siegeClans.toArray(new CMGSiegeClanObject[siegeClans.size()]);
        Arrays.parallelSort(clans, SiegeClanObject.SiegeClanComparator.getInstance());

        final List<CMGSiegeClanObject> temp = new ArrayList<>(4);

        for (final CMGSiegeClanObject siegeClan : clans) {
            SiegeClanDAO.getInstance().delete(getResidence(), siegeClan);

            if (temp.size() == 4) {
                siegeClans.remove(siegeClan);

                siegeClan.broadcast(SystemMsg.YOU_HAVE_FAILED_IN_YOUR_ATTEMPT_TO_REGISTER_FOR_THE_CLAN_HALL_WAR);
            } else {
                temp.add(siegeClan);

                siegeClan.broadcast(SystemMsg.YOU_HAVE_BEEN_REGISTERED_FOR_A_CLAN_HALL_WAR);
            }
        }

        _arenaClosed = false;

        super.startEvent();
    }

    @Override
    public void stopEvent(final boolean step) {
        removeBanishItems();

        final Clan newOwner = getResidence().getOwner();
        if (newOwner != null) {
            if (_oldOwner != newOwner) {
                newOwner.broadcastToOnlineMembers(PlaySound.SIEGE_VICTORY);

                newOwner.incReputation(1700, false, toString());
            }

            broadcastTo(new SystemMessage(SystemMsg.S1_CLAN_HAS_DEFEATED_S2).addString(newOwner.getName()).addResidenceName(getResidence()), ATTACKERS, DEFENDERS);
            broadcastTo(new SystemMessage(SystemMsg.THE_SIEGE_OF_S1_IS_FINISHED).addResidenceName(getResidence()), ATTACKERS, DEFENDERS);
        } else {
            broadcastTo(new SystemMessage(SystemMsg.THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW).addResidenceName(getResidence()), ATTACKERS);
        }

        updateParticles(false, ATTACKERS);

        removeObjects(ATTACKERS);

        super.stopEvent(step);

        _oldOwner = null;
    }

    public void nextStep() {
        final List<CMGSiegeClanObject> siegeClans = getObjects(ATTACKERS);
        for (int i = 0; i < siegeClans.size(); i++) {
            spawnAction("arena_" + i, true);
        }

        _arenaClosed = true;

        updateParticles(true, ATTACKERS);

        broadcastTo(new SystemMessage(SystemMsg.THE_SIEGE_TO_CONQUER_S1_HAS_BEGUN).addResidenceName(getResidence()), ATTACKERS);
    }

    @Override
    public void removeState(final int val) {
        super.removeState(val);

        if (val == REGISTRATION_STATE) {
            broadcastTo(SystemMsg.THE_REGISTRATION_PERIOD_FOR_A_CLAN_HALL_WAR_HAS_ENDED, ATTACKERS);
        }
    }

    @Override
    public CMGSiegeClanObject newSiegeClan(final String type, final int clanId, final long param, final Instant date) {
        final Clan clan = ClanTable.getInstance().getClan(clanId);
        return clan == null ? null : new CMGSiegeClanObject(type, clan, param, date);
    }

    @Override
    public void announce(final int val) {
        final int seconds = val % 60;
        final int min = val / 60;
        if (min > 0) {
            final SystemMsg msg = min > 10 ? SystemMsg.IN_S1_MINUTES_THE_GAME_WILL_BEGIN_ALL_PLAYERS_MUST_HURRY_AND_MOVE_TO_THE_LEFT_SIDE_OF_THE_CLAN_HALLS_ARENA : SystemMsg.IN_S1_MINUTES_THE_GAME_WILL_BEGIN_ALL_PLAYERS_PLEASE_ENTER_THE_ARENA_NOW;

            broadcastTo(new SystemMessage(msg).addNumber(min), ATTACKERS);
        } else {
            broadcastTo(new SystemMessage(SystemMsg.IN_S1_SECONDS_THE_GAME_WILL_BEGIN).addNumber(seconds), ATTACKERS);
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
        addObjects(REFUND, SiegeClanDAO.getInstance().load(getResidence(), REFUND));
    }

    @Override
    public void action(final String name, final boolean start) {
        if (name.equalsIgnoreCase(NEXT_STEP)) {
            nextStep();
        } else {
            super.action(name, start);
        }
    }

    @Override
    public int getUserRelation(final Player thisPlayer, final int result) {
        return result;
    }

    @Override
    public int getRelation(final Player thisPlayer, final Player targetPlayer, final int result) {
        return result;
    }

    public boolean isArenaClosed() {
        return _arenaClosed;
    }

    @Override
    public void onAddEvent(final GameObject object) {
        if (object.isItem()) {
            addBanishItem((ItemInstance) object);
        }
    }
}
