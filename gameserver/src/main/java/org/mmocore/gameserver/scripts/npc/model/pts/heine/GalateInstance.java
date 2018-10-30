package org.mmocore.gameserver.scripts.npc.model.pts.heine;

import org.mmocore.gameserver.manager.HellboundManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author KilRoy
 */
public class GalateInstance extends NpcInstance {
    public GalateInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        if (HellboundManager.getHellboundLevel() == 0) {
            if (player.isQuestCompleted(133)) {
                showChatWindow(player, "pts/heine/galate002.htm");
            } else {
                showChatWindow(player, "pts/heine/galate001.htm");
            }
        } else {
            if (player.isQuestCompleted(133)) {
                showChatWindow(player, "pts/heine/galate002c.htm");
            } else {
                showChatWindow(player, "pts/heine/galate001a.htm");
            }
        }
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        final int hellboundLevel = HellboundManager.getHellboundLevel();

        if (command.startsWith("galate_reply_1")) {
            if (hellboundLevel == 0) {
                showChatWindow(player, "pts/heine/galate003a.htm");
            } else {
                showChatWindow(player, "pts/heine/galate003b.htm");
            }
        } else if (command.startsWith("galate_reply_2")) {
            if (hellboundLevel == 0) {
                showChatWindow(player, "pts/heine/galate003c.htm");
            } else {
                showChatWindow(player, "pts/heine/galate003b.htm");
            }
        } else if (command.startsWith("galate_reply_11")) {
            final long count = ItemFunctions.getItemCount(player, 9693);
            if (count > 0) {
                ItemFunctions.removeItem(player, 9693, count);
                HellboundManager.addConfidence(count * 10);
                showChatWindow(player, "pts/heine/galate003d.htm");
            } else {
                showChatWindow(player, "pts/heine/galate003e.htm");
            }
        } else if (command.startsWith("galate_reply_12")) {
            final long count = ItemFunctions.getItemCount(player, 9695);
            if (count > 0) {
                ItemFunctions.removeItem(player, 9695, count);
                HellboundManager.addConfidence(count * 200);
                showChatWindow(player, "pts/heine/galate003d.htm");
            } else {
                showChatWindow(player, "pts/heine/galate003e.htm");
            }
        } else if (command.startsWith("galate_reply_13")) {
            final long count = ItemFunctions.getItemCount(player, 9696);
            if (count > 0) {
                ItemFunctions.removeItem(player, 9696, count);
                HellboundManager.addConfidence(count * 200);
                showChatWindow(player, "pts/heine/galate003d.htm");
            } else {
                showChatWindow(player, "pts/heine/galate003e.htm");
            }
        } else if (command.startsWith("galate_reply_14")) {
            final long count = ItemFunctions.getItemCount(player, 9697);
            if (count > 0) {
                ItemFunctions.removeItem(player, 9697, count);
                HellboundManager.addConfidence(count * 200);
                showChatWindow(player, "pts/heine/galate003d.htm");
            } else {
                showChatWindow(player, "pts/heine/galate003e.htm");
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}