package org.mmocore.gameserver.scripts.ai.pts.lair_of_antaras;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_dragon_guard extends Fighter {
    public ai_dragon_guard(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtDead(Creature killer) {
        if (Rnd.get(10) < 1) {
            if (Rnd.get(2) < 1) {
                ChatUtils.say(getActor(), NpcString.YOU_WILL_SOON_BECOME_THE_SACRIFICE_FOR_US_THOSE_FULL_OF_DECEIT_AND_SIN_WHOM_YOU_DESPISE);
            } else {
                ChatUtils.say(getActor(), NpcString.MY_BRETHREN_WHO_ARE_STRONGER_THAT_ME_WILL_PUNISH_YOU_YOU_WILL_SOON_BE_COVERED_IN_YOUR_OWN_BLOOD_AND_CRYING_IN_ANGUISH);
            }
        }
        super.onEvtDead(killer);
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}