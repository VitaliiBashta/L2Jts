package org.mmocore.gameserver.scripts.ai.isle_of_prayer;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;

public class WaterDragonDetractor extends Fighter {
    private static final int SPIRIT_OF_LAKE = 9689;
    private static final int BLUE_CRYSTAL = 9595;

    public WaterDragonDetractor(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        AddTimerEx(3001, 30 * 1000 + Rnd.get(30) * 1000);
        AddTimerEx(3002, 30 * 1000);
    }

    @Override
    protected void onEvtTimerFiredEx(final int timer_id, final Object arg1, final Object arg2) {
        final NpcInstance actor = getActor();
        if (actor == null || actor.isDead()) {
            return;
        }

        if (timer_id == 3001) {
            if (actor.getParam4() != null && !actor.getParam4().isDead()) {
                actor.getAggroList().clear(true);
                Attack(actor, true, false);
                int i0 = Rnd.get(20);
                if (i0 < 10) {
                    i0 += 10;
                }
                AddTimerEx(3001, i0 * 1000);
            }
        } else if (timer_id == 3002) {
            if (actor.getSpawnedLoc() != null && !actor.isInRange(actor.getSpawnedLoc(), 500)) {
                returnHome(true);
            }
            if (actor.getParam4() == null || actor.getParam4().isDead()) {
                actor.deleteMe();
            }
            AddTimerEx(3002, 30 * 1000);
        }
    }

    @Override
    protected void onEvtDead(Creature killer) {
        if (killer != null) {
            final Player player = killer.getPlayer();
            if (player != null) {
                final NpcInstance actor = getActor();
                actor.dropItem(player, SPIRIT_OF_LAKE, 1);
                if (Rnd.chance(10)) {
                    actor.dropItem(player, BLUE_CRYSTAL, 1);
                }
            }
            if (getActor().getParam4() != null) {
                final FafurionKindred leaderAi = (FafurionKindred) getActor().getParam4().getAI();
                if (leaderAi != null && !leaderAi.getActor().isDead()) {
                    leaderAi.privateDead(getActor().getNpcId());
                }
            }
        }
        super.onEvtDead(killer);
    }
}