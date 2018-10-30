package org.mmocore.gameserver.model.instances;

import org.jts.dataparser.data.holder.petdata.PetUtils;
import org.jts.dataparser.data.holder.petdata.PetUtils.PetId;
import org.mmocore.gameserver.model.entity.SevenSigns;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

import java.util.StringTokenizer;

public final class WyvernManagerInstance extends NpcInstance {
    public WyvernManagerInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(final Player player, final String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        final StringTokenizer st = new StringTokenizer(command, " ");
        final String actualCommand = st.nextToken();

        if ("RideHelp".equalsIgnoreCase(actualCommand)) {
            final HtmlMessage html = new HtmlMessage(this);
            html.setFile("wyvern/help_ride.htm");
            player.sendPacket(html);
            player.sendActionFailed();
        } else if ("RideWyvern".equalsIgnoreCase(actualCommand)) {
            if (!validateCondition(player)) {
                return;
            }
            if (!player.isRiding() || !PetUtils.isStrider(player.getMountNpcId())) {
                final HtmlMessage html = new HtmlMessage(this);
                html.setFile("wyvern/not_ready.htm");
                player.sendPacket(html);
            } else if (player.getInventory().getItemByItemId(1460) == null || player.getInventory().getItemByItemId(1460).getCount() < 25) {
                final HtmlMessage html = new HtmlMessage(this);
                html.setFile("wyvern/havenot_cry.htm");
                player.sendPacket(html);
            } else if (SevenSigns.getInstance().getCurrentPeriod() == 3 && SevenSigns.getInstance().getCabalHighestScore() == 3) {
                final HtmlMessage html = new HtmlMessage(this);
                html.setFile("wyvern/no_ride_dusk.htm");
                player.sendPacket(html);
            } else if (player.getInventory().destroyItemByItemId(1460, 25L)) {
                player.setMount(PetId.WYVERN_ID, player.getMountObjId(), player.getMountLevel(), player.getMountCurrentFed());
                final HtmlMessage html = new HtmlMessage(this);
                html.setFile("wyvern/after_ride.htm");
                player.sendPacket(html);
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    @Override
    public void showChatWindow(final Player player, final int val, final Object... arg) {
        if (!validateCondition(player)) {
            final HtmlMessage html = new HtmlMessage(this);
            html.setFile("wyvern/lord_only.htm");
            player.sendPacket(html);
            player.sendActionFailed();
            return;
        }
        final HtmlMessage html = new HtmlMessage(this);
        html.setFile("wyvern/lord_here.htm");
        html.replace("%Char_name%", String.valueOf(player.getName()));
        player.sendPacket(html);
        player.sendActionFailed();
    }

    private boolean validateCondition(final Player player) {
        Residence residence = getCastle();
        if (residence != null && residence.getId() > 0) {
            if (player.getClan() != null) {
                if (residence.getOwnerId() == player.getClanId() && player.isClanLeader()) // Leader of clan
                {
                    return true; // Owner
                }
            }
        }
        residence = getFortress();
        if (residence != null && residence.getId() > 0) {
            if (player.getClan() != null) {
                if (residence.getOwnerId() == player.getClanId() && player.isClanLeader()) // Leader of clan
                {
                    return true; // Owner
                }
            }
        }
        residence = getClanHall();
        if (residence != null && residence.getId() > 0) {
            if (player.getClan() != null) {
                if (residence.getOwnerId() == player.getClanId() && player.isClanLeader()) // Leader of clan
                {
                    return true; // Owner
                }
            }
        }
        return false;
    }
}