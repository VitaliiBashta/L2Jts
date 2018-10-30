package org.mmocore.gameserver.scripts.maker;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.threading.ThreadPoolManager;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.manager.SpawnManager;

/**
 * @author KilRoy, pchayka
 * Временный набросок мейкера. После фулл запила мейкеров, перенести на них.
 */
public class blood_altar_event_maker implements OnInitScriptListener {
    private static final String[] event_altar_raid_group =
            {
                    "bloodaltar_boss_aden",
                    "bloodaltar_boss_darkelf",
                    "bloodaltar_boss_dion",
                    "bloodaltar_boss_dwarw",
                    "bloodaltar_boss_giran",
                    "bloodaltar_boss_gludin",
                    "bloodaltar_boss_gludio",
                    "bloodaltar_boss_goddart",
                    "bloodaltar_boss_heine",
                    "bloodaltar_boss_orc",
                    "bloodaltar_boss_oren",
                    "bloodaltar_boss_schutgart"
            };

    private static final String event_alive_npc_group = "bloodaltar_alive_npc";
    private static final String event_dead_npc_group = "bloodaltar_dead_npc";
    private static final int RandRate = 14;
    private static final int RandomTime = 30;
    private long bossRespawnTimer = 0;
    private boolean bossesSpawned;

    @Override
    public void onInit() {
        initSpawns();
    }

    private void initSpawns() {
        manageNpcs(true);
        final int delay = Rnd.get(RandomTime) * 60 * 1000;
        ThreadPoolManager.getInstance().schedule(new respawnGroups(), delay);
    }

    private void manageNpcs(final boolean spawnAlive) {
        if (spawnAlive) {
            SpawnManager.getInstance().despawn(event_dead_npc_group);
            SpawnManager.getInstance().spawn(event_alive_npc_group);
        } else {
            SpawnManager.getInstance().despawn(event_alive_npc_group);
            SpawnManager.getInstance().spawn(event_dead_npc_group);
        }
    }

    private void manageBosses(final boolean spawn) {
        if (spawn) {
            for (final String s : event_altar_raid_group) {
                SpawnManager.getInstance().spawn(s);
            }
        } else {
            bossRespawnTimer = System.currentTimeMillis() + 4 * 3600 * 1000L;
            for (final String s : event_altar_raid_group) {
                SpawnManager.getInstance().despawn(s);
            }
        }
    }

    private class respawnGroups extends RunnableImpl {
        @Override
        public void runImpl() {
            if (Rnd.chance(RandRate) && bossRespawnTimer < System.currentTimeMillis()) {
                if (!bossesSpawned) {
                    manageNpcs(false);
                    manageBosses(true);
                    bossesSpawned = true;
                } else {
                    manageBosses(false);
                    manageNpcs(true);
                    bossesSpawned = false;
                }
            }
            final int delay = Rnd.get(RandomTime) * 60 * 1000;
            ThreadPoolManager.getInstance().schedule(this, delay);
        }
    }
}