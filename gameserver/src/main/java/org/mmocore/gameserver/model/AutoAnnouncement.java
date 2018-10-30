package org.mmocore.gameserver.model;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.utils.AnnouncementUtils;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Java-man
 */
public class AutoAnnouncement extends RunnableImpl {
    private Future<?> task;
    private Announcement announcement;
    private int limit;

    private AutoAnnouncement(final Announcement announcement) {
        this.announcement = announcement;
        this.limit = announcement.getLimit();
    }

    public static AutoAnnouncement createAutoAnnouncement(final Announcement announcement) {
        return new AutoAnnouncement(announcement);
    }

    @Override
    public void runImpl() throws Exception {
        start();
    }

    public void start() {
        if (limit == 0 || announcement == null) {
            stop();
            return;
        }
        AnnouncementUtils.announceToAll(announcement.getMessage());
        limit--;
    }

    public void stop() {
        if (task != null) {
            task.cancel(true);
            task = null;
        }
        announcement = null;
        limit = -1;
    }

    public Announcement getAnnouncement() {
        return announcement;
    }

    public void createTask() {
        if (task != null) {
            task.cancel(true);
            task = null;
        }
        task = ThreadPoolManager.getInstance().scheduleAtFixedRate(this, announcement.getInitialDelay(), announcement.getDelay(), TimeUnit.SECONDS);
    }
}
