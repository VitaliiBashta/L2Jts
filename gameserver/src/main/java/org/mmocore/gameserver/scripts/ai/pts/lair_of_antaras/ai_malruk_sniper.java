package org.mmocore.gameserver.scripts.ai.pts.lair_of_antaras;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_malruk_sniper extends Fighter {
    public ai_malruk_sniper(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtDead(Creature killer) {
        if (killer != null && killer.isPlayer()) {
            if (killer.isMageClass()) {
                if (Rnd.get(100) < 70) {
                    getActor().dropItem(killer.getPlayer(), 8603, 1);
                } else {
                    getActor().dropItem(killer.getPlayer(), 8604, 1);
                }
            }
        }
        super.onEvtDead(killer);
    }
}