package org.mmocore.gameserver.scripts.ai.pts;

import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.world.World;

import java.util.List;

/**
 * @author PaInKiLlEr
 * - AI для невидимого моба (18837).
 * - Каждые 30 секунд кастует скил, если игрок в радиусе то вешает баф на игрока.
 * - AI проверен и работает.
 */
public class legend_orc_buff extends DefaultAI {
    private long _wait_timeout = System.currentTimeMillis() + 30000;

    public legend_orc_buff(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean isGlobalAI() {
        return false;
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor == null)
            return false;

        if (_wait_timeout < System.currentTimeMillis()) {
            List<Player> players = World.getAroundPlayers(actor, 300, 300);
            for (Player player : players) {
                if (player == null)
                    continue;

                actor.doCast(SkillTable.getInstance().getSkillEntry(6235, 1), player, true);
            }
            _wait_timeout = (System.currentTimeMillis() + 30000);
        }

        return true;
    }
}
