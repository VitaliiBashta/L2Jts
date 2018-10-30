package org.mmocore.gameserver.scripts.npc.model.pts.hellbound;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.threading.ThreadPoolManager;
import org.mmocore.gameserver.manager.HellboundManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author KilRoy
 */
public class IncastleNativeInstance extends NpcInstance {
    private static final int permit_of_darion = 9674;

    public IncastleNativeInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        setUndying(false);
    }

    @Override
    public void showChatWindow(Player talker, int val, Object... arg) {
        final int i0 = HellboundManager.getHellboundLevel();

        if (i0 < 1) {
            showChatWindow(talker, "pts/hellbound/incastle_native001a.htm");
        } else if (i0 >= 1 && i0 <= 100) {
            showChatWindow(talker, "pts/hellbound/incastle_native001.htm");
        } else {
            showChatWindow(talker, "pts/hellbound/incastle_native001b.htm");
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
                final long i1 = ItemFunctions.getItemCount(player, permit_of_darion);
                if (i1 >= 5) {
                    HellboundManager.addConfidence(100);
                    ItemFunctions.removeItem(player, permit_of_darion, 5);
                    showChatWindow(player, "pts/hellbound/incastle_native002.htm");
                    final NpcInstance actor = this;
                    ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
                        @Override
                        public void runImpl() {
                            actor.suicide(null);
                            actor.endDecayTask();
                        }
                    }, 3000);
                } else {
                    showChatWindow(player, "pts/hellbound/incastle_native002a.htm");
                }
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}