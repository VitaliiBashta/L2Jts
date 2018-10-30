package org.mmocore.gameserver.taskmanager.tasks;

import org.mmocore.gameserver.scripts.bosses.*;
import org.mmocore.gameserver.utils.AnnouncementUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hack
 * Date: 08.10.2016 3:16
 */
public class EpicBossAnnounceTask extends AutomaticTask {
    private static final CronScheduleBuilder PATTERN = CronScheduleBuilder.cronSchedule("0 * * * * ?");
    private static final List<EpicBoss> bosses = new ArrayList<>();

    @Override
    protected void doTask() {
        bosses.forEach(EpicBoss::tryAnnounce);
    }

    @Override
    public Trigger getTrigger() {
        return TriggerBuilder.newTrigger()
                .startNow()
                .withSchedule(PATTERN)
                .build();
    }

    private void initBosses() {
        bosses.add(new EpicBoss() {
            @Override
            public boolean check() {
                return AntharasManager.getState().getState() == EpicBossState.State.NOTSPAWN;
            }

            @Override
            public String name() {
                return "Antharas";
            }
        });

        bosses.add(new EpicBoss() {
            @Override
            public boolean check() {
                return ValakasManager.getState().getState() == EpicBossState.State.NOTSPAWN;
            }

            @Override
            public String name() {
                return "Valakas";
            }
        });

        bosses.add(new EpicBoss() {
            @Override
            public boolean check() {
                return BaiumManager.getState().getState() == EpicBossState.State.NOTSPAWN;
            }

            @Override
            public String name() {
                return "Baium";
            }
        });

        bosses.add(new EpicBoss() {
            @Override
            public boolean check() {
                return BelethManager.checkBossSpawnCond();
            }

            @Override
            public String name() {
                return "Beleth";
            }
        });
    }

    @Override
    public void init(Scheduler scheduler) {
        initBosses();
        super.init(scheduler);
    }

    private abstract class EpicBoss {
        private boolean isAnnounced = false;

        public abstract boolean check();

        public abstract String name();

        public void tryAnnounce() {
            if (!isAnnounced && check()) {
                announce();
                isAnnounced = true;
            }
        }

        private void announce() {
            AnnouncementUtils.announceWithSplash("Эпический Босс " + name() + " явился в этот мир!",
                    "Epic Boss " + name() + " is alive now!");
        }
    }
}
