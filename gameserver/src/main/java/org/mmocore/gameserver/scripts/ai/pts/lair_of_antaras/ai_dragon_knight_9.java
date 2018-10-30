package org.mmocore.gameserver.scripts.ai.pts.lair_of_antaras;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.MonsterInstance;
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
public class ai_dragon_knight_9 extends Fighter {

    public ai_dragon_knight_9(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtDead(Creature killer) {
        final MonsterInstance actor = (MonsterInstance) getActor();
        if (actor != null && actor.isSpoiled()) {
            if (Rnd.get(10) < 3) {
                final int i0 = Rnd.get(1, 3);
                switch (i0) {
                    case 1:
                        ChatUtils.say(getActor(), NpcString.HOW_COULD_I_LOSE_AGAINST_THESE_WORTHLESS_CREATURES);
                        break;
                    case 2:
                        ChatUtils.say(getActor(), NpcString.FOOLISH_CREATURES_THE_FLAMES_OF_HELL_ARE_DRAWING_CLOSER);
                        break;
                    case 3:
                        ChatUtils.say(getActor(), NpcString.NO_MATTER_HOW_YOU_STRUGGLE_THIS_PLACE_WILL_SOON_BE_COVERED_WITH_YOUR_BLOOD);
                        break;
                }
                int invisible_npc = 18919;
                NpcUtils.createOnePrivateEx(invisible_npc, actor.getX(), actor.getY(), actor.getZ(), killer, 0, 0);
            }
        }
        super.onEvtDead(killer);
    }

    @Override
    protected void onEvtSpawn() {
        ChatUtils.say(getActor(), NpcString.IF_YOU_WISH_TO_SEE_HELL_I_WILL_GRANT_YOU_WISH);
        super.onEvtSpawn();
    }
}