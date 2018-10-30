package services;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.listener.actor.OnDeathListener;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.manager.RaidBossSpawnManager;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.SimpleSpawner;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.AnnouncementUtils;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.idfactory.IdFactory;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Date;

/**
 * Created by Hack
 * Date: 23.07.2016 0:30
 */
public class CustomWeaklySpawnManager implements OnInitScriptListener {
    private static final Logger log = LoggerFactory.getLogger(CustomWeaklySpawnManager.class);

    private class Spawner {
        private int id;
        private Location location;
        private long millis;

        public Spawner(int id, Location location, long millis) {
            this.id = id;
            this.location = location;
            this.millis = millis;
            spawn();
        }

        public void spawn() {
            NpcTemplate template = NpcHolder.getInstance().getTemplate(id);
            ThreadPoolManager.getInstance().schedule(new SpawnTask(template, location), millis - System.currentTimeMillis());
            log.info("Scheduled spawn for " + template.getName() + " at " + new Date(millis) + ". Remain: " + (millis / 60000) + " mins.");
        }
    }

    private class SpawnTask implements Runnable {
        private NpcTemplate boss;
        private Location location;
        private String ruAnnounce;
        private String enAnnounce;

        public SpawnTask(NpcTemplate boss, Location location) {
            this.boss = boss;
            this.location = location;
            ruAnnounce = "Эпический Босс " + boss.getName() + " явился в этот мир";
            enAnnounce = "Epic Boss " + boss.getName() + " appeared in the game world!";
        }

        @Override
        public void run() {
            announce();
            manageSpawn();
        }

        private void manageSpawn() {
            try {
                final SimpleSpawner spawn = new SimpleSpawner(boss);
                spawn.setLoc(location);
                spawn.setAmount(1);
                spawn.setHeading(location.h);
                spawn.setRespawnDelay(0);
                spawn.setReflection(ReflectionManager.getInstance().get(0));
                addDeathListener(spawn.initSingle());
                spawn.stopRespawn();
            } catch (Exception e) {
                log.warn("Error while spawn " + boss.getName());
                e.printStackTrace();
            }
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

    private long createDate(int dayOfWeek, int hourOfDay, int minutesOfHour, int randomMinutes) {
        ZonedDateTime date = ZonedDateTime.now(ZoneId.systemDefault()).toLocalDate().atStartOfDay(ZoneId.systemDefault())
                .with(ChronoField.DAY_OF_WEEK, dayOfWeek)
                .plusHours(hourOfDay)
                .plusMinutes(Rnd.get(minutesOfHour, minutesOfHour + randomMinutes));
        if (!date.isAfter(ZonedDateTime.now(ZoneId.systemDefault())))
            date = date.plusWeeks(1);
        return date.toInstant().toEpochMilli();
    }

    @Override
    public void onInit() {
        /**
         * сюда пишем инфо о спауне всех нужных боссов в формате:
         * new Spawner(id_босса, new Location(x, y, z, h), createDate(день_недели, час, минута, случайные_минуты));
         * День недели указывается числом от 1 до 7, где 1 - понедельник, 2 - вторник, ..., 7 - воскресение.
         * Случайные минуты добавляются к минутам спауна. Т.е. указав 1, 21, 0, 30 босс может появиться в рандомную минуту
         * с 21:00 до 21:30. Число рандомных минут не ограничено.
         */
		 
		/*
        new Spawner(29001, new Location(-21610, 181594, -5734, 0), createDate(1, 18, 30, 60));
		new Spawner(29001, new Location(-21610, 181594, -5734, 0), createDate(2, 18, 30, 60));
		new Spawner(29001, new Location(-21610, 181594, -5734, 0), createDate(3, 18, 30, 60));
		new Spawner(29001, new Location(-21610, 181594, -5734, 0), createDate(4, 18, 30, 60));
		new Spawner(29001, new Location(-21610, 181594, -5734, 0), createDate(5, 18, 30, 60));
		new Spawner(29001, new Location(-21610, 181594, -5734, 0), createDate(6, 18, 30, 60));
		new Spawner(29001, new Location(-21610, 181594, -5734, 0), createDate(7, 18, 30, 60));

		new Spawner(29006, new Location(17746, 108915, -6480, 0), createDate(1, 22, 30, 60));
		new Spawner(29006, new Location(17746, 108915, -6480, 0), createDate(2, 22, 30, 60));
		new Spawner(29006, new Location(17746, 108915, -6480, 0), createDate(3, 22, 30, 60));
		new Spawner(29006, new Location(17746, 108915, -6480, 0), createDate(4, 22, 30, 60));
		new Spawner(29006, new Location(17746, 108915, -6480, 0), createDate(5, 22, 30, 60));
		new Spawner(29006, new Location(17746, 108915, -6480, 0), createDate(6, 22, 30, 60));
		new Spawner(29006, new Location(17746, 108915, -6480, 0), createDate(7, 22, 30, 60));

		new Spawner(29014, new Location(55024, 17368, -5412, 0), createDate(1, 15, 30, 60));
		new Spawner(29014, new Location(55024, 17368, -5412, 0), createDate(2, 15, 30, 60));
		new Spawner(29014, new Location(55024, 17368, -5412, 0), createDate(3, 15, 30, 60));
		new Spawner(29014, new Location(55024, 17368, -5412, 0), createDate(4, 15, 30, 60));
		new Spawner(29014, new Location(55024, 17368, -5412, 0), createDate(5, 15, 30, 60));
		new Spawner(29014, new Location(55024, 17368, -5412, 0), createDate(6, 15, 30, 60));
		new Spawner(29014, new Location(55024, 17368, -5412, 0), createDate(7, 15, 30, 60));
		*/

    }
}
