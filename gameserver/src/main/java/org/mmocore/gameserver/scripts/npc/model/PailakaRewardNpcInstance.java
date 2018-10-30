package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author Magister
 * @date 10/12/2014
 */
public class PailakaRewardNpcInstance extends NpcInstance {
    private static final int pa36_reward_npc = 32510;
    private static final int pa61_reward_npc = 32511;
    private static final int pa73_reward_npc = 32512;
    private static final int pailaka36_instance_id = 43;
    private static final int pailaka61_instance_id = 44;
    private static final int pailaka73_instance_id = 45;

    public PailakaRewardNpcInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        if (player.getActiveReflection().getInstancedZoneId() == pailaka36_instance_id) {
            if (player.getQuestState(128) != null && player.getQuestState(128).isCompleted()) {
                if (getNpcId() == pa36_reward_npc) {
                    showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_128_PailakaSongofIceandFire/pa36_reward_npc_q0128_02.htm");
                }
            } else {
                super.showChatWindow(player, val);
            }
        } else if (player.getActiveReflection().getInstancedZoneId() == pailaka61_instance_id) {
            if (player.getQuestState(129) != null && player.getQuestState(129).isCompleted()) {
                if (getNpcId() == pa61_reward_npc) {
                    showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_129_PailakaDevilsLegacy/pa61_reward_npc_q0129_02.htm");
                }
            } else {
                super.showChatWindow(player, val);
            }
        } else if (player.getActiveReflection().getInstancedZoneId() == pailaka73_instance_id) {
            if (player.getQuestState(144) != null && player.getQuestState(144).isCompleted()) {
                if (getNpcId() == pa73_reward_npc) {
                    showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_144_PailakaInjuredDragon/pa73_reward_npc_q0144_02.htm");
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
        super.onBypassFeedback(player, command);
    }
}