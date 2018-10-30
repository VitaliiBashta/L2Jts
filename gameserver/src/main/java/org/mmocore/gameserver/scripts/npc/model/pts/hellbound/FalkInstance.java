package org.mmocore.gameserver.scripts.npc.model.pts.hellbound;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author KilRoy
 */
public class FalkInstance extends NpcInstance {
    private static final int caravan_friendship_1 = 9850;
    private static final int caravan_friendship_2 = 9851;
    private static final int caravan_friendship_3 = 9852;
    private static final int permit_of_darion = 9674;

    public FalkInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player talker, int val, Object... arg) {
        if (ItemFunctions.getItemCount(talker, caravan_friendship_1) < 1 && ItemFunctions.getItemCount(talker, caravan_friendship_2) < 1 && ItemFunctions.getItemCount(talker, caravan_friendship_3) < 1) {
            showChatWindow(talker, "pts/hellbound/falk001.htm");
        } else if (ItemFunctions.getItemCount(talker, caravan_friendship_1) >= 1 || ItemFunctions.getItemCount(talker, caravan_friendship_2) >= 1 || ItemFunctions.getItemCount(talker, caravan_friendship_3) >= 1) {
            showChatWindow(talker, "pts/hellbound/falk001a.htm");
        }
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (player == null) {
            return;
        }

        if (command.startsWith("menu_select?ask=-1006&")) {
            if (command.endsWith("reply=1")) {
                if (ItemFunctions.getItemCount(player, caravan_friendship_1) < 1 && ItemFunctions.getItemCount(player, caravan_friendship_2) < 1 && ItemFunctions.getItemCount(player, caravan_friendship_3) < 1) {
                    showChatWindow(player, "pts/hellbound/falk002.htm");
                } else if (ItemFunctions.getItemCount(player, caravan_friendship_1) >= 1 || ItemFunctions.getItemCount(player, caravan_friendship_2) >= 1 || ItemFunctions.getItemCount(player, caravan_friendship_3) >= 1) {
                    showChatWindow(player, "pts/hellbound/falk001a.htm");
                }
            } else if (command.endsWith("reply=2")) {
                if (ItemFunctions.getItemCount(player, caravan_friendship_1) < 1 && ItemFunctions.getItemCount(player, caravan_friendship_2) < 1 && ItemFunctions.getItemCount(player, caravan_friendship_3) < 1) {
                    if (ItemFunctions.getItemCount(player, permit_of_darion) >= 20) {
                        ItemFunctions.addItem(player, caravan_friendship_1, 1);
                        ItemFunctions.removeItem(player, permit_of_darion, 20);
                        showChatWindow(player, "pts/hellbound/falk002a.htm");
                    } else {
                        showChatWindow(player, "pts/hellbound/falk002b.htm");
                    }
                }
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}