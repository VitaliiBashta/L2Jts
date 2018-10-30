package org.mmocore.gameserver.model.entity.events.impl;

import org.mmocore.commons.collections.CollectionUtils;
import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.gameserver.database.dao.impl.SiegeClanDAO;
import org.mmocore.gameserver.database.dao.impl.SiegePlayerDAO;
import org.mmocore.gameserver.model.base.RestartType;
import org.mmocore.gameserver.model.entity.events.objects.CTBSiegeClanObject;
import org.mmocore.gameserver.model.entity.events.objects.CTBTeamObject;
import org.mmocore.gameserver.model.entity.events.objects.SiegeClanObject;
import org.mmocore.gameserver.model.entity.residence.ClanHall;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.PlaySound;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.tables.ClanTable;
import org.mmocore.gameserver.utils.Location;

import java.time.Instant;
import java.util.List;

/**
 * @author VISTALL
 * @date 15:22/14.02.2011
 */
public class ClanHallTeamBattleEvent extends SiegeEvent<ClanHall, CTBSiegeClanObject> {
    public static final String TRYOUT_PART = "tryout_part";
    public static final String CHALLENGER_RESTART_POINTS = "challenger_restart_points";
    public static final String FIRST_DOORS = "first_doors";
    public static final String SECOND_DOORS = "second_doors";
    public static final String NEXT_STEP = "next_step";

    public ClanHallTeamBattleEvent(final MultiValueSet<String> set) {
        super(set);
    }

    @Override
    public void startEvent() {
        final List<CTBSiegeClanObject> attackers = getObjects(ATTACKERS);
        if (attackers.isEmpty()) {
            if (getResidence().getOwner() == null) {
                broadcastInZone2(new SystemMessage(SystemMsg.THE_SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST).addResidenceName(getResidence()));
            } else {
                broadcastInZone2(new SystemMessage(SystemMsg.S1S_SIEGE_WAS_CANCELED_BECAUSE_THERE_WERE_NO_CLANS_THAT_PARTICIPATED).addResidenceName(getResidence()));
            }

            reCalcNextTime(false);
            return;
        }

        _oldOwner = getResidence().getOwner();
        if (_oldOwner != null) {
            addObject(DEFENDERS, new SiegeClanObject(DEFENDERS, _oldOwner, 0));
        }

        SiegeClanDAO.getInstance().delete(getResidence());
        SiegePlayerDAO.getInstance().delete(getResidence());

        final List<CTBTeamObject> teams = getObjects(TRYOUT_PART);
        for (int i = 0; i < 5; i++) {
            final CTBTeamObject team = teams.get(i);

            team.setSiegeClan(CollectionUtils.safeGet(attackers, i));
        }

        broadcastTo(new SystemMessage(SystemMsg.THE_SIEGE_TO_CONQUER_S1_HAS_BEGUN).addResidenceName(getResidence()), ATTACKERS, DEFENDERS);
        broadcastTo(SystemMsg.THE_TRYOUTS_ARE_ABOUT_TO_BEGIN, ATTACKERS);

        super.startEvent();
    }

    public void nextStep() {
        broadcastTo(SystemMsg.THE_TRYOUTS_HAVE_BEGUN, ATTACKERS, DEFENDERS);

        updateParticles(true, ATTACKERS, DEFENDERS);
    }

    public void processStep(final CTBTeamObject team) {
        if (team.getSiegeClan() != null) {
            final CTBSiegeClanObject object = team.getSiegeClan();

            object.setEvent(false, this);

            teleportPlayers(SPECTATORS, null);
        }

        team.despawnObject(this);

        final List<CTBTeamObject> teams = getObjects(TRYOUT_PART);

        boolean hasWinner = false;
        CTBTeamObject winnerTeam = null;

        for (final CTBTeamObject t : teams) {
            if (t.isParticle()) {
                hasWinner = winnerTeam == null;  // если зайдет второй раз то скажет что нету виннера

                winnerTeam = t;
            }
        }

        if (!hasWinner) {
            return;
        }

        final SiegeClanObject clan = winnerTeam.getSiegeClan();
        if (clan != null) {
            getResidence().changeOwner(clan.getClan());
        }

        stopEvent(true);
    }

    @Override
    public void announce(final int val) {
        final int minute = val / 60;
        if (minute > 0) {
            broadcastTo(new SystemMessage(SystemMsg.THE_CONTEST_WILL_BEGIN_IN_S1_MINUTES).addNumber(minute), ATTACKERS, DEFENDERS);
        } else {
            broadcastTo(new SystemMessage(SystemMsg.THE_PRELIMINARY_MATCH_WILL_BEGIN_IN_S1_SECONDS).addNumber(val), ATTACKERS, DEFENDERS);
        }
    }

    @Override
    public void stopEvent(final boolean step) {
        final Clan newOwner = getResidence().getOwner();
        if (newOwner != null) {
            if (_oldOwner != newOwner) {
                newOwner.broadcastToOnlineMembers(PlaySound.SIEGE_VICTORY);

                newOwner.incReputation(1700, false, toString());
            }

            broadcastTo(new SystemMessage(SystemMsg.S1_CLAN_HAS_DEFEATED_S2).addString(newOwner.getName()).addResidenceName(getResidence()), ATTACKERS, DEFENDERS);
            broadcastTo(new SystemMessage(SystemMsg.THE_SIEGE_OF_S1_IS_FINISHED).addResidenceName(getResidence()), ATTACKERS, DEFENDERS);
        } else {
            broadcastTo(new SystemMessage(SystemMsg.THE_PRELIMINARY_MATCH_OF_S1_HAS_ENDED_IN_A_DRAW).addResidenceName(getResidence()), ATTACKERS);
        }

        updateParticles(false, ATTACKERS, DEFENDERS);

        removeObjects(DEFENDERS);
        removeObjects(ATTACKERS);

        super.stopEvent(step);

        _oldOwner = null;
    }

    @Override
    public void loadSiegeClans() {
        final List<SiegeClanObject> siegeClanObjectList = SiegeClanDAO.getInstance().load(getResidence(), ATTACKERS);
        addObjects(ATTACKERS, siegeClanObjectList);

        final List<CTBSiegeClanObject> objects = getObjects(ATTACKERS);
        for (final CTBSiegeClanObject clan : objects) {
            clan.select(getResidence());
        }
    }

    @Override
    public CTBSiegeClanObject newSiegeClan(final String type, final int clanId, final long i, final Instant date) {
        final Clan clan = ClanTable.getInstance().getClan(clanId);
        return clan == null ? null : new CTBSiegeClanObject(type, clan, i, date);
    }

    @Override
    public void findEvent(final Player player) {
        if (!isInProgress() || player.getClan() == null) {
            return;
        }
        final CTBSiegeClanObject object = getSiegeClan(ATTACKERS, player.getClan());
        if (object != null && object.getPlayers().contains(player.getObjectId())) {
            player.addEvent(this);
        }
    }

    @Override
    public Location getRestartLoc(final Player player, final RestartType type) {
        if (!checkIfInZone(player)) {
            return null;
        }

        final SiegeClanObject attackerClan = getSiegeClan(ATTACKERS, player.getClan());

        Location loc = null;
        switch (type) {
            case TO_VILLAGE:
                if (attackerClan != null && checkIfInZone(player)) {
                    final List<SiegeClanObject> objectList = getObjects(ATTACKERS);
                    final List<Location> teleportList = getObjects(CHALLENGER_RESTART_POINTS);

                    final int index = objectList.indexOf(attackerClan);

                    loc = teleportList.get(index);
                }
                break;
        }
        return loc;
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
}
