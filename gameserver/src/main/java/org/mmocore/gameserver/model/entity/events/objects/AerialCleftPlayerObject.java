package org.mmocore.gameserver.model.entity.events.objects;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.entity.events.impl.AerialCleftEvent;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author VISTALL
 * @date 13:09/04.07.2011
 */
public class AerialCleftPlayerObject implements Serializable, Comparable<AerialCleftPlayerObject> {
    private final TeamType team;
    private final Player player;
    private final long registrationTime;
    private boolean showRank;
    private int points;
    private Future<?> ressurectTask;

    public AerialCleftPlayerObject(final Player player, final TeamType team) {
        this.player = player;
        this.team = team;
        registrationTime = System.currentTimeMillis();
    }

    public String getName() {
        return player.getName();
    }

    public boolean isShowRank() {
        return showRank;
    }

    public void setShowRank(final boolean showRank) {
        this.showRank = showRank;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(final int points) {
        this.points = points;
    }

    public long getRegistrationTime() {
        return registrationTime;
    }

    public int getObjectId() {
        return player.getObjectId();
    }

    public Player getPlayer() {
        return player;
    }

    public TeamType getTeam() {
        return team;
    }

    public void startRessurectTask() {
        if (ressurectTask != null) {
            return;
        }

        ressurectTask = ThreadPoolManager.getInstance().schedule(new RessurectTask(), 1000L);
    }

    public void stopRessurectTask() {
        if (ressurectTask != null) {
            ressurectTask.cancel(false);
            ressurectTask = null;
        }
    }

    @Override
    public int compareTo(final AerialCleftPlayerObject o) {
        if (getPoints() == o.getPoints()) {
            return (int) ((getRegistrationTime() - o.getRegistrationTime()) / 1000L);
        }
        return getPoints() - o.getPoints();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final AerialCleftPlayerObject that = (AerialCleftPlayerObject) o;

        if (points != that.points) {
            return false;
        }
        if (registrationTime != that.registrationTime) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (registrationTime ^ (registrationTime >>> 32));
        result = 31 * result + points;
        return result;
    }

    private class RessurectTask extends RunnableImpl {
        private int _seconds = 10;

        public RessurectTask() {
            //
        }

        @Override
        public void runImpl() {
            _seconds -= 1;
            if (_seconds == 0) {
                final AerialCleftEvent cubeEvent = player.getEvent(AerialCleftEvent.class);
                final List<Location> startLocs = cubeEvent.getObjects(cubeEvent.getParticlePlayer(player).getTeam());

                ressurectTask = null;

                player.teleToLocation(Rnd.get(startLocs));
                player.doRevive();
            } else {
                player.sendPacket(new SystemMessage(SystemMsg.RESURRECTION_WILL_TAKE_PLACE_IN_THE_WAITING_ROOM_AFTER_S1_SECONDS).addNumber(_seconds)); //FIXME[K] - взял с кратея, как на офе хз
                ressurectTask = ThreadPoolManager.getInstance().schedule(this, 1000L);
            }
        }
    }
}