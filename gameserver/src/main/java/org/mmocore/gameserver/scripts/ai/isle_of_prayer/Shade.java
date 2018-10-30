package org.mmocore.gameserver.scripts.ai.isle_of_prayer;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;

/**
 * AI мобов Shade на Isle of Prayer, спавнятся из АИ DarkWaterDragon.<br>
 * - Деспавнятся после 5 минут<br>
 * - Не используют функцию Random Walk<br>
 * ID: 22268, 22269
 *
 * @author SYS
 */
public class Shade extends Fighter {
    private static final int BLUE_CRYSTAL = 9595;

    public Shade(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        AddTimerEx(1001, 5 * 60 * 1000);
    }

    @Override
    protected void onEvtTimerFiredEx(final int timer_id, final Object arg1, final Object arg2) {
        final NpcInstance actor = getActor();
        if (actor == null || actor.isDead()) {
            return;
        }

        if (timer_id == 1001) {
            actor.deleteMe();
        }
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    @Override
    protected void onEvtDead(Creature killer) {
        if (killer != null) {
            final Player player = killer.getPlayer();
            if (player != null) {
                final NpcInstance actor = getActor();
                if (Rnd.chance(10)) {
                    actor.dropItem(player, BLUE_CRYSTAL, 1);
                }
            }
        }
        super.onEvtDead(killer);
    }
}