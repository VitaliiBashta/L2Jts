package org.mmocore.gameserver.model.instances;

import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.manager.CastleManorManager;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.*;
import org.mmocore.gameserver.network.lineage.serverpackets.Moving.ValidateLocation;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.TradeItem;
import org.mmocore.gameserver.templates.manor.CropProcure;
import org.mmocore.gameserver.templates.manor.SeedProduction;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class ManorManagerInstance extends MerchantInstance {
    public ManorManagerInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onAction(final Player player, final boolean shift) {
        if (player.getTarget() != this) {
            player.setTarget(this);
            player.sendPacket(new MyTargetSelected(getObjectId(), player.getLevel() - getLevel()), new ValidateLocation(this));
        } else {
            final MyTargetSelected my = new MyTargetSelected(getObjectId(), player.getLevel() - getLevel());
            player.sendPacket(my);
            if (!isInRangeZ(player, getInteractDistance(player))) {
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
                player.sendActionFailed();
            } else {
                if (CastleManorManager.getInstance().isDisabled()) {
                    final HtmlMessage html = new HtmlMessage(this);
                    html.setFile("npcdefault.htm");
                    html.replace("%objectId%", String.valueOf(getObjectId()));
                    html.replace("%npcname%", getName());
                    player.sendPacket(html);
                } else if (!player.isGM() // Player is not GM
                        && player.isClanLeader() // Player is clan leader of clan (then he is the lord)
                        && getCastle() != null // Verification of castle
                        && getCastle().getOwnerId() == player.getClanId() // Player's clan owning the castle
                        ) {
                    showMessageWindow(player, "manager-lord.htm");
                } else {
                    showMessageWindow(player, "manager.htm");
                }
                player.sendActionFailed();
            }
        }
    }

    @Override
    public void onBypassFeedback(final Player player, final String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.startsWith("manor_menu_select")) { // input string format:
            // manor_menu_select?ask=X&state=Y&time=X
            if (CastleManorManager.getInstance().isUnderMaintenance()) {
                player.sendPacket(ActionFail.STATIC, SystemMsg.THE_MANOR_SYSTEM_IS_CURRENTLY_UNDER_MAINTENANCE);
                return;
            }

            final String params = command.substring(command.indexOf('?') + 1);
            final StringTokenizer st = new StringTokenizer(params, "&");
            final int ask = Integer.parseInt(st.nextToken().split("=")[1]);
            final int state = Integer.parseInt(st.nextToken().split("=")[1]);
            final int time = Integer.parseInt(st.nextToken().split("=")[1]);

            final Castle castle = getCastle();

            final int castleId;
            if (state == -1) // info for current manor
            {
                castleId = castle.getId();
            } else
            // info for requested manor
            {
                castleId = state;
            }

            switch (ask) { // Main action
                case 1: // Seed purchase
                    if (castleId != castle.getId()) {
                        player.sendPacket(new SystemMessage(SystemMsg._HERE_YOU_CAN_BUY_ONLY_SEEDS_OF_S1_MANOR).addResidenceName(castle.getId()));
                    } else {
                        List<TradeItem> list = Collections.emptyList();
                        final List<SeedProduction> seeds = castle.getSeedProduction(CastleManorManager.PERIOD_CURRENT);
                        for (final SeedProduction s : seeds) {
                            if (list.isEmpty()) {
                                list = new ArrayList<>();
                            }
                            final TradeItem item = new TradeItem();
                            item.setItemId(s.getId());
                            item.setOwnersPrice(s.getPrice());
                            item.setCount(s.getCanProduce());
                            if (item.getCount() > 0 && item.getOwnersPrice() > 0) {
                                list.add(item);
                            }
                        }
                        final BuyListSeed bl = new BuyListSeed(list, castleId, player.getAdena());
                        player.sendPacket(bl);
                    }
                    break;
                case 2: // Crop sales
                    player.sendPacket(new ExShowSellCropList(player, castleId, castle.getCropProcure(CastleManorManager.PERIOD_CURRENT)));
                    break;
                case 3: // Current seeds (Manor info)
                    if (time == 1 && !ResidenceHolder.getInstance().getResidence(Castle.class, castleId).isNextPeriodApproved()) {
                        player.sendPacket(new ExShowSeedInfo(castleId, Collections.<SeedProduction>emptyList()));
                    } else {
                        player.sendPacket(new ExShowSeedInfo(castleId, ResidenceHolder.getInstance().getResidence(Castle.class, castleId).getSeedProduction(time)));
                    }
                    break;
                case 4: // Current crops (Manor info)
                    if (time == 1 && !ResidenceHolder.getInstance().getResidence(Castle.class, castleId).isNextPeriodApproved()) {
                        player.sendPacket(new ExShowCropInfo(castleId, Collections.<CropProcure>emptyList()));
                    } else {
                        player.sendPacket(new ExShowCropInfo(castleId, ResidenceHolder.getInstance().getResidence(Castle.class, castleId).getCropProcure(time)));
                    }
                    break;
                case 5: // Basic info (Manor info)
                    player.sendPacket(new ExShowManorDefaultInfo());
                    break;
                case 6: // Buy harvester
                    showShopWindow(player, 1, false);
                    break;
                case 9: // Edit sales (Crop sales)
                    player.sendPacket(new ExShowProcureCropDetail(state));
                    break;
            }
        } else if (command.startsWith("help")) {
            final StringTokenizer st = new StringTokenizer(command, " ");
            st.nextToken(); // discard first
            final String filename = "manor_client_help00" + st.nextToken() + ".htm";
            showMessageWindow(player, filename);
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    public String getHtmlPath() {
        return "manormanager/";
    }

    @Override
    public String getHtmlPath(final int npcId, final int val, final Player player) {
        return "manormanager/manager.htm"; // Used only in parent method
        // to return from "Territory status"
        // to initial screen.
    }

    private void showMessageWindow(final Player player, final String filename) {
        final HtmlMessage html = new HtmlMessage(this);
        html.setFile(getHtmlPath() + filename);
        html.replace("%objectId%", String.valueOf(getObjectId()));
        html.replace("%npcId%", String.valueOf(getNpcId()));
        html.replace("%npcname%", getName());
        player.sendPacket(html);
    }
}