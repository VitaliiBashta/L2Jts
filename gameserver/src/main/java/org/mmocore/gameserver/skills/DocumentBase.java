package org.mmocore.gameserver.skills;

import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.skills.effects.EffectTemplate;
import org.mmocore.gameserver.stats.StatTemplate;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.stats.conditions.*;
import org.mmocore.gameserver.stats.conditions.ConditionGameTime.CheckGameTime;
import org.mmocore.gameserver.stats.conditions.ConditionPlayerRiding.CheckPlayerRiding;
import org.mmocore.gameserver.stats.conditions.ConditionPlayerState.CheckPlayerState;
import org.mmocore.gameserver.stats.funcs.FuncTemplate;
import org.mmocore.gameserver.stats.triggers.TriggerInfo;
import org.mmocore.gameserver.stats.triggers.TriggerType;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.templates.item.ArmorTemplate.ArmorType;
import org.mmocore.gameserver.templates.item.WeaponTemplate.WeaponType;
import org.mmocore.gameserver.utils.PositionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

@Deprecated
abstract class DocumentBase {
    private static final Logger _log = LoggerFactory.getLogger(DocumentBase.class);

    private final File file;
    protected Map<String, Object[]> tables;

    DocumentBase(final File file) {
        this.file = file;
        tables = new HashMap<>();
    }

