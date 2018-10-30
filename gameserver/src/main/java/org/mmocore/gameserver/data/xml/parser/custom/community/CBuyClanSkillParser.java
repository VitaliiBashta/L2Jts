package org.mmocore.gameserver.data.xml.parser.custom.community;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractDirParser;
import org.mmocore.gameserver.data.xml.holder.custom.community.CBuyClanSkillHolder;
import org.mmocore.gameserver.templates.custom.community.BuyClanSkillTemplate;
import org.mmocore.gameserver.utils.Language;

import java.io.File;

/**
 * @author Mangol
 * @since 06.03.2016
 */
public class CBuyClanSkillParser extends AbstractDirParser<CBuyClanSkillHolder> {
    private static final CBuyClanSkillParser INSTANCE = new CBuyClanSkillParser();

    protected CBuyClanSkillParser() {
        super(CBuyClanSkillHolder.getInstance());
    }

    public static CBuyClanSkillParser getInstance() {
        return INSTANCE;
    }

    @Override
    public File getXMLDir() {
        return new File("data/xmlscript/community/service/clanSkill/");
    }

    @Override
    public boolean isIgnored(File f) {
        return false;
    }

    @Override
    public String getDTDFileName() {
        return "skill.dtd";
    }

    @Override
    protected void readData(final CBuyClanSkillHolder holder, final Element rootElement) {
        rootElement.getChildren("clan_skill").forEach(elem -> parseSkill(holder, elem));
    }

    private void parseSkill(final CBuyClanSkillHolder holder, final Element elem) {
        for (final Element element : elem.getChildren("skill")) {
            final int id = Integer.parseInt(element.getAttributeValue("id"));
            final int level = Integer.parseInt(element.getAttributeValue("level"));
            final int minLevel = Integer.parseInt(element.getAttributeValue("min_level"));
            final int item_id = Integer.parseInt(element.getAttributeValue("item_id"));
            final long item_count = Long.parseLong(element.getAttributeValue("item_count"));
            final String icon = element.getAttributeValue("icon");
            final String[] nameRu = new String[1];
            final String[] nameEn = new String[1];
            final String[] descRu = new String[1];
            final String[] descEn = new String[1];
            final Element names = element.getChild("names");
            names.getChildren().forEach(name -> {
                final Language language = Language.valueOf(name.getAttributeValue("lang"));
                switch (language) {
                    case RUSSIAN:
                        nameRu[0] = name.getAttributeValue("val");
                        break;
                    case ENGLISH:
                        nameEn[0] = name.getAttributeValue("val");
                        break;
                }
            });
            final Element descriptions = element.getChild("descriptions");
            descriptions.getChildren().forEach(desc -> {
                final Language language = Language.valueOf(desc.getAttributeValue("lang"));
                switch (language) {
                    case RUSSIAN:
                        descRu[0] = desc.getAttributeValue("val");
                        break;
                    case ENGLISH:
                        descEn[0] = desc.getAttributeValue("val");
                        break;
                }
            });
            final BuyClanSkillTemplate buyClanSkillTemplate = new BuyClanSkillTemplate(id, level, minLevel, item_id, item_count, icon, nameRu[0], nameEn[0], descRu[0], descEn[0]);
            holder.addClanSkill(buyClanSkillTemplate);
        }
    }
}
