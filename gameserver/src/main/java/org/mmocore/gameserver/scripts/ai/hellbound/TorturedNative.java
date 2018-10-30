package org.mmocore.gameserver.scripts.ai.hellbound;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.utils.ChatUtils;

/**
 * AI Tortured Native в городе-инстанте на Hellbound<br>
 * - периодически кричат
 *
 * @author SYS
 */
public class TorturedNative extends Fighter {
    public TorturedNative(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.isDead()) {
            return true;
        }

        if (Rnd.chance(1)) {
            if (Rnd.get(100) > 20) {
                ChatUtils.say(actor, NpcString.IT_WILL_KILL_EVERYONE);
            } else {
                ChatUtils.say(actor, NpcString.EEEK_I_FEEL_SICK_YOW);
            }
        }

        return super.thinkActive();
    }
}