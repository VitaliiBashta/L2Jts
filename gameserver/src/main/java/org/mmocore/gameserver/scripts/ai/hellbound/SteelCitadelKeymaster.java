package org.mmocore.gameserver.scripts.ai.hellbound;

import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.world.World;

/**
 * AI Steel Citadel Keymaster в городе-инстанте на Hellbound<br>
 * - кричит когда его атакуют первый раз
 * - не использует random walk
 *
 * @author SYS
 * @author KilRoy
 */
public class SteelCitadelKeymaster extends Fighter {
    private static final int AMASKARI_ID = 22449;
    private boolean _firstTimeAttacked = true;

    public SteelCitadelKeymaster(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();
        if (actor.isDead()) {
            return;
        }

        if (_firstTimeAttacked) {
            _firstTimeAttacked = false;
            ChatUtils.say(actor, NpcString.YOU_HAVE_DONE_WELL_IN_FINDING_ME_BUT_I_CANNOT_JUST_HAND_YOU_THE_KEY);
            for (final NpcInstance npc : World.getAroundNpc(actor, 5000, 5000)) {
                if (npc.getNpcId() == AMASKARI_ID && npc.getReflectionId() == actor.getReflectionId() && !npc.isDead()) {
                    npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 2);
                    break;
                }
            }
        }

        super.onEvtAttacked(attacker, skill, damage);
    }

    @Override
    protected void onEvtDead(Creature killer) {
        _firstTimeAttacked = true;
        super.onEvtDead(killer);
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}