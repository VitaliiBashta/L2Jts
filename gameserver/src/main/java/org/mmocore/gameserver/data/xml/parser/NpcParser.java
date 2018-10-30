package org.mmocore.gameserver.data.xml.parser;

import org.apache.commons.lang3.ArrayUtils;
import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractDirParser;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.model.TeleportLocation;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.base.NpcRace;
import org.mmocore.gameserver.model.buylist.BuyList;
import org.mmocore.gameserver.model.buylist.Product;
import org.mmocore.gameserver.model.reward.RewardData;
import org.mmocore.gameserver.model.reward.RewardGroup;
import org.mmocore.gameserver.model.reward.RewardList;
import org.mmocore.gameserver.model.reward.RewardType;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.npc.AbsorbInfo;
import org.mmocore.gameserver.templates.npc.Faction;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 * @date 16:16/14.12.2010
 */
public class NpcParser extends AbstractDirParser<NpcHolder> {
    protected NpcParser() {
        super(NpcHolder.getInstance());
    }

    public static NpcParser getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override
    public File getXMLDir() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/stats/npc/");
    }

    @Override
    public boolean isIgnored(final File f) {
        return f.getPath().contains("custom");
    }

    @Override
    public String getDTDFileName() {
        return "npc.dtd";
    }

    @Override
    protected void readData(final NpcHolder holder, final Element rootElement) throws Exception {
        for (final Element npcElement : rootElement.getChildren()) {
            final int npcId = Integer.parseInt(npcElement.getAttributeValue("id"));
            final int templateId = npcElement.getAttributeValue("template_id") == null ? 0 : Integer.parseInt(npcElement.getAttributeValue("id"));
            final String name = npcElement.getAttributeValue("name");
            final String title = npcElement.getAttributeValue("title");

            final StatsSet set = new StatsSet();
            set.set("npcId", npcId);
            set.set("displayId", templateId);
            set.set("name", name);
            set.set("title", title);
            set.set("baseCpReg", 0);
            set.set("baseCpMax", 0);

            for (final Element firstElement : npcElement.getChildren()) {
                if ("set".equalsIgnoreCase(firstElement.getName())) {
                    set.set(firstElement.getAttributeValue("name"), firstElement.getAttributeValue("value"));
                } else if ("equip".equalsIgnoreCase(firstElement.getName())) {
                    for (final Element eElement : firstElement.getChildren()) {
                        set.set(eElement.getName(), eElement.getAttributeValue("item_id"));
                    }
                } else if ("ai_params".equalsIgnoreCase(firstElement.getName())) {
                    final StatsSet ai = new StatsSet();
                    for (final Element eElement : firstElement.getChildren()) {
                        ai.set(eElement.getAttributeValue("name"), eElement.getAttributeValue("value"));
                    }

                    set.set("aiParams", ai);
                } else if ("attributes".equalsIgnoreCase(firstElement.getName())) {
                    final int[] attributeAttack = new int[6];
                    final int[] attributeDefence = new int[6];
                    for (final Element eElement : firstElement.getChildren()) {
                        // DS: name collision
                        final org.mmocore.gameserver.model.base.Element element;
                        if ("defence".equalsIgnoreCase(eElement.getName())) {
                            element = org.mmocore.gameserver.model.base.Element.getElementByName(eElement.getAttributeValue("attribute"));
                            attributeDefence[element.getId()] = Integer.parseInt(eElement.getAttributeValue("value"));
                        } else if ("attack".equalsIgnoreCase(eElement.getName())) {
                            element = org.mmocore.gameserver.model.base.Element.getElementByName(eElement.getAttributeValue("attribute"));
                            attributeAttack[element.getId()] = Integer.parseInt(eElement.getAttributeValue("value"));
                        }
                    }

                    set.set("baseAttributeAttack", attributeAttack);
                    set.set("baseAttributeDefence", attributeDefence);
                }
            }

            final NpcTemplate template = new NpcTemplate(set);

            for (final Element secondElement : npcElement.getChildren()) {
                final String nodeName = secondElement.getName();
                if ("faction".equalsIgnoreCase(nodeName)) {
                    final String factionId = secondElement.getAttributeValue("name");
                    final Faction faction = new Faction(factionId);
                    final int factionRange = Integer.parseInt(secondElement.getAttributeValue("range"));
                    faction.setRange(factionRange);
                    for (final Element nextElement : secondElement.getChildren()) {
                        final int ignoreId = Integer.parseInt(nextElement.getAttributeValue("npc_id"));
                        faction.addIgnoreNpcId(ignoreId);
                    }
                    template.setFaction(faction);
                } else if ("rewardlist".equalsIgnoreCase(nodeName)) {
                    final RewardType type = RewardType.valueOf(secondElement.getAttributeValue("type"));
                    final boolean autoLoot = secondElement.getAttributeValue("auto_loot") != null && Boolean.parseBoolean(secondElement.getAttributeValue(
                            "auto_loot"));
                    final RewardList list = new RewardList(type, autoLoot);

                    for (final Element nextElement : secondElement.getChildren()) {
                        final String nextName = nextElement.getName();
                        if ("group".equalsIgnoreCase(nextName)) {
                            final double enterChance = nextElement.getAttributeValue("chance") == null ? RewardList.MAX_CHANCE : Double.parseDouble(
                                    nextElement.getAttributeValue("chance")) * 10000;

                            final RewardGroup group = (type == RewardType.SWEEP || type == RewardType.NOT_RATED_NOT_GROUPED) ? null : new RewardGroup(
                                    enterChance);
                            for (final Element rewardElement : nextElement.getChildren()) {
                                final RewardData data = parseReward(rewardElement);
                                if (type == RewardType.SWEEP || type == RewardType.NOT_RATED_NOT_GROUPED) {
                                    warn("Can't load rewardlist from group: " + npcId + "; type: " + type);
                                } else {
                                    group.addData(data);
                                }
                            }

                            if (group != null) {
                                list.add(group);
                            }
                        } else if ("reward".equalsIgnoreCase(nextName)) {
                            if (type != RewardType.SWEEP && type != RewardType.NOT_RATED_NOT_GROUPED) {
                                warn("Reward can't be without group(and not grouped): " + npcId + "; type: " + type);
                                continue;
                            }

                            final RewardData data = parseReward(nextElement);
                            final RewardGroup g = new RewardGroup(RewardList.MAX_CHANCE);
                            g.addData(data);
                            list.add(g);
                        }
                    }

                    if (type == RewardType.RATED_GROUPED || type == RewardType.NOT_RATED_GROUPED) {
                        if (!list.validate()) {
                            warn("Problems with rewardlist for npc: " + npcId + "; type: " + type);
                        }
                    }

                    template.putRewardList(type, list);
                } else if ("skills".equalsIgnoreCase(nodeName)) {
                    for (final Element nextElement : secondElement.getChildren()) {
                        final int id = Integer.parseInt(nextElement.getAttributeValue("id"));
                        final int level = Integer.parseInt(nextElement.getAttributeValue("level"));

                        // Для определения расы используется скилл 4416
                        if (id == 4416)
                            template.setRace(NpcRace.values()[level]);

                        final SkillEntry skill = SkillTable.getInstance().getSkillEntry(id, level);

                        //TODO
                        //if(skill == null || skill.getSkillType() == L2Skill.SkillType.NOTDONE)
                        //	unimpl.add(Integer.valueOf(skillId));
                        if (skill == null) {
                            continue;
                        }

                        template.addSkill(skill);
                    }
                } else if ("teach_classes".equalsIgnoreCase(nodeName)) {
                    for (final Element nextElement : secondElement.getChildren()) {
                        final int id = Integer.parseInt(nextElement.getAttributeValue("id"));

                        template.addTeachInfo(ClassId.VALUES[id]);
                    }
                } else if ("absorblist".equalsIgnoreCase(nodeName)) {
                    for (final Element nextElement : secondElement.getChildren()) {
                        final int chance = Integer.parseInt(nextElement.getAttributeValue("chance"));
                        final int cursedChance = nextElement.getAttributeValue("cursed_chance") == null ? 0 : Integer.parseInt(
                                nextElement.getAttributeValue("cursed_chance"));
                        final int minLevel = Integer.parseInt(nextElement.getAttributeValue("min_level"));
                        final int maxLevel = Integer.parseInt(nextElement.getAttributeValue("max_level"));
                        final boolean skill = nextElement.getAttributeValue("skill") != null && Boolean.parseBoolean(nextElement.getAttributeValue(
                                "skill"));
                        final AbsorbInfo.AbsorbType absorbType = AbsorbInfo.AbsorbType.valueOf(nextElement.getAttributeValue("type"));

                        template.addAbsorbInfo(new AbsorbInfo(skill, absorbType, chance, cursedChance, minLevel, maxLevel));
                    }
                } else if ("teleportlist".equalsIgnoreCase(nodeName)) {
                    for (final Element subListElement : secondElement.getChildren()) {
                        final int id = Integer.parseInt(subListElement.getAttributeValue("id"));
                        final List<TeleportLocation> list = new ArrayList<>();
                        for (final Element targetElement : subListElement.getChildren()) {
                            final int itemId = Integer.parseInt(targetElement.getAttributeValue("item_id", "57"));
                            final long price = Integer.parseInt(targetElement.getAttributeValue("price"));
                            final int npcStringId = Integer.parseInt(targetElement.getAttributeValue("name"));
                            final int castleId = Integer.parseInt(targetElement.getAttributeValue("castle_id", "0"));
                            final TeleportLocation loc = new TeleportLocation(itemId, price, npcStringId, castleId);
                            loc.set(Location.parseLoc(targetElement.getAttributeValue("loc")));
                            list.add(loc);
                        }
                        template.addTeleportList(id, list.toArray(new TeleportLocation[list.size()]));
                    }
                } else if ("tradelist".equalsIgnoreCase(nodeName)) {
                    for (final Element subListElement : secondElement.getChildren()) {
                        final int tradeId = Integer.parseInt(subListElement.getAttributeValue("id"));
                        final BuyList tl = new BuyList(tradeId, npcId);
                        for (final Element targetElement : subListElement.getChildren()) {
                            final int itemId = Integer.parseInt(targetElement.getAttributeValue("item_id"));
                            final ItemTemplate temp = ItemTemplateHolder.getInstance().getTemplate(itemId);
                            if (!checkItem(temp)) {
                                continue;
                            }
                            final double percent = targetElement.getAttributeValue("markup") != null ? Double.parseDouble(targetElement.getAttributeValue("markup")) : 0;
                            final int itemCount = targetElement.getAttributeValue("count") != null ? Integer.parseInt(targetElement.getAttributeValue("count")) : 0;
                            final int itemRechargeTime = targetElement.getAttributeValue("time") != null ? Integer.parseInt(targetElement.getAttributeValue("time")) : 0;
                            tl.addProduct(new Product(tradeId, temp, percent, itemRechargeTime, itemCount));
                        }
                        template.addTradeList(tradeId, tl);
                    }
                }
            }

            addTemplate(holder, template);
        }
    }

    protected void addTemplate(final NpcHolder holder, final NpcTemplate template) {
        holder.addTemplate(template);
    }

    private RewardData parseReward(final Element rewardElement) {
        final int itemId = Integer.parseInt(rewardElement.getAttributeValue("item_id"));
        final RewardData data = new RewardData(itemId);

        long min = Long.parseLong(rewardElement.getAttributeValue("min"));
        long max = Long.parseLong(rewardElement.getAttributeValue("max"));

        if (data.getItem().isCommonItem()) {
            min *= ServerConfig.RATE_DROP_COMMON_ITEMS;
            max *= ServerConfig.RATE_DROP_COMMON_ITEMS;
        }

        // переводим в системный вид
        final int chance = (int) (Double.parseDouble(rewardElement.getAttributeValue("chance")) * 10000);
        data.setChance(chance);

        if (data.getItem().isArrow() // стрелы не рейтуются
                || (ServerConfig.NO_RATE_EQUIPMENT && data.getItem().isEquipment()) // отключаемая рейтовка эквипа
                || (ServerConfig.NO_RATE_KEY_MATERIAL && data.getItem().isKeyMatherial()) // отключаемая рейтовка ключевых материалов
                || (ServerConfig.NO_RATE_RECIPES && data.getItem().isRecipe()) // отключаемая рейтовка рецептов
                || ArrayUtils.contains(ServerConfig.NO_RATE_ITEMS, itemId)) // индивидаульная отключаемая рейтовка для списка предметов
        {
            data.setNotRate(true);
        }

        data.setMinDrop(min);
        data.setMaxDrop(max);

        return data;
    }

    public boolean checkItem(final ItemTemplate template) {
        if (template.isCommonItem() && !AllSettingsConfig.ALT_ALLOW_SELL_COMMON) {
            return false;
        }
        if (template.isEquipment() && !template.isForPet() && AllSettingsConfig.ALT_SHOP_PRICE_LIMITS.length > 0) {
            for (int i = 0; i < AllSettingsConfig.ALT_SHOP_PRICE_LIMITS.length; i += 2) {
                if (template.getBodyPart() == AllSettingsConfig.ALT_SHOP_PRICE_LIMITS[i]) {
                    if (template.getReferencePrice() > AllSettingsConfig.ALT_SHOP_PRICE_LIMITS[i + 1]) {
                        return false;
                    }
                    break;
                }
            }
        }
        if (AllSettingsConfig.ALT_SHOP_UNALLOWED_ITEMS.length > 0) {
            for (final int i : AllSettingsConfig.ALT_SHOP_UNALLOWED_ITEMS) {
                if (template.getItemId() == i) {
                    return false;
                }
            }
        }
        return true;
    }

    private static class LazyHolder {
        private static final NpcParser INSTANCE = new NpcParser();
    }
}
