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
public class OriginalSinWarden8Floor extends Fighter {
    //private static final int[] servants = { 22432, 22433, 22434, 22435, 22436, 22437, 22438 };
    private static final int[] DarionsFaithfulServants = {22408, 22409, 22410};

    public OriginalSinWarden8Floor(NpcInstance actor) {
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