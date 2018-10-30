package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.scripts.bosses.BelethManager;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.HtmlUtils;
import org.mmocore.gameserver.utils.ItemFunctions;

import java.util.StringTokenizer;

/**
 * @author pchayka
 */

public final class BelethCoffinInstance extends NpcInstance {
    private static final int RING = 10314;

    public BelethCoffinInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        StringTokenizer st = new StringTokenizer(command);
        if (st.nextToken().equals("request_ring")) {
            if (!BelethManager.isRingAvailable() || player.getParty() == null || !player.getParty().isInCommandChannel()
                    || player.getParty().getCommandChannel() != BelethManager.getCC() || player.getParty().getCommandChannel().getGroupLeader() != player) {
                // debug
                try {
                    if (!BelethManager.isRingAvailable())
                        player.sendMessage("Ring is not available!");
                    if (player.getParty() == null)
                        player.sendMessage("Not a group member!");
                    if (!player.getParty().isInCommandChannel())
                        player.sendMessage("Not a channel member!");
                    if (player.getParty().getCommandChannel() != BelethManager.getCC())
                        player.sendMessage("Your Command Channel is not locked by Beleth!");
                    if (player.getParty().getCommandChannel().getGroupLeader() != player)
                        player.sendMessage("You are not leader!");
                } catch (NullPointerException e) {
                    // ¯\_(ツ)_/¯
                }
                player.sendPacket(new HtmlMessage(this).setFile("default/32470-1.htm"));
                return;
            }
            ItemFunctions.addItem(player, RING, 1);
            BelethManager.setRingAvailable(false);
            HtmlMessage sp = new HtmlMessage(this).setHtml(HtmlUtils.htmlNpcString(NpcString.COMMAND_CHANNEL_LEADER_S1_BELETHS_RING_HAS_BEEN_ACQUIRED, player.getName()));
            BelethManager.getZone().broadcastPacket(sp, false);
            player.sendPacket(new HtmlMessage(this).setFile("default/32470-2.htm"));
            deleteMe();
            BelethManager.setCC(null);
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}