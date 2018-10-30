package org.mmocore.gameserver.model.entity.olympiad;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.configuration.config.OlympiadConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

public class WeeklyTask extends RunnableImpl {
    private static final Logger _log = LoggerFactory.getLogger(WeeklyTask.class);

    @Override
    public void runImpl() {
        Olympiad.doWeekTasks();
        _log.info("Olympiad System: Added weekly points to nobles");

        final Calendar nextChange = Calendar.getInstance();
        Olympiad._nextWeeklyChange = nextChange.getTimeInMillis() + OlympiadConfig.ALT_OLY_WPERIOD;
    }
}