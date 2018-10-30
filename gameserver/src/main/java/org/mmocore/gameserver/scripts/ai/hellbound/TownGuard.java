package org.mmocore.gameserver.scripts.ai.hellbound;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.utils.ChatUtils;

/**
 * AI Town Guard в городе-инстанте на Hellbound<br>
 * - перед тем как броситься в атаку кричат
 *
 * @author SYS
 * @author KilRoy
 */
public class TownGuard extends Fighter {
    private int i_ai1;

    public TownGuard(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        i_ai1 = -1;
        super.onEvtSpawn();
    }

    @Override
    protected void onIntentionAttack(Creature target) {
        NpcInstance actor = getActor();
        if (getIntention() == CtrlIntention.AI_INTENTION_ACTIVE && Rnd.chance(50)) {
            ChatUtils.say(actor, NpcString.INVADER);
        }
        if (i_ai1 == -1) {
            for (final NpcInstance npcs : actor.getAroundNpc(1000, 1000)) {
                if (!npcs.isDead() && npcs.isMonster()) {
//					npcs.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, target, 2);
                    ChatUtils.say(npcs, NpcString.INVADER);
                }
            }
            i_ai1 = 1;
        }
        super.onIntentionAttack(target);
    }
}