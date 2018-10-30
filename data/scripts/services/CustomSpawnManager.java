package services;

import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.listener.actor.OnDeathListener;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.AnnouncementUtils;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.idfactory.IdFactory;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Calendar;
import java.util.Date;

/**
 * Created by Hack
 * Date: 23.07.2016 0:30
 */
public class CustomSpawnManager implements OnInitScriptListener {
    private static final Logger log = LoggerFactory.getLogger(CustomSpawnManager.class);

    private class Spawner {
        private int id;
        private Location location;
        private int hour;
        private int minute;

        public Spawner(int id, Location location, int hour, int minute) {
            this.id = id;
            this.location = location;
            this.hour = hour;
            this.minute = minute;
            spawn();
        }

        public void spawn() {
            MonsterInstance instance = new MonsterInstance(IdFactory.getInstance().getNextId(),
                    NpcHolder.getInstance().getTemplate(id));
            long respawnMillis = getMillis(hour, minute);
            long millis = respawnMillis - System.currentTimeMillis();
            ThreadPoolManager.getInstance().schedule(new SpawnTask(instance, location), millis);
            log.info("Scheduled spawn for " + instance.getName() + " at " + new Date(respawnMillis) + ". Remain: " + (millis / 60000) + " mins.");
            System.out.println("----------------------------------------");
        }

        private long getMillis(int hour, int minute) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            if (calendar.getTimeInMillis() < System.currentTimeMillis())
                calendar.add(Calendar.DAY_OF_WEEK, 1);
            return calendar.getTimeInMillis();
        }
    }

    private class SpawnTask implements Runnable {
        private MonsterInstance boss;
        private Location location;
        private String ruAnnounce;
        private String enAnnounce;

        public SpawnTask(MonsterInstance boss, Location location) {
            this.boss = boss;
            this.location = location;
            ruAnnounce = "Мировой Босс " + boss.getName() + " явился в этот мир";
            enAnnounce = "Raid Boss " + boss.getName() + " appeared in the game world!";
        }

        @Override
        public void run() {
            announce();
            boss.setSpawnedLoc(location);
            boss.setHeading(location.h);
            boss.setLoc(location);
            boss.setReflection(0);
            boss.spawnMe();
            addDeathListener(boss);
        }

        private void announce() {
            AnnouncementUtils.announceWithSplash(ruAnnounce, enAnnounce);
            log.info("Spawned " + boss.getName() + ". Current Time: " + new Date());
        }

        private void addDeathListener(NpcInstance instance) {
            instance.addListener(new OnDeathListener() {
                @Override
                public void onDeath(Creature actor, Creature killer) {
                    if (actor != null && actor instanceof NpcInstance) {
                        AnnouncementUtils.announceBossDeath((NpcInstance) actor);
                    }
                }
            });
        }
    }

    @Override
    public void onInit() {
        /**
         * сюда пишем инфо о спауне всех нужных боссов в формате:
         * new Spawner(id_босса, new Location(x, y, z), часы, минуты);
         * Например: new Spawner(22854, new Location(74216, 88584, -3221), 17, 10); - заспаунит Кровавого Карика по
         * координатам (74216, 88584, -3221) ровно в 17:10
         */

        /*
		new Spawner(25282, new Location(112798, -76800, 8, 51210), 15, 0);
        new Spawner(25282, new Location(112798, -76800, 8, 51210), 17, 0);
		new Spawner(25282, new Location(112798, -76800, 8, 51210), 21, 0);
		new Spawner(25282, new Location(112798, -76800, 8, 51210), 1, 0);
		*/
    }
	
    public void onReload() {}

    public void onShutdown() {}
}
