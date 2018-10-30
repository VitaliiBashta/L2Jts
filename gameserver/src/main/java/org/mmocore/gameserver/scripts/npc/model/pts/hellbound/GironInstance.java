package org.mmocore.gameserver.scripts.npc.model.pts.hellbound;

import org.mmocore.gameserver.manager.HellboundManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author KilRoy
 */
public class GironInstance extends NpcInstance {
    public GironInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player talker, int val, Object... arg) {
        final int i0 = HellboundManager.getHellboundLevel();

        if (i0 == 0) {
            showChatWindow(talker, "pts/hellbound/giron001.htm");
        } else if (i0 == 1) {
            showChatWindow(talker, "pts/hellbound/giron001a.htm");
        } else if (i0 == 2) {
            showChatWindow(talker, "pts/hellbound/giron001b.htm");
        } else if (i0 == 3) {
            showChatWindow(talker, "pts/hellbound/giron001c.htm");
        } else if (i0 == 4) {
            showChatWindow(talker, "pts/hellbound/giron001h.htm");
        } else if (i0 == 5) {
            showChatWindow(talker, "pts/hellbound/giron001d.htm");
        } else if (i0 == 6) {
            showChatWindow(talker, "pts/hellbound/giron001i.htm");
        } else if (i0 == 7) {
            showChatWindow(talker, "pts/hellbound/giron001e.htm");
        } else if (i0 == 8) {
            showChatWindow(talker, "pts/hellbound/giron001f.htm");
        } else if (i0 == 9) {
            showChatWindow(talker, "pts/hellbound/giron001g.htm");
        } else if (i0 == 10) {
            showChatWindow(talker, "pts/hellbound/giron001j.htm");
        } else if (i0 == 11) {
            showChatWindow(talker, "pts/hellbound/giron001k.htm");
        }
    }
}