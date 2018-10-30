package org.mmocore.gameserver.scripts.ai;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.Mystic;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;

/**
 * @author PaInKiLlEr - AI для монстра Roughly Hewn Rock Golem (21103). - Перед тем как броситься в атаку, кричит в чат с шансом 50%. - AI проверен и
 * работает.
 */
public class RoughlyHewnRockGolem extends Mystic {
    public RoughlyHewnRockGolem(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onIntentionAttack(Creature target) {
        NpcInstance actor = getActor();
        if (actor == null)
            return;

        if (getIntention() == CtrlIntention.AI_INTENTION_ACTIVE && Rnd.chance(50))
            Functions.npcSay(actor, NpcString.DIE_YOU_COWARD);

        super.onIntentionAttack(target);
    }
}