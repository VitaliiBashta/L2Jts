package org.mmocore.gameserver.scripts.ai.residences;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.configuration.config.AiConfig;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.scripts.npc.model.residences.SiegeGuardInstance;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class SiegeGuard extends DefaultAI {
    public SiegeGuard(NpcInstance actor) {
        super(actor);
        MAX_PURSUE_RANGE = 1000;
    }

    @Override
    public SiegeGuardInstance getActor() {
        return (SiegeGuardInstance) super.getActor();
    }

    @Override
    public int getMaxPathfindFails() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaxAttackTimeout() {
        return 0;
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    @Override
    protected boolean randomAnimation() {
        return false;
    }

    @Override
    public boolean canSeeInSilentMove(Playable target) {
        // Осадные гварды могут видеть игроков в режиме Silent Move с вероятностью 10%
        return !target.isSilentMoving() || Rnd.chance(10);
    }

    @Override
    protected boolean isAggressive() {
        return true;
    }

    @Override
    protected boolean isGlobalAggro() {
        return true;
    }

    @Override
    protected void onEvtAggression(Creature target, int aggro) {
        SiegeGuardInstance actor = getActor();
        if (actor.isDead()) {
            return;
        }
        if (target == null || !actor.isAutoAttackable(target)) {
            return;
        }
        super.onEvtAggression(target, aggro);
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
            for (DefaultAI.TargetContains cha : targets) {
                if (cha.getCreature() != null && checkAggression(cha.getCreature())) {
                    return true;
                }
            }
        }

        Location sloc = actor.getSpawnedLoc();
        // Проверка на расстояние до точки спауна
        if (!actor.isInRange(sloc, 250)) {
            teleportHome();
            return true;
        }

        return false;
    }

    @Override
    protected Creature prepareTarget() {
        SiegeGuardInstance actor = getActor();
        if (actor.isDead()) {
            return null;
        }

        // Новая цель исходя из агрессивности
        List<Creature> hateList = actor.getAggroList().getHateList(MAX_PURSUE_RANGE);
        Creature hated = null;
        for (Creature cha : hateList) {
            //Не подходит, очищаем хейт
            if (!checkTarget(cha, MAX_PURSUE_RANGE)) {
                actor.getAggroList().remove(cha, true);
                continue;
            }
            hated = cha;
            break;
        }

        if (hated != null) {
            setAttackTarget(hated);
            return hated;
        }

        return null;
    }

    @Override
    protected boolean canAttackCharacter(Creature target) {
        return getActor().isAutoAttackable(target);
    }
}