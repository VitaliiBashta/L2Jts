package org.mmocore.gameserver.scripts.ai.pts.lair_of_antaras;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.utils.NpcUtils;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_dragon_knight_3 extends Fighter {
    private static final int dragon_knight_5 = 22845;

    public ai_dragon_knight_3(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtDead(Creature killer) {
        if (Rnd.get(5) < 1) {
            final NpcInstance npc = NpcUtils.spawnSingle(dragon_knight_5, getActor().getLoc());
            npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 2);
        }
        super.onEvtDead(killer);
    }
}