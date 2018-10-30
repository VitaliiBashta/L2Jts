package org.mmocore.gameserver.model.entity.events.objects;

import org.mmocore.gameserver.object.Player;

import java.io.Serializable;

/**
 * @author VISTALL
 * @date 4:02/26.08.2011
 */
public class UCMemberObject implements Serializable {
    private final Player _player;
    private final String _name;
    private int _kills;
    private int _deaths;

    public UCMemberObject(Player player) {
        _player = player;
        _name = player.getName();
    }

    public Player getPlayer() {
        return _player;
    }

    public int getKills() {
        return _kills;
    }

    public void incKills() {
        _kills++;
    }

    public int getDeaths() {
        return _deaths;
    }

    public void incDeaths() {
        _deaths++;
    }

    public String getName() {
        return _name;
    }
}