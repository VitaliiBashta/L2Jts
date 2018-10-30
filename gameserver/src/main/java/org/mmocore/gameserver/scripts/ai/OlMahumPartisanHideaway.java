package org.mmocore.gameserver.scripts.ai;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * @author PaInKiLlEr - AI для монстра Ol Mahum Raider (20208). - При атаке кричат в чат. - AI проверен и работает.
 */
public class OlMahumPartisanHideaway extends Fighter {
    public static final NpcString[] MsgAttacked20208 = {NpcString.WE_SHALL_SEE_ABOUT_THAT, NpcString.AM_I_THE_NEIGHBORHOOD_DRUM_FOR_BEATING, NpcString.RETREAT};
    public static final NpcString[] MsgAttacked20207 = {NpcString.INVADER, NpcString.FOLLOW_ME_IF_YOU_WANT};
    private boolean firstTimeAttacked = true;

    public OlMahumPartisanHideaway(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();
        if (actor == null)
            return;

        if (firstTimeAttacked && Rnd.chance(25)) {
            firstTimeAttacked = false;

            switch (actor.getNpcId()) {
                case 20207:
                    Functions.npcSay(actor, MsgAttacked20207[Rnd.get(MsgAttacked20207.length)]);
                    break;
                case 20208:
                    Functions.npcSay(actor, MsgAttacked20208[Rnd.get(MsgAttacked20208.length)]);
                    break;
            }
        }
        super.onEvtAttacked(attacker, skill, damage);
    }
}