package org.mmocore.gameserver.model.entity.events.impl;

import com.google.common.collect.Iterators;
import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.commons.threading.ThreadPoolManager;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.listener.actor.OnDeathListener;
import org.mmocore.gameserver.listener.actor.player.OnPlayerExitListener;
import org.mmocore.gameserver.listener.actor.player.OnTeleportListener;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.objects.AerialCleftPlayerObject;
import org.mmocore.gameserver.model.entity.olympiad.Olympiad;
import org.mmocore.gameserver.network.lineage.serverpackets.ExCleftList;
import org.mmocore.gameserver.network.lineage.serverpackets.ExCleftList.CleftType;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.List;

/**
 * @author KilRoy
 */
public class AerialCleftEvent extends Event implements Iterable<AerialCleftPlayerObject> {
    private static final String FINALS = "finals";
    private static final String RED_START_POINT = "red_start_point";
    private static final String BLUE_START_POINT = "blue_start_point";
    private static final String RED_BANISH_POINT = "red_banish_point";
    private static final String BLUE_BANISH_POINT = "blue_banish_point";
    private static int minLevel;
    private static int rewardItem;
    private static int rewardItemCountWin;
    private static int rewardItemCountLose;
    private final Listeners listeners = new Listeners();
    private ZonedDateTime startTime = ZonedDateTime.now().plusYears(1);
    private boolean isInProgress = false;
    private boolean registrationOver = false;
    private TeamType winnerTeam = TeamType.NONE;
    public AerialCleftEvent(final MultiValueSet<String> set) {
        super(set);
        minLevel = set.getInteger("minLevel");
        rewardItem = set.getInteger("reward_items");
        rewardItemCountWin = set.getInteger("reward_counts_win");
        rewardItemCountLose = set.getInteger("reward_counts_lose");
    }

    @Override
    public void initEvent() {
        super.initEvent();
    }

    @Override
    public void startEvent() {
        if (!isInProgress) {
            startTime = ZonedDateTime.now();
            isInProgress = true;
            winnerTeam = TeamType.NONE;
            registrationOver = true;
            for (final AerialCleftPlayerObject object : this) {
                final Player player = object.getPlayer();
                if (!checkPlayer(player)) {
                    removeObject(object.getTeam(), object);
                    player.removeEvent(this);
                    if (player.isTeleporting()) {
                        continue;
                    }
                }
            }
            if (getObjects(TeamType.RED).isEmpty() || getObjects(TeamType.BLUE).isEmpty() || (getObjects(TeamType.RED).size() < 9 || getObjects(TeamType.BLUE).size() < 9)) {
                ThreadPoolManager.getInstance().schedule(() -> {
                    isInProgress = false;
                    registrationOver = false;
                }, 3600000L);
                reCalcNextTime(false);
                return;
            }
            final List<Location> startRed = getObjects(RED_START_POINT);
            final List<Location> startBlue = getObjects(BLUE_START_POINT);
            for (final AerialCleftPlayerObject object : this) {
                final Player player = object.getPlayer();
                if (player != null) {
                    player.setTeam(object.getTeam());
                    player.setCurrentMp(player.getMaxMp());
                    player.setCurrentCp(player.getMaxCp());
                    player.setCurrentHp(player.getMaxHp(), true);
                    player.sendPacket(new ExCleftList(CleftType.TOTAL, this, object.getTeam()));
                    //player.sendPacket(new ExCleftState(CleftState.TOTAL)); TODO
                    switch (object.getTeam()) {
                        case NONE:
                            break;
                        case RED:
                            player.teleToLocation(startRed.get(Rnd.get(3)));
                            break;
                        case BLUE:
                            player.teleToLocation(startBlue.get(Rnd.get(3)));
                            break;
                    }
                }
            }
        }
    }

    @Override
    public void stopEvent() {
        startTime = ZonedDateTime.now().plusYears(1);
        for (final AerialCleftPlayerObject object : this) {
            final Player player = object.getPlayer();
            if (player != null) {
                player.sendPacket(ExCleftList.STATIC_CLOSE);
                player.setTeam(TeamType.NONE);
                player.teleToClosestTown();
                player.removeEvent(this);
                removeObject(object.getTeam(), object);
            }
        }
        ThreadPoolManager.getInstance().schedule(() -> {
            isInProgress = false;
            registrationOver = false;
        }, 3600000L);
        super.stopEvent();
    }

