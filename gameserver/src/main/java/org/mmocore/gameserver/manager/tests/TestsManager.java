package org.mmocore.gameserver.manager.tests;

import org.mmocore.gameserver.data.client.holder.QuestDataHolder;
import org.mmocore.gameserver.manager.QuestManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KilRoy
 */
public class TestsManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestsManager.class);
    private static final QuestDataHolder questHolder = QuestDataHolder.getInstance();
    private static int errorCount = 0;

    private TestsManager() {
        LOGGER.info("############### START TEST MODULE ###############");
        startQuestTest();
        if (errorCount == 0)
            LOGGER.info("Not found errors in the test process! :)");
        LOGGER.info("############### END TEST MODULE #################");
    }

    public static TestsManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    // Quest test
    private void startQuestTest() {
        boolean checked;
        for (final int questId : questHolder.getTestQuests().keys()) {
            checked = false;
            if (QuestManager.getQuest(questId) == null) {
                LOGGER.warn("QuestID={}: not allowed. Check thim!", questId);
                checked = true;
            } else {
                if (QuestManager.getQuest(questId).getMinLevel() != questHolder.getTestQuestInfo(questId).getTestMinLevel()) {
                    LOGGER.warn("QuestID={}: MINLevel={} not correct", questId, questHolder.getTestQuestInfo(questId).getTestMinLevel());
                    checked = true;
                }
                if (QuestManager.getQuest(questId).getMaxLevel() != questHolder.getTestQuestInfo(questId).getTestMaxLevel()) {
                    LOGGER.warn("QuestID={}: MAXLevel={} not correct", questId, questHolder.getTestQuestInfo(questId).getTestMaxLevel());
                    checked = true;
                }
            }
            if (checked) {
                LOGGER.warn("-------------------------------------------------------");
                errorCount++;
            }
        }
    }

    private static class LazyHolder {
        private static final TestsManager INSTANCE = new TestsManager();
    }
}