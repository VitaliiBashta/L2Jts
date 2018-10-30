package org.mmocore.gameserver.scripts.ai;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;

/**
 * @author Diamond
 */
public class EvasGiftBox extends Fighter {
    private static final int[] KISS_OF_EVA = new int[]{1073, 3252, 2076}; // на офе, только s_water_breathing = 1073

    private static final int Red_Coral = 9692;
    private static final int Crystal_Fragment = 9693;

    public EvasGiftBox(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();
        if (killer != null) {
            Player player = killer.getPlayer();
            if (player != null && player.getEffectList().containEffectFromSkills(KISS_OF_EVA)) {
                actor.dropItem(player, Rnd.chance(50) ? Red_Coral : Crystal_Fragment, 1);
            }
        }
        super.onEvtDead(killer);
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}