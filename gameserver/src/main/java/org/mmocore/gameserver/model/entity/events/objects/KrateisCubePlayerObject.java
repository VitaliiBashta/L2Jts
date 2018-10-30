package org.mmocore.gameserver.model.entity.events.objects;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.model.entity.events.impl.KrateisCubeEvent;
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
public class KrateisCubePlayerObject implements Serializable, Comparable<KrateisCubePlayerObject> {
    private final Player _player;
    private final long _registrationTime;
    private boolean _showRank;
    private int _points;
    private Future<?> _ressurectTask;

    public KrateisCubePlayerObject(final Player player) {
        _player = player;
        _registrationTime = System.currentTimeMillis();
    }

    public String getName() {
        return _player.getName();
    }

    public boolean isShowRank() {
        return _showRank;
    }

    public void setShowRank(final boolean showRank) {
        _showRank = showRank;
    }

    public int getPoints() {
        return _points;
    }

    public void setPoints(final int points) {
        _points = points;
    }

    public long getRegistrationTime() {
        return _registrationTime;
    }

    public int getObjectId() {
        return _player.getObjectId();
    }

    public Player getPlayer() {
        return _player;
    }

    public void startRessurectTask() {
        if (_ressurectTask != null) {
            return;
        }

        _ressurectTask = ThreadPoolManager.getInstance().schedule(new RessurectTask(), 1000L);
    }

    public void stopRessurectTask() {
        if (_ressurectTask != null) {
            _ressurectTask.cancel(false);
            _ressurectTask = null;
        }
    }

    @Override
    public int compareTo(final KrateisCubePlayerObject o) {
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

        final KrateisCubePlayerObject that = (KrateisCubePlayerObject) o;

        if (_points != that._points) {
            return false;
        }
        if (_registrationTime != that._registrationTime) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (_registrationTime ^ (_registrationTime >>> 32));
        result = 31 * result + _points;
        return result;
    }

    private class RessurectTask extends RunnableImpl {
        private int _seconds = 10;

        public RessurectTask() {
            //
        }

        @Override
        public void runImpl() throws Exception {
            _seconds -= 1;
            if (_seconds == 0) {
                final KrateisCubeEvent cubeEvent = _player.getEvent(KrateisCubeEvent.class);
                final List<Location> waitLocs = cubeEvent.getObjects(KrateisCubeEvent.WAIT_LOCS);

                _ressurectTask = null;

                _player.teleToLocation(Rnd.get(waitLocs));
                _player.doRevive();
            } else {
                _player.sendPacket(new SystemMessage(SystemMsg.RESURRECTION_WILL_TAKE_PLACE_IN_THE_WAITING_ROOM_AFTER_S1_SECONDS).addNumber(_seconds));
                _ressurectTask = ThreadPoolManager.getInstance().schedule(this, 1000L);
            }
        }
    }
}
