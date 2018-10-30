package org.mmocore.gameserver.scripts.maker;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.manager.SpawnManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.Earthquake;
import org.mmocore.gameserver.network.lineage.serverpackets.L2GameServerPacket;
import org.mmocore.gameserver.utils.ReflectionUtils;
import org.mmocore.gameserver.world.GameObjectsStorage;

/**
 * @author KilRoy
 * Временный набросок мейкера. После фулл запила мейкеров, перенести на них.
 */
public class valley_of_dragon_event_maker implements OnInitScriptListener {
    private static final String event_dc_monster_group = "event_dc_monster";
    private static final int RandRate = 14;
    private static final int LoopTime = 60;
    private static final int RandomTime = 15;

    @Override
    public void onInit() {
        initSpawns();
    }

    private void initSpawns() {
        SpawnManager.getInstance().spawn(event_dc_monster_group);
        final NpcInstance actor = GameObjectsStorage.getByNpcId(18969);
        if (actor != null) {
            final L2GameServerPacket eq = new Earthquake(actor.getLoc(), 50, 10);
            ReflectionUtils.getZone("[dragon_valley_earthquake]").getInsidePlayers().stream().forEach(player -> {
                player.sendPacket(eq);
            });
            final int i0 = Rnd.get(RandomTime);
            final long randomResp = (((LoopTime * 60) * 1000) + ((i0 * 60) * 1000));
            ThreadPoolManager.getInstance().schedule(new respawnGroup(), randomResp);
        }
    }

    private class respawnGroup extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            SpawnManager.getInstance().despawn(event_dc_monster_group);
            if (Rnd.chance(RandRate)) {
                initSpawns();
            } else {
                final int i0 = Rnd.get(RandomTime);
                final long randomResp = (((LoopTime * 60) * 1000) + ((i0 * 60) * 1000));
                ThreadPoolManager.getInstance().schedule(this, randomResp);
            }
        }
    }
}