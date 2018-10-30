package org.mmocore.gameserver.model.instances;

import org.mmocore.gameserver.configuration.config.ServicesConfig;
import org.mmocore.gameserver.data.xml.holder.MultiSellHolder;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.manager.MapRegionManager;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.buylist.BuyList;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExBuyList;
import org.mmocore.gameserver.network.lineage.serverpackets.ExBuySellList;
import org.mmocore.gameserver.network.lineage.serverpackets.ExGetPremiumItemList;
import org.mmocore.gameserver.network.lineage.serverpackets.ShopPreviewList;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.templates.mapregion.DomainArea;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringTokenizer;

/**
 * @author Mangol
 * @author ?
 */
public class MerchantInstance extends NpcInstance {
    protected static final Logger _log = LoggerFactory.getLogger(MerchantInstance.class);
    private static final long serialVersionUID = 1L;
    private final int trade_for_newbie = 201;
    private final int lesser_talisman_bracelet = 629;
    private final int bytime1 = 579;
    private final int bytime2 = 580;
    private final int bytime3 = 581;
    private final int galladuchi_by = 1;

    public MerchantInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(final Player player, final String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }
        StringTokenizer st = new StringTokenizer(command, " ");
        String actualCommand = st.nextToken(); // Get actual command
        if ("Multisell".equalsIgnoreCase(actualCommand))//мб тоже выпилить есть в наследнике...
        {
            if (st.countTokens() < 1) {
                return;
            }
            int val = Integer.parseInt(st.nextToken());
            Castle castle = getCastle(player);
            MultiSellHolder.getInstance().SeparateAndSend(val, player, getObjectId(), castle != null ? castle.getTaxRate() : 0);
        } else if ("ReceivePremium".equalsIgnoreCase(actualCommand))// Нигде не юзаеться выпилить ...
        {
            if (player.getPremiumAccountComponent().getPremiumItemList().isEmpty()) {
                player.sendPacket(SystemMsg.THERE_ARE_NO_MORE_VITAMIN_ITEMS_TO_BE_FOUND);
                return;
            }
            player.sendPacket(new ExGetPremiumItemList(player));
        } else if (command.equalsIgnoreCase("menu_select?ask=-1&reply=0")) {
            showShopWindow(player, 1, true);
        } else if (command.equalsIgnoreCase("menu_select?ask=-1&reply=1")) {
            showShopWindow(player, 2, true);
        } else if (command.equalsIgnoreCase("menu_select?ask=-1&reply=2")) {
            showWearWindow(player, 1);
        } else if (command.equalsIgnoreCase("menu_select?ask=-1&reply=3")) {
            showWearWindow(player, 2);
        } else if (command.equalsIgnoreCase("menu_select?ask=-1&reply=4")) {
            showShopWindow(player, 5, true);
        } else if (command.equalsIgnoreCase("menu_select?ask=-1&reply=5")) {
            showShopWindow(player, 6, true);
        } else if (command.equalsIgnoreCase("menu_select?ask=-1&reply=6")) {
            showShopWindow(player, 7, true);
        } else if (command.equalsIgnoreCase("menu_select?ask=-1&reply=7")) {
            showShopWindow(player, 8, true);
        } else if (command.equalsIgnoreCase("menu_select?ask=-303&reply=1")) {
            MultiSellHolder.getInstance().SeparateAndSend(galladuchi_by, player, getObjectId(), 0);
        } else if (command.equalsIgnoreCase("menu_select?ask=-305&reply=1")) {
            if (player.getLevel() < 25) {
                MultiSellHolder.getInstance().SeparateAndSend(trade_for_newbie, player, getObjectId(), 0);
            } else {
                showChatWindow(player, "pts/merchant/merchant_for_newbie001.htm");
            }
        } else if (command.equalsIgnoreCase("menu_select?ask=-303&reply=629")) {
            MultiSellHolder.getInstance().SeparateAndSend(lesser_talisman_bracelet, player, getObjectId(), 0);
        } else if (command.equalsIgnoreCase("menu_select?ask=-510&reply=1")) {
            if (player.getLevel() < 40) {
                showChatWindow(player, "pts/merchant/reflect_weapon_none.htm");
            } else if (player.getLevel() >= 40 && player.getLevel() < 46) {
                showChatWindow(player, "pts/merchant/reflect_weapon_d.htm");
            } else if (player.getLevel() >= 46 && player.getLevel() < 52) {
                showChatWindow(player, "pts/merchant/reflect_weapon_c.htm");
            }
            if (player.getLevel() >= 52) {
                showChatWindow(player, "pts/merchant/reflect_weapon_b.htm");
            }
        } else if (command.equalsIgnoreCase("menu_select?ask=-303&reply=579")) {
            MultiSellHolder.getInstance().SeparateAndSend(bytime1, player, getObjectId(), 0);
        } else if (command.equalsIgnoreCase("menu_select?ask=-303&reply=580")) {
            MultiSellHolder.getInstance().SeparateAndSend(bytime2, player, getObjectId(), 0);
        } else if (command.equalsIgnoreCase("menu_select?ask=-303&reply=581")) {
            MultiSellHolder.getInstance().SeparateAndSend(bytime3, player, getObjectId(), 0);
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    private void showWearWindow(final Player player, final int listId) {
        if (!player.getPlayerAccess().UseShop) {
            return;
        }
        final BuyList list = getTemplate().getTradeList(listId);
        if (list != null && list.getNpcId() == getNpcId()) {
            final ShopPreviewList bl = new ShopPreviewList(list, player.getAdena(), player.expertiseIndex);
            player.sendPacket(bl);
        } else {
            _log.warn("showWearWindow: npc_id" + list.getNpcId() + "no buylist with id:" + listId + " || " + list.getNpcId() + " != " + getNpcId());
            player.sendActionFailed();
        }
    }

    protected void showShopWindow(final Player player, final int listId, final boolean tax) {
        if (!player.getPlayerAccess().UseShop) {
            return;
        }
        double taxRate = 0;
        if (tax) {
            Castle castle = getCastle(player);
            if (castle != null) {
                taxRate = castle.getTaxRate();
            }
        }
        final BuyList list = getTemplate().getTradeList(listId);
        if (list != null) {
            final double finalTaxRate = taxRate;
            list.getProducts().stream().filter(m -> m.getPrice() != m.generatePrice(finalTaxRate)).forEach(m -> m.setPrice(m.generatePrice(finalTaxRate)));
        }
        if (list == null || list.getNpcId() == getNpcId()) {
            player.sendPacket(new ExBuyList(list, player));
            player.sendPacket(new ExBuySellList(player, false));
        } else {
            _log.warn("[L2MerchantInstance] possible client hacker: " + player.getName() + " attempting to buy from GM shop! < Ban him!");
            _log.warn("buylist id:" + listId + " / list_npc = " + list.getNpcId() + " / npc = " + getNpcId());
        }
    }

    @Override
    public Castle getCastle(final Player player) {
        if (ServicesConfig.SERVICES_OFFSHORE_NO_CASTLE_TAX || (getReflection() == ReflectionManager.PARNASSUS && ServicesConfig.SERVICES_PARNASSUS_NOTAX)) {
            return null;
        }
        if (getReflection() == ReflectionManager.GIRAN_HARBOR || getReflection() == ReflectionManager.PARNASSUS) {
            String var = player.getPlayerVariables().get(PlayerVariables.BACK_COORDINATES);
            if (var != null && !var.isEmpty()) {
                Location loc = Location.parseLoc(var);
                DomainArea domain = MapRegionManager.getInstance().getRegionData(DomainArea.class, loc);
                if (domain != null) {
                    return ResidenceHolder.getInstance().getResidence(Castle.class, domain.getId());
                }
            }
            return super.getCastle();
        }
        return super.getCastle(player);
    }

    @Override
    public boolean isMerchantNpc() {
        return true;
    }
}