package org.mmocore.gameserver.scripts.ai.pts.lair_of_antaras;

import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.tables.SkillTable;

/**
 * @author Mangol
 */
public class ai_party_vitality_herb extends DefaultAI {
    private final int TID_LIFETIME = 787878;

    public ai_party_vitality_herb(final NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        final NpcInstance actor = getActor();
        final Creature arg = actor.getParam4();
        if (arg != null && !arg.isDead()) {
            actor.doCast(SkillTable.getInstance().getSkillEntry(6883, 1), arg, true);
            if (arg.isPlayer() && arg.getPlayer().getParty() != null) //TODO: Придумать другой способ КОНЧЕНОЕ ДВИГЛО СКИЛОВ.
            {
                for (final Player player : arg.getPlayer().getParty().getPartyMembers()) {
                    actor.doCast(SkillTable.getInstance().getSkillEntry(6883, 1), player, true);
                }
            }
        }
        int TIME_LIFETIME = 3;
        AddTimerEx(TID_LIFETIME, TIME_LIFETIME * 1000);
    }

    @Override
    protected void onEvtTimerFiredEx(final int timer_id, final Object arg1, final Object arg2) {
        final NpcInstance actor = getActor();
        if (actor == null) {
            return;
        }
        if (timer_id == TID_LIFETIME) {
            actor.deleteMe();
        }
    }
}
