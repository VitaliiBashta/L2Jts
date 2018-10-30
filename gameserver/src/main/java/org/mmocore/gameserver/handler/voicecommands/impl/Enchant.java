package org.mmocore.gameserver.handler.voicecommands.impl;

import org.mmocore.gameserver.configuration.config.ServicesConfig;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.handler.voicecommands.IVoicedCommandHandler;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.scripts.services.autoenchant.EnchantParser;
import org.mmocore.gameserver.scripts.services.autoenchant.EnchantUtils;
import org.mmocore.gameserver.templates.item.ItemTemplate;

/**
 * Created by Hack
 * Date: 07.06.2016 1:24
 */
public class Enchant implements IVoicedCommandHandler {
    private static final String[] commands = {"enchant", "max_enchant", "item_limit", "common_for_safe", "begin_enchant",
            "item_choose", "item_change", "enchant_help"};

    @Override
    public boolean useVoicedCommand(String command, Player player, String params) {
        if (player == null || !ServicesConfig.ALLOW_ENCHANT_SERVICE)
            return false;

        switch (command) {
            case "enchant":
                EnchantParser.getInstance().showMainPage(player);
                break;
            case "max_enchant":
                if (params == null || params.isEmpty()) {
                    player.getEnchantParams().isChangingMaxEnchant = true;
                } else {
                    try {
                        int userMax = Integer.parseInt(params.split(" ")[0]);
                        ItemInstance enchant = player.getEnchantParams().upgradeItem;
                        ItemInstance targetItem = player.getEnchantParams().targetItem;
                        if (!EnchantUtils.getInstance().isAttribute(enchant)) {
                            int configMax;
                            if (targetItem.isWeapon()) configMax = ServicesConfig.ENCHANT_MAX_WEAPON;
                            else if (targetItem.isArmor()) configMax = ServicesConfig.ENCHANT_MAX_ARMOR;
                            else if (targetItem.isAccessory()) configMax = ServicesConfig.ENCHANT_MAX_JEWELRY;
                            else return false;
                            if (userMax < 1) userMax = 1;
                            if (userMax > configMax) userMax = configMax;
                            player.getEnchantParams().maxEnchant = userMax;
                            player.getEnchantParams().isChangingMaxEnchant = false;
                        } else {
                            if (userMax < 1) userMax = 1;
                            if (targetItem.isAccessory())
                                return false;
                            if (targetItem.isArmor())
                                if (userMax > 120) userMax = 120;
                            if (targetItem.isWeapon())
                                if (userMax > 300) userMax = 300;
                            player.getEnchantParams().maxEnchantAtt = userMax;
                            player.getEnchantParams().isChangingMaxEnchant = false;
                        }
                    } catch (Exception e) {
                        // don't show anything if player entered an incorrect values
                    }
                }
                EnchantParser.getInstance().showMainPage(player);
                break;
            case "item_limit":
                if (params == null || params.isEmpty())
                    player.getEnchantParams().isChangingUpgradeItemLimit = true;
                else {
                    try {
                        int userLimit = Integer.parseInt(params.split(" ")[0]);
                        ItemInstance enchant = player.getEnchantParams().upgradeItem;
                        if (userLimit < 1) userLimit = 1;
                        if (userLimit > enchant.getCount()) userLimit = (int) enchant.getCount();
                        if (userLimit > ServicesConfig.ENCHANT_MAX_ITEM_LIMIT)
                            userLimit = ServicesConfig.ENCHANT_MAX_ITEM_LIMIT;
                        player.getEnchantParams().upgradeItemLimit = userLimit;
                        player.getEnchantParams().isChangingUpgradeItemLimit = false;
                    } catch (Exception e) {
                        // don't show anything if player entered an incorrect values
                    }
                }
                EnchantParser.getInstance().showMainPage(player);
                break;
            case "common_for_safe":
                if (params == null || params.isEmpty())
                    return false;
                else {
                    int safe = Integer.parseInt(params.split(" ")[0]);
                    if (safe == 0)
                        player.getEnchantParams().isUseCommonScrollWhenSafe = false;
                    else if (safe == 1)
                        player.getEnchantParams().isUseCommonScrollWhenSafe = true;
                    else return false;
                    EnchantParser.getInstance().showMainPage(player);
                }
                break;
            /**
             * 1st param - type of item (1 - target item, 2 - upgrade item)
             * 2nd param - sort item (for example: weapon or jewelry)
             * 3rd param - number of page (if item count more then can placed on one page)
             */
            case "item_choose":
                if (params == null || params.isEmpty())
                    return false;
                else {
                    String[] arr = params.split(" ");
                    if (arr.length < 3)
                        return false;
                    EnchantParser.getInstance().showItemChoosePage(player, Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
                }
                break;
            case "item_change":
                if (params == null || params.isEmpty())
                    return false;
                else {
                    String[] arr = params.split(" ");
                    if (arr.length < 2)
                        return false;
                    if (arr[0].equals("0"))
                        player.getEnchantParams().targetItem = player.getInventory().getItemByObjectId(Integer.parseInt(arr[1]));
                    else
                        player.getEnchantParams().upgradeItem = player.getInventory().getItemByObjectId(Integer.parseInt(arr[1]));
                }
                EnchantParser.getInstance().showMainPage(player);
                break;
            case "begin_enchant":
                if (ServicesConfig.ENCHANT_SERVICE_ONLY_FOR_PREMIUM && !player.getPremiumAccountComponent().hasBonus()) {
                    player.sendMessage("Сервис доступен только для премиумов!");
                    return false;
                }
                ItemInstance consumeItem = player.getInventory().getItemByItemId(ServicesConfig.ENCHANT_CONSUME_ITEM);
                if (ServicesConfig.ENCHANT_CONSUME_ITEM != 0 && (consumeItem == null || consumeItem.getCount() < ServicesConfig.ENCHANT_CONSUME_ITEM_COUNT)) {
                    ItemTemplate template = ItemTemplateHolder.getInstance().getTemplate(ServicesConfig.ENCHANT_CONSUME_ITEM);
                    player.sendMessage("Для использования сервиса необходимо " + ServicesConfig.ENCHANT_CONSUME_ITEM_COUNT + " "
                            + (template != null ? template.getName() : "[Ошибка: не найден item id! Пожалуйста сообщение администратору об ошибке]") + "!");
                    return false;
                }
                EnchantUtils.getInstance().enchant(player);
                break;
            case "enchant_help":
                EnchantParser.getInstance().showHelpPage(player);
                break;
        }
        return true;
    }

    @Override
    public String[] getVoicedCommandList() {
        return commands;
    }

}
