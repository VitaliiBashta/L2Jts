package org.mmocore.gameserver.skills;

import org.mmocore.gameserver.model.Skill.SkillType;
import org.mmocore.gameserver.model.base.EnchantSkillLearn;
import org.mmocore.gameserver.object.components.items.ItemHolder;
import org.mmocore.gameserver.skills.skillclasses.Extract;
import org.mmocore.gameserver.stats.conditions.Condition;
import org.mmocore.gameserver.tables.SkillTreeTable;
import org.mmocore.gameserver.templates.StatsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.util.*;

@Deprecated
public final class DocumentSkill extends DocumentBase {
    private static final Logger _log = LoggerFactory.getLogger(DocumentSkill.class);
    private final Set<String> usedTables = new HashSet<>();
    private final List<org.mmocore.gameserver.model.Skill> skillsInFile = new ArrayList<>(50);
    private Skill currentSkill;
    DocumentSkill(final File file) {
        super(file);
    }

    @Override
    protected void resetTable() {
        if (!usedTables.isEmpty()) {
            for (final String table : tables.keySet()) {
                if (!usedTables.contains(table)) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("Unused table " + table + " for skill " + currentSkill.id);
                    }
                }
            }
        }
        usedTables.clear();
        super.resetTable();
    }

    private void setCurrentSkill(final Skill skill) {
        currentSkill = skill;
    }

    List<org.mmocore.gameserver.model.Skill> getSkills() {
        return skillsInFile;
    }

    @Override
    protected Object getTableValue(final String name) {
        try {
            usedTables.add(name);
            final Object[] a = tables.get(name);
            if (a.length - 1 >= currentSkill.currentLevel) {
                return a[currentSkill.currentLevel];
            }
            return a[a.length - 1];
        } catch (RuntimeException e) {
            _log.error("Error in table " + name + " of skill Id " + currentSkill.id, e);
            return 0;
        }
    }

    @Override
    protected Object getTableValue(final String name, int idx) {
        idx--;
        try {
            usedTables.add(name);
            final Object[] a = tables.get(name);
            if (a.length - 1 >= idx) {
                return a[idx];
            }
            return a[a.length - 1];
        } catch (Exception e) {
            _log.error("Wrong level count in skill Id " + currentSkill.id + " table " + name + " level " + idx, e);
            return 0;
        }
    }

    @Override
    protected void parseDocument(final Document doc) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("skill".equalsIgnoreCase(d.getNodeName())) {
                        setCurrentSkill(new Skill());
                        parseSkill(d);
                        skillsInFile.addAll(currentSkill.skills);
                        resetTable();
                    }
                }
            } else if ("skill".equalsIgnoreCase(n.getNodeName())) {
                setCurrentSkill(new Skill());
                parseSkill(n);
                skillsInFile.addAll(currentSkill.skills);
            }
        }
    }

    protected void parseSkill(Node n) {
        final NamedNodeMap attrs = n.getAttributes();
        final int skillId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
        final String skillName = attrs.getNamedItem("name").getNodeValue();
        final String levels = attrs.getNamedItem("levels").getNodeValue();
        int lastLvl = Integer.parseInt(levels);

        try {
            final Map<Integer, Integer> displayLevels = new HashMap<>();

            // перебираем энчанты
            Node enchant = null;
            final Map<String, Object[]> etables = new HashMap<>();
            int count = 0, eLevels = 0;
            final Node d = n.cloneNode(true);
            for (int k = 0; k < d.getChildNodes().getLength(); k++) {
                enchant = d.getChildNodes().item(k);
                if (!enchant.getNodeName().startsWith("enchant")) {
                    continue;
                }
                if (eLevels == 0) {
                    if (enchant.getAttributes().getNamedItem("levels") != null) {
                        eLevels = Integer.parseInt(enchant.getAttributes().getNamedItem("levels").getNodeValue());
                    } else {
                        eLevels = 30;
                    }
                }
                final String ename = enchant.getAttributes().getNamedItem("name").getNodeValue();
                for (int r = 1; r <= eLevels; r++) {
                    final int level = lastLvl + eLevels * count + r;
                    final EnchantSkillLearn e = new EnchantSkillLearn(skillId, 100 * (count + 1) + r, skillName, "+" + r + ' ' + ename,
                            (r == 1 ? lastLvl : 100 * (count + 1) + r - 1), lastLvl, eLevels);

                    List<EnchantSkillLearn> t = SkillTreeTable.ENCHANT.get(skillId);
                    if (t == null) {
                        t = new ArrayList<>();
                    }
                    t.add(e);
                    SkillTreeTable.ENCHANT.put(skillId, t);
                    displayLevels.put(level, ((count + 1) * 100 + r));
                }
                count++;
                final Node first = enchant.getFirstChild();
                Node curr = null;
                for (curr = first; curr != null; curr = curr.getNextSibling()) {
                    if ("table".equalsIgnoreCase(curr.getNodeName())) {
                        final NamedNodeMap a = curr.getAttributes();
                        final String name = a.getNamedItem("name").getNodeValue();
                        Object[] table = parseTable(curr);
                        table = fillTableToSize(table, eLevels);
                        Object[] fulltable = etables.get(name);
                        if (fulltable == null) {
                            fulltable = new Object[lastLvl + eLevels * 8 + 1];
                        }
                        System.arraycopy(table, 0, fulltable, lastLvl + (count - 1) * eLevels, eLevels);
                        etables.put(name, fulltable);
                    }
                }
            }
            lastLvl += eLevels * count;

            currentSkill.id = skillId;
            currentSkill.name = skillName;
            currentSkill.sets = new StatsSet[lastLvl];

            for (int i = 0; i < lastLvl; i++) {
                currentSkill.sets[i] = new StatsSet();
                currentSkill.sets[i].set("skill_id", currentSkill.id);
                currentSkill.sets[i].set("level", i + 1);
                currentSkill.sets[i].set("name", currentSkill.name);
                currentSkill.sets[i].set("base_level", levels);
            }

            if (currentSkill.sets.length != lastLvl) {
                throw new RuntimeException("Skill id=" + skillId + " number of levels missmatch, " + lastLvl + " levels expected");
            }

            final Node first = n.getFirstChild();
            for (n = first; n != null; n = n.getNextSibling()) {
                if ("table".equalsIgnoreCase(n.getNodeName())) {
                    parseTable(n);
                }
            }

            // обрабатываем таблицы сливая их с энчантами
            for (final Map.Entry<String, Object[]> stringEntry : tables.entrySet()) {
                final Object[] et = etables.get(stringEntry.getKey());
                if (et != null) {
                    final Object[] t = stringEntry.getValue();
                    final Object max = t[t.length - 1];
                    System.arraycopy(t, 0, et, 0, t.length);
                    for (int j = 0; j < et.length; j++) {
                        if (et[j] == null) {
                            et[j] = max;
                        }
                    }
                    tables.put(stringEntry.getKey(), et);
                }
            }

            for (int i = 1; i <= lastLvl; i++) {
                for (n = first; n != null; n = n.getNextSibling()) {
                    if ("set".equalsIgnoreCase(n.getNodeName())) {
                        parseBeanSet(n, currentSkill.sets[i - 1], i);
                    }
                }
            }

            for (n = first; n != null; n = n.getNextSibling()) {
                if ("extractlist".equalsIgnoreCase(n.getNodeName())) {
                    final int level = Integer.parseInt(n.getAttributes().getNamedItem("level").getNodeValue());
                    parseExtract(n, currentSkill.sets[level - 1]);
                }
            }

            makeSkills();
            for (int i = 0; i < lastLvl; i++) {
                currentSkill.currentLevel = i;
                final org.mmocore.gameserver.model.Skill current = currentSkill.currentSkills.get(i);
                if (displayLevels.get(current.getLevel()) != null) {
                    current.setDisplayLevel(displayLevels.get(current.getLevel()));
                }
                current.setEnchantLevelCount(eLevels);

                for (n = first; n != null; n = n.getNextSibling()) {
                    if ("cond".equalsIgnoreCase(n.getNodeName())) {
                        final Condition condition = parseCondition(n.getFirstChild());
                        if (condition != null) {
                            final Node msgAttribute = n.getAttributes().getNamedItem("msgId");
                            if (msgAttribute != null) {
                                final int msgId = parseNumber(msgAttribute.getNodeValue()).intValue();
                                condition.setSystemMsg(msgId);
                            }
                            current.attach(condition);
                        }
                    } else if ("for".equalsIgnoreCase(n.getNodeName())) {
                        parseTemplate(n, current);
                    } else if ("triggers".equalsIgnoreCase(n.getNodeName())) {
                        parseTrigger(n, current);
                    }
                }
            }
            currentSkill.skills.addAll(currentSkill.currentSkills);
        } catch (Exception e) {
            _log.error("Error loading skill " + skillId, e);
        }
    }

    protected void parseExtract(Node n, final StatsSet set) {
        final List<Extract.ExtractGroup> list = new ArrayList<>();

        for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("group".equalsIgnoreCase(n.getNodeName())) {
                NamedNodeMap map = n.getAttributes();

                final double chance = Double.parseDouble(map.getNamedItem("chance").getNodeValue());

                final Extract.ExtractGroup g = new Extract.ExtractGroup(chance);
                list.add(g);

                for (Node n2 = n.getFirstChild(); n2 != null; n2 = n2.getNextSibling()) {
                    if ("extract".equalsIgnoreCase(n2.getNodeName())) {
                        map = n2.getAttributes();

                        final int itemId = Integer.parseInt(map.getNamedItem("item_id").getNodeValue());
                        final int count = Integer.parseInt(map.getNamedItem("count").getNodeValue());

                        g.add(new ItemHolder(itemId, count));
                    }
                }
            }
        }

        set.set("extractlist", list);
    }

    private Object[] fillTableToSize(Object[] table, final int size) {
        if (table.length < size) {
            final Object[] ret = new Object[size];
            System.arraycopy(table, 0, ret, 0, table.length);
            table = ret;
        }
        for (int j = 1; j < size; j++) {
            if (table[j] == null) {
                table[j] = table[j - 1];
            }
        }
        return table;
    }

    private void makeSkills() {
        currentSkill.currentSkills = new ArrayList<>(currentSkill.sets.length);
        //LOGGER.info.println(sets.length);
        for (int i = 0; i < currentSkill.sets.length; i++) {
            currentSkill.currentSkills.add(i, currentSkill.sets[i].getEnum("skillType", SkillType.class).makeSkill(currentSkill.sets[i]));
        }
    }

    public static class Skill {
        public final List<org.mmocore.gameserver.model.Skill> skills = new ArrayList<>();
        public int id;
        public String name;
        public StatsSet[] sets;
        public int currentLevel;
        public List<org.mmocore.gameserver.model.Skill> currentSkills = new ArrayList<>();
    }
}