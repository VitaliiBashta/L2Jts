package org.mmocore.gameserver.data.xml.parser;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.data.xml.holder.OptionDataHolder;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.conditions.Condition;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.OptionDataTemplate;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.templates.item.*;
import org.mmocore.gameserver.templates.item.support.CapsuledItemsContainer;

import java.io.File;
import java.util.Map;

/**
 * @author VISTALL
 * @date 11:26/15.01.2011
 */
public final class ItemParser extends StatParser<ItemTemplateHolder> {
    private ItemParser() {
        super(ItemTemplateHolder.getInstance());
    }

    public static ItemParser getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override
    public File getXMLDir() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/stats/items/");
    }

    @Override
    public boolean isIgnored(final File f) {
        return false;
    }

    @Override
    public String getDTDFileName() {
        return "item.dtd";
    }

    @Override
    protected void readData(final ItemTemplateHolder holder, final Element rootElement) {
        for (final Element itemElement : rootElement.getChildren()) {
            final StatsSet set = new StatsSet();
            set.set("item_id", itemElement.getAttributeValue("id"));
            set.set("name", itemElement.getAttributeValue("name"));
            set.set("add_name", itemElement.getAttributeValue("add_name", StringUtils.EMPTY));

            int slot = 0;
            for (final Element subElement : itemElement.getChildren()) {
                final String subName = subElement.getName();
                if ("set".equalsIgnoreCase(subName)) {
                    set.set(subElement.getAttributeValue("name"), subElement.getAttributeValue("value"));
                } else if ("equip".equalsIgnoreCase(subName)) {
                    for (final Element slotElement : subElement.getChildren()) {
                        final Bodypart bodypart = Bodypart.valueOf(slotElement.getAttributeValue("id"));
                        if (bodypart.getReal() != null) {
                            slot = bodypart.mask();
                        } else {
                            slot |= bodypart.mask();
                        }
                    }
                }
            }

            set.set("bodypart", slot);

            final ItemTemplate template;
            try {
                if ("weapon".equalsIgnoreCase(itemElement.getName())) {
                    if (!set.containsKey("class")) {
                        if ((slot & ItemTemplate.SLOT_L_HAND) > 0) // щиты
                        {
                            set.set("class", ItemTemplate.ItemClass.ARMOR);
                        } else {
                            set.set("class", ItemTemplate.ItemClass.WEAPON);
                        }
                    }
                    template = new WeaponTemplate(set);
                } else if ("armor".equalsIgnoreCase(itemElement.getName())) {
                    if (!set.containsKey("class")) {
                        if ((slot & ItemTemplate.SLOTS_ARMOR) != 0) {
                            set.set("class", ItemTemplate.ItemClass.ARMOR);
                        } else if ((slot & ItemTemplate.SLOTS_JEWELRY) > 0) {
                            set.set("class", ItemTemplate.ItemClass.JEWELRY);
                        } else {
                            set.set("class", ItemTemplate.ItemClass.ACCESSORY);
                        }
                    }
                    template = new ArmorTemplate(set);
                } else {
                    template = new EtcItemTemplate(set);
                }
            } catch (Exception e) {
                for (final Map.Entry<String, Object> entry : set.entrySet()) {
                    info("set " + entry.getKey() + ':' + entry.getValue());
                }

                warn("Fail create item: " + set.get("item_id"), e);
                continue;
            }

            for (final Element subElement : itemElement.getChildren()) {
                final String subName = subElement.getName();
                if ("for".equalsIgnoreCase(subName)) {
                    parseFor(subElement, template);
                } else if ("triggers".equalsIgnoreCase(subName)) {
                    parseTriggers(subElement, template);
                } else if ("skills".equalsIgnoreCase(subName)) {
                    for (final Element nextElement : subElement.getChildren()) {
                        final int id = Integer.parseInt(nextElement.getAttributeValue("id"));
                        final int level = Integer.parseInt(nextElement.getAttributeValue("level"));

                        final SkillEntry skill = SkillTable.getInstance().getSkillEntry(id, level);

                        if (skill != null) {
                            template.attachSkill(skill);
                        } else {
                            info("Skill not found(" + id + ',' + level + ") for item:" + set.getObject("item_id") + "; file:" + getCurrentFileName());
                        }
                    }
                } else if ("enchant4_skill".equalsIgnoreCase(subName)) {
                    final int id = Integer.parseInt(subElement.getAttributeValue("id"));
                    final int level = Integer.parseInt(subElement.getAttributeValue("level"));

                    final SkillEntry skill = SkillTable.getInstance().getSkillEntry(id, level);
                    if (skill != null) {
                        template.setEnchant4Skill(skill);
                    }
                } else if ("cond".equalsIgnoreCase(subName)) {
                    final Condition condition = parseFirstCond(subElement);
                    if (condition != null) {
                        final int msgId = parseNumber(subElement.getAttributeValue("msgId")).intValue();
                        condition.setSystemMsg(msgId);

                        template.setCondition(condition);
                    }
                } else if ("attributes".equalsIgnoreCase(subName)) {
                    final int[] attributes = new int[6];
                    for (final Element nextElement : subElement.getChildren()) {
                        // DS: name collision
                        final org.mmocore.gameserver.model.base.Element element;
                        if ("attribute".equalsIgnoreCase(nextElement.getName())) {
                            element = org.mmocore.gameserver.model.base.Element.getElementByName(nextElement.getAttributeValue("element"));
                            attributes[element.getId()] = Integer.parseInt(nextElement.getAttributeValue("value"));
                        }
                    }
                    template.setBaseAtributeElements(attributes);
                } else if ("capsuled_item".equalsIgnoreCase(subName)) {
                    holder.capsuledItemsPut(template.getItemId(), new CapsuledItemsContainer(template.getItemId(), 1, 1, 100));
                    for (final Element nextElement : subElement.getChildren()) {
                        if (("capsuled_items").equalsIgnoreCase(nextElement.getName())) {
                            int c_item_id = Integer.parseInt(nextElement.getAttributeValue("id"));
                            int c_min_count = Integer.parseInt(nextElement.getAttributeValue("min_count"));
                            int c_max_count = Integer.parseInt(nextElement.getAttributeValue("max_count"));
                            double c_chance = Double.parseDouble(nextElement.getAttributeValue("chance"));
                            template.addCapsuledItem(new CapsuledItemsContainer(c_item_id, c_min_count, c_max_count, c_chance));
                        }
                    }
                } else if ("enchant_options".equalsIgnoreCase(subName)) {
                    for (final Element nextElement : subElement.getChildren()) {
                        if ("level".equalsIgnoreCase(nextElement.getName())) {
                            final int val = Integer.parseInt(nextElement.getAttributeValue("val"));

                            int i = 0;
                            final int[] options = new int[3];
                            for (final Element optionElement : nextElement.getChildren()) {
                                final OptionDataTemplate optionData = OptionDataHolder.getInstance().getTemplate(Integer.parseInt(
                                        optionElement.getAttributeValue("id")));
                                if (optionData == null) {
                                    error("Not found option_data for id: " + optionElement.getAttributeValue("id") + "; item_id: " + set.get(
                                            "item_id"));
                                    continue;
                                }
                                options[i++] = optionData.getId();
                            }
                            template.addEnchantOptions(val, options);
                        }
                    }
                }
            }

            holder.addItem(template);
        }
    }

    @Override
    protected Object getTableValue(final String name) {
        return null;
    }

    private static class LazyHolder {
        private static final ItemParser INSTANCE = new ItemParser();
    }
}
