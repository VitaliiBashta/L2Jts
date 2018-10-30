package org.mmocore.gameserver.scripts.npc.model.pts.hellbound;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author KilRoy
 */
public class CaravanBudenkaInstance extends NpcInstance {
    private static final int caravan_friendship_1 = 9850;
    private static final int caravan_friendship_2 = 9851;
    private static final int caravan_friendship_3 = 9852;

    public CaravanBudenkaInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player talker, int val, Object... arg) {
        if (ItemFunctions.getItemCount(talker, caravan_friendship_1) < 1 && ItemFunctions.getItemCount(talker, caravan_friendship_2) < 1 && ItemFunctions.getItemCount(talker, caravan_friendship_3) < 1) {
            showChatWindow(talker, "pts/hellbound/caravan_budenka001.htm");
        } else if (ItemFunctions.getItemCount(talker, caravan_friendship_1) >= 1 && ItemFunctions.getItemCount(talker, caravan_friendship_2) < 1 && ItemFunctions.getItemCount(talker, caravan_friendship_3) < 1) {
            showChatWindow(talker, "pts/hellbound/caravan_budenka002.htm");
        } else if (ItemFunctions.getItemCount(talker, caravan_friendship_2) >= 1 || ItemFunctions.getItemCount(talker, caravan_friendship_3) >= 1) {
            showChatWindow(talker, "pts/hellbound/caravan_budenka003.htm");
        }
    }
}