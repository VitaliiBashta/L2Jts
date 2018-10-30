package org.mmocore.gameserver.scripts.services.autoenchant;

import org.mmocore.gameserver.configuration.config.ServicesConfig;
import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Created by Hack
 * Date: 07.06.2016 1:51
 */
public class EnchantParser {
    private static EnchantParser instance;
    private final String mainColor = "BCBCBC";
    private final String fieldColor = "B59A75";
    private final String yes = "<font color=00c500>Yes</font>";
    private final String no = "<font color=c50000>No</font>";
    private final String unknown = setColor("?", fieldColor);

    // attribute colors
    private final String fire = setColor("◊", "f72c31");
    private final String water = setColor("◊", "1892ef");
    private final String wind = setColor("◊", "7bbebd");
    private final String earth = setColor("◊", "298a08");
    private final String holy = setColor("◊", "dedfde");
    private final String dark = setColor("◊", "9533b1");


    public static EnchantParser getInstance() {
        return instance == null ? instance = new EnchantParser() : instance;
    }

    private String setColor(String text, String color) {
        return "<font color=" + color + ">" + text + "</font>";
    }

    private String setColor(int value, String color) {
        return setColor("" + value, color);
    }

    private String setCenter(String text) {
        return "<center>" + text + "</center>";
    }

    private String setButton(String bypass, String name, int width, int height) {
        return "<button width=" + width + " height=" + height + " back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h " + bypass + "\" value=\"" + name + "\">";
    }

    private String setPressButton(String bypass, String name, int width, int height) {
        return "<button width=" + width + " height=" + height + " back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h " + bypass + "\" value=\"" + name + "\">";
    }


    private String setTextBox(String name, int width, int height) {
        return "<edit var=\"" + name + "\" width=" + width + " height=" + height + ">";
    }

    private String setIcon(String src) {
        return setIcon(src, 32, 32);
    }

    private String setIcon(String src, int width, int height) {
        return "<img src=\"" + src + "\" width=" + width + " height=" + height + ">";
    }

    public void showMainPage(Player player) {
        String page = HtmCache.getInstance().getHtml("command/enchant.htm", player);
        page = setTargetItem(player.getEnchantParams().targetItem, page);
        page = setEnchantItem(player.getEnchantParams().upgradeItem, page);
        page = setConfiguration(player, page);
        Functions.show(page, player, null);
    }

    public void showItemChoosePage(Player player, int item_type, int sort_type, int page_number) {
        String page = HtmCache.getInstance().getHtml("command/enchant_item_choose.htm", player);
        page = parseItemChoosePage(player, page, item_type, sort_type, page_number);
        Functions.show(page, player, null);
    }

    public void showResultPage(Player player, EnchantType type, Map<String, Integer> result) {
        String page;
        switch (type) {
            case Scroll:
                page = HtmCache.getInstance().getHtml("command/enchant_scroll_result.htm", player);
                page = page.replaceFirst("%crystallized%", result.get("crystallized") == 1 ? "Да" : "Нет");
                page = page.replaceFirst("%enchant%", "" + result.get("enchant"));
                page = page.replaceFirst("%max_enchant%", "" + result.get("maxenchant"));
                page = page.replaceFirst("%scrolls%", "" + result.get("scrolls"));
                page = page.replaceFirst("%common_scrolls%", "" + result.get("commonscrolls"));
                page = page.replaceFirst("%chance%", "" + ((double) result.get("chance") / 100));
                page = page.replaceFirst("%success%", result.get("success") == 1 ? "<font name=\"hs12\" color=\"00c500\">Успех</font>" : "<font name=\"hs12\" color=\"c50000\">Неудача</font>");
                Functions.show(page, player, null);
                break;
            case Attribute:
                page = HtmCache.getInstance().getHtml("command/enchant_attribute_result.htm", player);
                page = page.replaceFirst("%enchant%", "" + result.get("enchant"));
                page = page.replaceFirst("%stones%", "" + result.get("stones"));
                page = page.replaceFirst("%crystals%", "" + result.get("crystals"));
                page = page.replaceFirst("%chance%", "" + ((double) result.get("chance") / 100));
                page = page.replaceFirst("%success%", result.get("success") == 1 ? "<font name=\"hs12\" color=\"00c500\">Успех</font>" : "<font name=\"hs12\" color=\"c50000\">Неудача</font>");
                Functions.show(page, player, null);
                break;
        }
    }

