package handler.bbs.custom.refine;

import handler.bbs.ScriptBbsHandler;
import org.jts.dataparser.data.holder.variationdata.support.VariationStone;
import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.gameserver.configuration.config.clientCustoms.LostDreamCustom;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.data.xml.holder.OptionDataHolder;
import org.mmocore.gameserver.manager.VariationManager;
import org.mmocore.gameserver.network.lineage.clientpackets.ItemModification.abstracts.AbstractRefinePacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.InventoryUpdate;
import org.mmocore.gameserver.network.lineage.serverpackets.ShortCutRegister;
import org.mmocore.gameserver.object.Inventory;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.player.interfaces.ShortCut;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.funcs.FuncTemplate;
import org.mmocore.gameserver.templates.OptionDataTemplate;
import org.mmocore.gameserver.utils.VariationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hack
 * Date: 08.09.2016 6:44
 */
public class CommunityRefine extends ScriptBbsHandler {
    private static final List<OptionDataTemplate> weaponOptions = new ArrayList<>();
    private static final List<OptionDataTemplate> jewelryOptions = new ArrayList<>();
    private static final int elementsAtPage = 6;
    private static final String skillBg1 = "151515";
    private static final String skillBg2 = "212121";
    private static final String defaultItemIcon = "icon.noimage";
    private static final String indicator = "Выбрать";
    private static final String free = "<font color=\"00cc00\">Free</font>";
    private static final String aug = "<font color=\"cc0000\">Aug</font>";
    private static final String nul = "<font color=\"888888\">Null</font>";
    private static final String weaponRegex = "Item Skill:";
    private static final String jewelryRegex = "Augment Option -";

    @Override
    public String[] getBypassCommands() {
        return new String[] { "_bbsrefine" };
    }

    @Override
    public void onBypassCommand(Player player, String bypass) {
        String[] args = bypass.split(":");
        if (args.length < 3) {
            handleError(player);
            return;
        }

        switch (args[1]) {
            case "page":
                showMainPage(player, Integer.parseInt(args[2]));
                return;
            case "change_slot":
                player.getRefineComponent().setSelectedSlotId(Integer.parseInt(args[2]));
                showMainPage(player, 1);
                return;
            case "set_refine":
                setRefine(player, Integer.parseInt(args[2]));
                showMainPage(player, 1);
                return;
        }
        handleError(player);
    }

