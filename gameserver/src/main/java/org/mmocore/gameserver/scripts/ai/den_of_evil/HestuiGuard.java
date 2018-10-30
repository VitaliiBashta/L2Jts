package org.mmocore.gameserver.scripts.ai.den_of_evil;

import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.taskmanager.AiTaskManager;
import org.mmocore.gameserver.world.World;

/**
 * @author VISTALL
 * @date 19:24/28.08.2011
 * Npc Id: 32026
 * Кричит в чат - если лвл ниже чем 37 включно
 */
public class HestuiGuard extends DefaultAI {
    public HestuiGuard(NpcInstance actor) {
        super(actor);

    }

    @Override
    public final synchronized void startAITask() {
        if (haveAiTask.compareAndSet(false, true))
            aiTask = AiTaskManager.getInstance().scheduleAtFixedRate(this, 10000L, 10000L);
    }

    @Override
    protected final synchronized void switchAITask(long NEW_DELAY) {
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();

        for (Player player : World.getAroundPlayers(actor)) {
            if (player.getLevel() <= 37) {
                Functions.npcSay(actor, NpcString.THIS_PLACE_IS_DANGEROUS_S1__PLEASE_TURN_BACK, player.getName());
            }
        }

        return false;
    }
}
