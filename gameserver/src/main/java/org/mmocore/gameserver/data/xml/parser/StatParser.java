package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.commons.data.xml.AbstractDirParser;
import org.mmocore.gameserver.stats.StatTemplate;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.stats.conditions.*;
import org.mmocore.gameserver.stats.funcs.FuncTemplate;
import org.mmocore.gameserver.stats.triggers.TriggerInfo;
import org.mmocore.gameserver.stats.triggers.TriggerType;
import org.mmocore.gameserver.templates.item.ArmorTemplate;
import org.mmocore.gameserver.templates.item.WeaponTemplate;

import java.util.List;
import java.util.StringTokenizer;

/**
 * @author VISTALL
 * @date 13:48/10.01.2011
 */
public abstract class StatParser<H extends AbstractHolder> extends AbstractDirParser<H> {
    protected StatParser(final H holder) {
        super(holder);
    }

    protected Condition parseFirstCond(final Element sub) {
        final List<Element> e = sub.getChildren();
        if (e.isEmpty()) {
            return null;
        }
        final Element element = e.get(0);

        return parseCond(element);
    }

    protected Condition parseCond(final Element element) {
        final String name = element.getName();
        if ("and".equalsIgnoreCase(name)) {
            return parseLogicAnd(element);
        } else if ("or".equalsIgnoreCase(name)) {
            return parseLogicOr(element);
        } else if ("not".equalsIgnoreCase(name)) {
            return parseLogicNot(element);
        } else if ("target".equalsIgnoreCase(name)) {
            return parseTargetCondition(element);
        } else if ("player".equalsIgnoreCase(name)) {
            return parsePlayerCondition(element);
        } else if ("using".equalsIgnoreCase(name)) {
            return parseUsingCondition(element);
        } else if ("zone".equalsIgnoreCase(name)) {
            return parseZoneCondition(element);
        }

        return null;
    }

    protected Condition parseLogicAnd(final Element n) {
        final ConditionLogicAnd cond = new ConditionLogicAnd();
        for (final Element condElement : n.getChildren()) {
            cond.add(parseCond(condElement));
        }

        if (cond._conditions == null || cond._conditions.length == 0) {
            error("Empty <and> condition in " + getCurrentFileName());
        }
        return cond;
    }

    protected Condition parseLogicOr(final Element n) {
        final ConditionLogicOr cond = new ConditionLogicOr();
        for (final Element condElement : n.getChildren()) {
            cond.add(parseCond(condElement));
        }

        if (cond._conditions == null || cond._conditions.length == 0) {
            error("Empty <or> condition in " + getCurrentFileName());
        }
        return cond;
    }

    protected Condition parseLogicNot(final Element n) {
        for (final Element element : n.getChildren()) {
            return new ConditionLogicNot(parseCond(element));
        }

        error("Empty <not> condition in " + getCurrentFileName());
        return null;
    }

    protected Condition parseTargetCondition(final Element element) {
        Condition cond = null;
        for (final Attribute attribute : element.getAttributes()) {
            final String name = attribute.getName();
            final String value = attribute.getValue();
            if ("pvp".equalsIgnoreCase(name)) {
                cond = joinAnd(cond, new ConditionTargetPlayable(Boolean.parseBoolean(value)));
            }
        }

        return cond;
    }

    protected Condition parseZoneCondition(final Element element) {
        Condition cond = null;
        for (final Attribute attribute : element.getAttributes()) {
            final String name = attribute.getName();
            final String value = attribute.getValue();
            if ("type".equalsIgnoreCase(name)) {
                cond = joinAnd(cond, new ConditionZoneType(value));
            }
        }

        return cond;
    }

    protected Condition parsePlayerCondition(final Element element) {
        Condition cond = null;
        for (final Attribute attribute : element.getAttributes()) {
            final String name = attribute.getName();
            final String value = attribute.getValue();
            if ("residence".equalsIgnoreCase(name)) {
                final String[] st = value.split(";");
                cond = joinAnd(cond, new ConditionPlayerResidence(Integer.parseInt(st[1]), st[0]));
            } else if ("classId".equalsIgnoreCase(name)) {
                cond = joinAnd(cond, new ConditionPlayerClassId(value.split(",")));
            } else if ("mage_class".equalsIgnoreCase(name)) {
                cond = joinAnd(cond, new ConditionPlayerIsMageClass(Boolean.parseBoolean(value)));
            } else if ("olympiad".equalsIgnoreCase(name)) {
                cond = joinAnd(cond, new ConditionPlayerOlympiad(Boolean.parseBoolean(value)));
            } else if ("instance_zone".equalsIgnoreCase(name)) {
                cond = joinAnd(cond, new ConditionPlayerInstanceZone(Integer.parseInt(value)));
            } else if ("race".equalsIgnoreCase(name)) {
                cond = joinAnd(cond, new ConditionPlayerRace(value));
            } else if ("damage".equalsIgnoreCase(name)) {
                final String[] st = value.split(";");
                cond = joinAnd(cond, new ConditionPlayerMinMaxDamage(Double.parseDouble(st[0]), Double.parseDouble(st[1])));
            } else if ("has_pet".equalsIgnoreCase(name)) {
                final String[] st = value.split(";");
                cond = joinAnd(cond, new ConditionPlayerHasPet(st));
            } else if ("mounted".equalsIgnoreCase(name)) {
                final String[] st = value.split(";");
                cond = joinAnd(cond, new ConditionPlayerMounted(st));
            }
        }

        return cond;
    }

