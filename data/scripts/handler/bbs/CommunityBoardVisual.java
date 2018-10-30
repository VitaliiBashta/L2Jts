package handler.bbs;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.gameserver.configuration.config.ServicesConfig;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.model.visual.IItemCond;
import org.mmocore.gameserver.model.visual.VisualUtils;
import org.mmocore.gameserver.network.lineage.serverpackets.InventoryUpdate;
import org.mmocore.gameserver.network.lineage.serverpackets.ShowBoard;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Hack
 * Date: 08.06.2017 0:49
 */
public class CommunityBoardVisual extends ScriptBbsHandler {
    private static final Logger _log = LoggerFactory.getLogger(CommunityBoardVisual.class);
    private static final String PAGE_PATH = CBasicConfig.BBS_PATH + "/pages/visual/";
    private static final int ITEMS_PER_PAGE = 8;
    private static final int MAX_COLUMNS = 2;

    // attribute colors
    private final String fire = setColor("◊", "f72c31");
    private final String water = setColor("◊", "1892ef");
    private final String wind = setColor("◊", "7bbebd");
    private final String earth = setColor("◊", "298a08");
    private final String holy = setColor("◊", "dedfde");
    private final String dark = setColor("◊", "9533b1");

    private enum PageType {
        main("main", "Главная", null),
        item_select("selected_item", "Выбор предмета", (player, item) ->
                VisualUtils.isAllowedItem(item) && (item.isWeapon() || item.isArmor())
                        && !ArrayUtils.contains(ServicesConfig.VISUAL_DISALLOWED_FOR_VISUAL_ITEMS, item.getItemId())),
        consumable_select("selected_consumable", "Выбор внешности", (player, item) -> {
            if (!VisualUtils.isAllowedItem(item))
                return false;
            if (VisualUtils.isRejectedConsumable(item))
                return false;
            if (!item.isWeapon() && !item.isArmor() && item.getBodyPart() != ItemTemplate.SLOT_FORMAL_WEAR)
                return false;
            ItemInstance mainItem = player.getVisualParams().getItem();
            if (mainItem == null)
                return true;
            if (item.getItemType() != mainItem.getItemType() && !mainItem.isArmor())
                return false;
            if (mainItem.getBodyPart() != item.getBodyPart()) {
                if (mainItem.getBodyPart() == ItemTemplate.SLOT_CHEST) {
                    if (item.getBodyPart() != ItemTemplate.SLOT_FORMAL_WEAR)
                        return false;
                } else
                    return false;
            }
            if (item == mainItem)
                return false;
            return true;
        });
        private String buttonBypass;
        private String pageName;
        private IItemCond cond;
        PageType(String buttonBypass, String pageName, IItemCond cond) {
            this.buttonBypass = buttonBypass;
            this.pageName = pageName;
            this.cond = cond;
        }
    }

    private class Price {
        public int itemId;
        public int itemCount;

        public Price(int itemId, int itemCount) {
            this.itemId = itemId;
            this.itemCount = itemCount;
        }
    }

    @Override
    public void onBypassCommand(Player player, String bypass) {
        if (player == null)
            return;
        StringTokenizer st = new StringTokenizer(bypass, ":");
        st.nextToken();
        String action = st.hasMoreTokens() ? st.nextToken() : "main";

        switch (action) {
            case "insert": // visual installation
                insert(player);
                showPage(player, PageType.main);
                break;

            case "remove": // visual removing
                remove(player);
                showPage(player, PageType.main);
                break;

            case "selected_item": // main item selected. selected_item:obj_id
                if (!st.hasMoreTokens())
                    return;
                selected(player, st.nextToken(), true);
                showPage(player, PageType.main);
                break;

            case "selected_consumable": // consumable item selected. selected_consumable:obj_id
                if (!st.hasMoreTokens())
                    return;
                selected(player, st.nextToken(), false);
                showPage(player, PageType.main);
                break;

            default: // all opened pages
                int page = st.hasMoreElements() ? Integer.parseInt(st.nextToken()) : 1; // TODO: exception catch
                showPage(player, PageType.valueOf(action), page);
                break;
        }
    }

    private void showPage(Player player, PageType pageType) {
        showPage(player, pageType, 1);
    }

    private void showPage(Player player, PageType pageType, int page) {
        String html = null;
        switch (pageType) {
            case main:
                html = parseMainPage(player);
                break;

            case item_select:
            case consumable_select:
                html = parseItemChoosePage(player, pageType, page);
                break;

            default:
                showPage(player, PageType.main);
                break;
        }
        ShowBoard.separateAndSend(html, player);
    }

