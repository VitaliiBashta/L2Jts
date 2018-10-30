package org.mmocore.gameserver.model.entity.olympiad;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.utils.AnnouncementUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CompEndTask extends RunnableImpl {
    private static final Logger _log = LoggerFactory.getLogger(CompEndTask.class);

    @Override
    public void runImpl() {
        if (Olympiad.isOlympiadEnd()) {
            return;
        }

        Olympiad._inCompPeriod = false;
        Olympiad._isDoubleLoad = false;

        try {
            final OlympiadManager manager = Olympiad._manager;

            // Если остались игры, ждем их завершения еще одну минуту
            if (manager != null && !manager.getOlympiadGames().isEmpty()) {
                ThreadPoolManager.getInstance().schedule(new CompEndTask(), 60000);
                return;
            }

            AnnouncementUtils.announceToAll(SystemMsg.MUCH_CARNAGE_HAS_BEEN_LEFT_FOR_THE_CLEANUP_CREW_OF_THE_OLYMPIAD_STADIUM);
            _log.info("Olympiad System: Olympiad Game Ended");
            OlympiadDatabase.save();
        } catch (Exception e) {
            _log.warn("Olympiad System: Failed to save Olympiad configuration:");
            _log.error("", e);
        }
        Olympiad.init();
    }
}