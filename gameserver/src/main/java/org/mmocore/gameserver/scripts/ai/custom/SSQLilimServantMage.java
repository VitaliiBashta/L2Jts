package org.mmocore.gameserver.scripts.ai.custom;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.Mystic;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * AI Lilim Servants: 27371-27379
 *
 * @author pchayka
 */
public class SSQLilimServantMage extends Mystic {
    private boolean _attacked = false;

    public SSQLilimServantMage(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        super.onEvtAttacked(attacker, skill, damage);
        if (Rnd.chance(30) && !_attacked) {
            Functions.npcSay(getActor(), "Who dares enter this place?");
            _attacked = true;
        }
    }

    @Override
    protected void onEvtDead(Creature killer) {
        if (Rnd.chance(30)) {
            Functions.npcSay(getActor(), "Lord Shilen... some day... you will accomplish... this mission...");
        }
        super.onEvtDead(killer);
    }
}