    public void showHelpPage(Player player) {
        String page = HtmCache.getInstance().getHtml("command/enchant_help.htm", player);
        Functions.show(page, player, null);
    }

    private String setTargetItem(ItemInstance targetItem, String page) {
        if (targetItem == null) {
            page = page.replaceFirst("%item_icon%", setIcon("icon.weapon_long_sword_i00"));
            page = page.replaceFirst("%item_name%", setColor("Выберите предмет", mainColor));
            page = page.replaceFirst("%item_enchant_lvl%", unknown);
            page = page.replaceFirst("%item_att%", unknown);
            page = page.replaceFirst("%item_att_lvl%", unknown);
            page = page.replaceFirst("%item_button%", setButton("", "Выбрать", 55, 32));
            return page;
        }

        page = page.replaceFirst("%item_icon%", setIcon(targetItem.getTemplate().getIcon()));
        page = page.replaceFirst("%item_name%", setColor(targetItem.getName(), mainColor));
        page = page.replaceFirst("%item_enchant_lvl%", "" + setColor("+" + targetItem.getEnchantLevel(), fieldColor));
        page = page.replaceFirst("%item_button%", setButton("user_item_change 0 " + targetItem.getObjectId(), "Выбрать", 55, 32));
        page = setAttribute(targetItem, page);
        return page;
    }

    private String setEnchantItem(ItemInstance enchantItem, String page) {
        if (enchantItem == null) {
            page = page.replaceFirst("%ench_icon%", setIcon("icon.etc_scroll_of_enchant_weapon_i01"));
            page = page.replaceFirst("%ench_name%", setColor("Выберите свиток или камень", mainColor));
            page = page.replaceFirst("%ench_blessed_field%", setColor("Blessed:", mainColor));
            page = page.replaceFirst("%ench_blessed%", unknown);
            page = page.replaceFirst("%ench_type%", unknown);
            page = page.replaceFirst("%ench_count%", unknown);
            page = page.replaceFirst("%ench_button%", setButton("", "Выбрать", 55, 32));
            return page;
        }

        page = page.replaceFirst("%ench_icon%", setIcon(enchantItem.getTemplate().getIcon()));
        page = page.replaceFirst("%ench_name%", setColor(enchantItem.getName(), mainColor));
        if (EnchantUtils.getInstance().isAttribute(enchantItem)) {
            page = page.replaceFirst("%ench_blessed_field%", setColor("Стихия:", mainColor));
            page = page.replaceFirst("%ench_blessed%", setAttributeById(enchantItem.getItemId()));
            page = page.replaceFirst("%ench_type%", setColor(EnchantUtils.getInstance().isAttributeCrystal(enchantItem) ? "Кристалл" : "Камень", fieldColor));
        }
        page = page.replaceFirst("%ench_blessed_field%", setColor("Blessed:", mainColor));
        page = page.replaceFirst("%ench_blessed%", EnchantUtils.getInstance().isBlessed(enchantItem) ? yes : no);
        page = page.replaceFirst("%ench_type%", setColor(EnchantUtils.getInstance().isArmorScroll(enchantItem) ? "Армор" : "Оружие", mainColor));
        page = page.replaceFirst("%ench_count%", setColor("" + enchantItem.getCount(), fieldColor));
        page = page.replaceFirst("%ench_button%", setButton("user_item_change 1 " + enchantItem.getObjectId(), "Выбрать", 55, 32));
        return page;
    }

