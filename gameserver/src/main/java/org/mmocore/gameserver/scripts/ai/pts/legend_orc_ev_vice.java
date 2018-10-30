package org.mmocore.gameserver.scripts.ai.pts;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.NpcUtils;
import org.mmocore.gameserver.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 * @editor PaInKiLlEr
 * @editor Mangol
 * - AI для моба Turka Commanders Ghost (22707).
 * - При спавне ругается в чат, запускает таймер что бы вызвать своих помошников.
 * - При смерте ругаеться в чат
 * - Через 2 секунды ругается повторно и призывает 8 Turka Followers Ghost.
 * - AI проверен и работает.
 */
public class legend_orc_ev_vice extends Fighter {
    private static final int TurkaFollowersGhost = 22706;
    private boolean _mobsNotSpawned = true;

    public legend_orc_ev_vice(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        NpcInstance actor = getActor();
        if (actor == null)
            return;

        ThreadPoolManager.getInstance().schedule(new SayRunnable(), 5000);

        super.onEvtSpawn();
    }

    @Override
    protected void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();
        if (actor == null)
            return;
        Functions.npcSay(actor, NpcString.ALL_IS_VANITY_BUT_THIS_CANNOT_BE_THE_END);
        _mobsNotSpawned = true;
        super.onEvtDead(killer);
    }

    private class SayRunnable extends RunnableImpl {
        @Override
        public void runImpl() {
            NpcInstance actor = getActor();
            if (actor == null)
                return;

            Functions.npcSay(actor, NpcString.ALL_WILL_PAY_A_SEVERE_PRICE_TO_ME_AND_THESE_HERE);
            ThreadPoolManager.getInstance().schedule(new SpawnMobs(), 1000);
        }
    }

    private class SpawnMobs extends RunnableImpl {
        @Override
        public void runImpl() {
            NpcInstance actor = getActor();
            if (actor == null)
                return;

            if (_mobsNotSpawned) {
                _mobsNotSpawned = false;

                // список нпц для аггра
                List<NpcInstance> npcForAggro = new ArrayList<NpcInstance>(8 + 1);

                // спавним
                for (int i = 0; i < 8; i++) {
                    NpcInstance npc = NpcUtils.spawnSingle(TurkaFollowersGhost, actor.getX() + Rnd.get(50), actor.getY() + Rnd.get(50), actor.getZ(), 0);
                    npcForAggro.add(npc);
                }

                // сам главарь
                npcForAggro.add(actor);

                // список игроков на кого агримся
                List<Player> aggroList = World.getAroundPlayers(actor, 1000, 1000);
                for (NpcInstance $npc : npcForAggro) {
                    if ($npc == null)
                        continue;

                    Player aggroPlayer = Rnd.get(aggroList);

                    if (aggroPlayer != null)
                        $npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, aggroPlayer, Rnd.get(1, 100));
                }
            }
        }
    }
}