    private void setRefine(Player player, int idx) {
        ItemInstance item = getActiveItem(player);
        if (item == null) {
            player.sendPacket(SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM);
            return;
        }
        if (!AbstractRefinePacket.isValid(player, item) || !AbstractRefinePacket.isValid(item, player)) {
            player.sendPacket(SystemMsg.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
            return;
        }
        List<OptionDataTemplate> optionList = getActiveHolder(player);
        if (optionList == null)
            return;
        if (!getPay(player, getPriceId(player), getPriceCount(player), true)) {
            return;
        }
        int stoneId = item.isWeapon() ? 14169 : 14008;
        VariationStone stone = VariationManager.getInstance().getStone(item.getTemplate().getWeapontType(), stoneId);
        int variation1Id;
        do {
            variation1Id = VariationUtils.getRandomOptionId(stone.getVariation(1));
        } while (containsBaseStat(OptionDataHolder.getInstance().getTemplate(variation1Id).getAttachedFuncs()));
        int variation2Id = optionList.get(idx).getId();
        boolean isNeedEquip = false;
        if (item.isEquipped()) {
            player.getInventory().unEquipItem(item);
            isNeedEquip = true;
        }
        item.setVariationStoneId(stoneId);
        item.setVariation1Id(variation1Id);
        item.setVariation2Id(variation2Id);
        item.setJdbcState(JdbcEntityState.UPDATED);
        item.update();
        if(isNeedEquip)
            player.getInventory().equipItem(item);
        player.sendPacket(new InventoryUpdate().addModifiedItem(item));
        player.getShortCutComponent().getAllShortCuts().stream().filter(sc -> sc.getId() == item.getObjectId()
                && sc.getType() == ShortCut.TYPE_ITEM).forEach(sc -> {
            player.sendPacket(new ShortCutRegister(player, sc));
        });
        player.sendChanges();
    }

    private void handleError(Player player) {
        showMainPage(player, 1);
    }

    public void showMainPage(Player player,  int page) {
        String html = getCache().getHtml(CBasicConfig.BBS_PATH + "/refine/main.htm", player);
        html = html.replaceFirst("%available_skills%", getSkillsHtml(player, page));
        html = html.replaceFirst("%equiped_items%", getItemsHtml(player));
        html = html.replaceFirst("%prev_page_bypass%", getPageControlBypassButton(player, page - 1));
        html = html.replaceFirst("%next_page_bypass%", getPageControlBypassButton(player, page + 1));
        html = html.replaceFirst("%current_page%", page + "");
        separateAndSend(html, player);
    }

    @SuppressWarnings("all")
    private String getItemsHtml(Player player) {
        StringBuilder html = new StringBuilder("");
        html.append(getItemLine("Weapon", Inventory.PAPERDOLL_RHAND, player));
        html.append(getItemLine("Earring", Inventory.PAPERDOLL_REAR, player));
        html.append(getItemLine("Earring", Inventory.PAPERDOLL_LEAR, player));
        html.append(getItemLine("Necklace", Inventory.PAPERDOLL_NECK, player));
        html.append(getItemLine("Ring", Inventory.PAPERDOLL_RFINGER, player));
        html.append(getItemLine("Ring", Inventory.PAPERDOLL_LFINGER, player));
        return html.toString();
    }

    private String getItemLine(String prefixDesc, int slot, Player player) {
        String itemTemplateObj = getCache().getHtml(CBasicConfig.BBS_PATH + "/refine/item_object.htm", player);
        ItemInstance item = player.getInventory().getPaperdollItem(slot);
        itemTemplateObj = itemTemplateObj.replaceFirst("%icon%", getItemIcon(item));
        itemTemplateObj = itemTemplateObj.replaceFirst("%slot%", prefixDesc);
        itemTemplateObj = itemTemplateObj.replaceFirst("%colored_status%", getRefineStatus(item));
        itemTemplateObj = itemTemplateObj.replaceFirst("%button_bypass%", getItemButtonBypass(slot));
        itemTemplateObj = itemTemplateObj.replaceFirst("%indicator%", getIndicator(player, slot));
        return itemTemplateObj;
    }

    private String getItemIcon(ItemInstance item) {
        return item != null ? item.getTemplate().getIcon() : defaultItemIcon;
    }

    private String getRefineStatus(ItemInstance item) {
        if (item == null)
            return nul;
        else if (item.isAugmented())
            return aug;
        else return free;
    }

    private String getItemButtonBypass(int slot) {
        return "bypass _bbsrefine:change_slot:" + slot;
    }

    private String getPageControlBypassButton(Player player, int page) {
        List<OptionDataTemplate> holder = getActiveHolder(player);
        if (page < 1) page = 1;
        int maxPage;
        if (holder != null) {
            int div = holder.size() / elementsAtPage;
            maxPage = holder.size() % elementsAtPage == 0 ? div : div + 1;
        } else {
            maxPage = 1;
        }
        if (page > maxPage) page = maxPage;
        return "bypass _bbsrefine:page:" + page;
    }

    private String getIndicator(Player player, int slot) {
        return player.getRefineComponent().getSelectedSlotId() == slot ? indicator : "";
    }

    private String getSkillsHtml(Player player, int page) {
        StringBuilder html = new StringBuilder("");
        List<OptionDataTemplate> optionList = getActiveHolder(player);
        if (optionList == null)
            return html.append("<center>Skills not found.</center><br>").toString();
        String skillTemplateObj = getCache().getHtml(CBasicConfig.BBS_PATH + "/refine/skill_object.htm", player);
        for (int maxElement = page * elementsAtPage, i = maxElement - elementsAtPage, j = 1; i < maxElement && i < optionList.size(); i++, j++) {
            OptionDataTemplate template = optionList.get(i);
            String skillObject = "";
            skillObject += skillTemplateObj;
            if (template.getSkills().size() > 0) {
                SkillEntry showedSkill = template.getSkills().get(0);
                skillObject = skillObject.replaceFirst("%name%", getShortSkillName(showedSkill));
                skillObject = skillObject.replaceFirst("%icon%", showedSkill.getTemplate().getIcon());
                skillObject = skillObject.replaceFirst("%level%", showedSkill.getLevel() + "");
            } else {
                skillObject = skillObject.replaceFirst("%name%", "+1 " + getStatName(template));
                skillObject = skillObject.replaceFirst("%icon%", defaultItemIcon);
                skillObject = skillObject.replaceFirst("%level%", "1");
            }
            skillObject = skillObject.replaceFirst("%color%", j % 2 == 1 ? skillBg1 : skillBg2);
            skillObject = skillObject.replaceFirst("%price_item%", getPriceItemName(player) + "");
            skillObject = skillObject.replaceFirst("%price_count%", getPriceCount(player) + "");
            skillObject = skillObject.replaceFirst("%button_bypass%", getSkillBypass(i));
            html.append(skillObject);
        }
        return html.toString();
    }

    private String getShortSkillName(SkillEntry skill) {
        String name = skill.getTemplate().getName();
        name = name.replaceFirst(weaponRegex, "");
        name = name.replaceFirst(jewelryRegex, "");
        name = name.replaceFirst("Resistance", "Resist");
        return name;
    }

    private String getStatName(OptionDataTemplate template) {
        return template.getAttachedFuncs()[0]._stat.name().substring(5);
    }

    private List<OptionDataTemplate> getActiveHolder(Player player) {
        ItemInstance item = getActiveItem(player);
        if (item == null)
            return null;
        else if (item.isWeapon())
            return weaponOptions;
        else if (item.isAccessory())
            return jewelryOptions;
        else
            return null;
    }

    private ItemInstance getActiveItem(Player player) {
        return player.getInventory().getPaperdollItem(player.getRefineComponent().getSelectedSlotId());
    }

    @SuppressWarnings("all")
    private String getPriceItemName(Player player) {
        return ItemTemplateHolder.getInstance().getTemplate(getPriceId(player)).getName();
    }

    private int getPriceId(Player player) {
        ItemInstance targetItem = getActiveItem(player);
        if (targetItem == null)
            return 0;
        else if (targetItem.isWeapon())
            return LostDreamCustom.refineWeaponItem;
        else if (targetItem.isAccessory())
            return LostDreamCustom.refineJewelryItem;
        else
            return 0;
    }

    private int getPriceCount(Player player) {
        ItemInstance targetItem = getActiveItem(player);
        if (targetItem == null)
            return 0;
        else if (targetItem.isWeapon())
            return LostDreamCustom.refineWeaponPrice;
        else if (targetItem.isAccessory())
            return LostDreamCustom.refineJewelryPrice;
        else
            return 0;
    }

    private String getSkillBypass(int idx) {
        return "bypass _bbsrefine:set_refine:" + idx;
    }

    private void splitOptions() {
        Map<String, OptionDataTemplate> baseStatMap = new HashMap<>();
        Map<Integer, OptionDataTemplate> weaponMap = new HashMap<>(); // унифицируем по skill id
        Map<String, OptionDataTemplate> jewelryMap = new HashMap<>(); // унифицируем по имени скилла
        for (OptionDataTemplate template : OptionDataHolder.getInstance().getTemplates()) {
            if (template.getSkills().size() > 0) {
                SkillEntry skillEntry = template.getSkills().get(0);
                if (skillEntry.getTemplate().getName().contains(weaponRegex)) {
                    OptionDataTemplate storedTemplate = weaponMap.get(skillEntry.getId());
                    if (storedTemplate != null) {
                        if (storedTemplate.getSkills().get(0).getLevel() < skillEntry.getLevel())
                            weaponMap.put(skillEntry.getId(), template);
                    } else {
                        weaponMap.put(skillEntry.getId(), template);
                    }
                } else if (skillEntry.getTemplate().getName().contains(jewelryRegex)) {
                    OptionDataTemplate storedTemplate = jewelryMap.get(skillEntry.getTemplate().getName());
                    if (storedTemplate != null) {
                        if (storedTemplate.getSkills().get(0).getStatFuncs()[0].value < skillEntry.getStatFuncs()[0].value)
                            jewelryMap.put(skillEntry.getTemplate().getName(), template);
                    } else {
                        jewelryMap.put(skillEntry.getTemplate().getName(), template);
                    }
                }
            } else if (containsBaseStat(template.getAttachedFuncs())) {
                baseStatMap.put(getStatName(template), template);
            }
        }
        weaponOptions.addAll(baseStatMap.values());
        weaponOptions.addAll(weaponMap.values());
        jewelryOptions.addAll(jewelryMap.values());
    }

    private boolean containsBaseStat(FuncTemplate[] funcArray) {
        for (FuncTemplate func : funcArray)
            if (containsBaseStat(func))
                return true;
        return false;
    }

    private boolean containsBaseStat(FuncTemplate func) {
        switch (func._stat) {
            case STAT_STR:
            case STAT_CON:
            case STAT_DEX:
            case STAT_INT:
            case STAT_WIT:
            case STAT_MEN:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5) {}

    @Override
    public void onInit() {
        splitOptions();
        /*
        System.out.println("option size: " + OptionDataHolder.getInstance().getTemplates().size());
        System.out.println("weapon size: " + weaponOptions.size());
        System.out.println("jewelry size: " + jewelryOptions.size());
        */
        super.onInit();
    }
}