    private String setConfiguration(Player player, String page) {
        page = page.replaceFirst("%common_scrolls_for_safe%", player.getEnchantParams().isUseCommonScrollWhenSafe ? yes : no);
        if (player.getEnchantParams().isUseCommonScrollWhenSafe)
            page = page.replaceFirst("%common_scrolls_for_safe_button%", setButton("user_common_for_safe 0", "Off", 30, 22));
        else
            page = page.replaceFirst("%common_scrolls_for_safe_button%", setButton("user_common_for_safe 1", "On", 30, 22));

        ItemInstance enchantItem = player.getEnchantParams().upgradeItem;
        if (enchantItem == null) {
            page = page.replaceFirst("%max_enchant%", unknown);
            page = page.replaceFirst("%upgrade_item_limit%", unknown);
            page = page.replaceFirst("%item_limit_button%", setButton("", "Изменить", 62, 22));
            page = page.replaceFirst("%max_enchant_button%", setButton("", "Изменить", 62, 22));
            return page;
        }
        if (!player.getEnchantParams().isChangingMaxEnchant) {
            page = page.replaceFirst("%max_enchant_button%", setButton("user_max_enchant", "Изменить", 62, 22));
            if (EnchantUtils.getInstance().isAttribute(enchantItem))
                page = page.replaceFirst("%max_enchant%", setColor(player.getEnchantParams().maxEnchantAtt, fieldColor));
            else
                page = page.replaceFirst("%max_enchant%", setColor("+" + player.getEnchantParams().maxEnchant, fieldColor));
        } else {
            page = page.replaceFirst("%max_enchant%", setTextBox("max_enchant", 38, 12));
            page = page.replaceFirst("%max_enchant_button%", Matcher.quoteReplacement(setButton("user_max_enchant $max_enchant", "Задать", 62, 22)));
        }

        if (!player.getEnchantParams().isChangingUpgradeItemLimit) {
            page = page.replaceFirst("%upgrade_item_limit%", setColor(player.getEnchantParams().upgradeItemLimit, fieldColor));
            page = page.replaceFirst("%item_limit_button%", setButton("user_item_limit", "Изменить", 62, 22));
        } else {
            page = page.replaceFirst("%upgrade_item_limit%", setTextBox("upgrade_item_limit", 38, 12));
            page = page.replaceFirst("%item_limit_button%", Matcher.quoteReplacement(setButton("user_item_limit $upgrade_item_limit", "Задать", 62, 22)));
        }

        return page;
    }

    //TODO
    private String setAttribute(ItemInstance item, String page) {
        String attr = "";
        int power = 0;
        if (item.getAttributes().getFire() > 0) {
            attr += fire;
            power += item.getAttributes().getFire();
        } else if (item.getAttributes().getWater() > 0) {
            attr += water;
            power += item.getAttributes().getWater();
        }

        if (item.getAttributes().getWind() > 0) {
            attr += wind;
            power += item.getAttributes().getWind();
        } else if (item.getAttributes().getEarth() > 0) {
            attr += earth;
            power += item.getAttributes().getEarth();
        }

        if (item.getAttributes().getHoly() > 0) {
            attr += holy;
            power += item.getAttributes().getHoly();
        } else if (item.getAttributes().getUnholy() > 0) {
            attr += dark;
            power += item.getAttributes().getUnholy();
        }

        if (attr.equals(""))
            attr = setColor("Нет", fieldColor);

        page = page.replaceFirst("%item_att%", attr);
        page = page.replaceFirst("%item_att_lvl%", "" + power);
        return page;
    }

    private String setAttributeById(int id) {
        if (id == 9546 || id == 9552)
            return fire;
        if (id == 9547 || id == 9553)
            return water;
        if (id == 9548 || id == 9554)
            return earth;
        if (id == 9549 || id == 9555)
            return wind;
        if (id == 9550 || id == 9556)
            return dark;
        if (id == 9551 || id == 9557)
            return holy;
        return "";
    }

    /**
     * @param count how many buttons
     * @param type  0 - is target item, 1 - is enchant item
     * @return html code with parsed buttons
     */
    private String getPageButtons(int count, int type, int sort_type, int activePage) {
        String buttons = "<br><center><table><tr>";
        for (int i = 1; i <= count; i++) {
            if (i == activePage)
                buttons += "<td>" + setPressButton("user_item_choose " + type + " " + sort_type + " " + i, "" + i, 22, 22) + "</td>";
            else
                buttons += "<td>" + setButton("user_item_choose " + type + " " + sort_type + " " + i, "" + i, 22, 22) + "</td>";
        }
        buttons += "</tr></table></center>";
        return buttons;
    }

