package org.mmocore.gameserver.model.entity.events.objects;

import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mangol
 * @since 05.10.2016
 */
public class AbstractSnapshotObject {
    private final Player player;
    private final List<Effect> effects;
    private final Location returnLoc;
    private final double currentHp;
    private final double currentMp;
    private final double currentCp;
    private boolean isDead;

    public AbstractSnapshotObject(final Player player) {
        this.player = player;
        returnLoc = player.getReflection().getReturnLoc() == null ? player.getLoc() : player.getReflection().getReturnLoc();
        currentCp = player.getCurrentCp();
        currentHp = player.getCurrentHp();
        currentMp = player.getCurrentMp();
        final List<Effect> effectList = player.getEffectList().getAllEffects();
        effects = new ArrayList<>(effectList.size());
        for (final Effect effect : effectList) {
            final Effect e = effect.getTemplate().getEffect(effect.getEffector(), effect.getEffected(), effect.getSkill());
            if (e != null) {
                e.setCount(effect.getCount());
                e.setPeriod(effect.getCount() == 1 ? effect.getPeriod() - effect.getTime() : effect.getPeriod());
                effects.add(e);
            }
        }
    }

    public Player getPlayer() {
        return player;
    }

    public List<Effect> getEffects() {
        return effects;
    }

    public Location getReturnLoc() {
        return returnLoc;
    }

    public double getCurrentHp() {
        return currentHp;
    }

    public double getCurrentMp() {
        return currentMp;
    }

    public double getCurrentCp() {
        return currentCp;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead() {
        isDead = true;
    }

    public void restore(boolean abnormal) {
        if (!abnormal) {
            getPlayer().getEffectList().stopAllEffects();
            for (final Effect e : getEffects()) {
                getPlayer().getEffectList().addEffect(e);
            }
            getPlayer().setCurrentCp(getCurrentCp());
            getPlayer().setCurrentHpMp(getCurrentHp(), getCurrentCp());
        }
    }

    public void teleportBack() {
        getPlayer()._stablePoint = null;
        if (getPlayer().isFrozen()) {
            getPlayer().stopFrozen();
        }
        ThreadPoolManager.getInstance().schedule(() -> getPlayer().teleToLocation(getReturnLoc(), ReflectionManager.DEFAULT), 5000L);
    }
}
