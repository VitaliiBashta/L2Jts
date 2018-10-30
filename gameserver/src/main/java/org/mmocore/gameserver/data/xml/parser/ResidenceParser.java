package org.mmocore.gameserver.data.xml.parser;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractDirParser;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.model.TeleportLocation;
import org.mmocore.gameserver.model.entity.SevenSigns;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.entity.residence.Fortress;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.model.entity.residence.ResidenceFunction;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.item.support.MerchantGuard;
import org.mmocore.gameserver.utils.Location;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 * @date 0:18/12.02.2011
 */
public final class ResidenceParser extends AbstractDirParser<ResidenceHolder> {
    private ResidenceParser() {
        super(ResidenceHolder.getInstance());
    }

    public static ResidenceParser getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override
    public File getXMLDir() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/residences/");
    }

    @Override
    public boolean isIgnored(final File f) {
        return false;
    }

    @Override
    public String getDTDFileName() {
        return "residence.dtd";
    }

    @Override
    protected void readData(final ResidenceHolder holder, final Element rootElement) throws Exception {
        final String impl = rootElement.getAttributeValue("impl");
        Class<?> clazz;

        final StatsSet set = new StatsSet();
        for (final Attribute element : rootElement.getAttributes()) {
            set.set(element.getName(), element.getValue());
        }

        Residence residence;
        try {
            clazz = Class.forName("org.mmocore.gameserver.model.entity.residence." + impl);
            final Constructor<?> constructor = clazz.getConstructor(StatsSet.class);
            residence = (Residence) constructor.newInstance(set);
            holder.addResidence(residence);
        } catch (Exception e) {
            error("fail to init: " + getCurrentFileName(), e);
            return;
        }

        for (final Element element : rootElement.getChildren()) {
            final String nodeName = element.getName();
            final int level = element.getAttributeValue("level") == null ? 0 : Integer.parseInt(element.getAttributeValue("level"));
            final int lease = (int) ((element.getAttributeValue("lease") == null ? 0 : Integer.parseInt(element.getAttributeValue("lease"))));
            final int npcId = element.getAttributeValue("npcId") == null ? 0 : Integer.parseInt(element.getAttributeValue("npcId"));
            final int listId = element.getAttributeValue("listId") == null ? 0 : Integer.parseInt(element.getAttributeValue("listId"));

            ResidenceFunction function = null;
            if ("teleport".equalsIgnoreCase(nodeName)) {
                function = checkAndGetFunction(residence, ResidenceFunction.TELEPORT);
                final List<TeleportLocation> targets = new ArrayList<>();
                for (final Element teleportElement : element.getChildren("target")) {
                    final int npcStringId = Integer.parseInt(teleportElement.getAttributeValue("name"));
                    final long price = Long.parseLong(teleportElement.getAttributeValue("price"));
                    final int itemId = Integer.parseInt(teleportElement.getAttributeValue("item", String.valueOf(ItemTemplate.ITEM_ID_ADENA)));
                    final TeleportLocation loc = new TeleportLocation(itemId, price, npcStringId, 0);
                    loc.set(Location.parseLoc(teleportElement.getAttributeValue("loc")));
                    targets.add(loc);
                }
                function.addTeleports(level, targets.toArray(new TeleportLocation[targets.size()]));
            } else if ("support".equalsIgnoreCase(nodeName)) {
                if (level > 9 && !AllSettingsConfig.ALT_CH_ALLOW_1H_BUFFS) {
                    continue;
                }
                function = checkAndGetFunction(residence, ResidenceFunction.SUPPORT);
                function.addBuffs(level);
            } else if ("item_create".equalsIgnoreCase(nodeName)) {
                function = checkAndGetFunction(residence, ResidenceFunction.ITEM_CREATE);
                function.addBuylist(level, new int[]{npcId, listId});
            } else if ("curtain".equalsIgnoreCase(nodeName)) {
                function = checkAndGetFunction(residence, ResidenceFunction.CURTAIN);
            } else if ("platform".equalsIgnoreCase(nodeName)) {
                function = checkAndGetFunction(residence, ResidenceFunction.PLATFORM);
            } else if ("restore_exp".equalsIgnoreCase(nodeName)) {
                function = checkAndGetFunction(residence, ResidenceFunction.RESTORE_EXP);
            } else if ("restore_hp".equalsIgnoreCase(nodeName)) {
                function = checkAndGetFunction(residence, ResidenceFunction.RESTORE_HP);
            } else if ("restore_mp".equalsIgnoreCase(nodeName)) {
                function = checkAndGetFunction(residence, ResidenceFunction.RESTORE_MP);
            } else if ("skills".equalsIgnoreCase(nodeName)) {
                for (final Element nextElement : element.getChildren()) {
                    final int id2 = Integer.parseInt(nextElement.getAttributeValue("id"));
                    final int level2 = Integer.parseInt(nextElement.getAttributeValue("level"));

                    final SkillEntry skill = SkillTable.getInstance().getSkillEntry(id2, level2);
                    if (skill != null) {
                        residence.addSkill(skill);
                    }
                }
            } else if ("banish_points".equalsIgnoreCase(nodeName)) {
                for (final Element banishPointsElement : element.getChildren()) {
                    final Location loc = Location.parse(banishPointsElement);
                    residence.addBanishPoint(loc);
                }
            } else if ("owner_restart_points".equalsIgnoreCase(nodeName)) {
                for (final Element ownerRestartPointsElement : element.getChildren()) {
                    final Location loc = Location.parse(ownerRestartPointsElement);
                    residence.addOwnerRestartPoint(loc);
                }
            } else if ("other_restart_points".equalsIgnoreCase(nodeName)) {
                for (final Element otherRestartPointsElement : element.getChildren()) {
                    final Location loc = Location.parse(otherRestartPointsElement);
                    residence.addOtherRestartPoint(loc);
                }
            } else if ("chaos_restart_points".equalsIgnoreCase(nodeName)) {
                for (final Element chaosRestartPointsElement : element.getChildren()) {
                    final Location loc = Location.parse(chaosRestartPointsElement);
                    residence.addChaosRestartPoint(loc);
                }
            } else if ("related_fortresses".equalsIgnoreCase(nodeName)) {
                for (final Element subElement : element.getChildren()) {
                    if ("domain".equalsIgnoreCase(subElement.getName())) {
                        ((Castle) residence).addRelatedFortress(Fortress.DOMAIN, Integer.parseInt(subElement.getAttributeValue("fortress")));
                    } else if ("boundary".equalsIgnoreCase(subElement.getName())) {
                        ((Castle) residence).addRelatedFortress(Fortress.BOUNDARY, Integer.parseInt(subElement.getAttributeValue("fortress")));
                    }
                }
            } else if ("merchant_guards".equalsIgnoreCase(nodeName)) {
                for (final Element subElement : element.getChildren()) {
                    final int itemId = Integer.parseInt(subElement.getAttributeValue("item_id"));
                    final int npcId2 = Integer.parseInt(subElement.getAttributeValue("npc_id"));
                    final int maxGuard = Integer.parseInt(subElement.getAttributeValue("max"));
                    final TIntSet intSet = new TIntHashSet(3);
                    final String[] ssq = subElement.getAttributeValue("ssq").split(";");
                    for (final String q : ssq) {
                        if ("cabal_null".equalsIgnoreCase(q)) {
                            intSet.add(SevenSigns.CABAL_NULL);
                        } else if ("cabal_dusk".equalsIgnoreCase(q)) {
                            intSet.add(SevenSigns.CABAL_DUSK);
                        } else if ("cabal_dawn".equalsIgnoreCase(q)) {
                            intSet.add(SevenSigns.CABAL_DAWN);
                        } else {
                            error("Unknown ssq type: " + q + "; file: " + getCurrentFileName());
                        }
                    }

                    ((Castle) residence).addMerchantGuard(new MerchantGuard(itemId, npcId2, maxGuard, intSet));
                }
            }

            if (function != null) {
                function.addLease(level, lease);
            }
        }
    }

    private ResidenceFunction checkAndGetFunction(final Residence residence, final int type) {
        ResidenceFunction function = residence.getFunction(type);
        if (function == null) {
            function = new ResidenceFunction(residence.getId(), type);
            residence.addFunction(function);
        }
        return function;
    }

    private static class LazyHolder {
        private static final ResidenceParser INSTANCE = new ResidenceParser();
    }
}
