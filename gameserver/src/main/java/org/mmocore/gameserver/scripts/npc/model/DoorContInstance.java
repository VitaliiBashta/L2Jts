package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author Magister
 * @version 1.0
 * @date 21/01/2015
 * @memo Используется для квеста: _179_IntoTheLargeCavern.
 */
public final class DoorContInstance extends NpcInstance {
    private static final long serialVersionUID = -6482643964215706541L;
    private static final int future_door_cont = 32262;
    private static final int past_door_cont = 32260;
    private static final int present_door_cont = 32261;
    private static final int zone_id = 11;

    public DoorContInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        if (player.getActiveReflection().getInstancedZoneId() == zone_id) {
            final QuestState quest = player.getQuestState(179);
            if (quest != null && quest.isCompleted()) {
                if (getNpcId() == future_door_cont) {
                    showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_179_IntoTheLargeCavern/future_door_cont_q0179_02.htm");
                } else if (getNpcId() == past_door_cont) {
                    showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_179_IntoTheLargeCavern/past_door_cont_q0179_02.htm");
                } else if (getNpcId() == present_door_cont) {
                    showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_179_IntoTheLargeCavern/present_door_cont_q0179_02.htm");
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
        if (command.equalsIgnoreCase("menu_select?ask=179&reply=6")) {
            if (player.getActiveReflection().getInstancedZoneId() == zone_id) {
                final QuestState quest = player.getQuestState(179);
                if (quest != null && quest.isCompleted()) {
                    player.getReflection().openDoor(16200016);
                    player.sendPacket(new ExShowScreenMessage(NpcString.CHAOS_AND_TIME, 1500, ScreenMessageAlign.TOP_CENTER, true));
                }
            }
        } else if (command.equalsIgnoreCase("menu_select?ask=179&reply=5")) {
            if (player.getActiveReflection().getInstancedZoneId() == zone_id) {
                final QuestState quest = player.getQuestState(179);
                if (quest != null && quest.isCompleted()) {
                    player.getReflection().openDoor(16200014);
                    player.sendPacket(new ExShowScreenMessage(NpcString.THE_VEILED_CREATOR, 1500, ScreenMessageAlign.TOP_CENTER, true));
                }
            }
        } else if (command.equalsIgnoreCase("menu_select?ask=179&reply=7")) {
            if (player.getActiveReflection().getInstancedZoneId() == zone_id) {
                final QuestState quest = player.getQuestState(179);
                if (quest != null && quest.isCompleted()) {
                    player.getReflection().openDoor(16200015);
                    player.sendPacket(new ExShowScreenMessage(NpcString.THE_CONSPIRACY_OF_THE_ANCIENT_RACE, 1500, ScreenMessageAlign.TOP_CENTER, true));
                }
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}