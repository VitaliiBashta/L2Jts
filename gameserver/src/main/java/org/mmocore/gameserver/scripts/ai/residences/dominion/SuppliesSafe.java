package org.mmocore.gameserver.scripts.ai.residences.dominion;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.listener.actor.player.OnPlayerEnterListener;
import org.mmocore.gameserver.manager.QuestManager;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeEvent;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.PlayerListenerList;
import org.mmocore.gameserver.scripts.quests._730_ProtectTheSuppliesSafe;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.world.GameObjectsStorage;

/**
 * @author VISTALL
 * @date 3:31/23.06.2011
 */
public class SuppliesSafe extends DefaultAI {
    private static final TIntObjectMap<NpcString[]> MESSAGES = new TIntObjectHashMap<>(9);

    static {
        MESSAGES.put(81, new NpcString[]{NpcString.PROTECT_THE_SUPPLIES_SAFE_OF_GLUDIO, NpcString.THE_SUPPLIES_SAFE_OF_GLUDIO_HAS_BEEN_DESTROYED});
        MESSAGES.put(82, new NpcString[]{NpcString.PROTECT_THE_SUPPLIES_SAFE_OF_DION, NpcString.THE_SUPPLIES_SAFE_OF_DION_HAS_BEEN_DESTROYED});
        MESSAGES.put(83, new NpcString[]{NpcString.PROTECT_THE_SUPPLIES_SAFE_OF_GIRAN, NpcString.THE_SUPPLIES_SAFE_OF_GIRAN_HAS_BEEN_DESTROYED});
        MESSAGES.put(84, new NpcString[]{NpcString.PROTECT_THE_SUPPLIES_SAFE_OF_OREN, NpcString.THE_SUPPLIES_SAFE_OF_OREN_HAS_BEEN_DESTROYED});
        MESSAGES.put(85, new NpcString[]{NpcString.PROTECT_THE_SUPPLIES_SAFE_OF_ADEN, NpcString.THE_SUPPLIES_SAFE_OF_ADEN_HAS_BEEN_DESTROYED});
        MESSAGES.put(86, new NpcString[]{NpcString.PROTECT_THE_SUPPLIES_SAFE_OF_INNADRIL, NpcString.THE_SUPPLIES_SAFE_OF_INNADRIL_HAS_BEEN_DESTROYED});
        MESSAGES.put(87, new NpcString[]{NpcString.PROTECT_THE_SUPPLIES_SAFE_OF_GODDARD, NpcString.THE_SUPPLIES_SAFE_OF_GODDARD_HAS_BEEN_DESTROYED});
        MESSAGES.put(88, new NpcString[]{NpcString.PROTECT_THE_SUPPLIES_SAFE_OF_RUNE, NpcString.THE_SUPPLIES_SAFE_OF_RUNE_HAS_BEEN_DESTROYED});
        MESSAGES.put(89, new NpcString[]{NpcString.PROTECT_THE_SUPPLIES_SAFE_OF_SCHUTTGART, NpcString.THE_SUPPLIES_SAFE_OF_SCHUTTGART_HAS_BEEN_DESTROYED});
    }

    private final OnPlayerEnterListener listener = new OnPlayerEnterListenerImpl();

    public SuppliesSafe(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        return false;
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();

        DominionSiegeEvent siegeEvent = actor.getEvent(DominionSiegeEvent.class);
        if (siegeEvent == null) {
            return;
        }

        boolean first = actor.getParameter("dominion_first_attack", true);
        if (first) {
            actor.setParameter("dominion_first_attack", false);
            NpcString msg = MESSAGES.get(siegeEvent.getId())[0];
            Quest q = QuestManager.getQuest(_730_ProtectTheSuppliesSafe.class);
            final ExShowScreenMessage packet = new ExShowScreenMessage(msg, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER);

            for (Player player : GameObjectsStorage.getPlayers()) {
                if (player.getEvent(DominionSiegeEvent.class) == siegeEvent) {
                    player.sendPacket(packet);

                    QuestState questState = q.newQuestStateAndNotSave(player, Quest.CREATED);
                    questState.setCond(1, false);
                    questState.setStateAndNotSave(Quest.STARTED);
                }
            }
            PlayerListenerList.addGlobal(listener);
        }
    }

    @Override
    public void onEvtAggression(Creature attacker, int d) {
        //
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();
        super.onEvtDead(killer);
        DominionSiegeEvent siegeEvent = actor.getEvent(DominionSiegeEvent.class);
        if (siegeEvent == null) {
            return;
        }

        NpcString msg = MESSAGES.get(siegeEvent.getId())[1];
        final ExShowScreenMessage packet = new ExShowScreenMessage(msg, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER);

        for (Player player : GameObjectsStorage.getPlayers()) {
            if (player.getEvent(DominionSiegeEvent.class) == siegeEvent) {
                player.sendPacket(packet);

                QuestState questState = player.getQuestState(730);
                if (questState != null) {
                    questState.abortQuest();
                }
            }
        }

        Player player = killer.getPlayer();
        if (player == null) {
            return;
        }

        if (player.getParty() == null) {
            DominionSiegeEvent siegeEvent2 = player.getEvent(DominionSiegeEvent.class);
            if (siegeEvent2 == null || siegeEvent2 == siegeEvent) {
                return;
            }
            siegeEvent2.addReward(player, DominionSiegeEvent.STATIC_BADGES, 5);
        } else {
            for (Player $member : player.getParty()) {
                if ($member.isInRange(player, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE)) {
                    DominionSiegeEvent siegeEvent2 = $member.getEvent(DominionSiegeEvent.class);
                    if (siegeEvent2 == null || siegeEvent2 == siegeEvent) {
                        continue;
                    }
                    siegeEvent2.addReward($member, DominionSiegeEvent.STATIC_BADGES, 5);
                }
            }
        }
    }

    @Override
    public void onEvtDeSpawn() {
        super.onEvtDeSpawn();

        PlayerListenerList.removeGlobal(listener);
    }

    private class OnPlayerEnterListenerImpl implements OnPlayerEnterListener {
        @Override
        public void onPlayerEnter(Player player) {
            NpcInstance actor = getActor();
            DominionSiegeEvent siegeEvent = actor.getEvent(DominionSiegeEvent.class);
            if (siegeEvent == null) {
                return;
            }

            if (player.getEvent(DominionSiegeEvent.class) != siegeEvent) {
                return;
            }

            Quest q = QuestManager.getQuest(_730_ProtectTheSuppliesSafe.class);

            QuestState questState = q.newQuestStateAndNotSave(player, Quest.CREATED);
            questState.setCond(1, false);
            questState.setStateAndNotSave(Quest.STARTED);
        }
    }
}
