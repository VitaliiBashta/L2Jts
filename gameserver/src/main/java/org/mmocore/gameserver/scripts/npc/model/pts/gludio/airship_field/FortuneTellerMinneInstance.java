package org.mmocore.gameserver.scripts.npc.model.pts.gludio.airship_field;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author Mangol
 */
public class FortuneTellerMinneInstance extends NpcInstance {
    private static final String fnNotHaveAdena = "pts/gludio/airship_field/fortune_teller_minne004.htm";
    private static final String fnFortune = "pts/gludio/airship_field/fortune_teller_minne003.htm";
    private static final int fee_for_fortune = 1000;

    public FortuneTellerMinneInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }
        if (command.startsWith("menu_select?ask=-2013&")) {
            if (command.endsWith("reply=1")) {
                if (ItemFunctions.getItemCount(player, 57) >= fee_for_fortune) {
                    int i0 = Rnd.get(386);
                    int i1 = 1800309 + i0;
                    final HtmlMessage message = new HtmlMessage(this);
                    ItemFunctions.removeItem(player, 57, fee_for_fortune);
                    message.setFile(fnFortune);
                    message.replace("<?fortune_number?>", NpcString.valueOf(i1));
                    player.sendPacket(message);
                } else {
                    showChatWindow(player, fnNotHaveAdena);
                }
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}
