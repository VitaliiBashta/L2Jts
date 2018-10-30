package org.mmocore.gameserver.model.entity.olympiad;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.utils.AnnouncementUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CompStartTask extends RunnableImpl {
    private static final Logger _log = LoggerFactory.getLogger(CompStartTask.class);

    @Override
    public void runImpl() {
        if (Olympiad.isOlympiadEnd()) {
            return;
        }

        if (!Olympiad._isDoubleLoad) {
            Olympiad._isDoubleLoad = true;
            Olympiad._manager = new OlympiadManager();
            Olympiad._inCompPeriod = true;

            new Thread(Olympiad._manager).start();

            ThreadPoolManager.getInstance().schedule(new CompEndTask(), Olympiad.getMillisToCompEnd());

            AnnouncementUtils.announceToAll(SystemMsg.SHARPEN_YOUR_SWORDS_TIGHTEN_THE_STITCHING_IN_YOUR_ARMOR_AND_MAKE_HASTE_TO_A_GRAND_OLYMPIAD_MANAGER__BATTLES_IN_THE_GRAND_OLYMPIAD_GAMES_ARE_NOW_TAKING_PLACE);
            _log.info("Olympiad System: Olympiad Game Started");
        }
    }
}