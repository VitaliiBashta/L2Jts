package org.mmocore.gameserver.model.entity.events.objects;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.utils.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author VISTALL
 * @date 2:17/26.06.2011
 */
public class DuelSnapshotObject implements Serializable {
    private final TeamType _team;
    private Player _player;
    //
    private List<Effect> _effects = Collections.emptyList();
    private Location _returnLoc;
    private double _currentHp;
    private double _currentMp;
    private double _currentCp;

    private int currentReflection;

    private boolean _isDead;


    public DuelSnapshotObject(final Player player, final TeamType team, final boolean store) {
        _player = player;
        _team = team;
        if (store) {
            store();
        }
    }

    public void store() {
        _returnLoc = _player._stablePoint == null ? _player.getReflection().getReturnLoc() == null ? _player.getLoc() : _player.getReflection().getReturnLoc() : _player._stablePoint;
        _currentCp = _player.getCurrentCp();
        _currentHp = _player.getCurrentHp();
        _currentMp = _player.getCurrentMp();

        currentReflection = _player.getReflectionId();

        final List<Effect> effectList = _player.getEffectList().getAllEffects();
        _effects = new ArrayList<>(effectList.size());
        for (final Effect e : effectList) {
            final Effect effect = e.getTemplate().getEffect(e.getEffector(), e.getEffected(), e.getSkill());
            effect.setCount(e.getCount());
            effect.setPeriod(e.getCount() == 1 ? e.getPeriod() - e.getTime() : e.getPeriod());

            _effects.add(effect);
        }
    }

    public void restore() {
        if (_player == null) {
            return;
        }

        if (currentReflection != _player.getReflectionId())
            return;

        _player.getEffectList().stopAllEffects();
        for (final Effect e : _effects) {
            _player.getEffectList().addEffect(e);
        }

        _player.setCurrentCp(_currentCp);
        _player.setCurrentHpMp(_currentHp, _currentMp);
    }

    public void teleportBack() {
        if (_player == null) {
            return;
        }

        _player._stablePoint = null;

        ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
            @Override
            public void runImpl() {
                _player.stopFrozen();
                _player.teleToLocation(_returnLoc, ReflectionManager.DEFAULT);
            }
        }, 5000L);
    }

    public void blockUnblock() {
        if (_player == null) {
            return;
        }

        _player.block();
        final Servitor summon = _player.getServitor();
        if (summon != null) {
            summon.block();
        }

        ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
            @Override
            public void runImpl() {
                _player.unblock();
                if (summon != null) {
                    summon.unblock();
                }
            }
        }, 3000L);
    }

    public Player getPlayer() {
        return _player;
    }

    public boolean isDead() {
        return _isDead;
    }

    public void setDead() {
        _isDead = true;
    }

    public Location getLoc() {
        return _returnLoc;
    }

    public TeamType getTeam() {
        return _team;
    }

    public Location getReturnLoc() {
        return _returnLoc;
    }

    public void clear() {
        _player = null;
    }
}
