package org.mmocore.gameserver.scripts.ai.hellbound;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;

/**
 * Original Sin Warden 8-го этажа Tully Workshop
 *
 * @автор VAVAN
 */
public class OriginalSinWarden6Floor extends Fighter {
    //private static final int[] servants = { 22424, 22425, 22426, 22427, 22428, 22429, 22430 };
    private static final int[] DarionsFaithfulServants = {22405, 22406, 22407};

    public OriginalSinWarden6Floor(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();

        if (Rnd.chance(15)) {
            NpcUtils.spawnSingle(Rnd.get(DarionsFaithfulServants), Location.findPointToStay(actor, 150, 350));
        }
        super.onEvtDead(killer);
    }

}