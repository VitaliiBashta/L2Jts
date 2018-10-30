package org.mmocore.gameserver.manager;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.mmocore.gameserver.model.quest.Quest;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class QuestManager {
    private static final Map<String, Quest> QUESTS_BY_NAME = new HashMap<>();
    private static final TIntObjectMap<Quest> QUESTS_BY_ID = new TIntObjectHashMap<>();

    private QuestManager() {
    }

    public static Quest getQuest(final String name) {
        return QUESTS_BY_NAME.get(name);
    }

    public static Quest getQuest(final Class<?> quest) {
        return getQuest(quest.getSimpleName());
    }

    public static Quest getQuest(final int questId) {
        return QUESTS_BY_ID.get(questId);
    }

    public static Quest getQuest2(final String nameOrId) {
        if (QUESTS_BY_NAME.containsKey(nameOrId)) {
            return QUESTS_BY_NAME.get(nameOrId);
        }
        try {
            final int questId = Integer.parseInt(nameOrId);
            return QUESTS_BY_ID.get(questId);
        } catch (Exception e) {
            return null;
        }
    }

    public static void addQuest(final Quest newQuest) {
        QUESTS_BY_NAME.put(newQuest.getName(), newQuest);
        QUESTS_BY_ID.put(newQuest.getId(), newQuest);
    }

    public static Collection<Quest> getQuests() {
        return QUESTS_BY_NAME.values();
    }
}