    @Override
    public void reCalcNextTime(boolean onInit) {
        clearActions();
        registerActions();
    }

    public void finals() {
        synchronized (this) {
            int scoreRed = 0;
            int scoreBlue = 0;
            for (final TeamType team : TeamType.VALUES) {
                final List<AerialCleftPlayerObject> objects = getObjects(team);
                for (final AerialCleftPlayerObject obj : objects) {
                    if (obj.getTeam() == TeamType.RED) {
                        scoreRed += obj.getPoints();
                    } else {
                        scoreBlue += obj.getPoints();
                    }
                }
            }
            if (scoreRed > scoreBlue) {
                winnerTeam = TeamType.RED;
            } else {
                winnerTeam = TeamType.BLUE;
            }
        }
        switch (winnerTeam) {
            case NONE:
                break;
            case RED:
            case BLUE:
                final List<AerialCleftPlayerObject> winners = getObjects(winnerTeam);
                final List<AerialCleftPlayerObject> losers = getObjects(winnerTeam);
                final List<Location> banishRed = getObjects(RED_BANISH_POINT);
                final List<Location> banishBlue = getObjects(BLUE_BANISH_POINT);
                winners.stream().filter(winer -> winer.getPlayer() != null).forEach(winer -> {
                    winer.getPlayer().setTeam(TeamType.NONE);
                    ItemFunctions.addItem(winer.getPlayer(), rewardItem, rewardItemCountWin, true);
                });
                losers.stream().filter(loser -> loser.getPlayer() != null).forEach(loser -> {
                    ItemFunctions.addItem(loser.getPlayer(), rewardItem, rewardItemCountLose, true);
                    loser.getPlayer().sendPacket(ExCleftList.STATIC_CLOSE);
                    loser.getPlayer().setTeam(TeamType.NONE);
                    if (loser.getTeam() == TeamType.RED) {
                        loser.getPlayer().teleToLocation(banishRed.get(Rnd.get(3)));
                    } else {
                        loser.getPlayer().teleToLocation(banishBlue.get(Rnd.get(3)));
                    }
                    removeObject(loser.getTeam(), loser);
                    loser.getPlayer().removeEvent(this);
                });
                break;
        }
    }

    public int registerPlayer(final Player player) {
        for (final AerialCleftPlayerObject d : this) {
            if (d.getPlayer() == player) {
                return 0;
            }
        }
        final List<AerialCleftPlayerObject> blue = getObjects(TeamType.BLUE);
        final List<AerialCleftPlayerObject> red = getObjects(TeamType.RED);
        final TeamType team;
        if (blue.size() == red.size()) {
            team = Rnd.get(TeamType.VALUES);
        } else if (blue.size() > red.size()) {
            team = TeamType.RED;
        } else {
            team = TeamType.BLUE;
        }
        addObject(team, new AerialCleftPlayerObject(player, team));
        return getObjects(TeamType.BLUE).size() + getObjects(TeamType.RED).size();
    }

    public int getTotalMemberCount(final TeamType teamType) {
        return getObjects(teamType).size();
    }

    public int getTotalMatchScore(final TeamType teamType) {
        int score = 0;
        for (final AerialCleftPlayerObject d : this) {
            if (d.getPlayer() != null && d.getTeam() == teamType) {
                score += d.getPoints();
            }
        }
        return score;
    }

    public AerialCleftPlayerObject getParticlePlayer(final Player player) {
        for (final AerialCleftPlayerObject object : this) {
            if (object.getPlayer() == player) {
                return object;
            }
        }
        return null;
    }

    public void sendChange(final AerialCleftPlayerObject obj, final CleftType cleftType) {
        for (final AerialCleftPlayerObject object : this) {
            if (object.getPlayer() != null) {
                object.getPlayer().sendPacket(new ExCleftList(cleftType, object.getPlayer(), object.getTeam()));
            }
        }

    }

    public void updatePoints(final AerialCleftPlayerObject obj, final DestroyType towerType, final int zoneType, final int destroyPoint) {
        for (final AerialCleftPlayerObject object : this) {
            if (object.getPlayer() != null) {
                if (object == obj) {

                } else {

                }
            }
        }
    }