    private String parseItemChoosePage(Player player, PageType pageType, int page) {
        final String itemObj = HtmCache.getInstance().getHtml(getPagePath("item_object"), player);
        StringBuilder content = new StringBuilder("<tr><td>");
        List<ItemInstance> items = getItems(player, pageType.cond);
        final int maxElement = page * ITEMS_PER_PAGE;
        for (int i = maxElement - ITEMS_PER_PAGE; i < items.size() && i < maxElement; i++) {
            content.append(parseItem(items.get(i), itemObj, pageType.buttonBypass));
            if ((i + 1) % MAX_COLUMNS == 0)
                content.append("</td></tr><tr><td>");
            else
                content.append("</td><td>");
        }
        if (content.toString().endsWith("</td><td>")) {
            content.append(setIcon("L2UI.SquareGray", 313, 0));
            content.append("</td></tr>");
        } else
            content.delete(content.length() - "<tr><td>".length(), content.length());
        if (content.length() == 0)
            content.append("<tr><td>Нет доступных предметов</td></tr>");
        String choosePage = HtmCache.getInstance().getHtml(getPagePath("item_choose"), player);
        choosePage = choosePage.replaceFirst("%content%", content.toString());
        choosePage = choosePage.replaceFirst("%page_name%", pageType.pageName);
        choosePage = choosePage.replaceFirst("%prev_page_bypass%",
                "bypass _bbsvisual:" + pageType.name() + ":" + Math.max(page - 1, 1));
        choosePage = choosePage.replaceFirst("%current_page%", page + "");
        choosePage = choosePage.replaceFirst("%next_page_bypass%",
                "bypass _bbsvisual:" + pageType.name() + ":" + Math.min(page + 1, items.size() / ITEMS_PER_PAGE + 1));
        return choosePage;
    }

    private String parseMainPage(Player player) {
        ItemInstance item = player.getVisualParams().getItem();
        ItemInstance consumable = player.getVisualParams().getConsumable();
        String html = HtmCache.getInstance().getHtml(getPagePath("main"), player);
        if (item != null) {
            html = html.replaceFirst("%item_icon%", setIcon(item));
            html = html.replaceFirst("%item_name%", shortName(item));
        } else {
            html = html.replaceFirst("%item_icon%", setIcon("icon.weapon_long_sword_i00"));
            html = html.replaceFirst("%item_name%", "Не выбран");
        }

        if (consumable != null) {
            html = html.replaceFirst("%consumable_icon%", setIcon(consumable));
            html = html.replaceFirst("%consumable_name%", shortName(consumable));
        } else {
            html = html.replaceFirst("%consumable_icon%", setIcon("icon.weapon_giants_sword_i00"));
            html = html.replaceFirst("%consumable_name%", "Не выбран");
        }
        Price price = getPrice(player);
        html = html.replaceFirst("%price_count%", price.itemCount != -1 ? price.itemCount + "" : "—");
        ItemTemplate template = null;
        if (price.itemId != -1)
            template = ItemTemplateHolder.getInstance().getTemplate(price.itemId);
        html = html.replaceFirst("%price_name%", template != null ? template.getName() : "");
        return html;
    }

    private String parseItem(ItemInstance targetItem, String page, String bypass) {
        page = page.replaceFirst("%item_icon%", setIcon(targetItem.getTemplate().getIcon()));
        page = page.replaceFirst("%item_name%", shortName(targetItem));
        page = page.replaceFirst("%item_enchant_lvl%", "+" + targetItem.getEnchantLevel());
        page = page.replaceFirst("%item_bypass%", "bypass _bbsvisual:" + bypass + ":" + targetItem.getObjectId());
        page = setAttribute(targetItem, page);
        return page;
    }

    private Price getPrice(Player player) {
        ItemInstance consumable = player.getVisualParams().getConsumable();
        if (consumable == null)
            return new Price(-1, -1);
        if (consumable.isWeapon())
            return new Price(ServicesConfig.VISUAL_WEAPON_PRICE_ID, ServicesConfig.VISUAL_WEAPON_PRICE_COUNT);
        if (VisualUtils.isCostume(consumable.getItemId()))
            return new Price(ServicesConfig.VISUAL_COSTUME_PRICE_ID, ServicesConfig.VISUAL_COSTUME_PRICE_COUNT);
        return new Price(ServicesConfig.VISUAL_ARMOR_PRICE_ID, ServicesConfig.VISUAL_ARMOR_PRICE_COUNT);
    }

    private String shortName(ItemInstance item) {
        String name = item.getName();
        return name.length() > 26 ? name.substring(0, 26) : name;
    }

    private List<ItemInstance> getItems(Player player, IItemCond cond) {
        List<ItemInstance> items = new ArrayList<>();
        for (ItemInstance item : player.getInventory().getItems())
            if (cond.check(player, item))
                items.add(item);
        return items;
    }

    private String getPagePath(String pageName) {
        return PAGE_PATH + pageName + ".htm";
    }

