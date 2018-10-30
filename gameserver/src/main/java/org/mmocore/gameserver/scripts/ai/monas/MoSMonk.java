package org.mmocore.gameserver.scripts.ai.monas;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;

/**
 * AI монахов в Monastery of Silence<br>
 * - агрятся на чаров с оружием в руках
 * - перед тем как броситься в атаку кричат
 *
 * @author SYS
 */
public class MoSMonk extends Fighter {
    public MoSMonk(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onIntentionAttack(Creature target) {
        NpcInstance actor = getActor();
        if (getIntention() == CtrlIntention.AI_INTENTION_ACTIVE && Rnd.chance(20)) {
            Functions.npcSay(actor, NpcString.YOU_CANNOT_CARRY_A_WEAPON_WITHOUT_AUTHORIZATION);
        }
        super.onIntentionAttack(target);
    }

    @Override
    public boolean checkAggression(Creature target) {
        if (target.getActiveWeaponInstance() == null) {
            return false;
        }
        return super.checkAggression(target);
    }
}