package org.mmocore.gameserver.scripts.npc.model.pts.hellbound;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author KilRoy
 */
public class HudeInstance extends NpcInstance {
    private static final int caravan_friendship_1 = 9850;
    private static final int caravan_friendship_2 = 9851;
    private static final int caravan_friendship_3 = 9852;
    private static final int poison_sting_of_scorpion = 10012; //
    private static final int mark_of_betrayer = 9676;
    private static final int magic_energy = 9681;
    private static final int conservation_magic_energy = 9682;
    private static final int map_of_hellbound = 9994;

    public HudeInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player talker, int val, Object... arg) {
        if (ItemFunctions.getItemCount(talker, caravan_friendship_1) < 1 && ItemFunctions.getItemCount(talker, caravan_friendship_2) < 1 && ItemFunctions.getItemCount(talker, caravan_friendship_3) < 1) {
            showChatWindow(talker, "pts/hellbound/caravan_hude001.htm");
        } else if (ItemFunctions.getItemCount(talker, caravan_friendship_1) >= 1 && ItemFunctions.getItemCount(talker, caravan_friendship_2) < 1 && ItemFunctions.getItemCount(talker, caravan_friendship_3) < 1) {
            showChatWindow(talker, "pts/hellbound/caravan_hude003.htm");
        } else if (ItemFunctions.getItemCount(talker, caravan_friendship_2) >= 1 && ItemFunctions.getItemCount(talker, caravan_friendship_3) < 1) {
            showChatWindow(talker, "pts/hellbound/caravan_hude005.htm");
        } else if (ItemFunctions.getItemCount(talker, caravan_friendship_3) >= 1) {
            showChatWindow(talker, "pts/hellbound/caravan_hude007.htm");
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
            if (command.endsWith("reply=2")) {
                if (ItemFunctions.getItemCount(player, caravan_friendship_1) >= 1 && ItemFunctions.getItemCount(player, caravan_friendship_2) < 1 && ItemFunctions.getItemCount(player, caravan_friendship_3) < 1) {
                    if (ItemFunctions.getItemCount(player, poison_sting_of_scorpion) >= 60 && ItemFunctions.getItemCount(player, mark_of_betrayer) >= 30) {
                        ItemFunctions.addItem(player, caravan_friendship_2, 1);
                        ItemFunctions.removeItem(player, caravan_friendship_1, 1);
                        ItemFunctions.removeItem(player, poison_sting_of_scorpion, 60);
                        ItemFunctions.removeItem(player, mark_of_betrayer, 30);
                        showChatWindow(player, "pts/hellbound/caravan_hude004a.htm");
                    } else {
                        showChatWindow(player, "pts/hellbound/caravan_hude004b.htm");
                    }
                }
            } else if (command.endsWith("reply=3")) {
                if (ItemFunctions.getItemCount(player, caravan_friendship_2) >= 1 && ItemFunctions.getItemCount(player, caravan_friendship_3) < 1) {
                    if (ItemFunctions.getItemCount(player, magic_energy) >= 56 && ItemFunctions.getItemCount(player, conservation_magic_energy) >= 14) {
                        ItemFunctions.addItem(player, caravan_friendship_3, 1);
                        ItemFunctions.addItem(player, map_of_hellbound, 1);
                        ItemFunctions.removeItem(player, caravan_friendship_2, 1);
                        ItemFunctions.removeItem(player, magic_energy, 56);
                        ItemFunctions.removeItem(player, conservation_magic_energy, 14);
                        showChatWindow(player, "pts/hellbound/caravan_hude006a.htm");
                    } else {
                        showChatWindow(player, "pts/hellbound/caravan_hude006b.htm");
                    }
                }
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}