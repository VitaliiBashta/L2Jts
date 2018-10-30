package org.mmocore.gameserver.scripts.ai;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.utils.ChatUtils;

/**
 * AI рейдбосса Tiberias
 * любит поговорить после смерти
 *
 * @author n0nam3
 */
public class Tiberias extends Fighter {
    public Tiberias(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();

        ChatUtils.shout(actor, NpcString.YOUR_SKILL_IS_IMPRESSIVE_ILL_ADMIT_THAT_YOU_ARE_GOOD_ENOUGH_TO_PASS_TAKE_THE_KEY_AND_LEAVE_THIS_PLACE);

        super.onEvtDead(killer);
    }
}