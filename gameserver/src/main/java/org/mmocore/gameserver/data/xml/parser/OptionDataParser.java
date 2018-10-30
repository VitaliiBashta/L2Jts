package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Element;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.OptionDataHolder;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.OptionDataTemplate;

import java.io.File;

/**
 * @author VISTALL
 * @date 20:36/19.05.2011
 */
public final class OptionDataParser extends StatParser<OptionDataHolder> {
    private OptionDataParser() {
        super(OptionDataHolder.getInstance());
    }

    public static OptionDataParser getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override
    public File getXMLDir() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/stats/option_data");
    }

    @Override
    public boolean isIgnored(final File f) {
        return false;
    }

    @Override
    public String getDTDFileName() {
        return "option_data.dtd";
    }

    @Override
    protected void readData(final OptionDataHolder holder, final Element rootElement) {
        for (final Element optionDataElement : rootElement.getChildren("option_data")) {
            final OptionDataTemplate template = new OptionDataTemplate(Integer.parseInt(optionDataElement.getAttributeValue("id")));

            for (final Element subElement : optionDataElement.getChildren()) {
                final String subName = subElement.getName();

                if ("for".equalsIgnoreCase(subName)) {
                    parseFor(subElement, template);
                } else if ("triggers".equalsIgnoreCase(subName)) {
                    parseTriggers(subElement, template);
                } else if ("skills".equalsIgnoreCase(subName)) {
                    for (final Element skillElement : subElement.getChildren("skill")) {
                        final int id = Integer.parseInt(skillElement.getAttributeValue("id"));
                        final int level = Integer.parseInt(skillElement.getAttributeValue("level"));

                        final SkillEntry skill = SkillTable.getInstance().getSkillEntry(id, level);

                        if (skill != null) {
                            template.addSkill(skill);
                        } else {
                            info("Skill not found(" + id + ',' + level + ") for option data:" + template.getId() + "; file:" + getCurrentFileName());
                        }
                    }
                }
            }

            holder.addTemplate(template);
        }
    }

    @Override
    protected Object getTableValue(final String name) {
        return null;
    }

    private static class LazyHolder {
        private static final OptionDataParser INSTANCE = new OptionDataParser();
    }
}