    /**
     *
     * @param player - user
     * @param strObjId - string value of item object id
     * @param mainItem - true - basic item, false - consumable item
     */
    private void selected(Player player, String strObjId, boolean mainItem) {
        int objId;
        try {
            objId = Integer.parseInt(strObjId);
        } catch (Exception e) {
            return;
        }
        ItemInstance item =  player.getInventory().getItemByObjectId(objId);
        if (item == null)
            return;
        if (mainItem)
            player.getVisualParams().setItem(item);
        else
            player.getVisualParams().setConsumable(item);
    }

    private void insert(Player player) {
        ItemInstance item = player.getVisualParams().getItem();
        ItemInstance consume = player.getVisualParams().getConsumable();
        int consumeId;
        if (item == null || consume == null) {
            player.sendMessage("Выберите предметы!");
            return;
        }
        if (item.getVisualItemId() != 0) {
            player.sendMessage("Предмет уже визуализирован!");
            return;
        }
        if (item.getItemType() != consume.getItemType() && !item.isArmor()) {
            player.sendMessage("Тип предметов не соответствует!");
            return;
        }
        if (item.getBodyPart() != consume.getBodyPart()) {
            if (item.getBodyPart() == ItemTemplate.SLOT_CHEST) {
                if (consume.getBodyPart() != ItemTemplate.SLOT_FORMAL_WEAR) {
                    player.sendMessage("Слот предметов не соответствует!");
                    return;
                }
            } else {
                player.sendMessage("Слот предметов не соответствует!");
                return;
            }
        }

        Price price = getPrice(player);
        if (!Util.getPay(player, price.itemId, price.itemCount))
            return;
        consumeId = consume.getItemId();
        if (!item.isWeapon() && !item.isArmor())
            return;
        if (player.getInventory().getItemByObjectId(item.getObjectId()) == null) // just in case
            return;
        if (!Util.removeItem(player, consume))
            return;

        player.getInventory().unEquipItem(item);
        item.setVisualItemId(consumeId);
        item.setJdbcState(JdbcEntityState.UPDATED);
        item.update();
        player.getInventory().refreshEquip();
        player.sendPacket(new InventoryUpdate().addModifiedItem(item));
        player.broadcastCharInfo();
        player.getVisualParams().setConsumable(null);
        player.sendMessage("Предмет успешно визуализирован.");
    }

    private void remove(Player player) {
        ItemInstance item = player.getVisualParams().getItem();
        if (item == null) {
            player.sendMessage("Выберите предмет!");
            return;
        }
        final int visualId = item.getVisualItemId();
        if (visualId == 0) {
            player.sendMessage("Предмет не визуализирован!");
            return;
        }
        player.getInventory().unEquipItem(item);
        item.setVisualItemId(0);
        item.setJdbcState(JdbcEntityState.UPDATED);
        item.update();
        player.getInventory().refreshEquip();
        player.sendPacket(new InventoryUpdate().addModifiedItem(item));
        player.broadcastCharInfo();
        if (ServicesConfig.VISUAL_RETURN_VISUAL_ITEM)
            Util.addItem(player, visualId, 1);
        player.sendMessage("Визуализация успешно удалена.");
    }

    private String setIcon(ItemInstance item) {
        return setIcon(item.getTemplate().getIcon());
    }

    private String setIcon(String src) {
        return setIcon(src, 32, 32);
    }

    private String setIcon(String src, int width, int height) {
        return "<img src=\"" + src + "\" width=" + width + " height=" + height + ">";
    }

    private String setAttribute(ItemInstance item, String page) {
        String attr = "";
        int power = 0;
        if (item.getAttributes().getFire() > 0) {
            attr += fire;
            power += item.getAttributes().getFire();
        }
        else if (item.getAttributes().getWater() > 0) {
            attr += water;
            power += item.getAttributes().getWater();
        }

        if (item.getAttributes().getWind() > 0) {
            attr += wind;
            power += item.getAttributes().getWind();
        }
        else if (item.getAttributes().getEarth() > 0) {
            attr += earth;
            power += item.getAttributes().getEarth();
        }

        if (item.getAttributes().getHoly() > 0) {
            attr += holy;
            power += item.getAttributes().getHoly();
        }
        else if (item.getAttributes().getUnholy() > 0) {
            attr += dark;
            power += item.getAttributes().getUnholy();
        }

        if (attr.equals(""))
            attr = "—";

        page = page.replaceFirst("%item_att%", attr);
        page = page.replaceFirst("%item_att_lvl%", "" + power);
        return page;
    }

    private String setColor(String text, String color) {
        return "<font color=" + color + ">" + text + "</font>";
    }


    @Override
    public String[] getBypassCommands() {
        return new String[]{"_bbsvisual"};
    }

    @Override
    public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5) {}
}
