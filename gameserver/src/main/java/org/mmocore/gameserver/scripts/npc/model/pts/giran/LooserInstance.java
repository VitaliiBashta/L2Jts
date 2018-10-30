package org.mmocore.gameserver.scripts.npc.model.pts.giran;

import org.mmocore.gameserver.manager.SoIManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author : Mangol
 */
public class LooserInstance extends NpcInstance {
    private static final String fnHi = "pts/giran/looser_of_gracia001.htm";
    private static final String fnAdenFort = "pts/giran/looser_of_gracia002.htm";
    private static final String fnSeedState = "pts/giran/looser_of_gracia003.htm";
    private static final String fnNotHaveAdena = "pts/giran/looser_of_gracia004.htm";
    private static final String fnLowLevel = "pts/giran/looser_of_gracia005.htm";

    public LooserInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        showChatWindow(player, fnHi);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }
        if (command.startsWith("menu_select?ask=-1425&")) {
            if (command.endsWith("reply=1")) {
                if (player.getLevel() < 75) {
                    showChatWindow(player, fnLowLevel);
                    return;
                }
                if (player.getAdena() >= 150000) {
                    player.reduceAdena(150000, true);
                    player.teleToLocation(-149406, 255247, -85);
                } else {
                    showChatWindow(player, fnNotHaveAdena);
                }
            } else if (command.endsWith("reply=2")) {
                // FIXME?: Возможно что то неверно из стрингов
                final HtmlMessage html = new HtmlMessage(this);
                html.setFile(fnSeedState);
                int i0 = SoIManager.getCurrentStage();
                int i1 = 0;
                if (i0 <= 1) {
                    i1 = 1800711;
                } else if (i0 == 2) {
                    i1 = 1800712;
                } else if (i0 == 3) {
                    i1 = 1800713;
                } else if (i0 == 4) {
                    i1 = 1800714;
                } else if (i0 == 5) {
                    i1 = 1800715;
                } else if (i0 >= 6) {
                    i1 = 1800716;
                }
                html.replace("<?stat_unde?>", NpcString.valueOf(i1));
                int i2 = 0;
                if (i0 <= 1) {
                    i1 = 1800708;
                } else if (i0 == 2) {
                    i1 = 1800709;
                } else if (i0 >= 3) {
                    i1 = 1800710;
                }
                html.replace("<?stat_dest?>", NpcString.valueOf(i1));
                player.sendPacket(html);
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}
