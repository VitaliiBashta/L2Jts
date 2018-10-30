package org.mmocore.gameserver.scripts.ai.pts;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.utils.NpcUtils;

/**
 * @author Mangol
 * @version PTS Freya
 * @npc_id 22703 22704 22705
 * TODO - чуть допилить...
 */
public class legend_orc_warrior extends Fighter {
    private static final int legend_orc_2grudge = 22707;
    private static final int legend_orc_3grudge = 18815;

    public legend_orc_warrior(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();
        if (Rnd.get(100) < 2) {
            switch (Rnd.get(2)) {
                case 0: {
                    NpcUtils.spawnSingle(legend_orc_2grudge, actor.getLoc());
                    break;
                }
                case 1: {
                    if (Rnd.get(2) < 1) {
                        NpcUtils.spawnSingle(legend_orc_3grudge, actor.getLoc());
                    }
                    break;
                }
            }
        }
        super.onEvtDead(killer);
    }
}
