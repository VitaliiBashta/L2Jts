package org.mmocore.gameserver.scripts.npc.model.pts.hellbound;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author KilRoy
 */
public class RomanticCatMingmingInstance extends NpcInstance {
    private static final int king_of_cat = 14836;

    public RomanticCatMingmingInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player talker, int val, Object... arg) {
        if (talker.getServitor() != null && talker.getServitor().isSummon() && talker.getServitor().getNpcId() == king_of_cat) {
            showChatWindow(talker, "pts/hellbound/romantic_cat_mingming002.htm");
        } else {
            showChatWindow(talker, "pts/hellbound/romantic_cat_mingming001.htm");
        }
    }
}