    /**
     * @param player     - just player Oo
     * @param page       - base page which will be modified
     * @param page_index - number of page with items which will be show
     * @param sort_type  - type of items for sorted show
     * @return page, based on the incoming page in params with parsed items
     */
    private String parseItemChoosePage(Player player, String page, int itemType, int sort_type, int page_index) {
        int itemsOnPage = 5;
        String content = "";
        StringBuilder template = new StringBuilder(HtmCache.getInstance().getHtml(itemType == 0 ? "command/enchant_item_obj.htm" : "command/enchant_upgrade_item_obj.htm", player));
        List<ItemInstance> items = EnchantUtils.getInstance().getWeapon(player);
        switch (itemType) {
            case 0:
                switch (sort_type) {
                    case 0:
                        items = EnchantUtils.getInstance().getWeapon(player);
                        break;
                    case 1:
                        items = EnchantUtils.getInstance().getArmor(player);
                        break;
                    case 2:
                        items = EnchantUtils.getInstance().getJewelry(player);
                        break;
                }
                break;
            case 1:
                if (sort_type == 0 && !ServicesConfig.ENCHANT_ALLOW_SCROLLS)
                    sort_type++;
                if (sort_type == 1 && !ServicesConfig.ENCHANT_ALLOW_ATTRIBUTE) {
                    if (ServicesConfig.ENCHANT_ALLOW_SCROLLS)
                        sort_type--;
                    else
                        sort_type++;
                }
                switch (sort_type) {
                    case 0:
                        items = EnchantUtils.getInstance().getScrolls(player);
                        break;
                    case 1:
                        items = EnchantUtils.getInstance().getAtributes(player);
                        break;
                }
                break;
        }
        content += getMenuButtons(sort_type, itemType);
        StringBuilder parsed_items = new StringBuilder();
        int page_count = (items.size() + itemsOnPage) / itemsOnPage;
        if (items.size() > itemsOnPage) {
            if (page_index > page_count)
                page_index = page_count;
            for (int i = page_index * itemsOnPage - itemsOnPage, startIdx = i; i < items.size() && i < startIdx + itemsOnPage; i++) {
                if (itemType == 0)
                    parsed_items.append(setTargetItem(items.get(i), template.toString()));
                else
                    parsed_items.append(setEnchantItem(items.get(i), template.toString()));
            }
            parsed_items.append(new StringBuilder(getPageButtons(page_count, itemType, sort_type, page_index)));
        } else
            for (ItemInstance item : items) {
                if (itemType == 0)
                    parsed_items.append(new StringBuilder(setTargetItem(item, template.toString())));
                else
                    parsed_items.append(new StringBuilder(setEnchantItem(item, template.toString())));
            }
        if (parsed_items.toString().equals(""))
            parsed_items.append(new StringBuilder(setCenter(setColor("Нет доступных предметов", mainColor))));
        content += parsed_items;
        return page.replaceFirst("%content%", content);
    }

    /**
     * @param activeButton - index of pressed button
     * @param item_type    - 0 - target item, 1 - upgrade item
     * @return all buttons with html parse
     */
    private String getMenuButtons(int activeButton, int item_type) {
        String buttons = "<center><table border=0><tr>";
        int summaryWidth = 240;
        int height = 25;
        String[][] itemButtons = {{"Оружие", "Броня", "Бижутерия"},
                {ServicesConfig.ENCHANT_ALLOW_SCROLLS ? "Заточка" : "unallowed", ServicesConfig.ENCHANT_ALLOW_ATTRIBUTE ? "Атрибут" : "unallowed"}};
//        int unallowed = 0;
        for (int i = 0; i < itemButtons[item_type].length; i++) {
            if (itemButtons[item_type][i].equals("unallowed"))
                continue;
            if (i == activeButton)
                buttons += "<td>" + setPressButton("user_item_choose " + item_type + " " + i + " 1", itemButtons[item_type][i], summaryWidth / itemButtons[item_type].length, height) + "</td>";
            else
                buttons += "<td>" + setButton("user_item_choose " + item_type + " " + i + " 1", itemButtons[item_type][i], summaryWidth / itemButtons[item_type].length, height) + "</td>";
        }
        buttons += "</tr></table></center>";
        return buttons;
    }

}
