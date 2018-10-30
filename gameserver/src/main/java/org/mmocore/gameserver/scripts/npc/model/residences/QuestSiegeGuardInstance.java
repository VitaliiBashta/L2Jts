package org.mmocore.gameserver.scripts.npc.model.residences;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestEventType;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.npc.AggroList;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author VISTALL
 * @date 19:28/23.06.2011
 */
public class QuestSiegeGuardInstance extends SiegeGuardInstance {
    public QuestSiegeGuardInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onDeath(Creature lastAttacker) {
        super.onDeath(lastAttacker);

        Player killer = lastAttacker.getPlayer();
        if (killer == null) {
            return;
        }

        Map<Playable, AggroList.HateInfo> aggroMap = getAggroList().getPlayableMap();

        Quest[] quests = getTemplate().getEventQuests(QuestEventType.MOB_KILLED_WITH_QUEST);
        if (quests != null && quests.length > 0) {
            List<Player> players = null; // массив с игроками, которые могут быть заинтересованы в квестах
            if (isRaid() && AllSettingsConfig.ALT_NO_LASTHIT) // Для альта на ластхит берем всех игроков вокруг
            {
                players = new ArrayList<Player>();
                for (Playable pl : aggroMap.keySet()) {
                    if (!pl.isDead() && (isInRangeZ(pl, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE) || killer.isInRangeZ(pl, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE))) {
                        players.add(pl.getPlayer());
                    }
                }
            } else if (killer.getParty() != null && killer.getParty().getCommandChannel() != null) // если киллер в ЦЦ - берем всю цц
            {
                players = new ArrayList<>(killer.getParty().getCommandChannel().getMemberCount());
                for (Party p : killer.getParty().getCommandChannel().getParties())
                    for (Player pl : p.getPartyMembers())
                        if (!pl.isDead() && (isInRangeZ(pl, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE * 2) || killer.isInRangeZ(pl, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE * 2)))
                            players.add(pl);
            } else if (killer.getParty() != null) // если киллер в пати без ЦЦ
            {
                players = new ArrayList<>(killer.getParty().getMemberCount());
                for (Player pl : killer.getParty().getPartyMembers()) {
                    if (!pl.isDead() && (isInRangeZ(pl, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE) || killer.isInRangeZ(pl, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE))) {
                        players.add(pl);
                    }
                }
            }

            for (Quest quest : quests) {
                Player toReward = killer;
                if (quest.getParty() != Quest.PARTY_NONE && players != null) {
                    if (isRaid() || quest.getParty() == Quest.PARTY_ALL || quest.getParty() == Quest.PARTY_CC) // если цель рейд или квест для всей пати награждаем всех участников
                    {
                        for (Player pl : players) {
                            QuestState qs = pl.getQuestState(quest);
                            if (qs != null && !qs.isCompleted()) {
                                quest.notifyKill(this, qs);
                            }
                        }
                        toReward = null;
                    } else { // иначе выбираем одного
                        List<Player> interested = new ArrayList<Player>(players.size());
                        for (Player pl : players) {
                            QuestState qs = pl.getQuestState(quest);
                            if (qs != null && !qs.isCompleted()) // из тех, у кого взят квест
                            {
                                interested.add(pl);
                            }
                        }

                        if (interested.isEmpty()) {
                            continue;
                        }

                        toReward = interested.get(Rnd.get(interested.size()));
                        if (toReward == null) {
                            toReward = killer;
                        }
                    }
                }

                if (toReward != null) {
                    QuestState qs = toReward.getQuestState(quest.getId());
                    if (qs != null && !qs.isCompleted()) {
                        quest.notifyKill(this, qs);
                    }
                }
            }
        }
    }
}
