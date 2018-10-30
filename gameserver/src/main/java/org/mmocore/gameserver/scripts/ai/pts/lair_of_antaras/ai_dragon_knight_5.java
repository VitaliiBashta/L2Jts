package org.mmocore.gameserver.scripts.ai.pts.lair_of_antaras;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.NpcUtils;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_dragon_knight_5 extends Fighter {
    private static final int dragon_knight_9 = 22846;

    public ai_dragon_knight_5(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtDead(Creature killer) {
        if (Rnd.get(10) < 1) {
            final NpcInstance npc = NpcUtils.spawnSingle(dragon_knight_9, getActor().getLoc());
            npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 2);
        }
        super.onEvtDead(killer);
    }

    @Override
    protected void onEvtSpawn() {
        if (Rnd.get(2) < 1) {
            ChatUtils.say(getActor(), NpcString.THOSE_WHO_SET_FOOT_IN_THIS_PLACE_SHALL_NOT_LEAVE_ALIVE);
        } else {
            ChatUtils.say(getActor(), NpcString.WORTHLESS_CREATURES_I_WILL_GRANT_YOU_ETERNAL_SLEEP_IN_FIRE_AND_BRIMSTONE);
        }
    }
}