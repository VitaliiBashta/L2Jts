package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractDirParser;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.SkillAcquireHolder;
import org.mmocore.gameserver.model.SkillLearn;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 * @date 20:55/30.11.2010
 */
public final class SkillAcquireParser extends AbstractDirParser<SkillAcquireHolder> {
    private SkillAcquireParser() {
        super(SkillAcquireHolder.getInstance());
    }

    public static SkillAcquireParser getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override
    public File getXMLDir() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/skill_tree/");
    }

    @Override
    public boolean isIgnored(final File b) {
        if (b.getName().equalsIgnoreCase("servitor_skill_tree.xml")) {
            return true;
        }
        return false;
    }

    @Override
    public String getDTDFileName() {
        return "tree.dtd";
    }

    @Override
    protected void readData(final SkillAcquireHolder holder, final Element rootElement) {
        for (final Element element : rootElement.getChildren("certification_skill_tree")) {
            holder.addAllCertificationLearns(parseSkillLearn(element));
        }

        for (final Element element : rootElement.getChildren("sub_unit_skill_tree")) {
            holder.addAllSubUnitLearns(parseSkillLearn(element));
        }

        for (final Element element : rootElement.getChildren("pledge_skill_tree")) {
            holder.addAllPledgeLearns(parseSkillLearn(element));
        }

        for (final Element element : rootElement.getChildren("collection_skill_tree")) {
            holder.addAllCollectionLearns(parseSkillLearn(element));
        }

        for (final Element element : rootElement.getChildren("fishing_skill_tree")) {
            holder.addAllFishingLearns(parseSkillLearn(element));
        }

        for (final Element element : rootElement.getChildren("fishing_non_dwarf_skill_tree")) {
            holder.addAllFishingNonDwarfLearns(parseSkillLearn(element));
        }

        for (final Element nxt : rootElement.getChildren("transfer_skill_tree")) {
            for (final Element classElement : nxt.getChildren("class")) {
                final int classId = Integer.parseInt(classElement.getAttributeValue("id"));
                final List<SkillLearn> learns = parseSkillLearn(classElement);
                holder.addAllTransferLearns(classId, learns);
            }
        }

        for (final Element nxt : rootElement.getChildren("normal_skill_tree")) {
            final Element classElement = nxt.getChild("class");

            final int classId = Integer.parseInt(classElement.getAttributeValue("id"));
            final List<SkillLearn> learns = parseSkillLearn(classElement);

            holder.addNormalSkillLearns(classId, learns);
        }

        for (final Element nxt : rootElement.getChildren("transformation_skill_tree")) {
            for (final Element classElement : nxt.getChildren("race")) {
                final int race = Integer.parseInt(classElement.getAttributeValue("id"));
                final List<SkillLearn> learns = parseSkillLearn(classElement);
                holder.addAllTransformationLearns(race, learns);
            }
        }
    }

    public List<SkillLearn> parseSkillLearn(final Element tree) {
        final List<SkillLearn> skillLearns = new ArrayList<>();
        for (final Element element : tree.getChildren("skill")) {
            final int id = Integer.parseInt(element.getAttributeValue("id"));
            final int level = Integer.parseInt(element.getAttributeValue("level"));
            final int cost = element.getAttributeValue("cost") == null ? 0 : Integer.parseInt(element.getAttributeValue("cost"));
            final int min_level = Integer.parseInt(element.getAttributeValue("min_level"));
            final int item_id = element.getAttributeValue("item_id") == null ? 0 : Integer.parseInt(element.getAttributeValue("item_id"));
            final long item_count = element.getAttributeValue("item_count") == null ? 1 : Long.parseLong(element.getAttributeValue("item_count"));
            final boolean clicked = element.getAttributeValue("clicked") != null && Boolean.parseBoolean(element.getAttributeValue("clicked"));

            skillLearns.add(new SkillLearn(id, level, min_level, cost, item_id, item_count, clicked));
        }

        return skillLearns;
    }

    private static class LazyHolder {
        private static final SkillAcquireParser INSTANCE = new SkillAcquireParser();
    }
}