    protected Condition parseUsingCondition(final Element element) {
        Condition cond = null;
        for (final Attribute attribute : element.getAttributes()) {
            final String name = attribute.getName();
            final String value = attribute.getValue();
            if ("slotitem".equalsIgnoreCase(name)) {
                final StringTokenizer st = new StringTokenizer(value, ";");
                final int id = Integer.parseInt(st.nextToken().trim());
                final int slot = Integer.parseInt(st.nextToken().trim());
                int enchant = 0;
                if (st.hasMoreTokens()) {
                    enchant = Integer.parseInt(st.nextToken().trim());
                }
                cond = joinAnd(cond, new ConditionSlotItemId(slot, id, enchant));
            } else if ("kind".equalsIgnoreCase(name) || "weapon".equalsIgnoreCase(name)) {
                long mask = 0;
                final StringTokenizer st = new StringTokenizer(value, ",");
                tokens:
                while (st.hasMoreTokens()) {
                    final String item = st.nextToken().trim();
                    for (final WeaponTemplate.WeaponType wt : WeaponTemplate.WeaponType.VALUES) {
                        if (wt.toString().equalsIgnoreCase(item)) {
                            mask |= wt.mask();
                            continue tokens;
                        }
                    }
                    for (final ArmorTemplate.ArmorType at : ArmorTemplate.ArmorType.VALUES) {
                        if (at.toString().equalsIgnoreCase(item)) {
                            mask |= at.mask();
                            continue tokens;
                        }
                    }

                    error("Invalid item kind: \"" + item + "\" in " + getCurrentFileName());
                }
                if (mask != 0) {
                    cond = joinAnd(cond, new ConditionUsingItemType(mask));
                }
            } else if ("skill".equalsIgnoreCase(name)) {
                cond = joinAnd(cond, new ConditionUsingSkill(Integer.parseInt(value)));
            }
        }
        return cond;
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

    protected void parseFor(final Element forElement, final StatTemplate template) {
        for (final Element element : forElement.getChildren()) {
            final String elementName = element.getName();
            if ("add".equalsIgnoreCase(elementName)) {
                attachFunc(element, template, "Add");
            } else if ("set".equalsIgnoreCase(elementName)) {
                attachFunc(element, template, "Set");
            } else if ("sub".equalsIgnoreCase(elementName)) {
                attachFunc(element, template, "Sub");
            } else if ("mul".equalsIgnoreCase(elementName)) {
                attachFunc(element, template, "Mul");
            } else if ("div".equalsIgnoreCase(elementName)) {
                attachFunc(element, template, "Div");
            } else if ("enchant".equalsIgnoreCase(elementName)) {
                attachFunc(element, template, "Enchant");
            }
        }
    }

    protected void parseTriggers(final Element f, final StatTemplate triggerable) {
        for (final Element element : f.getChildren()) {
            final int id = parseNumber(element.getAttributeValue("id")).intValue();
            final int level = parseNumber(element.getAttributeValue("level")).intValue();
            final TriggerType t = TriggerType.valueOf(element.getAttributeValue("type"));
            final double chance = parseNumber(element.getAttributeValue("chance")).doubleValue();

            final TriggerInfo trigger = new TriggerInfo(id, level, t, chance);

            triggerable.addTrigger(trigger);
            for (final Element subElement : element.getChildren()) {
                final Condition condition = parseFirstCond(subElement);
                if (condition != null) {
                    trigger.addCondition(condition);
                }
            }
        }
    }

    protected void attachFunc(final Element n, final StatTemplate template, final String name) {
        final Stats stat = Stats.valueOfXml(n.getAttributeValue("stat"));
        final String order = n.getAttributeValue("order");
        final int ord = parseNumber(order).intValue();
        final Condition applyCond = parseFirstCond(n);
        double val = 0;
        if (n.getAttributeValue("value") != null) {
            val = parseNumber(n.getAttributeValue("value")).doubleValue();
        }

        template.attachFunc(new FuncTemplate(applyCond, name, stat, ord, val));
    }

    protected Number parseNumber(String value) {
        if (value.charAt(0) == '#') {
            value = getTableValue(value).toString();
        }
        try {
            if (value.indexOf('.') == -1) {
                int radix = 10;
                if (value.length() > 2 && "0x".equalsIgnoreCase(value.substring(0, 2))) {
                    value = value.substring(2);
                    radix = 16;
                }
                return Integer.valueOf(value, radix);
            }
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected abstract Object getTableValue(String name);
}
