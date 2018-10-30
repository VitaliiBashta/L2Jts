package org.mmocore.gameserver.skills.effects;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.instances.SummonInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.PositionUtils;

import java.util.concurrent.ScheduledFuture;

public final class EffectFear extends Effect {
    private static final double FEAR_RANGE = 9999;
    private int targetX, targetY;
    private ScheduledFuture<?> taskCheckMove;
    public EffectFear(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public boolean checkCondition() {
        if (_effected.isFearImmune()) {
            getEffector().sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return false;
        }

        // Fear нельзя наложить на осадных саммонов
        final Player player = _effected.getPlayer();
        if (player != null) {
            final SiegeEvent<?, ?> siegeEvent = player.getEvent(SiegeEvent.class);
            if (_effected.isSummon() && siegeEvent != null && siegeEvent.containsSiegeSummon((SummonInstance) _effected)) {
                getEffector().sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
                return false;
            }
        }

        if (_effected.isInZonePeace()) {
            getEffector().sendPacket(SystemMsg.YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE);
            return false;
        }

        return super.checkCondition();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!_effected.startFear()) {
            _effected.abortAttack(true, true);
            _effected.abortCast(true, true);
            _effected.stopMove();
        }

        final double angle = Math.toRadians(PositionUtils.calculateAngleFrom(_effector, _effected));
        targetX = _effected.getX() + (int) (FEAR_RANGE * Math.cos(angle));
        targetY = _effected.getY() + (int) (FEAR_RANGE * Math.sin(angle));

        if (taskCheckMove != null)
            taskCheckMove.cancel(true);

        taskCheckMove = ThreadPoolManager.getInstance().scheduleAtFixedRate(new checkMove(), 0, 2000);
    }

    @Override
    public void onExit() {
        super.onExit();
        _effected.stopFear();
        if (!_effected.isDead() && !_effected.isSummon()) {
            _effected.stopMove(true, true);
            _effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }

        if (taskCheckMove != null)
            taskCheckMove.cancel(true);
    }

    @Override
    public boolean onActionTime() {
        _effected.moveToLocation(GeoEngine.moveCheck(_effected.getX(), _effected.getY(), _effected.getZ(), targetX, targetY, _effected.getGeoIndex()), 0, false);
        return getTimeLeft() > 0;
    }

    private class checkMove extends RunnableImpl {
        @Override
        public void runImpl() {
            onActionTime();
        }
    }
}