    Document parse() {
        final Document doc;
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringComments(true);
            doc = factory.newDocumentBuilder().parse(file);
        } catch (Exception e) {
            _log.error("Error loading file " + file, e);
            return null;
        }
        try {
            parseDocument(doc);
        } catch (Exception e) {
            _log.error("Error in file " + file, e);
            return null;
        }
        return doc;
    }

    protected abstract void parseDocument(Document doc);

    protected abstract Object getTableValue(String name);

    protected abstract Object getTableValue(String name, int idx);

    protected void resetTable() {
        tables = new HashMap<>();
    }

    protected void setTable(final String name, final Object[] table) {
        tables.put(name, table);
    }

    protected void parseTemplate(Node n, final StatTemplate template) {
        n = n.getFirstChild();
        if (n == null) {
            return;
        }
        for (; n != null; n = n.getNextSibling()) {
            final String nodeName = n.getNodeName();
            if ("add".equalsIgnoreCase(nodeName)) {
                attachFunc(n, template, "Add");
            } else if ("sub".equalsIgnoreCase(nodeName)) {
                attachFunc(n, template, "Sub");
            } else if ("mul".equalsIgnoreCase(nodeName)) {
                attachFunc(n, template, "Mul");
            } else if ("div".equalsIgnoreCase(nodeName)) {
                attachFunc(n, template, "Div");
            } else if ("set".equalsIgnoreCase(nodeName)) {
                attachFunc(n, template, "Set");
            } else if ("enchant".equalsIgnoreCase(nodeName)) {
                attachFunc(n, template, "Enchant");
            } else if ("effect".equalsIgnoreCase(nodeName)) {
                if (template instanceof EffectTemplate) {
                    throw new RuntimeException("Nested effects");
                }
                attachEffect(n, template);
            } else if (template instanceof EffectTemplate) {
                if ("def".equalsIgnoreCase(nodeName)) {
                    parseBeanSet(n, ((EffectTemplate) template).getParam(), ((Skill) ((EffectTemplate) template).getParam().getObject("object"))
                            .getLevel());
                } else {
                    final Condition cond = parseCondition(n);
                    if (cond != null) {
                        ((EffectTemplate) template).attachCond(cond);
                    }
                }
            }
        }
    }

    protected void parseTrigger(Node n, final StatTemplate template) {
        for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("trigger".equalsIgnoreCase(n.getNodeName())) {
                final NamedNodeMap map = n.getAttributes();

                final int id = parseNumber(map.getNamedItem("id").getNodeValue()).intValue();
                final int level = parseNumber(map.getNamedItem("level").getNodeValue()).intValue();
                final TriggerType t = TriggerType.valueOf(map.getNamedItem("type").getNodeValue());
                final double chance = parseNumber(map.getNamedItem("chance").getNodeValue()).doubleValue();

                final TriggerInfo trigger = new TriggerInfo(id, level, t, chance);

                template.addTrigger(trigger);
                for (Node n2 = n.getFirstChild(); n2 != null; n2 = n2.getNextSibling()) {
                    final Condition condition = parseCondition(n.getFirstChild());
                    if (condition != null) {
                        trigger.addCondition(condition);
                    }
                }
            }
        }
    }

    protected void attachFunc(final Node n, final StatTemplate template, final String name) {
        final Stats stat = Stats.valueOfXml(n.getAttributes().getNamedItem("stat").getNodeValue());
        final String order = n.getAttributes().getNamedItem("order").getNodeValue();
        final int ord = parseNumber(order).intValue();
        final Condition applyCond = parseCondition(n.getFirstChild());
        double val = 0;
        if (n.getAttributes().getNamedItem("val") != null) {
            val = parseNumber(n.getAttributes().getNamedItem("val").getNodeValue()).doubleValue();
        }

        template.attachFunc(new FuncTemplate(applyCond, name, stat, ord, val));
    }

    protected void attachEffect(final Node n, final Object template) {
        final NamedNodeMap attrs = n.getAttributes();
        final StatsSet set = new StatsSet();

        set.set("name", attrs.getNamedItem("name").getNodeValue());
        set.set("object", template);
        if (attrs.getNamedItem("count") != null) {
            set.set("count", parseNumber(attrs.getNamedItem("count").getNodeValue()).intValue());
        }
        if (attrs.getNamedItem("time") != null) {
            set.set("time", parseNumber(attrs.getNamedItem("time").getNodeValue()).intValue());
        }

        set.set("value", attrs.getNamedItem("val") != null ? parseNumber(attrs.getNamedItem("val").getNodeValue()).doubleValue() : 0.);

        if (attrs.getNamedItem("abnormal") != null) {
            set.set("abnormal", attrs.getNamedItem("abnormal").getNodeValue());
        }
        if (attrs.getNamedItem("stackType") != null) {
            set.set("stackType", attrs.getNamedItem("stackType").getNodeValue());
        }
        if (attrs.getNamedItem("stackType2") != null) {
            set.set("stackType2", attrs.getNamedItem("stackType2").getNodeValue());
        }
        if (attrs.getNamedItem("stackOrder") != null) {
            set.set("stackOrder", parseNumber(attrs.getNamedItem("stackOrder").getNodeValue()).intValue());
        }

        if (attrs.getNamedItem("applyOnCaster") != null) {
            set.set("applyOnCaster", Boolean.parseBoolean(attrs.getNamedItem("applyOnCaster").getNodeValue()));
        }
        if (attrs.getNamedItem("applyOnSummon") != null) {
            set.set("applyOnSummon", Boolean.parseBoolean(attrs.getNamedItem("applyOnSummon").getNodeValue()));
        }

        if (attrs.getNamedItem("displayId") != null) {
            set.set("displayId", parseNumber(attrs.getNamedItem("displayId").getNodeValue()).intValue());
        }
        if (attrs.getNamedItem("displayLevel") != null) {
            set.set("displayLevel", parseNumber(attrs.getNamedItem("displayLevel").getNodeValue()).intValue());
        }
        if (attrs.getNamedItem("chance") != null) {
            set.set("chance", parseNumber(attrs.getNamedItem("chance").getNodeValue()).intValue());
        }
        if (attrs.getNamedItem("cancelOnAction") != null) {
            set.set("cancelOnAction", Boolean.parseBoolean(attrs.getNamedItem("cancelOnAction").getNodeValue()));
        }
        if (attrs.getNamedItem("isOffensive") != null) {
            set.set("isOffensive", Boolean.parseBoolean(attrs.getNamedItem("isOffensive").getNodeValue()));
        }
        if (attrs.getNamedItem("isReflectable") != null) {
            set.set("isReflectable", Boolean.parseBoolean(attrs.getNamedItem("isReflectable").getNodeValue()));
        }
        if (attrs.getNamedItem("ignoreInvul") != null) {
            set.set("ignoreInvul", Boolean.parseBoolean(attrs.getNamedItem("ignoreInvul").getNodeValue()));
        }
        if (attrs.getNamedItem("removeOnCritical") != null) {
            set.set("removeOnCritical", Boolean.parseBoolean(attrs.getNamedItem("removeOnCritical").getNodeValue()));
        }
        if (attrs.getNamedItem("removeOnCriticalChance") != null) {
            set.set("removeOnCriticalChance", Integer.parseInt(attrs.getNamedItem("removeOnCriticalChance").getNodeValue()));
        }

        final EffectTemplate lt = new EffectTemplate(set);

        parseTemplate(n, lt);
        for (Node n1 = n.getFirstChild(); n1 != null; n1 = n1.getNextSibling()) {
            if ("triggers".equalsIgnoreCase(n1.getNodeName())) {
                parseTrigger(n1, lt);
            }
        }

        if (template instanceof Skill) {
            ((Skill) template).attach(lt);
        }
    }

    protected Condition parseCondition(Node n) {
        while (n != null && n.getNodeType() != Node.ELEMENT_NODE) {
            n = n.getNextSibling();
        }
        if (n == null) {
            return null;
        }
        if ("and".equalsIgnoreCase(n.getNodeName())) {
            return parseLogicAnd(n);
        }
        if ("or".equalsIgnoreCase(n.getNodeName())) {
            return parseLogicOr(n);
        }
        if ("not".equalsIgnoreCase(n.getNodeName())) {
            return parseLogicNot(n);
        }
        if ("player".equalsIgnoreCase(n.getNodeName())) {
            return parsePlayerCondition(n);
        }
        if ("target".equalsIgnoreCase(n.getNodeName())) {
            return parseTargetCondition(n);
        }
        if ("has".equalsIgnoreCase(n.getNodeName())) {
            return parseHasCondition(n);
        }
        if ("using".equalsIgnoreCase(n.getNodeName())) {
            return parseUsingCondition(n);
        }
        if ("game".equalsIgnoreCase(n.getNodeName())) {
            return parseGameCondition(n);
        }
        if ("zone".equalsIgnoreCase(n.getNodeName())) {
            return parseZoneCondition(n);
        }
        if ("pts".equalsIgnoreCase(n.getNodeName())) {
            return parsePTSCondition(n);
        }
        return null;
    }

    protected Condition parseLogicAnd(Node n) {
        final ConditionLogicAnd cond = new ConditionLogicAnd();
        for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                cond.add(parseCondition(n));
            }
        }
        if (cond._conditions == null || cond._conditions.length == 0) {
            _log.error("Empty <and> condition in " + file);
        }
        return cond;
    }

    protected Condition parseLogicOr(Node n) {
        final ConditionLogicOr cond = new ConditionLogicOr();
        for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                cond.add(parseCondition(n));
            }
        }
        if (cond._conditions == null || cond._conditions.length == 0) {
            _log.error("Empty <or> condition in " + file);
        }
        return cond;
    }

    protected Condition parseLogicNot(Node n) {
        for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                return new ConditionLogicNot(parseCondition(n));
            }
        }
        _log.error("Empty <not> condition in " + file);
        return null;
    }

    protected Condition parsePTSCondition(final Node n) {
        Condition cond = null;
        final NamedNodeMap attrs = n.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            final Node a = attrs.item(i);
            final String nodeName = a.getNodeName();
            final String nodeValue = a.getNodeValue();

            if ("op_agathion_energy".equalsIgnoreCase(nodeName)) {
                final int val = parseNumber(a.getNodeValue()).intValue();
                cond = joinAnd(cond, new Condition_op_agathion_energy(val));
            } else if ("op_encumbered".equalsIgnoreCase(nodeName)) {
                final String[] st = a.getNodeValue().split(";");
                cond = joinAnd(cond, new Condition_op_encumbered(Integer.parseInt(st[0]), Integer.parseInt(st[1])));
            } else if ("soul_saved".equalsIgnoreCase(nodeName)) {
                final int val = parseNumber(a.getNodeValue()).intValue();
                cond = joinAnd(cond, new Condition_soul_saved(val));
            } else if ("op_exist_npc".equalsIgnoreCase(nodeName)) {
                String name = n.getAttributes().getNamedItem("radius").getNodeValue();
                cond = joinAnd(cond, new Condition_op_exist_npc(nodeValue, Integer.parseInt(name)));
            } else if ("check_sex".equalsIgnoreCase(nodeName)) {
                final String val = a.getNodeValue();
                cond = joinAnd(cond, new Condition_check_sex(val));
            } else if ("op_check_abnormal".equalsIgnoreCase(nodeName)) {
                final String type = a.getNodeValue();
                final int level = parseNumber(n.getAttributes().getNamedItem("level").getNodeValue()).intValue();
                final String target = n.getAttributes().getNamedItem("target").getNodeValue();
                cond = joinAnd(cond, new Condition_op_check_abnormal(type, level, target));
            } else if ("can_summon_cubic".equalsIgnoreCase(nodeName)) {
                cond = joinAnd(cond, new Condition_can_summon_cubic());
            } else if ("op_use_praseed".equalsIgnoreCase(nodeName)) {
                final int val = parseNumber(a.getNodeValue()).intValue();
                cond = joinAnd(cond, new Condition_op_use_praseed(val));
            } else if ("op_need_agathion".equalsIgnoreCase(nodeName)) {
                final boolean val = Boolean.parseBoolean(a.getNodeValue());
                cond = joinAnd(cond, new Condition_op_need_agathion(val));
            } else if ("can_transform".equalsIgnoreCase(nodeName)) {
                final int val = parseNumber(a.getNodeValue()).intValue();
                cond = joinAnd(cond, new Condition_can_transform(val));
            } else if ("can_transform_in_dominion".equalsIgnoreCase(nodeName)) {
                final int val = parseNumber(a.getNodeValue()).intValue();
                cond = joinAnd(cond, new Condition_can_transform_in_dominion(val));
            } else if ("can_untransform".equalsIgnoreCase(nodeName)) {
                final boolean val = Boolean.parseBoolean(a.getNodeValue());
                cond = joinAnd(cond, new Condition_can_untransform(val));
            }
        }
        if (cond == null) {
            _log.error("Unrecognized <PTS> condition in " + file);
        }
        return cond;
    }

    protected Condition parsePlayerCondition(final Node n) {
        Condition cond = null;
        final NamedNodeMap attrs = n.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            final Node a = attrs.item(i);
            final String nodeName = a.getNodeName();
            if ("race".equalsIgnoreCase(nodeName)) {
                cond = joinAnd(cond, new ConditionPlayerRace(a.getNodeValue()));
            } else if ("minLevel".equalsIgnoreCase(nodeName)) {
                final int lvl = parseNumber(a.getNodeValue()).intValue();
                cond = joinAnd(cond, new ConditionPlayerMinLevel(lvl));
            } else if ("summon_siege_golem".equalsIgnoreCase(nodeName)) {
                cond = joinAnd(cond, new ConditionPlayerSummonSiegeGolem());
            } else if ("has_summon".equalsIgnoreCase(nodeName)) {
                final boolean val = Boolean.parseBoolean(a.getNodeValue());
                cond = joinAnd(cond, new ConditionPlayerHasSummon(val));
            } else if ("maxLevel".equalsIgnoreCase(nodeName)) {
                final int lvl = parseNumber(a.getNodeValue()).intValue();
                cond = joinAnd(cond, new ConditionPlayerMaxLevel(lvl));
            } else if ("maxPK".equalsIgnoreCase(nodeName)) {
                final int pk = parseNumber(a.getNodeValue()).intValue();
                cond = joinAnd(cond, new ConditionPlayerMaxPK(pk));
            } else if ("resting".equalsIgnoreCase(nodeName)) {
                final boolean val = Boolean.parseBoolean(a.getNodeValue());
                cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.RESTING, val));
            } else if ("moving".equalsIgnoreCase(nodeName)) {
                final boolean val = Boolean.parseBoolean(a.getNodeValue());
                cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.MOVING, val));
            } else if ("running".equalsIgnoreCase(nodeName)) {
                final boolean val = Boolean.parseBoolean(a.getNodeValue());
                cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.RUNNING, val));
            } else if ("standing".equalsIgnoreCase(nodeName)) {
                final boolean val = Boolean.parseBoolean(a.getNodeValue());
                cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.STANDING, val));
            } else if ("flying".equalsIgnoreCase(a.getNodeName())) {
                final boolean val = Boolean.parseBoolean(a.getNodeValue());
                cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.FLYING, val));
            } else if ("flyingTransform".equalsIgnoreCase(a.getNodeName())) {
                final boolean val = Boolean.parseBoolean(a.getNodeValue());
                cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.FLYING_TRANSFORM, val));
            } else if ("boat".equalsIgnoreCase(a.getNodeName())) {
                cond = joinAnd(cond, new ConditionPlayerBoat(Boolean.parseBoolean(a.getNodeValue())));
            } else if ("olympiad".equalsIgnoreCase(a.getNodeName())) {
                final boolean val = Boolean.parseBoolean(a.getNodeValue());
                cond = joinAnd(cond, new ConditionPlayerOlympiad(val));
            } else if ("percentHP".equalsIgnoreCase(nodeName)) {
                final int percentHP = parseNumber(a.getNodeValue()).intValue();
                cond = joinAnd(cond, new ConditionPlayerPercentHp(percentHP));
            } else if ("percentMP".equalsIgnoreCase(nodeName)) {
                final int percentMP = parseNumber(a.getNodeValue()).intValue();
                cond = joinAnd(cond, new ConditionPlayerPercentMp(percentMP));
            } else if ("percentCP".equalsIgnoreCase(nodeName)) {
                final int percentCP = parseNumber(a.getNodeValue()).intValue();
                cond = joinAnd(cond, new ConditionPlayerPercentCp(percentCP));
            } else if ("instance_zone".equalsIgnoreCase(nodeName)) {
                final int id = parseNumber(a.getNodeValue()).intValue();
                cond = joinAnd(cond, new ConditionPlayerInstanceZone(id));
            } else if ("riding".equalsIgnoreCase(nodeName)) {
                final String riding = a.getNodeValue();
                if ("strider".equalsIgnoreCase(riding)) {
                    cond = joinAnd(cond, new ConditionPlayerRiding(CheckPlayerRiding.STRIDER));
                } else if ("wyvern".equalsIgnoreCase(riding)) {
                    cond = joinAnd(cond, new ConditionPlayerRiding(CheckPlayerRiding.WYVERN));
                } else if ("none".equalsIgnoreCase(riding)) {
                    cond = joinAnd(cond, new ConditionPlayerRiding(CheckPlayerRiding.NONE));
                }
            } else if ("classId".equalsIgnoreCase(nodeName)) {
                cond = joinAnd(cond, new ConditionPlayerClassId(a.getNodeValue().split(",")));
            } else if ("hasBuffId".equalsIgnoreCase(nodeName)) {
                final StringTokenizer st = new StringTokenizer(a.getNodeValue(), ";");
                final int id = Integer.parseInt(st.nextToken().trim());
                int level = -1;
                if (st.hasMoreTokens()) {
                    level = Integer.parseInt(st.nextToken().trim());
                }
                cond = joinAnd(cond, new ConditionPlayerHasBuffId(id, level));
            } else if ("hasBuff".equalsIgnoreCase(nodeName)) {
                final StringTokenizer st = new StringTokenizer(a.getNodeValue(), ";");
                final EffectType et = Enum.valueOf(EffectType.class, st.nextToken().trim());
                int level = -1;
                if (st.hasMoreTokens()) {
                    level = Integer.parseInt(st.nextToken().trim());
                }
                cond = joinAnd(cond, new ConditionPlayerHasBuff(et, level));
            } else if ("damage".equalsIgnoreCase(nodeName)) {
                final String[] st = a.getNodeValue().split(";");
                cond = joinAnd(cond, new ConditionPlayerMinMaxDamage(Double.parseDouble(st[0]), Double.parseDouble(st[1])));
            } else if ("chargesMin".equalsIgnoreCase(nodeName)) {
                final int val = parseNumber(a.getNodeValue()).intValue();
                cond = joinAnd(cond, new ConditionPlayerChargesMin(val));
            } else if ("chargesMax".equalsIgnoreCase(nodeName)) {
                final int val = parseNumber(a.getNodeValue()).intValue();
                cond = joinAnd(cond, new ConditionPlayerChargesMax(val));
            }
        }

        if (cond == null) {
            _log.error("Unrecognized <player> condition in " + file);
        }
        return cond;
    }

    protected Condition parseTargetCondition(final Node n) {
        Condition cond = null;
        final NamedNodeMap attrs = n.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            final Node a = attrs.item(i);
            final String nodeName = a.getNodeName();
            final String nodeValue = a.getNodeValue();
            if ("aggro".equalsIgnoreCase(nodeName)) {
                cond = joinAnd(cond, new ConditionTargetAggro(Boolean.parseBoolean(nodeValue)));
            } else if ("pvp".equalsIgnoreCase(nodeName)) {
                cond = joinAnd(cond, new ConditionTargetPlayable(Boolean.parseBoolean(nodeValue)));
            } else if ("player".equalsIgnoreCase(nodeName)) {
                cond = joinAnd(cond, new ConditionTargetPlayer(Boolean.parseBoolean(nodeValue)));
            } else if ("summon".equalsIgnoreCase(nodeName)) {
                cond = joinAnd(cond, new ConditionTargetSummon(Boolean.parseBoolean(nodeValue)));
            } else if ("mob".equalsIgnoreCase(nodeName)) {
                cond = joinAnd(cond, new ConditionTargetMob(Boolean.parseBoolean(nodeValue)));
            } else if ("mobId".equalsIgnoreCase(nodeName)) {
                cond = joinAnd(cond, new ConditionTargetMobId(Integer.parseInt(nodeValue)));
            } else if ("race".equalsIgnoreCase(nodeName)) {
                cond = joinAnd(cond, new ConditionTargetRace(nodeValue));
            } else if ("npc_class".equalsIgnoreCase(nodeName)) {
                cond = joinAnd(cond, new ConditionTargetNpcClass(nodeValue));
            } else if ("playerRace".equalsIgnoreCase(nodeName)) {
                cond = joinAnd(cond, new ConditionTargetPlayerRace(nodeValue));
            } else if ("forbiddenClassIds".equalsIgnoreCase(nodeName)) {
                cond = joinAnd(cond, new ConditionTargetForbiddenClassId(nodeValue.split(";")));
            } else if ("playerSameClan".equalsIgnoreCase(nodeName)) {
                cond = joinAnd(cond, new ConditionTargetClan(nodeValue));
            } else if ("castledoor".equalsIgnoreCase(nodeName)) {
                cond = joinAnd(cond, new ConditionTargetCastleDoor(Boolean.parseBoolean(nodeValue)));
            } else if ("direction".equalsIgnoreCase(nodeName)) {
                cond = joinAnd(cond, new ConditionTargetDirection(PositionUtils.TargetDirection.valueOf(nodeValue.toUpperCase())));
            } else if ("percentHP".equalsIgnoreCase(nodeName)) {
                cond = joinAnd(cond, new ConditionTargetPercentHp(parseNumber(a.getNodeValue()).intValue()));
            } else if ("percentMP".equalsIgnoreCase(nodeName)) {
                cond = joinAnd(cond, new ConditionTargetPercentMp(parseNumber(a.getNodeValue()).intValue()));
            } else if ("percentCP".equalsIgnoreCase(nodeName)) {
                cond = joinAnd(cond, new ConditionTargetPercentCp(parseNumber(a.getNodeValue()).intValue()));
            } else if ("hasBuffId".equalsIgnoreCase(nodeName)) {
                final StringTokenizer st = new StringTokenizer(nodeValue, ";");
                final int id = Integer.parseInt(st.nextToken().trim());
                int level = -1;
                if (st.hasMoreTokens()) {
                    level = Integer.parseInt(st.nextToken().trim());
                }
                cond = joinAnd(cond, new ConditionTargetHasBuffId(id, level));
            } else if ("hasBuff".equalsIgnoreCase(nodeName)) {
                final StringTokenizer st = new StringTokenizer(nodeValue, ";");
                final EffectType et = Enum.valueOf(EffectType.class, st.nextToken().trim());
                int level = -1;
                if (st.hasMoreTokens()) {
                    level = Integer.parseInt(st.nextToken().trim());
                }
                cond = joinAnd(cond, new ConditionTargetHasBuff(et, level));
            } else if ("hasForbiddenSkill".equalsIgnoreCase(nodeName)) {
                cond = joinAnd(cond, new ConditionTargetHasForbiddenSkill(parseNumber(a.getNodeValue()).intValue()));
            }
        }
        if (cond == null) {
            _log.error("Unrecognized <target> condition in " + file);
        }
        return cond;
    }

    protected Condition parseUsingCondition(final Node n) {
        Condition cond = null;
        final NamedNodeMap attrs = n.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            final Node a = attrs.item(i);
            final String nodeName = a.getNodeName();
            final String nodeValue = a.getNodeValue();
            if ("kind".equalsIgnoreCase(nodeName) || "weapon".equalsIgnoreCase(nodeName)) {
                long mask = 0;
                final StringTokenizer st = new StringTokenizer(nodeValue, ",");
                tokens:
                while (st.hasMoreTokens()) {
                    final String item = st.nextToken().trim();
                    for (final WeaponType wt : WeaponType.VALUES) {
                        if (wt.toString().equalsIgnoreCase(item)) {
                            mask |= wt.mask();
                            continue tokens;
                        }
                    }
                    for (final ArmorType at : ArmorType.VALUES) {
                        if (at.toString().equalsIgnoreCase(item)) {
                            mask |= at.mask();
                            continue tokens;
                        }
                    }
                    _log.error("Invalid item kind: \"" + item + "\" in " + file);
                }
                if (mask != 0) {
                    cond = joinAnd(cond, new ConditionUsingItemType(mask));
                }
            } else if ("armor".equalsIgnoreCase(nodeName)) {
                final ArmorType armor = ArmorType.valueOf(nodeValue.toUpperCase());
                cond = joinAnd(cond, new ConditionUsingArmor(armor));
            } else if ("skill".equalsIgnoreCase(nodeName)) {
                cond = joinAnd(cond, new ConditionUsingSkill(Integer.parseInt(nodeValue)));
            } else if ("slotitem".equalsIgnoreCase(nodeName)) {
                final StringTokenizer st = new StringTokenizer(nodeValue, ";");
                final int id = Integer.parseInt(st.nextToken().trim());
                final int slot = Integer.parseInt(st.nextToken().trim());
                int enchant = 0;
                if (st.hasMoreTokens()) {
                    enchant = Integer.parseInt(st.nextToken().trim());
                }
                cond = joinAnd(cond, new ConditionSlotItemId(slot, id, enchant));
            }
        }
        if (cond == null) {
            _log.error("Unrecognized <using> condition in " + file);
        }
        return cond;
    }

    protected Condition parseHasCondition(final Node n) {
        Condition cond = null;
        final NamedNodeMap attrs = n.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            final Node a = attrs.item(i);
            final String nodeName = a.getNodeName();
            final String nodeValue = a.getNodeValue();
            if ("skill".equalsIgnoreCase(nodeName)) {
                final StringTokenizer st = new StringTokenizer(nodeValue, ";");
                final Integer id = parseNumber(st.nextToken().trim()).intValue();
                final int level = parseNumber(st.nextToken().trim()).shortValue();
                cond = joinAnd(cond, new ConditionHasSkill(id, level));
            } else if ("success".equalsIgnoreCase(nodeName)) {
                cond = joinAnd(cond, new ConditionFirstEffectSuccess(Boolean.parseBoolean(nodeValue)));
            }
        }
        if (cond == null) {
            _log.error("Unrecognized <has> condition in " + file);
        }
        return cond;
    }

    protected Condition parseGameCondition(final Node n) {
        Condition cond = null;
        final NamedNodeMap attrs = n.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            final Node a = attrs.item(i);
            if ("night".equalsIgnoreCase(a.getNodeName())) {
                final boolean val = Boolean.parseBoolean(a.getNodeValue());
                cond = joinAnd(cond, new ConditionGameTime(CheckGameTime.NIGHT, val));
            }
        }
        if (cond == null) {
            _log.error("Unrecognized <game> condition in " + file);
        }
        return cond;
    }

    protected Condition parseZoneCondition(final Node n) {
        Condition cond = null;
        final NamedNodeMap attrs = n.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            final Node a = attrs.item(i);
            if ("type".equalsIgnoreCase(a.getNodeName())) {
                cond = joinAnd(cond, new ConditionZoneType(a.getNodeValue()));
            }
        }
        if (cond == null) {
            _log.error("Unrecognized <zone> condition in " + file);
        }
        return cond;
    }

    protected Object[] parseTable(final Node n) {
        final NamedNodeMap attrs = n.getAttributes();
        final String name = attrs.getNamedItem("name").getNodeValue();
        if (name.charAt(0) != '#') {
            throw new IllegalArgumentException("Table name must start with #");
        }
        final StringTokenizer data = new StringTokenizer(n.getFirstChild().getNodeValue());
        final List<String> array = new ArrayList<>();
        while (data.hasMoreTokens()) {
            array.add(data.nextToken());
        }
        final Object[] res = array.toArray(new Object[array.size()]);
        setTable(name, res);
        return res;
    }

    protected void parseBeanSet(final Node n, final StatsSet set, final int level) {
        try {
            final String name = n.getAttributes().getNamedItem("name").getNodeValue().trim();
            String value = n.getAttributes().getNamedItem("val").getNodeValue().trim();
            final char ch = value.isEmpty() ? ' ' : value.charAt(0);
            if (value.contains("#") && ch != '#') // кошмарная затычка на таблицы в сложных строках вроде triggerActions
            {
                for (final String str : value.split("[;: ]+")) {
                    if (str.charAt(0) == '#') {
                        value = value.replace(str, String.valueOf(getTableValue(str, level)));
                    }
                }
            }
            if (ch == '#') {
                final Object tableVal = getTableValue(value, level);
                final Number parsedVal = parseNumber(tableVal.toString());
                set.set(name, parsedVal == null ? tableVal : String.valueOf(parsedVal));
            } else if ((Character.isDigit(ch) || ch == '-') && !value.contains(" ") && !value.contains(";")) {
                set.set(name, String.valueOf(parseNumber(value)));
            } else {
                set.set(name, value);
            }
        } catch (Exception e) {
            System.out.println(n.getAttributes().getNamedItem("name") + " " + set.get("skill_id"));
            e.printStackTrace();
        }
    }

    /**
     * Разбирает параметр Value как число, приводя его к Number, либо возвращает значение из таблицы если строка начинается с #
     */
    protected Number parseNumber(String value) {
        if (value.charAt(0) == '#') {
            value = getTableValue(value).toString();
        }
        try {
            if ("max".equalsIgnoreCase(value)) {
                return Double.POSITIVE_INFINITY;
            }
            if ("min".equalsIgnoreCase(value)) {
                return Double.NEGATIVE_INFINITY;
            }

            if (value.indexOf('.') == -1) {
                int radix = 10;
                if (value.length() > 2 && "0x".equalsIgnoreCase(value.substring(0, 2))) {
                    value = value.substring(2);
                    radix = 16;
                }
                return Integer.parseInt(value, radix);
            }
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected Condition joinAnd(final Condition cond, final Condition c) {
        if (cond == null) {
            return c;
        }
        if (cond instanceof ConditionLogicAnd) {
            ((ConditionLogicAnd) cond).add(c);
            return cond;
        }
        final ConditionLogicAnd and = new ConditionLogicAnd();
        and.add(cond);
        and.add(c);
        return and;
    }
}