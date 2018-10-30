package org.mmocore.gameserver.scripts.ai.monas;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.utils.ChatUtils;

/**
 * @author PaInKiLlEr - AI для моба Solina Knight Captain (18910). - Перед тем как броситься в атаку, кричат рандомную фразу. - AI проверен и
 * работает.
 */
public class SolinaKnightCaptain extends Fighter {
    public static final NpcString[] text = {NpcString.FOR_THE_GLORY_OF_SOLINA1, NpcString.PUNISH_ALL_THOSE_WHO_TREAD_FOOTSTEPS_IN_THIS_PLACE, NpcString.WE_ARE_SWORD_OF_TRUSH_THE_SWORD_OF_SOLINA, NpcString.WE_RAISE_OUR_BLADES_FOR_THE_GLORY_OF_SOLINA, NpcString.FOR_THE_GLORY_OF_SOLINA2};

    public SolinaKnightCaptain(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onIntentionAttack(Creature target) {
        NpcInstance actor = getActor();
        if (actor == null)
            return;

        if (getIntention() == CtrlIntention.AI_INTENTION_ACTIVE && Rnd.chance(50))
            ChatUtils.say(actor, text[Rnd.get(text.length)]);

        super.onIntentionAttack(target);
    }
}