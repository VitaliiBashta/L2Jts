package org.mmocore.gameserver.scripts.npc.model.pts.beastfarm;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author : Mangol
 */
public class BeastfarmQuestInstance extends NpcInstance {

    public BeastfarmQuestInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }
        if (command.startsWith("menu_select?ask=-191&")) {
            if (command.endsWith("reply=1")) {
                final QuestState st = player.getQuestState(20);
                int GetMemoState = 0;
                if (st != null) {
                    GetMemoState = st.getInt("givemelove");
                }
                int training_whip = 15473;
                if (ItemFunctions.getItemCount(player, training_whip) >= 1) {
                    showChatWindow(player, "pts/beastfarm/beast_herder_tunatun002.htm");
                } else if (ItemFunctions.getItemCount(player, training_whip) < 1 && GetMemoState != 1 && player.getLevel() < 82) {
                    showChatWindow(player, "pts/beastfarm/beast_herder_tunatun003.htm");
                } else if (ItemFunctions.getItemCount(player, training_whip) < 1 && (GetMemoState == 1 || player.getLevel() >= 82)) {
                    showChatWindow(player, "pts/beastfarm/beast_herder_tunatun004.htm");
                    ItemFunctions.addItem(player, training_whip, 1);
                }
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}
