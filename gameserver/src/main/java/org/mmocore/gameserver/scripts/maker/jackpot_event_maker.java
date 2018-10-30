package org.mmocore.gameserver.scripts.maker;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.data.xml.holder.SpawnHolder;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.HardSpawner;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.templates.spawn.SpawnTemplate;
import org.mmocore.gameserver.world.GameObjectsStorage;

import java.util.Arrays;
import java.util.List;

/**
 * @author KilRoy
 * Спавним 1ну свинью в мир, и после 24минут(по АИ 1440 сек) с шансом, респавним. Вроде кагНаОфе теперь.
 * Временный набросок мейкера. После фулл запила мейкеров, перенести на них.
 */
public class jackpot_event_maker extends Functions implements OnInitScriptListener {
    private static final String[] LAKFI_SPAWN_GROUP = {"lakkfi_1", "lakkfi_2", "lakkfi_3", "lakkfi_4", "lakkfi_5", "lakkfi_6", "lakkfi_7", "lakkfi_8", "lakkfi_9"};
    private static final int RandRate = 10;

    private static final List<Integer> npc_ids = Arrays.asList(2501, 18664, 18665);

    @Override
    public void onInit() {
        initSpawns();
    }

    private void initSpawns() {
        List<SpawnTemplate> LAKFI_LIST_TEMPLATE = SpawnHolder.getInstance().getSpawn(LAKFI_SPAWN_GROUP[Rnd.get(LAKFI_SPAWN_GROUP.length)]);
        if (LAKFI_LIST_TEMPLATE == null || LAKFI_LIST_TEMPLATE.isEmpty())
            return;
        final int index = Rnd.get(LAKFI_LIST_TEMPLATE.size() - 1);
        final SpawnTemplate template = LAKFI_LIST_TEMPLATE.get(index);
        initSpawnForGrp(template);
        ThreadPoolManager.getInstance().schedule(new respawnGroup(), 1440000L);
        if (LAKFI_LIST_TEMPLATE != null || !LAKFI_LIST_TEMPLATE.isEmpty())
            LAKFI_LIST_TEMPLATE = null;
    }

    private void initSpawnForGrp(SpawnTemplate template) {
        HardSpawner spawner = new HardSpawner(template);

        spawner.setAmount(1);
        spawner.setRespawnDelay(0, 0);
        spawner.setReflection(ReflectionManager.DEFAULT);
        spawner.setRespawnTime(0);
        spawner.doSpawn(true);
    }

    private class respawnGroup extends RunnableImpl {
        @Override
        public void runImpl() {
            for (NpcInstance npc : GameObjectsStorage.getAllByNpcId(npc_ids, true)) {
                if (npc != null)
                    npc.deleteMe();
            }

            if (Rnd.chance(RandRate)) {
                initSpawns();
            } else {
                ThreadPoolManager.getInstance().schedule(this, 1440000L);
            }
        }
    }
}