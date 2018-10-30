package org.mmocore.gameserver.scripts.ai;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.utils.ChatUtils;

//import org.mmocore.gameserver.model.Creature;

public class BlacksmithMammon extends DefaultAI {
    private static final long chatDelay = 30 * 60 * 1000L;
    /**
     * Messages of NPCs *
     */
    private static final NpcString[] mamonText = {
            NpcString.RULERS_OF_THE_SEAL_I_BRING_YOU_WONDROUS_GIFTS,
            NpcString.RULERS_OF_THE_SEAL_I_HAVE_SOME_EXCELLENT_WEAPONS_TO_SHOW_YOU,
            NpcString.IVE_BEEN_SO_BUSY_LATELY_IN_ADDITION_TO_PLANNING_MY_TRIP};
    private long _chatVar = 0;

    public BlacksmithMammon(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.isDead()) {
            return true;
        }

        if (_chatVar + chatDelay < System.currentTimeMillis()) {
            _chatVar = System.currentTimeMillis();
            ChatUtils.shout(actor, mamonText[Rnd.get(mamonText.length)]);
        }

        return false;
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }
}