package org.mmocore.gameserver.scripts.ai.residences.clanhall;

import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.configuration.config.AiConfig;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.instances.residences.clanhall.CTBBossInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author VISTALL
 * @date 7:15/24.04.2011
 */
public abstract class MatchFighter extends Fighter {
    public MatchFighter(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.isActionsDisabled()) {
            return true;
        }
        if (_def_think) {
            if (doTask()) {
                clearTasks();
            }
            return true;
        }

        long now = System.currentTimeMillis();
        if (now - _checkAggroTimestamp > AiConfig.AGGRO_CHECK_INTERVAL) {
            _checkAggroTimestamp = now;
            List<TargetContains> targets = new ArrayList<>();
            List<Creature> chars = World.getAroundCharacters(actor, actor.getAggroRange(), actor.getAggroRange());
            for (Creature creature : chars) {
                if (creature == null) {
                    continue;
                }
                double distance = actor.getDistance3D(creature);
                TargetContains target = new TargetContains(distance, creature);
                targets.add(target);
            }
            Collections.sort(targets, _nearestTargetComparator);
            for (TargetContains cha : targets) {
                if (cha.getCreature() != null && checkAggression(cha.getCreature())) {
                    return true;
                }
            }
        }

        if (randomWalk()) {
            return true;
        }

        return false;
    }

    @Override
    protected boolean checkAggression(Creature target) {
        CTBBossInstance actor = getActor();

        if (getIntention() != CtrlIntention.AI_INTENTION_ACTIVE) {
            return false;
        }

        if (target.isAlikeDead() || target.isInvul()) {
            return false;
        }

        if (!actor.isAttackable(target)) {
            return false;
        }
        if (!GeoEngine.canSeeTarget(actor, target, false)) {
            return false;
        }

        actor.getAggroList().addDamageHate(target, 0, 2);

        if ((target.isSummon() || target.isPet())) {
            actor.getAggroList().addDamageHate(target.getPlayer(), 0, 1);
        }

        startRunningTask(AI_TASK_ATTACK_DELAY);
        setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);

        return true;
    }

    @Override
    protected boolean canAttackCharacter(Creature target) {
        NpcInstance actor = getActor();
        return actor.isAttackable(target);
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        CTBBossInstance actor = getActor();

        int x = (int) (actor.getX() + 800 * Math.cos(actor.headingToRadians(actor.getHeading() - 32768)));
        int y = (int) (actor.getY() + 800 * Math.sin(actor.headingToRadians(actor.getHeading() - 32768)));

        actor.setSpawnedLoc(new Location(x, y, actor.getZ()));
        addTaskMove(actor.getSpawnedLoc(), true);
        doTask();
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    public CTBBossInstance getActor() {
        return (CTBBossInstance) super.getActor();
    }
}