    @Override
    public boolean isInProgress() {
        return isInProgress;
    }

    public boolean isRegistrationOver() {
        return registrationOver;
    }

    private boolean checkPlayer(final Player player) {
        if (player.isInOfflineMode()) {
            return false;
        }

        if (player.getLevel() < minLevel) {
            return false;
        }

        if (player.isMounted() || player.isDead() || player.isInObserverMode()) {
            return false;
        }

        if (player.isInDuel()) {
            return false;
        }

        if (player.getTeam() != TeamType.NONE) {
            return false;
        }

        if (player.getOlympiadGame() != null || Olympiad.isRegistered(player)) {
            return false;
        }

        if (player.isInParty() && player.getParty().isInDimensionalRift()) {
            return false;
        }

        if (player.isTeleporting()) {
            return false;
        }

        if (player.getEvent(SiegeEvent.class) != null) {
            return false;
        }

        if (player.isTerritoryFlagEquipped()) {
            return false;
        }

        if (!false && !player.getReflection().isDefault()) {
            return false;
        }

        return true;
    }

    @Override
    public void onAddEvent(final GameObject o) {
        if (o.isPlayer()) {
            o.getPlayer().addListener(listeners);
        }
    }

    @Override
    public void onRemoveEvent(final GameObject o) {
        if (o.isPlayer()) {
            o.getPlayer().removeListener(listeners);
        }
    }

    @Override
    public void action(final String name, final boolean start) {
        if (name.equalsIgnoreCase(FINALS)) {
            finals();
        } else {
            super.action(name, start);
        }
    }

    public int getMinLevel() {
        return minLevel;
    }

    @Override
    public EventType getType() {
        return EventType.PVP_EVENT;
    }

    @Override
    protected Instant startTime() {
        return startTime.toInstant();
    }

    @Override
    public Iterator<AerialCleftPlayerObject> iterator() {
        final List<AerialCleftPlayerObject> blue = getObjects(TeamType.BLUE);
        final List<AerialCleftPlayerObject> red = getObjects(TeamType.RED);
        return Iterators.concat(blue.iterator(), red.iterator());
    }

    public enum DestroyType {
        CHAR,
        CENTRAL_COMPRESSOR,
        I_COMPRESSOR,
        II_COMPRESSOR,
        III_COMPRESSOR
    }

    private class Listeners implements OnDeathListener, OnPlayerExitListener, OnTeleportListener {
        @Override
        public void onDeath(final Creature actor, final Creature killer) {
            if (!actor.isPlayer()) {
                return;
            }
            final AerialCleftEvent aerialCleftEvent = actor.getEvent(AerialCleftEvent.class);
            if (aerialCleftEvent != AerialCleftEvent.this) {
                return;
            }

            if (killer.isPlayer()) {
                final AerialCleftPlayerObject winnerPlayer = getParticlePlayer((Player) killer);

                winnerPlayer.setPoints(winnerPlayer.getPoints() + 1);
                updatePoints(winnerPlayer, DestroyType.CHAR, 0, 1);
            }

            final AerialCleftPlayerObject looserPlayer = getParticlePlayer((Player) actor);

            looserPlayer.startRessurectTask();
        }

        @Override
        public void onPlayerExit(final Player player) {
            final AerialCleftEvent aerialCleftEvent = player.getEvent(AerialCleftEvent.class);
            player.removeEvent(aerialCleftEvent);
            sendChange(aerialCleftEvent.getParticlePlayer(player), CleftType.REMOVE);
            removeObject(aerialCleftEvent.getParticlePlayer(player).getTeam(), aerialCleftEvent.getParticlePlayer(player));
        }

        @Override
        public void onTeleport(final Player player, final int x, final int y, final int z, final Reflection reflection) {
            final AerialCleftEvent aerialCleftEvent = player.getEvent(AerialCleftEvent.class);
            player.removeEvent(aerialCleftEvent);
            sendChange(aerialCleftEvent.getParticlePlayer(player), CleftType.REMOVE);
            removeObject(aerialCleftEvent.getParticlePlayer(player).getTeam(), aerialCleftEvent.getParticlePlayer(player));
        }
    }
}