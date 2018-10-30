package org.mmocore.gameserver.scripts.npc.model.pts.hellbound;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author KilRoy
 */
public class WanderingghostKendalInstance extends NpcInstance {
    private static final int[] occupation = {6, 91, 13, 95};

    public WanderingghostKendalInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player talker, int val, Object... arg) {
        if (ArrayUtils.contains(occupation, talker.getPlayerClassComponent().getClassId().getId())) {
            showChatWindow(talker, "pts/hellbound/wanderingghost_kendal002.htm");
        } else {
            showChatWindow(talker, "pts/hellbound/wanderingghost_kendal001.htm");
        }
    }
}