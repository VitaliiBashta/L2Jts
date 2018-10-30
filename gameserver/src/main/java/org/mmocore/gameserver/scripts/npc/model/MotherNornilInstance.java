package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author KilRoy
 * for 236 quest Seed of Chaos
 */
public final class MotherNornilInstance extends NpcInstance {
    private static final int NPC_ID_MOTHER_NORNIL = 32239;
    private static final int NPC_ID_RODEN = 32237;
    private static final int INSTANCE_ID = 12;

    public MotherNornilInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        if (player.getActiveReflection().getInstancedZoneId() == INSTANCE_ID) {
            final QuestState quest = player.getQuestState(236);
            if (quest != null && quest.isCompleted()) {
                if (getNpcId() == NPC_ID_MOTHER_NORNIL) {
                    showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_236_SeedsOfChaos/mother_nornil_q0236002.htm");
                } else if (getNpcId() == NPC_ID_RODEN) {
                    showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_236_SeedsOfChaos/roden_q0236002.htm");
                }
            } else {
                super.showChatWindow(player, val);
            }
        } else {
            super.showChatWindow(player, val);
        }
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.equalsIgnoreCase("request_exitInstance")) {
            if (player.getActiveReflection().getInstancedZoneId() == INSTANCE_ID) {
                final QuestState quest = player.getQuestState(236);
                if (quest != null && quest.isCompleted()) {
                    reflection.collapse();
                    showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_236_SeedsOfChaos/mother_nornil_q0236003.htm");
                }
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}