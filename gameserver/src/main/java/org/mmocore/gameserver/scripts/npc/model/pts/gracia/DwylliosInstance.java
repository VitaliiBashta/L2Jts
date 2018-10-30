package org.mmocore.gameserver.scripts.npc.model.pts.gracia;

import org.mmocore.gameserver.manager.SoDManager;
import org.mmocore.gameserver.manager.SoIManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author KilRoy
 */
public class DwylliosInstance extends NpcInstance {
    public DwylliosInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.startsWith("menu_select?ask=-1101&")) {
            if (command.endsWith("reply=1")) {
                final int kill = SoDManager.getTiatKills();
                if (kill == 0) {
                    showChatWindow(player, "pts/gracia/dwyllios005a.htm");
                } else if (kill > 0 && kill < 9) {
                    showChatWindow(player, "pts/gracia/dwyllios005b.htm");
                } else if (kill > 9) {
                    showChatWindow(player, "pts/gracia/dwyllios005c.htm");
                }
            } else if (command.endsWith("reply=2")) {
                final int stage = SoIManager.getCurrentStage();
                if (stage == 1) {
                    showChatWindow(player, "pts/gracia/dwyllios006a.htm");
                } else if (stage == 2) {
                    showChatWindow(player, "pts/gracia/dwyllios006b.htm");
                } else if (stage == 3) {
                    showChatWindow(player, "pts/gracia/dwyllios006c.htm");
                } else if (stage == 4) {
                    showChatWindow(player, "pts/gracia/dwyllios006e.htm");
                } else if (stage == 5) {
                    showChatWindow(player, "pts/gracia/dwyllios006f.htm");
                }
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}